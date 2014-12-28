package my.eventmessage;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pettyurin on 07/11/2014.
 */
public class SendMessageListener implements View.OnKeyListener {
    public DiscussArrayAdapter adapter;
    public EditText editText;
    public AsyncTask currentTask;
    public MainActivity ma;
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        // If the event is a key-down event on the "enter" button
        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {
            new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] params) {
                    // Send message only if we're near some event
                    if (ma.nearEvent) {
                        String inputText = editText.getText().toString();
                        // Send the message to the server
                        if (GCM_send(inputText)){
                            Log.d("DEBUG", "Message sent");
                            return inputText;
                        } else {
                            return "";
                        }
                    } else {
//                        Toast.makeText(ma, "Message delivery failed. Away from any event.",
//                                Toast.LENGTH_SHORT).show();
                        Log.d("DEBUG", "Message delivery failed. Away from any event.");
                        return "";
                    }
                }

                @Override
                protected void onPostExecute(Object text) {
                    if (!((String)text).isEmpty()) {
                        // Add the text to the list view
                        adapter.add(new Message(false, (String)text));
                        editText.setText("");
                    }
                }

            }.execute(null, null, null);

            return true;
        }
        return false;
    }
    private boolean GCM_send(String msg){
        try {
            Bundle data = new Bundle();
            data.putString("my_message", msg);
            data.putString("my_action",
                    "com.google.android.gcm.demo.app.ECHO_NOW");
            String id = Integer.toString(ma.msgId.incrementAndGet());
            ma.gcm.send(ma.SENDER_ID + "@gcm.googleapis.com", id, data);
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            Toast.makeText(ma, "IOException on send: " + ex.getMessage(),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
