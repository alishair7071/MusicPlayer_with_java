package com.example.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service {

    private MediaPlayer mediaPlayer;
    private String currentSongPath;
    private ArrayList<String> mediaListString = new ArrayList<>();
    int currentSongIndex;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        currentSongPath = intent.getStringExtra("CURRENT_SONG_PATH");
        currentSongIndex= intent.getIntExtra("CURRENT_INDEX",0);
        mediaListString= intent.getStringArrayListExtra("KEY_SONG_LIST");
        playSong(currentSongPath);

        return START_STICKY;
    }

    private void playSong(String currentSongPath) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        } else {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }

        try {
            Uri songUri = Uri.parse(currentSongPath);
            mediaPlayer.setDataSource(getApplicationContext(), songUri);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(this, "Playing audio", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error playing audio", Toast.LENGTH_SHORT).show();
        }


        mediaPlayer.setOnCompletionListener(mp -> {
            currentSongIndex++;
            if (currentSongIndex < mediaListString.size()) {
                playSong(mediaListString.get(currentSongIndex)); // Play the next song
            } else {
                //stopSelf();// Stop the service if there are no more songs
                currentSongIndex=0;
                playSong(mediaListString.get(currentSongIndex));
            }
        });

    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}
