package my.eventmessage;

import android.os.AsyncTask;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

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
            // Send message only if we're near some event
            if (ma.nearEvent) {
                String inputText = editText.getText().toString();
                // Add the text to the list view
                adapter.add(new Message(false, inputText));
                editText.setText("");
                // Send the message to the server
                //TODO: test
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("message", "" + inputText));
                currentTask = new AsyncHttpPost().execute("http://YOUR.WEBSITE.HERE", nameValuePairs);
            } else {
                Toast.makeText(ma, "Message delivery failed. Away from any event.",
                               Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return false;
    }
}
