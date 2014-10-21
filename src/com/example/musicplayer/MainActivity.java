package com.example.musicplayer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

public class MainActivity extends Activity {

	private BroadcastReceiver broadcastReceiver;
	private ServiceConnection serviceConnection;
	private boolean connectedWithService;
	private MusicPlayerService musicService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// TODO init

		startService(new Intent(this, MusicPlayerService.class));
		serviceConnection = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
				connectedWithService = false;
				musicService = null;
			}

			@Override
			public void onServiceConnected(ComponentName name,
					IBinder musicBinder) {
				musicService = ((MusicPlayerService.MusicBinder) musicBinder)
						.getMusicPlayerService();
				connectedWithService = true;
				updateUI();
			}
		};

		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				updateUI();
			}
		};
		LocalBroadcastManager.getInstance(this).registerReceiver(
				broadcastReceiver, new IntentFilter(ACTION_PLAYER));

	}

	protected void updateUI() {
		int musicStatus = 0;
		if (connectedWithService) {
			musicStatus = musicService.getPlayingStatus();
		} else {
			musicStatus = MusicPlayerService.STATUS_IDLE;
		}
		switch (musicStatus) {
		case MusicPlayerService.STATUS_IDLE:

			break;
		case MusicPlayerService.STATUS_PLAYING:

			break;
		case MusicPlayerService.STATUS_PAUSED:

			break;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		bindService(new Intent(this, MusicPlayerService.class),
				serviceConnection, 0);
		if (connectedWithService) {
			//musicService.onRecieveCommand();
		}
	}
}
