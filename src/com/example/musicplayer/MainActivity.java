package com.example.musicplayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	protected static final String ACTION_PLAYER = "player";
	private ServiceConnection serviceConnection;
	private boolean connectedWithService;
	private MusicPlayerService musicService;
	private Button button;
	private TextView statusView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		button = (Button) findViewById(R.id.serviceButton);
		button.setOnClickListener(this);
		statusView = (TextView) findViewById(R.id.statusView);

		startService(new Intent(this, MusicPlayerService.class));

		initServiceConnection();

	}

	@Override
	protected void onStart() {
		super.onStart();
		bindService(new Intent(this, MusicPlayerService.class),
				serviceConnection, 0);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (!connectedWithService)
			return;
		unbindService(serviceConnection);
		connectedWithService = false;
	}

	protected void updateUI() {
		int currentStatus = 0;
		if (connectedWithService) {
			currentStatus = musicService.getPlayingStatus();
		} else {
			currentStatus = MusicPlayerService.STATUS_IDLE;
		}
		switch (currentStatus) {
		case MusicPlayerService.STATUS_IDLE:
			updateUIIdle();
			break;
		case MusicPlayerService.STATUS_PLAYING:
			updateUIPlaying();
			break;
		case MusicPlayerService.STATUS_PAUSED:
			updateUIPaused();
			break;
		}

	}

	private void initServiceConnection() {
		serviceConnection = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
				connectedWithService = false;
				musicService = null;
				updateUI();
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
	}

	private void updateUIIdle() {
		button.setText(R.string.play);
		statusView.setText(R.string.idle);
	}

	private void updateUIPlaying() {
		button.setText(R.string.pause);
		statusView.setText(R.string.playing);
	}

	private void updateUIPaused() {
		button.setText(R.string.play);
		statusView.setText(R.string.paused);
	}

	@Override
	public void onClick(View v) {
		if (!connectedWithService)
			initServiceConnection();
		if (connectedWithService) {
			musicService.onRecieveCommand();
			updateUI();
		}
	}

}
