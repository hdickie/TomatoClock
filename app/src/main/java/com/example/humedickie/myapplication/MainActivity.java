//this code was taken from https://examples.javacodegeeks.com/android/core/os/handler/android-timer-example/
package com.example.humedickie.myapplication;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private Button startButton;

    private Button pauseButton;

    private Button resetButton; //I added this

    private TextView timerValue;

    private long startTime = 0L;

    private Handler customHandler = new Handler();

    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    //I added the members below
    private MediaPlayer mp = new MediaPlayer();
    private int tomatoLength = 15;
    private int shortBreakLength = 5;
    private int longBreakLength = 10;
    private int numTomatoes = 0;

    enum State{
        shortBreak,longBreak,tomato
    };

    State state = State.tomato;

    int sound = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        timerValue = (TextView) findViewById(R.id.timerValue);

        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(tomatoThread, 0);
            }
        });

        pauseButton = (Button) findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                timeSwapBuff += timeInMilliseconds;
                customHandler.removeCallbacks(tomatoThread);
            }
        });

        resetButton = (Button) findViewById(R.id.restartButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                timeSwapBuff += timeInMilliseconds;
                customHandler.removeCallbacks(tomatoThread);

                timeSwapBuff = 0;
                timeInMilliseconds = 0;
                timerValue.setText("0:00");
            }
        });
    }

    private void updateTimeDisplay(long millis){
        int mins = (int)(millis/1000)/60;
        int secs = (int)(millis/1000)%60;
        timerValue.setText(mins+":"+secs);
    }

    private void startTomato(){
        new CountDownTimer(tomatoLength*1000,1000){

            public void onTick(long millisUntilFinished){
                updateTimeDisplay(millisUntilFinished);
            }

            public void onFinish() {
                if (numTomatoes <= 3) {
                    state = State.shortBreak;
                    numTomatoes++;
                } else {
                    state = State.longBreak;
                    numTomatoes = 0;
                }
                customHandler.post(tomatoThread);
            }
        }.start();
    }

    private void startShortBreak(){
        new CountDownTimer(shortBreakLength*1000,1000){

            public void onTick(long millisUntilFinished){
                updateTimeDisplay(millisUntilFinished);
            }

            public void onFinish() {
                customHandler.post(tomatoThread);
            }
        }.start();
    }

    private void startLongBreak(){
        new CountDownTimer(longBreakLength*1000,1000){

            public void onTick(long millisUntilFinished){
                updateTimeDisplay(millisUntilFinished);
            }

            public void onFinish() {
                customHandler.post(tomatoThread);
            }
        }.start();
    }

    private Runnable tomatoThread = new Runnable() {
        public void run() {
            switch(state){
                default:
                    break;
                case tomato:
                    startTomato();
                    break;
                case shortBreak:
                    startShortBreak();
                    break;
                case longBreak:
                    startLongBreak();
                    break;
            }
        }
    };
}
