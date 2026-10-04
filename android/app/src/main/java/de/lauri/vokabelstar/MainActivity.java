package de.lauri.vokabelstar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.webkit.JavascriptInterface;
import com.getcapacitor.BridgeActivity;
import org.json.JSONObject;
import java.util.Locale;

public class MainActivity extends BridgeActivity implements TextToSpeech.OnInitListener {
    private static final String TAG = "VokabelStarSync";
    private TextToSpeech tts;
    private boolean ttsReady = false;

    private final BroadcastReceiver syncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Sync broadcast received!");
            syncFromVocabHub();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            tts = new TextToSpeech(this, this);
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize TextToSpeech", e);
        }

        IntentFilter filter = new IntentFilter("de.lauri.vokabel.SYNC");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(syncReceiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(syncReceiver, filter);
        }

        registerJsBridge();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            ttsReady = true;
            if (tts != null) {
                tts.setLanguage(Locale.US);
            }
            Log.d(TAG, "TextToSpeech initialized successfully");
        } else {
            Log.e(TAG, "TextToSpeech init failed: " + status);
        }
    }

    private void registerJsBridge() {
        if (bridge != null && bridge.getWebView() != null) {
            bridge.getWebView().addJavascriptInterface(new Object() {
                @JavascriptInterface
                public void requestSync() {
                    Log.d(TAG, "requestSync called from JS");
                    syncFromVocabHub();
                }

                @JavascriptInterface
                public void speak(String text, String lang) {
                    if (tts == null || !ttsReady || text == null || text.trim().isEmpty()) {
                        return;
                    }
                    try {
                        Locale loc = Locale.US;
                        if (lang != null) {
                            String l = lang.toLowerCase();
                            if (l.startsWith("de")) loc = Locale.GERMAN;
                            else if (l.contains("gb") || l.contains("uk")) loc = Locale.UK;
                            else if (l.startsWith("es")) loc = new Locale("es", "ES");
                            else if (l.startsWith("fr")) loc = Locale.FRENCH;
                        }
                        tts.setLanguage(loc);
                        tts.setSpeechRate(1.0f);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts_" + System.currentTimeMillis());
                        } else {
                            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "speak error", e);
                    }
                }

                @JavascriptInterface
                public void stopSpeech() {
                    if (tts != null) {
                        tts.stop();
                    }
                }
            }, "AndroidSyncBridge");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerJsBridge();
        syncFromVocabHub();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(syncReceiver);
        } catch (Exception ignored) {}
        if (tts != null) {
            try {
                tts.stop();
                tts.shutdown();
            } catch (Exception ignored) {}
        }
    }

    public void syncFromVocabHub() {
        try {
            Uri uri = Uri.parse("content://de.lauri.bfwenglisch.vocabprovider");
            Bundle res = getContentResolver().call(uri, "getInstalledTopics", null, null);
            if (res != null && res.containsKey("topics_json")) {
                String json = res.getString("topics_json");
                Log.d(TAG, "Received topics_json length: " + (json != null ? json.length() : 0));
                if (json != null && !json.isEmpty() && !json.equals("[]") && bridge != null && bridge.getWebView() != null) {
                    bridge.getWebView().post(() -> {
                        bridge.getWebView().evaluateJavascript(
                            "if (window.onExternalTopicsSynced) { window.onExternalTopicsSynced(" + JSONObject.quote(json) + "); }",
                            null
                        );
                    });
                }
            } else {
                Log.w(TAG, "ContentProvider call returned null or missing topics_json");
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to query VocabContentProvider", e);
        }
    }
}
