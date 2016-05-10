package com.kid.ninjacovergame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button btn_start;
    private Button btn_tutorial;
    private Button btn_exit;
    private Button btn_story;
    private Button btn_select;
    private Button btn_advertisement;
    private TextView tv_level;
    private ProgressBar pb_level;

    private SensorManager mSensorManager;

    private String SP_LEVEL = "level";
    private String SP_KILLED = "killed";
    private String SP_NEED_KILL = "need_kill";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_start = (Button) findViewById(R.id.button_start);
        btn_tutorial = (Button) findViewById(R.id.button_tutorial);
        btn_exit = (Button) findViewById(R.id.button_exit);
        btn_story = (Button) findViewById(R.id.button_story);
        btn_select = (Button) findViewById(R.id.btn_select);
        btn_advertisement = (Button) findViewById(R.id.btn_advertisement);
        tv_level = (TextView) findViewById(R.id.tv_level);
        pb_level = (ProgressBar) findViewById(R.id.progressBar);

        //检查sensor
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // Use the accelerometer.
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) == null || mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) == null){
            Toast.makeText(MainActivity.this, getResources().getText(R.string.no_sensor),Toast.LENGTH_LONG).show();
        }

        //显示最高分数
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final int level = sp.getInt(SP_LEVEL, 1);
        int killed = sp.getInt(SP_KILLED, 0);
        int need_kill = sp.getInt(SP_NEED_KILL, 30);
        tv_level.setText("LV " + level);
        pb_level.setProgress((int) (killed * 100 / need_kill));
        //pb_level.setProgress(70);
        Log.e("killed", killed + "   " + need_kill);

        if(level > 5){
            btn_select.setVisibility(View.VISIBLE);
        }
        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, StorySelectActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("level", level);
                intent.setClass(MainActivity.this, GameActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, StoryActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, TutorialActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_advertisement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, AdvertiseActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }
}
