# 📝 AI Notes Generator (AInotes)

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg?style=for-the-badge&logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Compose-1.6.0--alpha-purple.svg?style=for-the-badge&logo=android)](https://developer.android.com/jetpack/compose)
[![Gemini](https://img.shields.io/badge/Gemini_API-2.5--Flash-orange.svg?style=for-the-badge&logo=googlegemini)](https://ai.google.dev/)
[![Firebase](https://img.shields.io/badge/Firebase-Sync--Auth-yellow.svg?style=for-the-badge&logo=firebase)](https://firebase.google.com/)
[![Room DB](https://img.shields.io/badge/Room_DB-Local_SQLite-green.svg?style=for-the-badge&logo=sqlite)](https://developer.android.com/training/data-storage/room)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM_Clean-red.svg?style=for-the-badge)](https://developer.android.com/topic/architecture)

An ultra-premium, high-fidelity Android application designed to elevate the study experience. Built entirely in **Jetpack Compose** and **Kotlin**, AInotes leverages local **Google ML Kit OCR**, raw **PDF text extraction**, and Google's next-generation **Gemini API REST fallbacks** to transform documents, images, and handwritten notes into comprehensive study guides (summarized key points, formulas, interactive flashcards, and practice exam questions).

---

## 📖 Table of Contents
1. [🎨 Premium UI/UX & Visual Preview](#-premium-uiux--visual-preview)
2. [✨ Key Features](#-key-features)
3. [⚙️ System Architecture](#%EF%B8%8F-system-architecture)
4. [🤖 AI Model Fallback Pipeline](#-ai-model-fallback-pipeline)
5. [📁 Codebase Directory Structure](#-codebase-directory-structure)
6. [🛠️ Setup & Installation](#%EF%B8%8F-setup--installation)
7. [🔐 Data Model & Local Schema](#-data-model--local-schema)
8. [🤝 Contribution Guidelines](#-contribution-guidelines)

---

## 🎨 Premium UI/UX & Visual Preview

AInotes is engineered with **state-of-the-art mobile UI aesthetics** featuring a harmonized **Violet & Indigo** visual theme, smooth canvas rendering, glassmorphism elements, and glowing gradients.

### 📱 Screenshots & Visual Walkthrough

| 🚀 1. Onboarding Screen | 🏠 2. Time-Aware Dashboard | ✏️ 3. Create Note Screen |
| :---: | :---: | :---: |
| ![Onboarding](../assets/screenshots/ss_onboarding.png) | ![Dashboard](../assets/screenshots/ss_dashboard.png) | ![Create Note](../assets/screenshots/ss_create_note.png) |

| 🤖 4. AI Orbit Loader | 📚 5. My Notes Library |
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

## ⚙️ System Architecture

AInotes is architected using **MVVM (Model-View-ViewModel)** and **Clean Architecture** patterns to ensure maximum testability, modularity, and clean separation of concerns.

```mermaid
flowchart TD
    subgraph Presentation Layer
        UI[Jetpack Compose UI Screens] <--> VM[ViewModels]
    end
    
    subgraph Domain & Data Layer
        VM <--> Repo[Clean Repository Interfaces]
        Repo <--> Room[Local Room Database Cache]
        Repo <--> Sync[FirebaseSyncRepository]
        Repo <--> Gemini[Gemini API Client]
    end

    subgraph External Services
        Sync <--> Fire[Cloud Firestore & Auth]
        Gemini <--> GoogleAPI[Google Generative Language API]
    end
    
    style Presentation Layer fill:#efe,stroke:#3b3,stroke-width:2px
    style Domain & Data Layer fill:#eef,stroke:#33b,stroke-width:2px
    style External Services fill:#fee,stroke:#b33,stroke-width:2px
```

---

## 🤖 AI Model Fallback Pipeline

To ensure **99.9% processing uptime** and protect users against API quota exhaustion, AInotes implements an automated **Generative Model Fallback Chain**. If the primary model fails or returns a rate limit (HTTP 429), the repository seamlessly falls back to the next model in line within milliseconds.

Aligned with the latest **2026 API deprecation cycles**, AInotes routes calls through active, high-quota endpoints on `v1beta`:

```mermaid
graph LR
    Start([Start Note Request]) --> M1[1. gemini-2.5-flash]
    M1 -- Quota / Error --> M2[2. gemini-3.5-flash]
    M2 -- Quota / Error --> M3[3. gemini-3.1-flash-lite]
    M3 -- Quota / Error --> M4[4. gemini-flash-latest]
    M4 -- Quota / Error --> M5[5. gemini-2.0-flash]
    M5 -- Quota / Error --> M6[6. gemini-2.5-pro]
    M6 -- Success --> End([Study Guide Generated!])
    
    style M1 fill:#d5f5e3,stroke:#27ae60
    style M2 fill:#d5f5e3,stroke:#27ae60
    style M3 fill:#d5f5e3,stroke:#27ae60
    style End fill:#ebf5fb,stroke:#2980b9
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
│       ├── GeminiRepository.kt    # Robust direct REST generative client with fallback chain
│       ├── SessionRepository.kt   # Local data persistence coordinator
│       ├── ProfileRepository.kt   # Interface for User Profile fetching
│       ├── ProfileRepositoryImpl.kt # Firestore Implementation for User Profiles
│       └── FirebaseSyncRepository.kt # Cloud sync & Auth coordinator
│
├── di
│   └── AppModule.kt               # Dagger Hilt dependency Injection bindings
│
├── service
│   └── DocumentProcessingService.kt # Background task runner
│
├── ui
│   ├── screens
│   │   ├── home                   # Dashboard & Create Note Screen
│   │   ├── history                # Saved Study Library & Category Filters
│   │   ├── results                # 5-Tab Interactive Study View
│   │   ├── login                  # Premium Authentication Portal
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

## 🛠️ Setup & Installation

### 1. Requirements
*   Android Studio Jellyfish (or newer)
*   JDK 17 configured in Android Studio Gradle settings
*   Android SDK 34 (Android 14) or higher

### 2. Clone and Open
```bash
git clone https://github.com/anuragsutar887-hash/ai-notes-generator.git
```
Open the project in Android Studio and let Gradle synchronize files.

### 3. Add API Keys
Open `local.properties` in your project's root folder and append your API key:
```properties
sdk.dir=C\:\\Users\\YOUR_NAME\\AppData\\Local\\Android\\Sdk
GEMINI_API_KEY=AIzaSyCXapWMdSEcCrD2jpG57ERJZAIKtfYMoGA
```
*(Gradle will automatically inject this value into `BuildConfig.GEMINI_API_KEY` at compile-time).*

### 4. Connect Firebase Configuration
1.  Navigate to [Firebase Console](https://console.firebase.google.com/) and register a new Android application under the package name `com.ainotes`.
2.  Activate **Email & Password Authentication** and **Cloud Firestore Database**.
3.  Download your generated `google-services.json` file and place it in the `/app/` directory of the project.

### 5. Build debug APK
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

### `note_sessions` Table (Room Persistence)
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

1.  Fork the repository and clone it.
2.  Create a descriptive branch (`git checkout -b feature/CoolNewComponent`).
3.  Implement changes, adhering to clean MVVM and Kotlin Coroutine standards.
4.  Ensure local builds compile cleanly using `.\gradlew.bat compileDebugKotlin`.
5.  Submit a detailed Pull Request.

---

*Designed and Developed with 💜 by Anurag*
