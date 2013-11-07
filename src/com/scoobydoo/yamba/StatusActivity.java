package com.scoobydoo.yamba;

import java.security.Provider;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StatusActivity extends BaseActivity implements OnClickListener, TextWatcher, LocationListener {
	private static final String TAG = "StatusActivity";
	private static final long LOCATION_MIN_TIME = 3600000;
	private static final float LOCATION_MIN_DISTANCE = 1000;
	EditText editText;
	Button updateButton;
	TextView textCount;
	SharedPreferences prefs;
	LocationManager locationManager;
	Location location;
	String provider;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status);
        
        editText = (EditText) findViewById(R.id.editText);
        updateButton = (Button) findViewById(R.id.buttonUpdate);
        updateButton.setOnClickListener(this);
        
        textCount = (TextView) findViewById(R.id.textCount);
        textCount.setText(Integer.toString(140));
        textCount.setTextColor(Color.GREEN);
        editText.addTextChangedListener(this);
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		
		provider = ((YambaApplication) getApplication()).getProvider();
		if (!YambaApplication.LOCATION_PROVIDER_NONE.equals(provider)) {
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		}
		
		if (locationManager != null) {
			location = locationManager.getLastKnownLocation(provider);
			locationManager.requestLocationUpdates(provider, LOCATION_MIN_TIME, LOCATION_MIN_DISTANCE, this);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		locationManager.removeUpdates(this);
	}
    
    class PostToTwitter extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... statuses) {
			try {
				if (location != null) {
					double latlong[] = {location.getLatitude(), location.getLongitude()};
					((YambaApplication) getApplication()).getTwitter().setMyLocation(latlong);
				}
				
				Twitter.Status status = ((YambaApplication) getApplication())
						.getTwitter().updateStatus(statuses[0]);
				return status.text;
			} catch (TwitterException e) {
				Log.e(TAG, e.toString());
				e.printStackTrace();
				return "Failed to post";
			}
		}
		
		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG).show();
		}
    	
    }
    
    public void onClick(View v) {
    	String status = editText.getText().toString();
    	new PostToTwitter().execute(status);
    	Log.d(TAG, "onClicked");
    }

	@Override
	public void afterTextChanged(Editable statusText) {
		int count = 140 - statusText.length();
		textCount.setText(Integer.toString(count));

		textCount.setTextColor(Color.GREEN);
		
		if (count < 10) {
			textCount.setTextColor(Color.YELLOW);
		}
		
		if (count < 0) {
			textCount.setTextColor(Color.RED);
		}
	}


	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}
	
	public void onLocationChanged(Location location) {
		this.location = location;
	}
	
	public void onProviderDisabled(String provider) {
		if (this.provider.equals(provider)) {
			locationManager.removeUpdates(this);
		}
	}
	
	public void onProviderEnabled(String provider) {
		if (this.provider.equals(provider)) {
			locationManager.requestLocationUpdates(this.provider, LOCATION_MIN_TIME, LOCATION_MIN_DISTANCE, this);
		}
	}
	
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
}
