package de.lauri.vokabelstar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import com.getcapacitor.BridgeActivity;
import org.json.JSONObject;

public class MainActivity extends BridgeActivity {
    private final BroadcastReceiver syncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
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
                if (json != null && !json.isEmpty() && bridge != null && bridge.getWebView() != null) {
                    bridge.getWebView().post(() -> {
                        bridge.getWebView().evaluateJavascript(
                            "if (window.onExternalTopicsSynced) window.onExternalTopicsSynced(" + JSONObject.quote(json) + ");",
                            null
                        );
                    });
                }
            }
        } catch (Exception ignored) {}
    }
}
