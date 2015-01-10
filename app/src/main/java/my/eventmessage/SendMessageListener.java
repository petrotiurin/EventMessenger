package my.eventmessage;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
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
                    if (true) {
                        String inputText = editText.getText().toString();
                        // Send the message to the server
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpResponse response;
                        String responseString = null;
                        try {
                            HttpPost post = new HttpPost("http://CORAL-LIGHTNING-739.APPSPOT.COM/message");
                            // Add your data
                            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                            nameValuePairs.add(new BasicNameValuePair("message", inputText));
                            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                            response = httpclient.execute(post);
                            StatusLine statusLine = response.getStatusLine();
                            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                                ByteArrayOutputStream out = new ByteArrayOutputStream();
                                response.getEntity().writeTo(out);
                                out.close();
                                responseString = out.toString();
                                Log.d("HTTPLOG", responseString);
                            } else{
                                //Closes the connection.
                                response.getEntity().getContent().close();
                                Log.d("status",""+ statusLine.getStatusCode());
                                throw new IOException(statusLine.getReasonPhrase());
                            }
                        } catch (ClientProtocolException e) {
                            e.printStackTrace();
                            //TODO Handle problems.. or meh...
                        } catch (IOException e) {
                            e.printStackTrace();
                            //TODO Handle problems..
                        }
                        return responseString;
                    } else {
//                        Toast.makeText(ma, "Message delivery failed. Away from any event.",
//                                Toast.LENGTH_SHORT).show();
                        Log.d("DEBUG", "Message delivery failed. Away from any event.");
                        return "";
                    }
                }

                @Override
                protected void onPostExecute(Object text) {
//                    if (!((String)text).isEmpty()) {
                        // Add the text to the list view
//                        adapter.add(new Message(false, (String)text));
                        editText.setText("");
//                    }
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
