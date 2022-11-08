package com.dalosa.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //VARS GLOBALES
    RecyclerView recyclerView;
    TextView tvSongTitle, tvCurrentPos, tvTotalDuration;
    ImageView btnPrev, btnPlay, btnNext;
    SeekBar sbPosition;

    ArrayList<ModelSong> songArrayList;
    private Context sondArrayList;

    //Inititialisation des composants graphiques
    private void  initiUI(){
        recyclerView = findViewById(R.id.recyclerView);
      tvSongTitle = findViewById(R.id.tvSongTitle);
        tvCurrentPos = findViewById(R.id.tvCurrentPos);
       tvTotalDuration = findViewById(R.id.tvTotalDuration);
       btnPrev = findViewById(R.id.btnPrev);
       btnPlay = findViewById(R.id.btnPlay);
       btnNext = findViewById(R.id.btnNext);
       sbPosition = findViewById(R.id.sbPosition);

        MediaPlayer mediaPlayer = new MediaPlayer();

       songArrayList = new ArrayList<>();

       //init du recycler
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }
    public static final int PERMISSION_READ = 0;
    //Méthode pour vérifier l'accès aux données situées sur l'espace de stockage externe
    public boolean checkPermission() {
        int READ_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if(READ_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_READ);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_READ:{
                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)){
                    if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                        Toast.makeText(getApplicationContext(),"Please allow storage permission",Toast.LENGTH_SHORT).show();
                    }else{
                    setSong();
                    }
                }
            }
        }
    }
    private void setSong(){
getAudioFiles();
    }

    public void getAudioFiles(){
        ContentResolver contentResolver = getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        if(cursor != null && cursor.moveToFirst()){
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String artist =  cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String duration =  cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                String url =  cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                Long albumId =  cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

                Uri coverFolder = Uri.parse("content://media/external/audio/albumart");
                Uri albumArtUri = ContentUris.withAppendedId(coverFolder, albumId);

                //Remplissage
                ModelSong modelSong = new ModelSong();
                modelSong.setSongTitle(title);
                modelSong.setSongArtist(artist);

                modelSong.setSongUri(Uri.parse(url));

                modelSong.setSongDuration(duration);
                modelSong.setSongCover(albumArtUri);

                songArrayList.add(modelSong);

            }while (cursor.moveToNext());
        }
        AdapterSong adapterSong = new AdapterSong(this, songArrayList);
        recyclerView.setAdapter(adapterSong);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initiUI();

        if(checkPermission()){
            setSong();
        }
    }
}