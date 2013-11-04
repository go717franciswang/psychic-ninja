package com.scoobydoo.yamba;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

public class TimelineActivity extends BaseActivity {
	StatusData statusData;
	Cursor cursor;
	ListView listTimeline;
	TimelineAdapter adapter;
	static final String[] FROM = { StatusData.C_CREATED_AT, StatusData.C_USER, 
		StatusData.C_TEXT
	};
	static final int[] TO = { R.id.textCreatedAt, R.id.textUser, R.id.textText };
	TimelineReceiver receiver;
	IntentFilter filter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timeline);
		
		listTimeline = (ListView) findViewById(R.id.listTimeline);
		
		statusData = new StatusData(this);
		
		if (PreferenceManager.getDefaultSharedPreferences(this).getString("username", null) == null) {
			startActivity(new Intent(this, PrefsActivity.class));
			Toast.makeText(this, R.string.msgSetupPrefs, Toast.LENGTH_LONG).show();
		}
		
		receiver = new TimelineReceiver();
		filter = new IntentFilter(UpdaterService.NEW_STATUS_INTENT);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		statusData.close();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		this.setupList();
		registerReceiver(receiver, filter);
	}
	
	@SuppressWarnings("deprecation")
	private void setupList() {
		cursor = statusData.getStatusUpdates();
		startManagingCursor(cursor);
		
		adapter = new TimelineAdapter(this, cursor);
		listTimeline.setAdapter(adapter);
	}
	
	class TimelineReceiver extends BroadcastReceiver {
		
		@SuppressWarnings("deprecation")
		@Override
		public void onReceive (Context context, Intent intent) {
			cursor.requery();
			adapter.notifyDataSetChanged();
			Log.d("TimelineReceiver", "onReceived");
		}
	}
}
