# RQX Patente B

RQX Cinematic V15 is the **stable** quiz + 3D driving simulator baseline.

## Android app status

- Official user-provided gold/silver RQX logo is bundled into the Android source project as launch icon and splash screen.
- Android Studio wrapper uses local WebView assets and optional native Italian text-to-speech.
- **No APK has been built or installed yet.** Android-device tests are pending.
- The full project bundle is held as a downloadable ZIP in the ChatGPT conversation, **not yet uploaded to this repository**.
- The workflow at `.github/workflows/android-apk.yml` needs the `android/` project uploaded before it can produce an APK.
- This repository is currently **public**. Do not commit secret API keys, passwords, signing keys or private information.

## Next step

Upload the extracted source files from `RQX_Patente_B_V15_Official_Android_App_Source.zip` to the repository root, preserving `android/` and `.github/`. Then run **Actions → Build RQX Android APK → Run workflow**. An artifact with the debug APK should appear if the build succeeds.

## Product scope

Educational practice for Italian Patente B. Original V15 graphics are stylized 3D; not photorealistic. The quiz dataset reflects a 2023 collection and current official rules should be checked.
