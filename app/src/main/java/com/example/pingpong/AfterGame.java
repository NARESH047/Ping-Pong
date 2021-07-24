package com.example.pingpong;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AfterGame extends AppCompatActivity {

    TextView currentScore;
    TextView highestScore;
    SharedPreferences sharedPreferences;
    ImageView highest_image;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.after_game);
        currentScore = findViewById(R.id.current_score);
        highestScore = findViewById(R.id.highest_score);
        sharedPreferences = getSharedPreferences("preferences", 0);
        int score = sharedPreferences.getInt("currentScore", 0);
        currentScore.setText(String.valueOf(score));
        int highest = sharedPreferences.getInt("highest", 0);
        if(score > highest){
            highest = score;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("highest", highest);
            editor.commit();
        }
        highestScore.setText(String.valueOf(highest));
    }

    public void restart(View view) {
        Intent newGame = new Intent(AfterGame.this, com.example.pingpong.MainActivity.class);
        startActivity(newGame);
        finish();
    }

    public void exit(View view) {
        finish();
    }

}
