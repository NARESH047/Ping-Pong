package com.example.pingpong;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.RequiresApi;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.VIBRATOR_SERVICE;

public class DuringGame extends View {
    Context Context;
    float xCordBall, yCordBall, xCordBat, yCordBat;
    int xVel;
    int yVel;
    Paint textPaint, healthBarPaint, objPaint, borderPaint;
    float xTouchBefMove, xBatBefMove;
    int score = 0;
    int life = 5;
    Bitmap ballOrg, batOrg, bat, ball;
    int dWidth, dHeight;
    MediaPlayer HIT, MISS, WALL, WIN, END;
    Random random;
    SharedPreferences sharedPreferences;
    int gameType;
    Boolean audioState;
    Timer timer = new Timer();

    public DuringGame(Context context) {
        super(context);
        Context = context;
        textPaint = new Paint();
        objPaint = new Paint();
        healthBarPaint = new Paint();
        borderPaint = new Paint();
        ballOrg = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        batOrg = BitmapFactory.decodeResource(getResources(), R.drawable.bat);

        HIT = MediaPlayer.create(context, R.raw.hit);
        MISS = MediaPlayer.create(context, R.raw.miss);
        WALL = MediaPlayer.create(context, R.raw.wall);
        WIN = MediaPlayer.create(context, R.raw.win);
        END = MediaPlayer.create(context, R.raw.game_end);

        objPaint.setAntiAlias(true);
        objPaint.setFilterBitmap(true);
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(120f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.STROKE);
        healthBarPaint.setColor(Color.GREEN);
        borderPaint.setStrokeWidth(10);
        borderPaint.setColor(Color.parseColor("#FFD770"));
        borderPaint.setStyle(Paint.Style.STROKE);

        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dWidth = size.x;
        dHeight = size.y;
        ball = Bitmap.createScaledBitmap(ballOrg, dWidth/16, dWidth/16, true);
        bat = Bitmap.createScaledBitmap(batOrg, dWidth/4, dHeight/32, true);
        random = new Random();
        xCordBall = random.nextInt(dWidth);
        yCordBall = dHeight/7;
        xCordBat = dWidth / 2 - bat.getWidth() /2;
        yCordBat = (dHeight) * 5/ 6;
        sharedPreferences = context.getSharedPreferences("preferences", 0);
        gameType = sharedPreferences.getInt("gameType", 0);
        audioState = sharedPreferences.getBoolean("audioState", true);
        if(gameType == 0) {
            xVel = dWidth/80;
            yVel = dHeight/200;
        }
        else if(gameType == 1) {
            xVel = dWidth/60;
            yVel = dHeight/150;
        }
        else {
            xVel = dWidth/30;
            yVel = dHeight/75;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        canvas.drawRect(0,dHeight/7 + 2,dWidth, dHeight,borderPaint);
        xCordBall = xCordBall + xVel;
        yCordBall = yCordBall + yVel;

        if(( xCordBall <= 0 || xCordBall >= dWidth - ball.getWidth())){
            xVel = -xVel;
            if(WALL != null && audioState){
                WALL.start();
            }
        }
        if(yCordBall <= dHeight/7){
            yVel = -yVel;
            if(WALL != null && audioState){
                WALL.start();
            }
        }
        if(yCordBall > yCordBat + bat.getHeight()) {
            xCordBall = 1 + random.nextInt(dWidth - ball.getWidth() - 1);
            yCordBall = dHeight / 7;
            if (MISS != null && audioState) {
                MISS.start();
            }
            life--;
            Vibrator w = (Vibrator) Context.getSystemService(VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                w.vibrate(VibrationEffect.createOneShot(800, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                w.vibrate(800);
            }
            if (life < 3 && life > 0) {
                canvas.drawColor(Color.RED, BlendMode.SRC_OVER);
                timer.schedule(new TimerTask() {
                    @RequiresApi(api = Build.VERSION_CODES.Q)
                    @Override
                    public void run() {
                        canvas.drawColor(Color.BLACK);
                    }
                    ;}, 800);
            }
            if (life == 0) {
                if (END != null && audioState) {
                    END.start();
                }
                xCordBall = dWidth/2;
                yCordBall = dHeight/2;
                xVel = yVel = 0;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("currentScore", score);
                editor.commit();
                int highest = sharedPreferences.getInt("highest", 0);
                if(score > highest){
                    highest = score;
                    editor.putInt("highest", highest);
                    editor.commit();
                }
                Intent intent = new Intent(Context, AfterGame.class);
                Context.startActivity(intent);
                ((Activity) Context).finish();
            }
        }
        if(((xCordBall+ball.getWidth()) >= xCordBat)
        && (xCordBall <= xCordBat + bat.getWidth())
        && (yCordBall + ball.getHeight() >= yCordBat)
        ){
            if((score % 4 !=0 || score == 0 ) && HIT != null && audioState){
                HIT.start();
            }
            if(score %4 ==0 && score!=0 && HIT!= null && audioState){
                WIN.start();
            }
            if(score>2) {
                textPaint.setColor(Color.GREEN);
            }
            yCordBall = yCordBat - ball.getHeight();
            xVel++;
            yVel = -(yVel+1);
            score++;
        }
        canvas.drawBitmap(ball, xCordBall, yCordBall, objPaint);
        canvas.drawBitmap(bat, xCordBat, yCordBat, objPaint);
        canvas.drawText("Score: "+score, dWidth/2, 180f, textPaint);
        if(life == 2){
            healthBarPaint.setColor(Color.YELLOW);
        }else if(life == 1){
            healthBarPaint.setColor(Color.RED);
        }
        canvas.drawRect(0, 200f,life*(dWidth/5), dHeight/7, healthBarPaint);
        invalidate();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float xTouched = event.getX();
        float yTouched = event.getY();
        if(yTouched >= yCordBat){
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                xTouchBefMove = event.getX();
                xBatBefMove = xCordBat;
            }
            if(event.getAction() == MotionEvent.ACTION_MOVE){
                float xBatAftMove = xBatBefMove + xTouched - xTouchBefMove;
                if(xBatAftMove <= 0){
                    xCordBat = 0;}
                else if(xBatAftMove >= dWidth - bat.getWidth()){
                    xCordBat = dWidth - bat.getWidth();}
                else{
                    xCordBat = xBatAftMove;}
            }
        }
        return true;
    }



}
