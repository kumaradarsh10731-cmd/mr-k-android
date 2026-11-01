# MR. K — Android voice assistant starter

This is a real native Kotlin Android project scaffold, not a web chatbot.

## What this first build implements
- Foreground service with a persistent notification.
- User-initiated one-shot voice recognition from the notification's LISTEN action.
- Launch an installed app by spoken app name (e.g. "open YouTube").
- Open Android Settings, Home and Back.
- Accessibility-service foundation for scroll up/down.

## Important honest limitations
- **"Hey Mr. K" wake-word detection is NOT implemented in this starter build.** A reliable offline wake word requires an embedded wake-word engine and a trained model for the phrase. This project deliberately does not pretend that a notification-triggered speech recognizer is always listening.
- Continuous always-on microphone listening is restricted by Android, battery management, and foreground-service rules. This starter uses a visible notification and starts recognition only after you tap LISTEN.
- General in-app search, arbitrary typing, sending messages, and music playback need app-specific actions or carefully scoped Accessibility interactions. They are not universal and are not implemented yet.
- Android 11+ package visibility and store policies restrict broad app enumeration. This project requests QUERY_ALL_PACKAGES for a personal sideloaded assistant; distribution on Google Play would need policy review.
- Speech recognition availability/language depends on the phone's speech service and may use network services.

## Build using GitHub Actions (phone-friendly)
1. Create a GitHub repository named `mr-k-android`.
2. Upload all files/folders from this project to the repository root.
3. Open **Actions** and run **Build MR. K APK** (or push to `main`).
4. Open the completed workflow run, download artifact `MR-K-debug-apk`, extract it, and install `app-debug.apk`.
5. Open MR. K, grant microphone and notification permissions, press START MR. K.
6. In Android Accessibility settings, enable MR. K only if you want Back/scroll controls.
7. Use the persistent notification's LISTEN action and say "open YouTube".

## Privacy
The microphone is used only after you tap LISTEN in this starter. The app does not upload or store command transcripts itself. Android's selected speech recognition provider may process audio according to its own privacy policy.
