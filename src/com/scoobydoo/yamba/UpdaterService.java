package com.scoobydoo.yamba;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends IntentService {
	private static final String TAG = "UpdaterService";
	
	public static final String NEW_STATUS_INTENT = "com.scoobydoo.yamba.NEW_STATUS";
	public static final String NEW_STATUS_EXTRA_COUNT = "NEW_STATUS_EXTRA_COUNT";
	public static final String RECEIVE_TIMELINE_NOTIFICATIONS = "com.scoobydoo.yamba.RECEIVE_TIMELINE_NOTIFICATIONS";
	
	public UpdaterService() {
		super(TAG);
		
		Log.d(TAG, "UpdaterService constructed");
	}
	
	@Override
	protected void onHandleIntent(Intent inIntent) {
		Intent intent;
		Log.d(TAG, "onHandleIntent'ing");
		YambaApplication yamba = (YambaApplication) getApplication();
		int newUpdates = yamba.fetchStatusUpdates();
		if (newUpdates > 0) {
			Log.d(TAG, "We have a new status");
			intent = new Intent(NEW_STATUS_INTENT);
			intent.putExtra(NEW_STATUS_EXTRA_COUNT, newUpdates);
			sendBroadcast(intent, RECEIVE_TIMELINE_NOTIFICATIONS);
		}
	}
}
