# 🔐 Secure Firestore API Key Setup (No Blaze Plan Required!)

This project now uses a **100% free** secure Firestore-based API key system. 

By storing the Gemini API key in your Cloud Firestore database instead of hardcoding it in the APK, you get all the benefits of a production application without having to switch to a paid Firebase Blaze plan or adding a credit card!

---

## 💡 How It Works

```
[AInotes Client (Android APK)]
             │
             │ (1. Fetches secure API key dynamically on generation)
             ▼
[Firebase Cloud Firestore] ──► Reads 'secrets/gemini' document
             │
             │ (2. Calls Gemini API securely using the fetched key)
             ▼
[Google Gemini API] ──► Returns structured study notes
```

*   **100% Free**: Operates entirely within the free Firebase Spark plan. No credit card required.
*   **Zero Setup for Users**: Your users just open the app, log in, and use it. No API key inputs.
*   **Dynamic Key Rotation**: You can change, revoke, or rotate your API key in the Firestore console at any time without rebuilding or re-releasing the APK!

---

## 🛠️ Step-by-Step Setup Guide

Follow these 3 simple steps in your Firebase Console to activate your app:

### Step 1: Open Your Firestore Database
1. Open the [Firebase Console](https://console.firebase.google.com/).
2. Select your project `ainotes`.
3. In the left-hand navigation bar, click **Firestore Database**.

---

### Step 2: Add the Secrets Document
Create the document containing your Gemini API key:
1. Click **"Start collection"** (or add a collection if one exists).
2. For **Collection ID**, enter: `secrets`
3. Click **Next**.
4. For **Document ID**, enter: `gemini`
5. In the fields section, add:
   *   **Field name**: `key`
   *   **Type**: `string`
   *   **Value**: *Paste your free Gemini API key here* (obtain one for free at [Google AI Studio](https://aistudio.google.com/apikey)).
6. Click **Save**.

Your Firestore database structure will now look like this:
📂 `secrets` (Collection)  
  └─ 📄 `gemini` (Document)  
       └─ 🔑 `key`: `"AIzaSy..."`

---

### Step 3: Secure Your Database Rules (Highly Recommended)
To prevent unauthorized users from editing your key while allowing authenticated users to fetch it:
1. In the **Firestore Database** section, click on the **Rules** tab.
2. Replace your rules with the following secure configuration:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Allow any authenticated user to read the Gemini API key, but block writes
    match /secrets/gemini {
      allow read: if request.auth != null;
      allow write: if false;
    }
    
    // Existing rules for user profiles
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```
3. Click **Publish**.

---

## 📱 Build & Test the APK

1. In Android Studio, select your device/emulator.
2. Click **Run** or build the debug APK:
   *   **Windows**: Run `.\gradlew.bat assembleDebug` in the terminal.
   *   **Output Path**: `app/build/outputs/apk/debug/app-debug.apk`

**That's it!** The app now loads and runs securely without the Firebase Blaze billing plan. 🎉
