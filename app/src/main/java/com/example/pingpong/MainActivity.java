package com.example.pingpong;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    Boolean audioState;
    ImageButton audioImage;
    int gameType;
    MediaPlayer GAMEMUSIC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        audioImage = findViewById(R.id.audioImage);
        sharedPreferences = getSharedPreferences("preferences",0);
        gameType = sharedPreferences.getInt("gameType", 0);
        audioState = sharedPreferences.getBoolean("audioState", true);
        if(audioState){
            audioImage.setImageResource(R.drawable.audio_on);
            GAMEMUSIC = MediaPlayer.create(this, R.raw.arcade_music_loop);
            GAMEMUSIC.start();
            GAMEMUSIC.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    GAMEMUSIC.start();
                }
            });
        }else{
            if (GAMEMUSIC != null ) {
                GAMEMUSIC.release();}
            audioImage.setImageResource(R.drawable.audio_off);
        }
    }

    public void audioPref(View view) {
        if(audioState){
            audioState = false;
            audioImage.setImageResource(R.drawable.audio_off);
            if (GAMEMUSIC != null ) {
            GAMEMUSIC.release();}
        }else{
            audioState = true;
            audioImage.setImageResource(R.drawable.audio_on);
            GAMEMUSIC = MediaPlayer.create(this, R.raw.arcade_music_loop);
            GAMEMUSIC.start();
            GAMEMUSIC.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    GAMEMUSIC.start();
                }
            });
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("audioState", audioState);
        editor.commit();    }


    public void easyGame(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("gameType", 0);
        editor.commit();
        DuringGame duringGame = new DuringGame(this);
        setContentView(duringGame);
    }



    public void mediumGame(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("gameType", 1);
        editor.commit();
        DuringGame duringGame = new DuringGame(this);
        setContentView(duringGame);
    }

    public void hardGame(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("gameType", 2);
        editor.commit();
        DuringGame duringGame = new DuringGame(this);
        setContentView(duringGame);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (GAMEMUSIC != null ) {
            GAMEMUSIC.release();
            GAMEMUSIC = MediaPlayer.create(this, R.raw.arcade_music_loop);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (GAMEMUSIC != null ) {
            GAMEMUSIC.start();
        }
    }

    public void exit(View view) {
        finish();
    }

}