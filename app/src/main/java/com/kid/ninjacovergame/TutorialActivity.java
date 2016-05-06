package com.kid.ninjacovergame;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Looper;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class TutorialActivity extends AppCompatActivity implements SensorEventListener {


    private SensorManager mSensorManager;
    private Sensor mSensorAcceleration;
    private Sensor mSensorGravity;
    private SoundPool mSoundPool;
    private Vibrator vibrator;

    private TextView tv_content;
    private ImageView iv_picture;
    private Button btn_test;
    private Button btn_back;
    private RadioGroup rg_selected;

    private String radio_status = "";
    private String LOG = "GameTutorialLog";

    private boolean directionJudge = false;

    private int CutDirection = 0;// 1 is left, 2 is middle, 3 is right, 0 is static
    private float MinCutAcceleration = 18;//最小挥刀加速度，s/M²

    //在X和Z轴的绝对值最大加速度
    private Float MaxX = 0.f;
    private Float MaxZ = 0.f;
    //重力在Z轴上的量
    float angleZ = 0.f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        tv_content = (TextView) findViewById(R.id.tv_content);
        iv_picture = (ImageView) findViewById(R.id.image_view);
        btn_test = (Button) findViewById(R.id.button_test);
        btn_back = (Button) findViewById(R.id.button_back);
        rg_selected = (RadioGroup) findViewById(R.id.radio_group);

        //设置为屏幕一半高度
        iv_picture.setMaxHeight(getWindowManager().getDefaultDisplay().getHeight() / 2);

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


        rg_selected.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_left) {
                    tv_content.setText(getResources().getText(R.string.cut_left_tutorial));
                    iv_picture.setImageResource(R.drawable.left);
                    radio_status = "left";
                } else if (checkedId == R.id.radio_center) {
                    tv_content.setText(getResources().getText(R.string.cut_center_tutorial));
                    iv_picture.setImageResource(R.drawable.forward);
                    radio_status = "center";
                } else if (checkedId == R.id.radio_right) {
                    tv_content.setText(getResources().getText(R.string.cut_right_tutorial));
                    iv_picture.setImageResource(R.drawable.right);
                    radio_status = "right";
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(TutorialActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TutorialActivity.this, getResources().getText(R.string.please_move),Toast.LENGTH_LONG).show();

                CutDirection = 0;
                MaxX = 0f;
                MaxZ = 0f;
                directionJudge = true;
                new Thread(new ThreadShow()).start();
            }
        });
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
        if (angleZ > 7) {
            if (Math.abs(x) > MinCutAcceleration) {
                CutDirection = 1;
            } else {
                CutDirection = 0;
            }
        } else if (angleZ < -7) {
            if (Math.abs(x) > MinCutAcceleration) {
                CutDirection = 3;
            } else {
                CutDirection = 0;
            }
        } else if (angleZ > -5 && angleZ < 5) {
            if (Math.abs(z) > MinCutAcceleration) {
                CutDirection = 2;
            } else {
                CutDirection = 0;
            }
        } else {
            CutDirection = 0;
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // 线程类
    class ThreadShow implements Runnable {
        @Override
        public void run() {
            try {
                Thread.currentThread().sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (directionJudge) {
                try {
                    switch (radio_status) {
                        case "left":

                            if (CutDirection == 1) {
                                /*想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到*/
                                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                long[] pattern = {100, 400};   // 停止 开启 停止 开启
                                vibrator.vibrate(pattern, -1);           //重复两次上面的pattern 如果只想震动一次，index设为-1

                                Looper.prepare();
                                Toast.makeText(TutorialActivity.this, getResources().getText(R.string.success), Toast.LENGTH_LONG).show();
                                Looper.loop();// 进入loop中的循环，查看消息队列

                            } else {
                                Looper.prepare();
                                Toast.makeText(TutorialActivity.this, getResources().getText(R.string.failed), Toast.LENGTH_LONG).show();
                                Looper.loop();// 进入loop中的循环，查看消息队列
                            }
                            CutDirection = 0;
                            MaxX = 0f;
                            MaxZ = 0f;
                            directionJudge = false;
                            break;
                        case "center":

                            if (CutDirection == 2) {
                                /*想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到*/
                                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                long[] pattern = {100, 400};   // 停止 开启 停止 开启
                                vibrator.vibrate(pattern, -1);           //重复两次上面的pattern 如果只想震动一次，index设为-1

                                Looper.prepare();
                                Toast.makeText(TutorialActivity.this, getResources().getText(R.string.success), Toast.LENGTH_LONG).show();
                                Looper.loop();// 进入loop中的循环，查看消息队列
                            } else {
                                Looper.prepare();
                                Toast.makeText(TutorialActivity.this, getResources().getText(R.string.failed), Toast.LENGTH_LONG).show();
                                Looper.loop();// 进入loop中的循环，查看消息队列
                            }
                            CutDirection = 0;
                            MaxX = 0f;
                            MaxZ = 0f;
                            directionJudge = false;
                            break;
                        case "right":

                            if (CutDirection == 3) {
                                /*想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到*/
                                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                long[] pattern = {100, 400};   // 停止 开启 停止 开启
                                vibrator.vibrate(pattern, -1);           //重复两次上面的pattern 如果只想震动一次，index设为-1

                                Looper.prepare();
                                Toast.makeText(TutorialActivity.this, getResources().getText(R.string.success), Toast.LENGTH_LONG).show();
                                Looper.loop();// 进入loop中的循环，查看消息队列
                            } else {
                                Looper.prepare();
                                Toast.makeText(TutorialActivity.this, getResources().getText(R.string.failed), Toast.LENGTH_LONG).show();
                                Looper.loop();// 进入loop中的循环，查看消息队列
                            }
                            CutDirection = 0;
                            MaxX = 0f;
                            MaxZ = 0f;
                            directionJudge = false;
                            break;
                        default:
                            directionJudge = false;
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("thread error...");
                }
                Thread.currentThread().interrupt();
                // Pause for 4 seconds
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    // We've been interrupted: no more messages.
                    return;
                }

            }

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.setClass(TutorialActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        mSensorManager.unregisterListener(this);
    }
}
