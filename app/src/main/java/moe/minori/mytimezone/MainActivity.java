package moe.minori.mytimezone;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;
import java.util.TimeZone;


public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    TextView digitalClockView;

    TextView currentLongitudeText;
    TextView currentTimezoneText;
    TextView statusText;

    double longitude = 0;

    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        digitalClockView = (TextView) findViewById(R.id.digitalClock);

        currentLongitudeText = (TextView) findViewById(R.id.currentLongitudeText);
        currentTimezoneText = (TextView) findViewById(R.id.currentTimezoneText);
        statusText = (TextView) findViewById(R.id.statusText);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateDisplay();
            }
        }, 100);

        googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();

        statusText.setText("Requesting location...");

    }

    @Override
    protected void onStart() {
        super.onStart();

        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        googleApiClient.disconnect();
    }

    public void updateDisplay() {
        // get current time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

        calendar.add(Calendar.SECOND, (int) Calculator.getTimeZoneInSecond(longitude));

        StringBuilder builder = new StringBuilder();

        builder.append(spacer(calendar.get(Calendar.HOUR_OF_DAY)));
        builder.append(":");
        builder.append(spacer(calendar.get(Calendar.MINUTE)));
        builder.append(":");
        builder.append(spacer(calendar.get(Calendar.SECOND)));

        digitalClockView.setText(builder.toString());

        currentLongitudeText.setText(longitude + "");
        currentTimezoneText.setText("GMT " + Calculator.getTimeZoneInSecond(longitude) + "s");

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateDisplay();
            }
        }, 100);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(500);

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                statusText.setText("Got location from system");
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private String spacer(int num) {
        if (num < 10) // 1 digit
            return "0" + num;
        else
            return num + "";
    }
}
