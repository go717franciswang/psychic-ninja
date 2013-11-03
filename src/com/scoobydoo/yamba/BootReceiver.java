package com.scoobydoo.yamba;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	static final String TAG = "BootReceiver";

	@Override
	public void onReceive(Context context, Intent arg1) {
		context.startService(new Intent(context, UpdaterService.class));
		Log.d(TAG, "onReceived");
	}

}
