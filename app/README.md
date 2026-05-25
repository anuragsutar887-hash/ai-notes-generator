# 📝 AI Notes Generator (AInotes)

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg?style=for-the-badge&logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Compose-1.6.0-purple.svg?style=for-the-badge&logo=android)](https://developer.android.com/jetpack/compose)
[![Firebase Cloud Functions](https://img.shields.io/badge/Firebase_Backend-Cloud_Functions-orange.svg?style=for-the-badge&logo=firebase)](https://firebase.google.com/)
[![Room DB](https://img.shields.io/badge/Room_DB-Local_SQLite-green.svg?style=for-the-badge&logo=sqlite)](https://developer.android.com/training/data-storage/room)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM_Clean-red.svg?style=for-the-badge)](https://developer.android.com/topic/architecture)

An ultra-premium, high-fidelity Android application designed to elevate the study experience. Built entirely in **Jetpack Compose** and **Kotlin**, AInotes leverages local **Google ML Kit OCR**, raw **PDF text extraction**, and a secure **Firebase Cloud Functions server-side AI proxy** to transform documents, images, and handwritten notes into comprehensive study guides (summarized key points, formulas, interactive 3D flashcards, and practice exam questions).

Unlike generic AI apps that force users to register their own API keys, **AInotes operates like a real-world production application**. The Gemini API key is stored securely on the backend server—never embedded inside the APK or visible to clients. Users simply open the app, authenticate with Google, and begin generating notes instantly.

---

## 📖 Table of Contents
1. [🎨 Premium UI/UX & Visual Preview](#-premium-uiux--visual-preview)
2. [✨ Key Features](#-key-features)
3. [🛡️ Secure Server-Side Architecture](#%EF%B8%8F-secure-server-side-architecture)
4. [🤖 AI Model Fallback Chain](#-ai-model-fallback-chain)
5. [⚙️ System Architecture Diagram](#%EF%B8%8F-system-architecture-diagram)
6. [📁 Codebase Directory Structure](#-codebase-directory-structure)
7. [🛠️ Developer Setup & Deployment](#%EF%B8%8F-developer-setup--deployment)
8. [🔐 Data Model & Local Schema](#-data-model--local-schema)
9. [🤝 Contribution Guidelines](#-contribution-guidelines)

---

## 🎨 Premium UI/UX & Visual Preview

AInotes is engineered with **state-of-the-art mobile UI aesthetics** featuring a harmonized **Violet & Indigo** visual theme, smooth canvas rendering, glassmorphism elements, and glowing gradients.

### 📱 Screenshots & Visual Walkthrough

| 🚀 1. Onboarding Screen | 🏠 2. Time-Aware Dashboard | ✏️ 3. Create Note Screen |
| :---: | :---: | :---: |
| ![Onboarding](../assets/screenshots/ss_onboarding.png) | ![Dashboard](../assets/screenshots/ss_dashboard.png) | ![Create Note](../assets/screenshots/ss_create_note.png) |

| 🤖 4. AI Orbit Loader | 📚 5. My Notes Library |
| :---: | :---: |
| ![AI Loader](../assets/screenshots/ss_ai_loading.png) | ![My Notes](../assets/screenshots/ss_my_notes.png) |

### 🎨 Design Highlights:
*   **Premium Theme Palette:** Built on a customized violet primary (`#6C5CE7`), deep indigo highlights, soft lavender card containers (`#F7F6FF`), and teal accents (`#00B894`).
*   **Time-Aware Dashboard:** Greets users dynamically based on time of day (e.g. *"Good evening, Anurag! 👋"*), displaying first-name extractions from Cloud Firestore alongside custom circular initial avatars.
*   **Interactive Orbit Loader:** When processing notes, users are presented with a gorgeous animated robot with canvas-drawn orbiting rings utilizing sweep gradients, floating particles, and a glowing linear loader tracking progress.
*   **Textbook-Style Formula Blocks:** Displays chemistry, physical, and mathematical equations inside premium green-bordered formula boxes matching real academic textbooks.
*   **Visual File Upload Panel:** Spacious upload zone that renders custom status changes—including glowing state badges that transition into interactive checkmarks once a document is successfully loaded.
*   **Interactive Study View:** Includes 3D flashcards with smooth 180-degree flip animations, toggleable bookmarks, clipboard sharing, and text downloads.

---

## ✨ Key Features

*   **Multi-Format Document Ingestion:** Ingests PDFs, printed images, handwritten notes, and plain text.
*   **Local On-Device OCR:** Employs **Google ML Kit Text Recognition** for instant, secure local OCR processing of physical notes, documents, and screenshots.
*   **Chunked PDF Processing:** Seamlessly processes documents of unlimited length by smart page-chunking via `PdfBox-Android` to dodge token and gateway constraints.
*   **Comprehensive Study Modes:** Generates 5 distinct content types:
    *   `Study Notes / Key Points` with expandable rich-media topic cards.
    *   `Formulae & Scientific Laws` with explanation blocks.
    *   `3D Flashcards` with smooth, touch-activated flip transitions.
    *   `Exam Preparation` featuring MCQs, short answers, and detailed model responses.
    *   `Custom Prompts` allowing custom analytical queries on top of documents.
*   **Persistent Offline-First Cache:** Stores generated notes inside local SQLite DBs using **Room Persistence Library**, ensuring complete offline availability.
*   **Instant Cloud Synchronization:** Integrates **Firebase Authentication** and **Google Cloud Firestore / Storage** to back up and synchronize study documents across all devices automatically.

---

## 🛡️ Secure Server-Side Architecture

In standard client-side AI apps, the Gemini API key must be compiled into `local.properties` or typed in by the user. This poses major security risks (key leakage) and creates friction for everyday users. 

AInotes completely resolves this with a **Firebase Cloud Functions backend proxy**:

```
[User Phone (AInotes Client)] 
           │ (HTTPS Callable)
           ▼
[Firebase Cloud Function (generateNotes)] ──► Reads API key securely from server config
           │ 
           ▼
[Google Gemini API] ──► Processes document & returns structured study notes
```

### Key Advantages:
1. **Zero Setup for Users:** Users do not need to register on Google AI Studio or obtain an API key. The app "just works" out of the box.
2. **Hidden Secrets:** The Gemini API key resides strictly in your Firebase Google Cloud console config—completely safe from reverse-engineering or APK decompilation.
3. **Protected Endpoints:** Only successfully authenticated Firebase users can call the proxy function, preventing anonymous API abuse and spam.

---

## 🤖 AI Model Fallback Chain

To guarantee high availability and bypass API rate limits under heavy traffic, our backend implements a robust **Exponential Backoff & Generative Model Fallback Chain**. If the primary model fails or returns a rate limit (HTTP 429), the server seamlessly cascades through next-in-line Gemini models:

```mermaid
graph LR
    Start([Start Note Request]) --> M1[1. gemini-2.0-flash]
    M1 -- HTTP 429 / Error --> M2[2. gemini-2.0-flash-lite]
    M2 -- HTTP 429 / Error --> M3[3. gemini-1.5-flash]
    M3 -- HTTP 429 / Error --> M4[4. gemini-1.5-flash-8b]
    M4 -- HTTP 429 / Error --> M5[5. gemini-1.5-pro]
    M5 -- Success --> End([Study Guide Generated!])
    
    style M1 fill:#d5f5e3,stroke:#27ae60
    style M2 fill:#d5f5e3,stroke:#27ae60
    style M3 fill:#d5f5e3,stroke:#27ae60
    style End fill:#ebf5fb,stroke:#2980b9
```

---

## ⚙️ System Architecture Diagram

The codebase is built on strict **MVVM (Model-View-ViewModel)** and **Clean Architecture** patterns:

```mermaid
flowchart TD
    subgraph PresLayer["Presentation Layer (Jetpack Compose UI)"]
        UI[Screens / Composables] <--> VM[ViewModels]
    end
    
    subgraph DomainDataLayer["Domain & Data Layer"]
        VM <--> Repo[SessionRepository / AuthRepository]
        Repo <--> Room[Local Room SQLite DB]
        Repo <--> CloudSync[FirebaseSyncRepository]
        Repo <--> BackendProxy[FirebaseFunctions calling generateNotes]
    end

    subgraph ExtServices["Secure External Infrastructure"]
        CloudSync <--> FireStore[Cloud Firestore & Auth]
        BackendProxy <--> GoogleGemini[Google Generative Language API]
    end
    
    style PresLayer fill:#efe,stroke:#3b3,stroke-width:2px
    style DomainDataLayer fill:#eef,stroke:#33b,stroke-width:2px
    style ExtServices fill:#fee,stroke:#b33,stroke-width:2px
```

---

## 📁 Codebase Directory Structure

```
com.ainotes
│
├── data
│   ├── local
│   │   ├── AppDatabase.kt         # Room SQLite DB & Type Converters
│   │   ├── ThemePreferences.kt    # SharedPreferences for Theme status
│   │   └── UserPreferences.kt     # SharedPreferences (e.g. Onboarding Status)
│   │
│   ├── model
│   │   ├── Models.kt              # Data Structures (NoteSession, StudyNotes, Flashcard)
│   │   └── UserProfile.kt         # Firestore Sync Profile structures
│   │
│   └── repository
│       ├── AuthRepository.kt      # Interface for Firebase Auth
│       ├── AuthRepositoryImpl.kt  # Implementation of Firebase Auth
│       ├── GeminiRepository.kt    # Secure HttpsCallable proxy wrapper with model backoffs
│       ├── SessionRepository.kt   # Local data persistence coordinator
│       ├── ProfileRepository.kt   # Interface for User Profile fetching
│       ├── ProfileRepositoryImpl.kt # Firestore Implementation for User Profiles
│       └── FirebaseSyncRepository.kt # Cloud sync & Auth coordinator
│
├── di
│   ├── AppModule.kt               # Dagger Hilt Database / Utility bindings
│   └── FirebaseModule.kt          # Dagger Hilt Firebase Auth / Functions bindings
│
├── service
│   └── DocumentProcessingService.kt # Background task runner
│
├── ui
│   ├── screens
│   │   ├── home                   # Dashboard & Create Note Screen
│   │   ├── history                # Bookmarked Study Library & Category Filters
│   │   ├── results                # 5-Tab Interactive Study View
│   │   ├── login                  # Secure Authentication Portal
│   │   └── profile                # User Profile & Setup screens
│   │
│   └── theme
│       ├── Color.kt               # Premium color tokens & gradients
│       ├── ColorScheme.kt         # Lavender light/dark palettes
│       └── Theme.kt               # Compose custom application theme
│
└── util
    ├── PdfChunker.kt              # PDF text parser and chunk coordinator
    ├── OcrHelper.kt               # Local Google ML Kit OCR engine
    └── FileHelper.kt              # Internal content resolver & extension helpers
```

---

## 🛠️ Developer Setup & Deployment

### 1. Requirements
*   Android Studio Jellyfish (or newer)
*   JDK 17 configured in Android Studio Gradle settings
*   Android SDK 34 (Android 14) or higher
*   Node.js (v18+) and npm installed locally (for backend deployment)

### 2. Connect Firebase Config
1. Go to [Firebase Console](https://console.firebase.google.com/) and create a new project named `ainotes`.
2. Register a new Android application under the package name `com.ainotes`.
3. Enable **Email & Password Authentication** and **Cloud Firestore Database**.
4. Download your `google-services.json` file and place it in the `/app/` directory of the project.

---

### 🚀 Deploying the Backend Proxy (One-Time Setup)

To activate the AI server-side proxy so the APK works without a locally compiled key, run the following steps:

#### **Step A: Upgrade to Blaze Plan**
Firebase Cloud Functions require the **Blaze (pay-as-you-go) plan**. 
* **Cost Note:** You remain inside the massive free tier (**2 million free function invocations per month**). You will not be charged unless you exceed these limits.
1. Go to: `https://console.firebase.google.com/project/ainotes-mpf7bf5u/usage/details`
2. Click **"Upgrade"** and choose the **Blaze Plan**.

#### **Step B: Install Firebase CLI**
Open your terminal and run:
```bash
npm install -g firebase-tools
```

#### **Step C: Login to Firebase**
```bash
firebase login
```
A browser window will open. Sign in with your Google account that owns the Firebase project.

#### **Step D: Set the Gemini API Key on Firebase**
Inject your Gemini API key securely into your cloud environment (replace with your actual key from [Google AI Studio](https://aistudio.google.com/)):
```bash
firebase functions:config:set gemini.key="YOUR_GEMINI_API_KEY" --project ainotes-mpf7bf5u
```

#### **Step E: Deploy Cloud Functions**
Deploy the Javascript backend server directly to Firebase:
```bash
# Navigate to the root directory
cd "c:\Users\anurag\Desktop\AInotes"

# Deploy functions
firebase deploy --only functions --project ainotes-mpf7bf5u
```
Once completed successfully, you'll see `✔  Deploy complete!` and your function is live!

---

### 3. Build the APK
Run from Gradle terminal:
```bash
# Windows
.\gradlew.bat assembleDebug

# macOS / Linux
./gradlew assembleDebug
```
The resulting package will be compiled at `app/build/outputs/apk/debug/app-debug.apk`. 

---

## 🔐 Data Model & Local Schema

### `note_sessions` Table (Room SQLite Cache)
| Column | DataType | Description |
| :--- | :--- | :--- |
| `id` | `String` (Primary Key) | Auto-generated Session UUID |
| `title` | `String` | Document/Topic title (e.g. *"Pasted Text"*, *"Cell Biology.pdf"*) |
| `inputType` | `String` | `PDF`, `IMAGE`, `TEXT`, or `HANDWRITTEN` |
| `mode` | `String` | Selected Generation Mode |
| `createdAt` | `Long` | Millisecond epoch timestamp |
| `isSaved` | `Boolean` | Bookmark status (toggled inside study results view) |
| `customQuery` | `String` | Optional prompt queried by the user |
| `notes` | `String` (JSON Blob) | Serialized `StudyNotes` parsed via custom `Gson` type converters |
| `pageCount` | `Int` | Number of parsed pages from the document |
| `processingTimeMs`| `Long` | Time taken to generate the AI notes |

---

## 🤝 Contribution Guidelines

We highly appreciate contributions to make AInotes even better!

1. Fork the repository and clone it.
2. Create a descriptive branch (`git checkout -b feature/CoolNewComponent`).
3. Implement changes, adhering to clean MVVM and Kotlin Coroutine standards.
4. Ensure local builds compile cleanly using `.\gradlew.bat compileDebugKotlin`.
5. Submit a detailed Pull Request.

---

*Designed and Developed with 💜 by Anurag*
