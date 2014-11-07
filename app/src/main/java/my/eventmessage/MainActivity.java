package my.eventmessage;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private DiscussArrayAdapter adapter;
    private ListView lv;
    private EditText editText1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView) findViewById(R.id.listView1);
        adapter = new DiscussArrayAdapter(getApplicationContext(), R.layout.listitem);
        lv.setAdapter(adapter);

        editText1 = (EditText) findViewById(R.id.editText1);
        editText1.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    adapter.add(new Message(false, editText1.getText().toString()));
                    editText1.setText("");
                    return true;
                }
                return false;
            }
        });

        adapter.add(new Message(false, "potato"));
        adapter.add(new Message(true, "tomato"));
        for (int i = 0; i < 20; i++) {
            adapter.add(new Message(true, "test"));
        }

//        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//        nameValuePairs.add(new BasicNameValuePair("event", "Tomato"));
//        nameValuePairs.add(new BasicNameValuePair("lat", "666"));
//        nameValuePairs.add(new BasicNameValuePair("lon", "999"));
//        nameValuePairs.add(new BasicNameValuePair("dist", "42"));
//        AsyncTask at = new AsyncHttpPost().execute("http://CORAL-LIGHTNING-739.APPSPOT.COM", nameValuePairs);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
