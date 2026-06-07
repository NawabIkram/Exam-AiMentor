# CSS AI Mentor

Phase 1 MVP Android app for CSS exam preparation in Pakistan, built with Kotlin, Jetpack Compose, Material 3, Hilt, Navigation Compose, Firebase-ready repositories, Room, and Gemini API integration.

## Project Path

`C:\Users\Nawab Ikram\AndroidStudioProjects\CSSAIMentor`

## What Is Included

- Splash screen, onboarding, login, signup, forgot password
- Premium dark Compose design system with reusable cards, buttons, fields, shimmer, and animations
- Home dashboard with streak, accuracy, weekly progress, and feature cards
- ChatGPT-style AI mentor screen with Gemini-ready repository and demo fallback response
- Past papers and books library with search, filters, favorites, and PDF open
- Native PDF viewer with download, zoom, bookmark, and reading progress storage
- MCQ quiz flow with timer, answer checking, explanations, result screen, and progress save hook
- Profile screen with stats, settings placeholder, and logout
- Room database for favorites, chat history, and PDF bookmarks
- Firebase Auth, Firestore, Storage, Messaging, and Remote Config wiring

## Run From Android Studio

1. Open Android Studio.
2. Choose `Open`.
3. Select `C:\Users\Nawab Ikram\AndroidStudioProjects\CSSAIMentor`.
4. Add your real Firebase config at `app\google-services.json` using `app\google-services.example.json` as the template.
5. Let Gradle sync finish.
6. Run `app` on an emulator or device.

## Command Line Build

```powershell
$env:JAVA_HOME='E:\Android\jbr'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat :app:assembleDebug --offline --console=plain
```

Debug APK:

`app\build\outputs\apk\debug\app-debug.apk`

## Demo Login

Use any valid email and an 8+ character password, for example:

- Email: `demo@css.com`
- Password: `password123`

The app is launch-demo friendly: if real Firebase or Gemini keys are not configured yet, repositories fall back to polished sample data so the MVP still runs.

## Production Firebase Setup

The real `app\google-services.json` is intentionally ignored by Git. Use `app\google-services.example.json` as a shape reference, then place the real Firebase Android config locally for package:

`com.cssaimentor.app`

Recommended Firestore collections:

- `users`
- `papers`
- `books`
- `quizzes`
- `quiz_results`
- `chats`

PDF files should be uploaded to Firebase Storage and their download URLs stored in `papers` or `books`.

## Gemini Setup

For development, pass a key through Gradle:

```powershell
.\gradlew.bat :app:assembleDebug -PGEMINI_API_KEY="YOUR_KEY"
```

Or keep it local in `C:\Users\Nawab Ikram\.gradle\gradle.properties`:

```properties
GEMINI_API_KEY=YOUR_KEY
```

For production, use a secure backend/proxy instead of shipping the Gemini API key inside the mobile app.
