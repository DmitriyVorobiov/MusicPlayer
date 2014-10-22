package com.example.musicplayer;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;

public class MusicPlayerService extends Service implements OnCompletionListener {

	private static MediaPlayer mediaPlayer;
	private MusicBinder binder;
	private int playingStatus;
	protected final static int STATUS_IDLE = 0;
	protected final static int STATUS_PLAYING = 1;
	protected final static int STATUS_PAUSED = 2;

	class MusicBinder extends Binder {
		MusicPlayerService getMusicPlayerService() {
			return MusicPlayerService.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		playingStatus = STATUS_IDLE;
		stopSelf();
		releaseMediaPlayer();
		stopForeground(true);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		binder = new MusicBinder();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		releaseMediaPlayer();
		mediaPlayer = null;
	}

	private void releaseMediaPlayer() {
		if (mediaPlayer != null) {
			if (mediaPlayer.isPlaying())
				mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	protected void onRecieveCommand() {
		switch (playingStatus) {
		case STATUS_IDLE:
			releaseMediaPlayer();
			mediaPlayer = MediaPlayer.create(this, R.raw.audio);
			mediaPlayer.setOnCompletionListener(this);
			mediaPlayer.start();
			playingStatus = STATUS_PLAYING;

			break;
		case STATUS_PLAYING:
			mediaPlayer.pause();
			playingStatus = STATUS_PAUSED;

			break;
		case STATUS_PAUSED:
			mediaPlayer.start();
			playingStatus = STATUS_PLAYING;
			break;
		}
		if (playingStatus == STATUS_PLAYING) {
			startForeground(1, new Notification());
		} else {
			stopForeground(true);
		}
	}

	protected int getPlayingStatus() {
		return playingStatus;
	}
}
