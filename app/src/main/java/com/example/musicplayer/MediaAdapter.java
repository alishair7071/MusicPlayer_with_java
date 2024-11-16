package com.example.musicplayer;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder>{



    private ArrayList<Uri> mediaListUri;
    private ArrayList<String> mediaListString;

    Context context;
    int count=0;

    MediaAdapter(Context context,ArrayList<Uri> mediaListUri){

        this.mediaListUri=mediaListUri;
        this.context=context;

        mediaListString=new ArrayList<>();


        for (int i=0;i < mediaListUri.size();i++){

            String songUriString = mediaListUri.get(i).toString();
            mediaListString.add(songUriString);

        }

    }

    @NonNull
    @Override
    public MediaAdapter.MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_media, parent, false);
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaAdapter.MediaViewHolder holder, int position) {


        Uri paths = mediaListUri.get(position);
        String audioFileName = getSongNameFromUri(paths);
        holder.audioTitle.setText(audioFileName);
        count++;

        Log.d("MediaAdapter", "Loading uri URI: " + paths);


        holder.itemView.setOnClickListener(v -> {

            try {
                if (paths!=null) {
                    Intent intent = new Intent(context, PlayerActivity.class);
                    intent.putExtra("KEY_STRING1", audioFileName);
                    intent.putExtra("CURRENT_INDEX",position);
                     intent.putStringArrayListExtra("KEY_SONG_LIST", mediaListString);
                    String currentSong = paths.toString();
                    intent.putExtra("CURRENT_SONG_PATH", currentSong);
                    context.startActivity(intent);
                }
            }catch (Exception e){

            }

            // Handle audio playback here


        });


    }

    @Override
    public int getItemCount() {
        return mediaListUri.size();
    }


    public class MediaViewHolder  extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView audioTitle;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            audioTitle=itemView.findViewById(R.id.audio_title);
        }
    }


    public String getSongNameFromUri(Uri uri) {
        String songName = null;
        // Projection: the columns you want to retrieve
        String[] projection = {MediaStore.Audio.Media.DISPLAY_NAME};

        // Query the content resolver with the provided Uri
        try (Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                // Retrieve the index of the DISPLAY_NAME column
                int nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                songName = cursor.getString(nameIndex); // Get the song name
            }
        } catch (Exception e) {
            Log.e("SongName", "Error fetching song name: " + e.toString());
        }

        return songName; // Return the song name
    }
}
