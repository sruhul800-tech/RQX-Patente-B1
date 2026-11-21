package com.rqx.patenteb;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.media.AudioAttributes;
import android.provider.Settings;
import org.json.JSONObject;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.webkit.WebViewAssetLoader;

import java.util.Locale;

public class MainActivity extends Activity {
    private WebView web;
    private ImageView splash;
    private TextToSpeech voice;
    private boolean ttsReady = false;
    private String ttsState = "loading";
    private String pendingSpeech = null;
    private String pendingLanguage = "it-IT";
    private double pendingRate = 0.92;
    private int utteranceNumber = 0;
    private static final String HOST = "appassets.androidplatform.net";
    private static final String START = "https://" + HOST + "/assets/index.html";

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(8,18,31));
        getWindow().setNavigationBarColor(Color.rgb(8,18,31));
        voice = new TextToSpeech(this, status -> runOnUiThread(() -> {
            if (status != TextToSpeech.SUCCESS || voice == null) {
                ttsState = "engine_missing";
                notifyTts("error", "Il motore vocale Android non e disponibile. Apri le impostazioni della sintesi vocale.");
                return;
            }
            voice.setAudioAttributes(new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributes.USAGE_MEDIA).build());
            voice.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override public void onStart(String id) { notifyTts("start", id); }
                @Override public void onDone(String id) { notifyTts("done", id); }
                @Override public void onError(String id) { notifyTts("error", "Sintesi vocale non riuscita. Controlla la lingua installata."); }
                @Override public void onError(String id, int errorCode) { onError(id); }
            });
            int italian = voice.isLanguageAvailable(Locale.ITALY);
            ttsState = (italian == TextToSpeech.LANG_MISSING_DATA || italian == TextToSpeech.LANG_NOT_SUPPORTED)
                    ? "language_missing" : "ready";
            ttsReady = true;
            notifyTts(ttsState, ttsState.equals("ready") ? "Voce italiana pronta" : "Scarica la voce italiana nelle impostazioni Android");
            if (pendingSpeech != null) {
                String message = pendingSpeech;
                pendingSpeech = null;
                speakInternal(message, pendingRate, pendingLanguage);
            }
        }));
        FrameLayout holder = new FrameLayout(this);
        web = new WebView(this);
        holder.addView(web, new FrameLayout.LayoutParams(-1,-1));
        splash = new ImageView(this);
        splash.setImageResource(com.rqx.patenteb.R.drawable.rqx_splash);
        splash.setScaleType(ImageView.ScaleType.FIT_CENTER);
        splash.setBackgroundColor(Color.rgb(8,18,31));
        holder.addView(splash, new FrameLayout.LayoutParams(-1,-1));
        setContentView(holder);
        web.setBackgroundColor(Color.rgb(8,18,31));
        web.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true); // trusted bundled application code only
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(false);
        s.setAllowContentAccess(false);
        s.setJavaScriptCanOpenWindowsAutomatically(false);
        s.setSupportMultipleWindows(false);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        s.setMediaPlaybackRequiresUserGesture(false);
        web.addJavascriptInterface(new NativeSpeech(), "RQXNative");
        WebViewAssetLoader loader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();
        web.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView view, String url) {
                if (START.equals(url) && splash != null) {
                    ImageView current = splash;
                    splash = null;
                    current.animate().alpha(0f).setDuration(450).withEndAction(() -> {
                        if (current.getParent() instanceof FrameLayout) ((FrameLayout)current.getParent()).removeView(current);
                    }).start();
                }
            }
            @Override public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return loader.shouldInterceptRequest(request.getUrl());
            }
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri u = request.getUrl();
                if ("https".equals(u.getScheme()) && HOST.equals(u.getHost())) return false;
                if (request.isForMainFrame()) {
                    try { startActivity(new Intent(Intent.ACTION_VIEW,u)); } catch (Exception ignored) {}
                    return true;
                }
                return false;
            }
        });
        web.loadUrl(START);
    }

    private void notifyTts(String type, String message) {
        runOnUiThread(() -> {
            if (web == null) return;
            String detail = "{type:" + JSONObject.quote(type) + ",message:" + JSONObject.quote(message) + "}";
            String script = "(function(){window.dispatchEvent(new CustomEvent('rqx-native-tts',{detail:"+detail+"}));"
                    + "try{var f=document.getElementById('rqx-city-iframe');"
                    + "if(f&&f.contentWindow)f.contentWindow.dispatchEvent(new CustomEvent('rqx-native-tts',{detail:"+detail+"}));"
                    + "}catch(e){} })();";
            web.evaluateJavascript(script, null);
        });
    }

    private void speakInternal(String text, double speed, String language) {
        if (text == null || text.trim().isEmpty()) return;
        if (!ttsReady || voice == null) {
            pendingSpeech = text;
            pendingRate = speed;
            pendingLanguage = language;
            notifyTts("loading", "Preparazione voce Android...");
            return;
        }
        Locale locale = language != null && language.toLowerCase(Locale.ROOT).startsWith("bn")
                ? new Locale("bn", "BD") : Locale.ITALY;
        int available = voice.isLanguageAvailable(locale);
        if (available == TextToSpeech.LANG_MISSING_DATA || available == TextToSpeech.LANG_NOT_SUPPORTED) {
            ttsState = "language_missing";
            notifyTts("language_missing", locale.getLanguage().equals("bn")
                    ? "Voce bengalese non installata. Attiva o scarica la voce nelle impostazioni Android."
                    : "Voce italiana non installata. Attiva o scarica la voce nelle impostazioni Android.");
            return;
        }
        voice.setLanguage(locale);
        voice.setSpeechRate((float)Math.max(0.5, Math.min(1.7, speed)));
        int result = voice.speak(text.substring(0, Math.min(text.length(), 3800)),
                TextToSpeech.QUEUE_FLUSH, null, "rqx_" + (++utteranceNumber));
        if (result == TextToSpeech.ERROR) notifyTts("error", "Impossibile riprodurre l'audio. Controlla il volume multimediale.");
    }

    private final class NativeSpeech {
        @JavascriptInterface public void speak(String text, double speed, String language) {
            if (text == null || text.isEmpty()) return;
            runOnUiThread(() -> speakInternal(text, speed, language));
        }
        @JavascriptInterface public void stop() {
            runOnUiThread(() -> { pendingSpeech = null; if (voice != null) voice.stop(); });
        }
        @JavascriptInterface public String status() { return ttsState; }
        @JavascriptInterface public void openSettings() {
            runOnUiThread(() -> {
                try { startActivity(new Intent("com.android.settings.TTS_SETTINGS")); }
                catch (Exception ex) { startActivity(new Intent(Settings.ACTION_SETTINGS)); }
            });
        }
        @JavascriptInterface public void test() {
            runOnUiThread(() -> speakInternal("Benvenuto in RQX Patente B. La voce italiana funziona correttamente.", 0.92, "it-IT"));
        }
    }

    @Override public void onBackPressed() {
        if (web == null) { super.onBackPressed(); return; }
        web.evaluateJavascript("(function(){var m=document.getElementById('rqx-city-modal');if(m&&!m.hidden){var b=document.getElementById('rqx-city-close');if(b)b.click();return true;}return false;})()", value -> {
            if ("true".equals(value)) return;
            if (web.canGoBack()) web.goBack();
            else new AlertDialog.Builder(this).setTitle("RQX Patente B")
                    .setMessage("Vuoi uscire dall'app?")
                    .setNegativeButton("Annulla", null)
                    .setPositiveButton("Esci", (d,which) -> finish()).show();
        });
    }
    @Override protected void onDestroy() {
        if (voice != null) { voice.stop(); voice.shutdown(); voice = null; }
        if (web != null) { web.removeJavascriptInterface("RQXNative"); web.destroy(); web = null; }
        super.onDestroy();
    }
}
