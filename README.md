# 🧠 AI Notes Generator

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green?style=for-the-badge&logo=android" />
  <img src="https://img.shields.io/badge/Language-Kotlin-blue?style=for-the-badge&logo=kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-purple?style=for-the-badge&logo=jetpack-compose" />
  <img src="https://img.shields.io/badge/AI-Gemini-orange?style=for-the-badge&logo=google" />
  <img src="https://img.shields.io/badge/minSdk-26-red?style=for-the-badge" />
</p>

> **Create smart, beautiful AI-powered study notes from PDFs, images, and documents — in seconds.**

---

## ✨ Features

| Feature | Description |
|---|---|
| 📤 **Smart Document Upload** | Upload PDFs, images, or any file and let AI do the work |
| 📘 **Study Notes** | Auto-generated structured key-point notes |
| 📇 **Flashcards** | Question & answer cards for active recall |
| ❓ **Exam Prep** | Practice questions generated from your material |
| 🔢 **Formulae** | Extract all formulas from documents |
| 💬 **Custom Mode** | Ask anything about your uploaded document |
| 💾 **Save & Revisit** | Bookmark your favourite note sessions |
| 🌓 **Dark / Light Mode** | Fully themed premium UI |
| 🔐 **Google Sign-In** | Secure Firebase Authentication |

---

## 📱 Tech Stack

- **UI**: Jetpack Compose with Material 3
- **Architecture**: MVVM + Hilt Dependency Injection
- **AI**: Google Gemini API (multi-model fallback chain)
- **Database**: Room (local SQLite)
- **Backend**: Firebase Auth, Firestore, Storage
- **OCR**: ML Kit Text Recognition
- **PDF Parsing**: PdfBox Android
- **Navigation**: Jetpack Navigation Compose

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- Android SDK 26+
- A Google account for Firebase

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/AInotes.git
   cd AInotes
   ```

2. **Set up local.properties**
   ```bash
   cp local.properties.example local.properties
   ```
   Then fill in your:
   - `sdk.dir` — your Android SDK path
   - `GEMINI_API_KEY` — get one free at [Google AI Studio](https://aistudio.google.com/)

3. **Set up Firebase**
   - Create a project at [Firebase Console](https://console.firebase.google.com/)
   - Add Android app with package: `com.ainotes`
   - Enable: **Google Sign-In**, **Firestore**, **Storage**
   - Download `google-services.json` → place in `app/` directory
   - See `app/google-services.json.example` for guidance

4. **Build & Run**
   ```bash
   ./gradlew assembleDebug
   ```
   Or open in Android Studio and press ▶️

---

## 📂 Project Structure

```
app/src/main/java/com/ainotes/
├── data/
│   ├── local/          # Room DB, SharedPreferences
│   ├── model/          # Data models & entities
│   └── repository/     # Data access layer
├── di/                 # Hilt dependency injection modules
├── service/            # AI generation service (Gemini)
├── ui/
│   ├── navigation/     # Navigation graph
│   ├── screens/        # All app screens
│   │   ├── home/       # Dashboard + Create Note + Loading
│   │   ├── history/    # My Notes / Saved notes
│   │   ├── login/      # Google Sign-In
│   │   ├── profile/    # Profile setup & edit, Splash
│   │   └── results/    # Generated notes viewer
│   └── theme/          # Color, typography, theme
└── util/               # Helper utilities
```

---

## 🔑 Environment Variables

| Variable | Where to get it |
|---|---|
| `GEMINI_API_KEY` | [Google AI Studio](https://aistudio.google.com/) — Free tier available |
| `google-services.json` | [Firebase Console](https://console.firebase.google.com/) |

> ⚠️ **Never commit** `local.properties` or `google-services.json` — they are in `.gitignore`

---

## 🙌 Contributing

Pull requests are welcome! For major changes, please open an issue first.

---

## 📄 License

This project is licensed under the MIT License.

---

<p align="center">Made with ❤️ using Kotlin & Jetpack Compose</p>
