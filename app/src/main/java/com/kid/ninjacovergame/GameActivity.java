package com.kid.ninjacovergame;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import java.util.Random;

public class GameActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensorAcceleration;
    private Sensor mSensorGravity;
    private SoundPool mSoundPool;
    MediaPlayer bgmPlayer;
    //设置手机震动的参数
    private Vibrator vibrator;
    //音效加载的ID
    int leftSoundId, centerSoundId, rightSoundId;

    private String LOG = "GameActivityLog";
    private String SENSOR_LOG = "SensorLog";

    private double MaxNextEnemyTime = 5.0;// the next enemy will appear no more than 5 seconds, set 0-5 seconds
    private int CutDirection = 0;// 1 is left, 2 is middle, 3 is right, 0 is static
    private double MaxReflectionTime = 2.0;//最大反应时间，秒
    private int GamePoints = 0;
    private float MinCutAcceleration = 18;//最小挥刀加速度，s/M²
    private double MinNextEnemyTime = 1.0;

    //在X和Z轴的绝对值最大加速度
    private Float MaxX = 0.f;
    private Float MaxZ = 0.f;
    //重力在Z轴上的量
    float angleZ = 0.f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_game);

        mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 5);

        Intent intent = getIntent();
        int level = intent.getIntExtra("level", 1);

        switch (level){
            case 1:
                leftSoundId = mSoundPool.load(GameActivity.this, R.raw.stage1_left, 1);
                centerSoundId = mSoundPool.load(GameActivity.this, R.raw.stage1_center, 1);
                rightSoundId = mSoundPool.load(GameActivity.this, R.raw.stage1_right, 1);

                bgmPlayer = MediaPlayer.create(GameActivity.this, R.raw.stage1);
                bgmPlayer.setLooping(true);
                bgmPlayer.start();
                break;
            case 2:
                leftSoundId = mSoundPool.load(GameActivity.this, R.raw.stage2_left, 1);
                centerSoundId = mSoundPool.load(GameActivity.this, R.raw.stage2_center, 1);
                rightSoundId = mSoundPool.load(GameActivity.this, R.raw.stage2_right, 1);

                bgmPlayer = MediaPlayer.create(GameActivity.this, R.raw.stage2);
                bgmPlayer.setLooping(true);
                bgmPlayer.start();
                break;
            case 3:
                leftSoundId = mSoundPool.load(GameActivity.this, R.raw.stage3_left, 1);
                centerSoundId = mSoundPool.load(GameActivity.this, R.raw.stage3_center, 1);
                rightSoundId = mSoundPool.load(GameActivity.this, R.raw.stage3_right, 1);

                bgmPlayer = MediaPlayer.create(GameActivity.this, R.raw.stage3);
                bgmPlayer.setLooping(true);
                bgmPlayer.start();
                break;
            case 4:
                leftSoundId = mSoundPool.load(GameActivity.this, R.raw.stage4_left, 1);
                centerSoundId = mSoundPool.load(GameActivity.this, R.raw.stage4_center, 1);
                rightSoundId = mSoundPool.load(GameActivity.this, R.raw.stage4_right, 1);

                bgmPlayer = MediaPlayer.create(GameActivity.this, R.raw.stage4);
                bgmPlayer.setLooping(true);
                bgmPlayer.start();
                break;
            default:
                leftSoundId = mSoundPool.load(GameActivity.this, R.raw.stage5_left, 1);
                centerSoundId = mSoundPool.load(GameActivity.this, R.raw.stage5_center, 1);
                rightSoundId = mSoundPool.load(GameActivity.this, R.raw.stage5_right, 1);

                bgmPlayer = MediaPlayer.create(GameActivity.this, R.raw.stage5);
                bgmPlayer.setLooping(true);
                bgmPlayer.start();
                break;
        }


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // Use the accelerometer.
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null) {

            mSensorAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            Log.e(LOG, "Get accelerometer sensor success.");

            mSensorManager.registerListener(this, mSensorAcceleration, mSensorManager.SENSOR_DELAY_FASTEST);

        } else {
            Log.e(LOG, "Sorry, there are no accelerometers on your device. You can't play this game.");
        }

        // Use the gyroscope sensor.
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {

            mSensorGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            Log.e(LOG, "Get gyroscope sensor success.");

            mSensorManager.registerListener(this, mSensorGravity, mSensorManager.SENSOR_DELAY_FASTEST);

        } else {
            Log.e(LOG, "Sorry, there are no gyroscope sensor on your device. You can't play this game.");
        }

        new Thread(new ThreadShow()).start();
    }

    // 线程类
    class ThreadShow implements Runnable {
        @Override
        public void run() {
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Random random = new Random(System.currentTimeMillis());
                    double nextEnemyTime;
                    int nextEnemyDirection;
                    //set nest enemy time is 0 - 5 seconds
                    nextEnemyTime = (random.nextDouble() * MaxNextEnemyTime);
                    nextEnemyDirection = random.nextInt(3) + 1;

                    //随着时间的推移，反应时间从两秒开始递减，知道1.5秒
                    if (MaxReflectionTime > 1) {
                        MaxReflectionTime -= 0.02;
                    }
                    //随着时间的推移，下一次敌人出现的最慢时间减少，直到不低于两秒
                    if (MaxNextEnemyTime > MinNextEnemyTime) {
                        MaxNextEnemyTime = MaxNextEnemyTime * 0.9;
                    }

                    Log.e(LOG, "nextEnemyTime: " + nextEnemyTime);
                    Thread.currentThread().sleep((int) (1000 * nextEnemyTime));

                    Log.e(LOG, "nextEnemyDirection: " + nextEnemyDirection);
                    if (nextEnemyDirection == 1) {
                        //left
                        mSoundPool.play(leftSoundId, 0.5f, 0.5f, 1, 0, 1);
                    } else if (nextEnemyDirection == 2) {
                        //center
                        mSoundPool.play(centerSoundId, 0.5f, 0.5f, 1, 0, 1);
                    } else {
                        //right
                        mSoundPool.play(rightSoundId, 0.5f, 0.5f, 1, 0, 1);
                    }
                    //after reflection time, we decide whether user choose the right direction
                    Thread.currentThread().sleep((int) (1000 * MaxReflectionTime));
                    if (CutDirection == nextEnemyDirection) {
                        GamePoints++;
                        /*想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到*/
                        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        long[] pattern = {100, 400};   // 停止 开启 停止 开启
                        vibrator.vibrate(pattern, -1);           //重复两次上面的pattern 如果只想震动一次，index设为-1

                        CutDirection = 0;
                        MaxX = 0.f;
                        MaxZ = 0.f;

                        Log.e(LOG, "gamePoints: " + GamePoints);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.putExtra("GamePoints", GamePoints);
                        intent.setClass(GameActivity.this, GameOverActivity.class);
                        bgmPlayer.stop();
                        startActivity(intent);

                        GameActivity.this.finish();
                        Thread.currentThread().interrupt();
                        // Pause for 4 seconds
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            // We've been interrupted: no more messages.
                            return;
                        }
                        System.exit(0);
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.out.println("thread error...");
                }
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor mySensor = event.sensor;

        //对于x和z轴方向的线性加速度
        float x = 0;
        float z = 0;

        if (mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

            x = Math.max(Math.abs(event.values[0]), Math.abs(MaxX)) == Math.abs(MaxX) ? MaxX : event.values[0];
            z = Math.max(Math.abs(event.values[2]), Math.abs(MaxZ)) == Math.abs(MaxZ) ? MaxZ : event.values[2];

            MaxX = x;
            MaxZ = z;
        } else if (mySensor.getType() == Sensor.TYPE_GRAVITY) {

            //values0,1,2分别代表重力方向与X,Y,Z轴的夹角
            //Z大于9表示正面朝上，Z小于-9表示背面朝上
            if (event.values[2] != 0) {
                angleZ = event.values[2];
            }
            //Log.e(SENSOR_LOG, "angleZ:" + angleZ);
        }
        //用方向传感器和线性加速度传感器共同判定
        //比如，向左挥为正面朝上，X加速度超过-18
        Log.e(SENSOR_LOG, "angleZ:" + angleZ + "  " + "x:" + x + "  " + "z:" + z);
        if (angleZ > 7) {
            if (Math.abs(x) > MinCutAcceleration) {
                CutDirection = 1;
                Log.e(LOG, "cut left");
            } else {
                CutDirection = 0;
                Log.e(LOG, "cut false");
            }
        } else if (angleZ < -7) {
            if (Math.abs(x) > MinCutAcceleration) {
                CutDirection = 3;
                Log.e(LOG, "cut right");
            } else {
                CutDirection = 0;
                Log.e(LOG, "cut false");
            }
        } else if (angleZ > -5 && angleZ < 5) {
            if (Math.abs(z) > MinCutAcceleration) {
                CutDirection = 2;
                Log.e(LOG, "cut center");
            } else {
                CutDirection = 0;
                Log.e(LOG, "cut false");
            }
        } else {
            CutDirection = 0;
            Log.e(LOG, "cut false");
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * set SCREEN_ORIENTATION_PORTRAIT
         */
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        bgmPlayer.start();
        mSensorManager.registerListener(this, mSensorAcceleration, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bgmPlayer.stop();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onBackPressed() {
        Thread.currentThread().interrupt();
        this.finish();
        System.exit(0);
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        mSoundPool.release();
        bgmPlayer.release();
    }
}
