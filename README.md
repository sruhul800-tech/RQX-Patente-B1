# RQX Patente B · Cinematic V15
Android Studio project with the original V15 quiz and RealCity driving game. **APK build pending CI verification.**

## Build
Open [Actions](https://github.com/sruhul800-tech/RQX-Patente-B1/actions) and select **Build RQX Android APK**, then Run workflow. The workflow should produce a debug APK artifact.

Android Studio: open `android/`, use Java 17 and Android SDK 35, then run `gradle assembleDebug`.

## Files
- `android/app/src/main/assets/index.html` — full V15 quiz app.
- `android/app/src/main/assets/simulator.html` — standalone V15 3D game.
- `index.html` — web preview.
- `simulator.html` — web standalone game.

The original 2023 quiz dataset is included inside the HTML. This repository is currently **public**. Keep API keys, signing keys and personal data out of commits. This is an educational simulator and is not an official Italian exam app.