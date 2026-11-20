package com.rqx.patenteb;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.view.Gravity;
import androidx.webkit.WebViewAssetLoader;
import java.util.Locale;

public class MainActivity extends Activity {
 private static final String HOST="appassets.androidplatform.net";
 private static final String HOME="https://"+HOST+"/assets/index.html";
 private WebView web;
 private TextToSpeech speaker;
 private FrameLayout holder;
 private TextView splash;
 @Override public void onCreate(Bundle state) {
  super.onCreate(state);
  getWindow().setStatusBarColor(Color.rgb(8,18,31));
  getWindow().setNavigationBarColor(Color.rgb(8,18,31));
  holder=new FrameLayout(this);
  web=new WebView(this);
  holder.addView(web,new FrameLayout.LayoutParams(-1,-1));
  splash=new TextView(this);
  splash.setText("♛\nRQX\nPATENTE B");
  splash.setTextColor(Color.rgb(234,195,130));
  splash.setTextSize(36);
  splash.setGravity(Gravity.CENTER);
  splash.setBackgroundColor(Color.rgb(8,18,31));
  holder.addView(splash,new FrameLayout.LayoutParams(-1,-1));
  setContentView(holder);
  WebSettings s=web.getSettings();
  s.setJavaScriptEnabled(true);
  s.setDomStorageEnabled(true);
  s.setAllowFileAccess(false);
  s.setAllowContentAccess(false);
  s.setJavaScriptCanOpenWindowsAutomatically(false);
  s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
  s.setMediaPlaybackRequiresUserGesture(false);
  web.setBackgroundColor(Color.rgb(8,18,31));
  web.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
  speaker=new TextToSpeech(this,status->{ if(status==TextToSpeech.SUCCESS && speaker!=null) speaker.setLanguage(Locale.ITALY); });
  web.addJavascriptInterface(new NativeSpeech(),"RQXNative");
  WebViewAssetLoader loader=new WebViewAssetLoader.Builder().addPathHandler("/assets/",new WebViewAssetLoader.AssetsPathHandler(this)).build();
  web.setWebViewClient(new WebViewClient(){
   @Override public WebResourceResponse shouldInterceptRequest(WebView v,WebResourceRequest r){return loader.shouldInterceptRequest(r.getUrl());}
   @Override public void onPageFinished(WebView v,String url){if(HOME.equals(url)&&splash!=null){TextView view=splash;splash=null;view.animate().alpha(0).setDuration(350).withEndAction(()->holder.removeView(view)).start();}}
   @Override public boolean shouldOverrideUrlLoading(WebView v,WebResourceRequest r){
    Uri u=r.getUrl();
    if("https".equals(u.getScheme())&&HOST.equals(u.getHost()))return false;
    if(r.isForMainFrame()){try{startActivity(new Intent(Intent.ACTION_VIEW,u));}catch(Exception ignored){}return true;}
    return false;
   }
  });
  web.loadUrl(HOME);
 }
 private final class NativeSpeech {
  @JavascriptInterface public void speak(String text,double speed,String language){
   if(text==null||text.isEmpty())return;
   String msg=text.substring(0,Math.min(text.length(),1800));
   runOnUiThread(()->{if(speaker!=null){speaker.stop();speaker.setLanguage(language!=null&&language.startsWith("bn")?new Locale("bn","BD"):Locale.ITALY);speaker.setSpeechRate((float)Math.max(.5,Math.min(1.7,speed)));speaker.speak(msg,TextToSpeech.QUEUE_FLUSH,null,"rqx");}});
  }
  @JavascriptInterface public void stop(){runOnUiThread(()->{if(speaker!=null)speaker.stop();});}
 }
 @Override public void onBackPressed(){
  if(web==null){super.onBackPressed();return;}
  web.evaluateJavascript("(function(){var m=document.getElementById('rqx-city-modal');if(m&&!m.hidden){document.getElementById('rqx-city-close')?.click();return true;}return false;})()",x->{
   if("true".equals(x))return;
   if(web.canGoBack())web.goBack();
   else new AlertDialog.Builder(this).setTitle("RQX Patente B").setMessage("Vuoi uscire dall'app?").setNegativeButton("Annulla",null).setPositiveButton("Esci",(d,w)->finish()).show();
  });
 }
 @Override public void onDestroy(){if(speaker!=null){speaker.stop();speaker.shutdown();speaker=null;}if(web!=null){web.removeJavascriptInterface("RQXNative");web.destroy();web=null;}super.onDestroy();}
}