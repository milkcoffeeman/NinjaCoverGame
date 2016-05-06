package com.kid.ninjacovergame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StoryActivity extends AppCompatActivity {

    private TextView tv_story;
    private Button btn_back;

    private String SP_LEVEL = "level";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        tv_story = (TextView) findViewById(R.id.tv_story);
        btn_back = (Button) findViewById(R.id.btn_back);

        //设置为可滚动
        tv_story.setMovementMethod(ScrollingMovementMethod.getInstance());

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int level = sp.getInt(SP_LEVEL, 1);

        String story = getResources().getString(R.string.story);
        String story_part[] = story.split("\\.");
        if (level <= story_part.length) {
            story="";
            for (int i = 0; i < level; i++) {
                story += story_part[i];
            }
            story += "\n...";
        } else {
            story += "\n";
            story += getResources().getString(R.string.thanks);
        }
        tv_story.setText(story);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(StoryActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.setClass(StoryActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
