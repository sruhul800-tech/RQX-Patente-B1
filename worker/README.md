# RQX AI Driving Instructor — Secure Cloudflare Worker

Cloudflare Worker backend for the RQX V15 bilingual (Italian + Bangla) AI tutor.

## Activate live AI

1. Create a Cloudflare Workers account and deploy this `worker/` project with `npx wrangler login` then `npx wrangler deploy`.
2. Set encrypted **Worker Secrets**: `OPENAI_API_KEY` (private OpenAI Platform API key) and `RQX_ACCESS_CODE` (a separate long random code). Use `npx wrangler secret put OPENAI_API_KEY` and `npx wrangler secret put RQX_ACCESS_CODE`.
3. Copy your actual Worker URL with `/api/explain`. In the RQX Android app open **AI Tutor → Configura AI privata**, enter this URL and your **RQX access code**, NOT the OpenAI key.
4. The API now returns Italian (`it`), Bangla (`bn`), and a learning tip (`tip`) for each study question.
5. For a public app, add real user authentication, quotas, and abuse monitoring before sharing the access code. Optional Cloudflare KV binding `RQX_LIMITS` allows a best-effort IP throttle.

No OpenAI API key is included in these files. The service will not work until the owner deploys it and supplies both secrets. OpenAI API requests may cost money. All AI explanations should be checked against current official driving rules.

Tests: `node --test test/*.test.mjs`.
