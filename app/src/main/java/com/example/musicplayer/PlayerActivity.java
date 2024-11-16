package com.example.musicplayer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    TextView playingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        playingText = findViewById(R.id.palyingText);

        Intent intent = getIntent();
        String name = intent.getStringExtra("KEY_STRING1");
        String currentSongPath = intent.getStringExtra("CURRENT_SONG_PATH");
        int currentSongIndex= intent.getIntExtra("CURRENT_INDEX",0);
        ArrayList<String> mediaListString= intent.getStringArrayListExtra("KEY_SONG_LIST");
        playingText.setText(name);

        // Start the MusicService and pass the song URI
        Intent serviceIntent = new Intent(this, MusicService.class);

        serviceIntent.putExtra("CURRENT_SONG_PATH", currentSongPath);
        serviceIntent.putExtra("CURRENT_INDEX",currentSongIndex);
        serviceIntent.putStringArrayListExtra("KEY_SONG_LIST", mediaListString);
        startService(serviceIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Optional: Stop the service here if you want to stop playback when the activity is destroyed
        // stopService(new Intent(this, MusicService.class));
    }
}
