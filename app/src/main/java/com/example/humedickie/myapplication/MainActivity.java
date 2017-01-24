//this code was taken from https://examples.javacodegeeks.com/android/core/os/handler/android-timer-example/
package com.example.humedickie.myapplication;

//for tomato icon
// <div>Icons made by <a href="http://www.flaticon.com/authors/roundicons" title="Roundicons">Roundicons</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private final String TAG = "MyApplication";

    private Button startButton;

    private Button pauseButton;

    private Button resetButton; //I added this

    private TextView timerValue;

    private long startTime = 0L;

    private Handler customHandler = new Handler();

    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;

    //I added the members below
    private MediaPlayer mp;
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

        if (Integer.toString(secs).length() == 1) { //pad a zero if single digit second
            timerValue.setText(mins + ":0" + secs);
        } else {
            timerValue.setText(mins + ":" + secs);
        }
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
                Log.v(TAG,"tomato finished");
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
                Log.v(TAG,"short break finished");
                state = State.tomato;
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
                Log.v(TAG,"long break finished");
                state = State.tomato;
                customHandler.post(tomatoThread);
            }
        }.start();
    }

    private Runnable tomatoThread = new Runnable() {
        public void run() {
            Log.v(TAG,"tomatoThread()::run()");
            switch(state){
                default:
                    break;
                case tomato:
                    mp = MediaPlayer.create(MainActivity.this,R.raw.resume);
                    try{
                        mp.setDataSource(Environment.getExternalStorageDirectory().getPath() + "/resume.mp3");
                        mp.prepare();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                    Log.v(TAG,"playing resume sound");
                    mp.start();

                    startTomato();
                    break;
                case shortBreak:
                    mp = MediaPlayer.create(MainActivity.this,R.raw.shortbreak);
                    try{
                        mp.setDataSource(Environment.getExternalStorageDirectory().getPath() + "/shortbreak.mp3");
                        mp.prepare();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                    Log.v(TAG,"playing shortbreak sound");
                    mp.start();

                    startShortBreak();
                    break;
                case longBreak:
                    mp = MediaPlayer.create(MainActivity.this,R.raw.longbreak);
                    try{
                        mp.setDataSource(Environment.getExternalStorageDirectory().getPath() + "/longbreak.mp3");
                        mp.prepare();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                    Log.v(TAG,"playing longbreak sound");
                    mp.start();

                    startLongBreak();
                    break;
            }
        }
    };
}
