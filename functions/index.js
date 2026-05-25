const functions = require("firebase-functions");
const admin = require("firebase-admin");
const https = require("https");

admin.initializeApp();

const BASE_URL = "generativelanguage.googleapis.com";
const MODELS = [
  "gemini-2.0-flash",
  "gemini-2.0-flash-lite",
  "gemini-1.5-flash",
  "gemini-1.5-flash-8b",
  "gemini-1.5-pro",
];

/**
 * Firebase HTTPS Callable Function — generateNotes
 *
 * Called from the Android app. Proxies the Gemini API request server-side.
 * The API key is stored in Firebase environment config — never in the APK.
 *
 * Deploy with:
 *   firebase functions:config:set gemini.key="YOUR_GEMINI_API_KEY"
 *   firebase deploy --only functions
 */
exports.generateNotes = functions
  .runWith({
    timeoutSeconds: 180,
    memory: "256MB",
  })
  .https.onCall(async (data, context) => {
    // 1. Require authenticated users only
    if (!context.auth) {
      throw new functions.https.HttpsError(
        "unauthenticated",
        "You must be signed in to use AI Notes."
      );
    }

    const { prompt } = data;
    if (!prompt || typeof prompt !== "string" || prompt.trim().length === 0) {
      throw new functions.https.HttpsError(
        "invalid-argument",
        "A valid prompt is required."
      );
    }

    // 2. Get API key from server-side config (never exposed to client)
    const apiKey = functions.config().gemini?.key;
    if (!apiKey) {
      throw new functions.https.HttpsError(
        "failed-precondition",
        "Server is not configured. Please contact support."
      );
    }

    // 3. Try each model with exponential backoff on rate limits
    const requestBody = JSON.stringify({
      contents: [{ parts: [{ text: prompt }] }],
      generationConfig: {
        temperature: 0.3,
        maxOutputTokens: 8192,
      },
    });

    let lastError = "All AI models failed. Please try again.";

    for (const modelName of MODELS) {
      let retries = 0;
      const maxRetries = 3;

      while (retries <= maxRetries) {
        try {
          if (retries > 0) {
            const backoffMs = 2000 * Math.pow(2, retries - 1); // 2s, 4s, 8s
            await new Promise((resolve) => setTimeout(resolve, backoffMs));
          }

          const result = await callGeminiAPI(modelName, apiKey, requestBody);

          if (result.success) {
            return { text: result.text };
          } else if (result.status === 429) {
            // Rate limited — retry this model
            lastError = result.error;
            retries++;
          } else if (result.status === 404) {
            // Model doesn't exist — skip to next
            lastError = `Model ${modelName} not available`;
            break;
          } else {
            // Other error — skip to next model
            lastError = result.error;
            break;
          }
        } catch (err) {
          lastError = err.message || "Unknown error";
          break;
        }
      }
    }

    throw new functions.https.HttpsError("unavailable", lastError);
  });

/** Makes an HTTPS POST to the Gemini API */
function callGeminiAPI(modelName, apiKey, requestBody) {
  return new Promise((resolve) => {
    const options = {
      hostname: BASE_URL,
      path: `/v1beta/models/${modelName}:generateContent?key=${apiKey}`,
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Content-Length": Buffer.byteLength(requestBody),
      },
    };

    const req = https.request(options, (res) => {
      let data = "";
      res.on("data", (chunk) => (data += chunk));
      res.on("end", () => {
        if (res.statusCode === 200) {
          try {
            const json = JSON.parse(data);
            const text =
              json?.candidates?.[0]?.content?.parts?.[0]?.text ?? "";
            if (text) {
              resolve({ success: true, text });
            } else {
              const blockReason =
                json?.promptFeedback?.blockReason;
              resolve({
                success: false,
                status: 400,
                error: blockReason
                  ? `Content blocked: ${blockReason}`
                  : "Empty response from AI",
              });
            }
          } catch (e) {
            resolve({ success: false, status: 500, error: "Failed to parse AI response" });
          }
        } else {
          let errorMsg = `HTTP ${res.statusCode}`;
          try {
            const json = JSON.parse(data);
            errorMsg = json?.error?.message || errorMsg;
          } catch (_) {}
          resolve({ success: false, status: res.statusCode, error: errorMsg });
        }
      });
    });

    req.on("error", (err) => {
      resolve({ success: false, status: 0, error: err.message });
    });

    req.write(requestBody);
    req.end();
  });
}
