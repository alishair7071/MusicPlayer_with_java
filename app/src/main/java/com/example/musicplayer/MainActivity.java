package com.example.musicplayer;

import static androidx.core.content.ContextCompat.startActivity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {



    RecyclerView recyclerView;
    ArrayList<Uri> audioFiles = new ArrayList<>();
    private static final int REQUEST_CODE = 100;
    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));


        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        }else {

            if (audioFiles.size()>0) {

            }
            else {
                fetchAudioFiles();
            }


            if (audioFiles.size()>0){

                MediaAdapter adapter = new MediaAdapter(MainActivity.this,audioFiles);
                recyclerView.setAdapter(adapter);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (audioFiles.size()>0) {

                }
                else {
                    fetchAudioFiles();
                }

                if (audioFiles.size()>0){

                    MediaAdapter adapter = new MediaAdapter(MainActivity.this,audioFiles);
                    recyclerView.setAdapter(adapter);
                }
            } else {
                Toast.makeText(MainActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public void fetchAudioFiles() {


        ContentResolver contentResolver = MainActivity.this.getContentResolver();
        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }

        // Define the projection (the columns to retrieve)
        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATA // For Android 10 and lower to get the file path
        };

        // Define the selection (which rows to retrieve)
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        // Query the external storage for audio files
        try (Cursor cursor = contentResolver.query(collection, projection, selection, null, null)) {
            if (cursor != null) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                int displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA); // For file path

                while (cursor.moveToNext()) {
                    long id = cursor.getLong(idColumn);
                    String displayName = cursor.getString(displayNameColumn);
                    String filePath = cursor.getString(dataColumn); // Get file path (only available on Android 9 and below)

                    Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

                    // If you're interested in just URIs, add to your list
                    audioFiles.add(contentUri); // Store Uri

                    // If you want the file path as well, use filePath (on Android 9 and below)
                    Log.d("AudioFile", "Loaded: " + displayName + " at " + filePath);

                    count++; // Increase count
                }
                Toast.makeText(MainActivity.this, count + " audio files loaded", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("FetchAudioFiles", "Error: " + e.toString());
            Toast.makeText(MainActivity.this, "Failed to load audio files: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }




}