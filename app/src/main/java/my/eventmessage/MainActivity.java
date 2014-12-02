package my.eventmessage;

import android.app.Activity;
import android.app.Application;
import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener,
        AsyncResponse{

    private DiscussArrayAdapter adapter;
    private ListView lv;
    private EditText editText1;
    private LocationRequest mLocationRequest;
    private LocationClient mLocationClient;
    public boolean nearEvent = false;

    // Keep track of the last task that sends HTTP request
    AsyncTask currentTask;

    // Variables for location API
    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 3;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView) findViewById(R.id.listView1);
        adapter = new DiscussArrayAdapter(getApplicationContext(), R.layout.listitem);
        lv.setAdapter(adapter);

        editText1 = (EditText) findViewById(R.id.editText1);

        // Set listener to check if user wants to send the message
        SendMessageListener sl = new SendMessageListener();
        sl.adapter = adapter;
        sl.editText = editText1;
        sl.ma = this;
        editText1.setOnKeyListener(sl);
        // Dummy messages for debugging the layout
        adapter.add(new Message(false, "potato"));
        adapter.add(new Message(true, "tomato"));
        for (int i = 0; i < 20; i++) {
            adapter.add(new Message(true, "test"));
        }
        // Check if play services are enabled
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode == ConnectionResult.SUCCESS){
            // Set location update parameters
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
            mLocationClient = new LocationClient(this, this, this);
        } else {
            Log.e("PlayServices", "GooglePlayServices are not available.");
            System.exit(0);
        }
//        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//        nameValuePairs.add(new BasicNameValuePair("event", "Tomato"));
//        nameValuePairs.add(new BasicNameValuePair("lat", "666"));
//        nameValuePairs.add(new BasicNameValuePair("lon", "999"));
//        nameValuePairs.add(new BasicNameValuePair("dist", "42"));
//        AsyncTask at = new AsyncHttpPost().execute("http://CORAL-LIGHTNING-739.APPSPOT.COM", nameValuePairs);
    }

    @Override
    protected void onStart(){
        super.onStart();
        mLocationClient.connect();
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

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        mLocationClient.requestLocationUpdates(mLocationRequest, this);
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.e("ConnectionError", "ConnectionFailed! :(");
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        // Report to the UI that the location was updated
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // Send the updated location to the server
        //TODO: test
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("lat", ""+location.getLatitude()));
        nameValuePairs.add(new BasicNameValuePair("lon", ""+location.getLongitude()));
        AsyncHttpPost post = new AsyncHttpPost();
        post.delegate = this;
        post.execute("http://coral-lightning-739.appspot.com/check", nameValuePairs);
    }

    // Receive the response from the server to see if there's an event nearby
    @Override
    public void processFinish(String output) {
        if (output.contains("Event found!")) {
            nearEvent = true;
        } else {
            nearEvent = false;
        }
    }
}
