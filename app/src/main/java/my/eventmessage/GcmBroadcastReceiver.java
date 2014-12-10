package my.eventmessage;

/**
 * Created by pettyurin on 02/12/2014.
 */

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmBroadcastReceiver extends BroadcastReceiver {

    public DiscussArrayAdapter adapter;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        String messageType = gcm.getMessageType(intent);
        adapter.add(new Message(true, extras.getString("message")));
        Log.d("TYPE", messageType);
        if (!extras.isEmpty() && !messageType.equals("send_event")) {
            for (int i = 0; i < 5; i++) {
                Log.i("Receive", "Working... " + (i + 1)
                        + "/5 @ " + SystemClock.elapsedRealtime());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
            }
            Log.d("RECEIVED", extras.getString("r"));
        }
    }
}
