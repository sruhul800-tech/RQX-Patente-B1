# RQX V15 + AI Instructor — বাংলা নির্দেশনা

এই ভার্সনটি Cinematic V15-এর Quiz ও 3D simulator অপরিবর্তিত রেখে নতুন AI Tutor panel যোগ করেছে।

## এখনই কী কাজ করবে?

- Quiz-এর উত্তর দেওয়ার পরে **AI Tutor → Domanda attuale / Spiega l’errore** ব্যবহার করতে পারবে।
- 3D Driving Game-এর উপরে **✦ AI Coach** বোতাম আছে। গেমের পরিস্থিতি বা sign নিয়ে প্রশ্ন করতে পারবে।
- AI server সেটআপ না থাকলেও offline study guidance দেখা যাবে। Offline guide-কে live AI হিসেবে দেখানো হয় না।
- Italian ও বাংলা voice-এর জন্য device speech support প্রয়োজন।

## Live AI সক্রিয় করতে (server ছাড়া চলবে না)

1. নিজের OpenAI Platform account থেকে API key নিতে হবে; **এখানে বা GitHub-এ কখনো পাঠাবে না**। API ব্যবহারে খরচ হতে পারে।
2. `worker/` folder Cloudflare Workers-এ deploy করো। Workers dashboard-এর Secrets-এ `OPENAI_API_KEY` ও লম্বা আলাদা `RQX_ACCESS_CODE` সেট করো।
3. Worker URL এর শেষে `/api/explain` বসাও। উদাহরণ: `https://rqx-patente-b-ai.<account>.workers.dev/api/explain` (এটি example, বাস্তব URL নয়)।
4. Android অ্যাপে **AI Tutor → Configura AI privata** এ worker endpoint এবং তোমার `RQX_ACCESS_CODE` দাও। OpenAI API key নয়।
5. এরপর প্রশ্নের ব্যাখ্যা AI থেকে আসবে। ব্যর্থ হলে offline guide থাকবে।

## GitHub ও APK

- V15 এর stable branch অক্ষত রাখতে হবে।
- `android/` folder GitHub এ আপলোড হলে APK workflow চালানো যাবে।
- Android debug APK build সফল হলেই সেটাকে downloadable বলবে; installation ও real-device performance আলাদাভাবে পরীক্ষা করতে হবে।
- Repo public হলে কোনো secret commit করা যাবে না।

## গুরুত্বপূর্ণ

AI-generated উত্তর সবসময় নির্ভুল নয়। ২০২৩ সালের dataset-এ কিছু তথ্য পুরোনো হতে পারে; Italian সরকারি নিয়মের সঙ্গে মিলিয়ে শেখো।