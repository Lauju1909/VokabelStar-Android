package de.lauri.vokabelstar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import com.getcapacitor.BridgeActivity;
import org.json.JSONObject;

public class MainActivity extends BridgeActivity {
    private static final String TAG = "VokabelStarSync";

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

        IntentFilter filter = new IntentFilter("de.lauri.vokabel.SYNC");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(syncReceiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(syncReceiver, filter);
        }

        if (bridge != null && bridge.getWebView() != null) {
            bridge.getWebView().addJavascriptInterface(new Object() {
                @JavascriptInterface
                public void requestSync() {
                    Log.d(TAG, "requestSync called from JS");
                    syncFromVocabHub();
                }
            }, "AndroidSyncBridge");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        syncFromVocabHub();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(syncReceiver);
        } catch (Exception ignored) {}
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
