/**
 * Re-download updated google-services.json (with OAuth client after SHA-1 was added)
 */
const http = require('http');
const https = require('https');
const fs = require('fs');
const path = require('path');
const crypto = require('crypto');

const CLIENT_ID = '563584335869-fgrhgmd47bqnekij5i8b5pr03ho849e6.apps.googleusercontent.com';
const CLIENT_SECRET = 'j9iVZfS8kkCEFUPaAeJV0sAi';
const REDIRECT_PORT = 9005;
const REDIRECT_URI = `http://localhost:${REDIRECT_PORT}`;
const SCOPES = 'https://www.googleapis.com/auth/cloud-platform https://www.googleapis.com/auth/firebase';
const PROJECT_ID = 'ainotes-mpf7bf5u';
const APP_ID = '1:327916648998:android:4b097ee429ab34c61284e7';
const OUTPUT_FILE = path.join(__dirname, 'app', 'google-services.json');

function httpsRequest(options, body) {
  return new Promise((resolve, reject) => {
    const req = https.request(options, (res) => {
      let data = '';
      res.on('data', chunk => data += chunk);
      res.on('end', () => {
        try { resolve({ status: res.statusCode, body: JSON.parse(data) }); }
        catch { resolve({ status: res.statusCode, body: data }); }
      });
    });
    req.on('error', reject);
    if (body) req.write(body);
    req.end();
  });
}

async function getAccessToken() {
  return new Promise((resolve, reject) => {
    const state = crypto.randomBytes(16).toString('hex');
    const authUrl = `https://accounts.google.com/o/oauth2/v2/auth?client_id=${CLIENT_ID}&redirect_uri=${encodeURIComponent(REDIRECT_URI)}&response_type=code&scope=${encodeURIComponent(SCOPES)}&state=${state}&access_type=offline&prompt=consent`;
    console.log('\n══════════════════════════════════════════════════════');
    console.log('  OPEN THIS URL IN YOUR BROWSER:');
    console.log('══════════════════════════════════════════════════════');
    console.log(authUrl);
    console.log('══════════════════════════════════════════════════════\n');
    const server = http.createServer(async (req, res) => {
      const parsed = new URL(req.url, `http://localhost:${REDIRECT_PORT}`);
      if (parsed.pathname !== '/') { res.end(); return; }
      const code = parsed.searchParams.get('code');
      res.writeHead(200, { 'Content-Type': 'text/html' });
      res.end('<html><body style="font-family:sans-serif;text-align:center;padding-top:80px;background:#0a0a1a;color:white"><h1>✅ Done! Close this tab.</h1></body></html>');
      server.close();
      const bodyStr = new URLSearchParams({ code, client_id: CLIENT_ID, client_secret: CLIENT_SECRET, redirect_uri: REDIRECT_URI, grant_type: 'authorization_code' }).toString();
      const resp = await httpsRequest({ hostname: 'oauth2.googleapis.com', path: '/token', method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded', 'Content-Length': Buffer.byteLength(bodyStr) } }, bodyStr);
      if (resp.status !== 200 || !resp.body.access_token) { reject(new Error('Auth failed: ' + JSON.stringify(resp.body))); return; }
      console.log('🔑 Token obtained!\n'); resolve(resp.body.access_token);
    });
    server.listen(REDIRECT_PORT); server.on('error', reject);
    console.log(`⏳ Waiting on port ${REDIRECT_PORT}...`);
  });
}

async function main() {
  console.log('╔══════════════════════════════════════════════════╗');
  console.log('║  Downloading updated google-services.json        ║');
  console.log('╚══════════════════════════════════════════════════╝\n');
  const token = await getAccessToken();

  const resp = await httpsRequest({
    hostname: 'firebase.googleapis.com',
    path: `/v1beta1/projects/${PROJECT_ID}/androidApps/${APP_ID}/config`,
    method: 'GET',
    headers: { Authorization: `Bearer ${token}` }
  });

  if (resp.status !== 200) throw new Error('Failed: ' + JSON.stringify(resp.body));
  const content = Buffer.from(resp.body.configFileContents, 'base64').toString('utf8');
  fs.writeFileSync(OUTPUT_FILE, content);
  const parsed = JSON.parse(content);
  const oauthClients = parsed.client?.[0]?.oauth_client || [];
  console.log('✅ google-services.json updated!');
  console.log('   OAuth clients found:', oauthClients.length);
  oauthClients.forEach(c => console.log('   -', c.client_id, '| type:', c.client_type));
  if (oauthClients.length === 0) {
    console.log('\n⚠️  No OAuth client yet. This means Google Sign-In OAuth client');
    console.log('   needs to be created in Firebase Console:');
    console.log('   Authentication > Sign-in method > Google > Enable > Save');
    console.log('   Then re-run this script.\n');
  } else {
    console.log('\n🎉 Google Sign-In OAuth client is present — app will work!');
  }
}
main().catch(err => { console.error('❌', err.message); process.exit(1); });
