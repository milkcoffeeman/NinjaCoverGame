package com.kid.ninjacovergame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class GameOverActivity extends AppCompatActivity {

    private TextView tvGamePoints;
    private TextView tvGameHighScore;
    private Button btnBack;
    private TextView tv_level;
    private ProgressBar pb_level;

    private String LOG = "GameOverActivityLog";
    private String SP_HIGHSCORE = "high_score";

    private String SP_KILLED = "killed";
    private String SP_NEED_KILL = "need_kill";
    private String SP_LEVEL = "level";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        tvGamePoints = (TextView) findViewById(R.id.game_points);
        tvGameHighScore = (TextView) findViewById(R.id.game_high_score);
        btnBack = (Button) findViewById(R.id.button_back);
        tv_level = (TextView) findViewById(R.id.tv_level);
        pb_level = (ProgressBar) findViewById(R.id.progressBar);

        System.gc();
        //get floor ID
        final Intent intent = getIntent();
        //get floor Id from main activity
        int GamePoints = intent.getIntExtra("GamePoints", 0);
        Log.e(LOG, "GamePoints: " + GamePoints);

        //显示最高分数
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int oldHighScore = sp.getInt(SP_HIGHSCORE, 0); //default value will be null in this case, if there is no such key

        int level = sp.getInt(SP_LEVEL, 1);
        int killed = sp.getInt(SP_KILLED, 0);
        int need_kill = sp.getInt(SP_NEED_KILL, 30);
        if (GamePoints > 10 || level == 1) {
            killed += GamePoints;
        }
        if (killed > need_kill) {
            level++;
            killed = 0;
            need_kill += 20;
            if (level < 5) {
                Toast.makeText(getApplicationContext(), getResources().getText(R.string.story_unclock), Toast.LENGTH_LONG).show();
            }
        }
        Log.e("killed", killed + "");
        pb_level.setProgress((int) (killed * 100 / need_kill));
        tv_level.setText("LV " + level);
        SharedPreferences.Editor editor1 = sp.edit();
        editor1.putInt(SP_LEVEL, level);
        editor1.putInt(SP_KILLED, killed);
        editor1.putInt(SP_NEED_KILL, need_kill);
        editor1.apply();

        if (oldHighScore < GamePoints) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(SP_HIGHSCORE, GamePoints);
            editor.apply();
            tvGameHighScore.setText(getResources().getText(R.string.congraduationNewScore));
            tvGameHighScore.invalidate();
        } else {
            tvGameHighScore.setText(getResources().getText(R.string.highScore) + " " + oldHighScore);
            tvGameHighScore.invalidate();
        }

        tvGamePoints.setText(GamePoints + " " + (getResources().getText(R.string.enemyDie)));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.setClass(GameOverActivity.this, MainActivity.class);
                startActivity(intent1);
                GameOverActivity.this.finish();
//                System.exit(0);
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
        Intent intent1 = new Intent();
        intent1.setClass(GameOverActivity.this, MainActivity.class);
        startActivity(intent1);
        GameOverActivity.this.finish();
        super.onBackPressed();
    }
}
