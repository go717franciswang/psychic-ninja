package com.scoobydoo.yamba;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service {
	public static final String NEW_STATUS_INTENT = "com.scoobydoo.yamb.NEW_STATUS";
	public static final String NEW_STATUS_EXTRA_COUNT = "NEW_STATUS_EXTRA_COUNT";
	static final String TAG = "UpdaterService";
	
	static final int DELAY = 60000;
	private boolean runFlag = false;
	private Updater updater;
	private YambaApplication yamba;

	@Override
	public void onCreate() {
		super.onCreate();
		this.yamba = (YambaApplication) getApplication();
		this.updater = new Updater();
		
		Log.d(TAG, "onCreated");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		this.runFlag = false;
		this.updater.interrupt();
		this.updater = null;
		this.yamba.setServiceRuning(false);
		
		Log.d(TAG, "onDestroyed");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		
		if (! this.runFlag) {
			this.runFlag = true;
			this.updater.start();
			this.yamba.setServiceRuning(true);			
		}
				
		Log.d(TAG, "onStarted");
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	private class Updater extends Thread {
		static final String RECEIVE_TIMELINE_NOTIFICATIONS = 
				"com.scoobydoo.yamba.RECEIVE_TIMELINE_NOTIFICATIONS";
		
		public Updater() {
			super("UpdateService-Updater");
		}

		@Override
		public void run() {
			UpdaterService updaterService = UpdaterService.this;
			while (updaterService.runFlag) {
				Log.d(TAG, "Running background thread");
				try {
					int newUpdates = ((YambaApplication) getApplication()).fetchStatusUpdates();
					if (newUpdates > 0) {
						Log.d(TAG, "We have a new status");
						
						Intent intent = new Intent(NEW_STATUS_INTENT);
						intent.putExtra(NEW_STATUS_EXTRA_COUNT, newUpdates);
						
						// make sure receiver has our permission
						updaterService.sendBroadcast(intent, RECEIVE_TIMELINE_NOTIFICATIONS);
					}
					
					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
					updaterService.runFlag = false;
				}
			}
		}
	}
}
