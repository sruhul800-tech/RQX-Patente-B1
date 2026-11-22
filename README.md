# RQX Patente B — Cinematic V15 + AI Coach

Preserves V15 quiz + 3D game and adds the bilingual RQX AI Driving Instructor.

## Contents
- `web/index.html` — web app with RQX AI Tutor overlay and original V15 quiz.
- `web/simulator.html` — V15 3D game with a driving-scene AI Coach overlay.
- `android/` — branded Android Studio WebView wrapper and bundled V15+AI HTML. Version `15.1.0-AI` / code `16`.
- `worker/src/index.js` — Cloudflare Worker securing OpenAI API calls behind a separate owner access code.
- `worker/test/worker.test.mjs` — automated backend tests; run `node --test worker/test/*.test.mjs`.
- `docs/SETUP_BN.md` — Bangla installation and AI activation instructions.

## Status

**AI panels and API integration code are implemented. Live AI is not switched on** until the owner deploys the worker and sets both `OPENAI_API_KEY` and `RQX_ACCESS_CODE` as secrets. They must never be put in the HTML, APK, or public GitHub.

Server response includes `it`, `bn`, and `tip`; model outputs can be wrong and should be checked against official rules. The original 2023 quiz collection is retained. This project is educational, not an official driving exam app.

An access code protects a small private pilot; for a public release, use user authentication, quotas and per-account billing protection. Cloudflare KV optional best-effort IP throttling is available.

## Android build

Open `android/` with Android Studio; build using JDK 17, Android SDK 35 and Gradle 8.9. Existing GitHub Actions workflow can build `app-debug.apk`; this package itself does not contain a newly compiled APK.