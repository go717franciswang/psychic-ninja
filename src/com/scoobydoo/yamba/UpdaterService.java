package com.scoobydoo.yamba;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service {
	static final String TAG = "UpdaterService";
	
	static final int DELAY = 60000;
	private boolean runFlag = false;
	private Updater updater;
	private YambaApplication yamba;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		this.yamba = (YambaApplication) getApplication();
		this.updater = new Updater();
		
		Log.d(TAG, "onCreated");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		this.runFlag = false;
		this.updater.interrupt();
		this.updater = null;
		this.yamba.setServiceRuning(false);
		
		Log.d(TAG, "onDestroyed");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		super.onStartCommand(intent, flags, startId);
		
		this.runFlag = true;
		this.updater.start();
		this.yamba.setServiceRuning(true);
		
		Log.d(TAG, "onStarted");
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private class Updater extends Thread {
		List<Twitter.Status> timeline;
		
		public Updater() {
			super("UpdateService-Updater");
		}

		@Override
		public void run() {
			UpdaterService updaterService = UpdaterService.this;
			while (updaterService.runFlag) {
				Log.d(TAG, "Updater running");
				try {
					// Get the timeline from the cloud
					try {
						timeline = yamba.getTwitter().getFriendsTimeline();
					} catch (TwitterException e) {
						Log.e(TAG, "Failed to connect to twitter service", e);
					}
					
					for (Twitter.Status status : timeline) {
						Log.d(TAG, String.format("%s: %s",  status.user.name, status.text));
					}
					
					Log.d(TAG, "Updater ran");
					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
					updaterService.runFlag = false;
				}
			}
		}
	}
}
