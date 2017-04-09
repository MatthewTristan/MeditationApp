/*Created by Matthew Tristan as a first project in the Android Studio IDE
as a demonstration of proficiency in mobile application development
and object oriented programming.
The application is intended to be a simple application for meditation.
Intended functionality:
Select the intended Meditation Duration using a spinner which has possible
durations in minutes. Then press the Play Bell button. A bell will play and at this point
the user will be starting their practice of meditation. After the chosen time period,
the bell will play again, ending the user's meditation session.
1/11/17
V0.01
 */

package com.example.matt.meditationapp;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private Spinner spinner;
    private int duration;
    private Button playBell;
    private Bell bell;
    private ArrayAdapter<CharSequence> adapter;
    private Timer myTimer;
    private PowerManager manager;
    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner =  (Spinner) findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this,R.array.Durations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        manager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BellLock");
        playBell = (Button) this.findViewById(R.id.button2);
        bell = new Bell();
        myTimer = new Timer();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                duration = Integer.parseInt((String) parent.getItemAtPosition(position));
            }
            //if nothing is selected, standard session is 10 minutes.
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                duration = 10;
            }
        });
    }
    /*WakeLock is used in order for the TimerTask to not time out. The Wakelock is used conservatively.
   The timer is purged on each click, then a new task is scheduled for the particular button click.
   Essentially, each time the Play Bell button is clicked, a new meditation session has started.
   It is intended to only have one Timer Task at a time, and the WakeLock is only needed during that
   task.
    */
    public void buttonClick(View v) throws IOException {
        if (wakeLock.isHeld()) wakeLock.release();
        bell.playBell();
        wakeLock.acquire();
        myTimer.purge();
        myTimer.schedule(new TimerTask(){
            @Override
            public void run(){
                manager = (PowerManager) getSystemService(Context.POWER_SERVICE);
                bell.playBell();
                wakeLock.release();
            }
        },duration*60*1000);
    }
    /*Bell class is used to have the main function of a bell.
    Bell has a sound, that is the sound in the Media player.
    The bell also has a method in which it is played.
    */
    class Bell{
        private MediaPlayer bellSound;
        public Bell(){
            bellSound = MediaPlayer.create(getBaseContext(),R.raw.bell);
        }
        public void playBell() {
            if(bellSound.isPlaying()){
                bellSound.stop();
                try {
                    bellSound.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            bellSound.start();
        }
    }
}