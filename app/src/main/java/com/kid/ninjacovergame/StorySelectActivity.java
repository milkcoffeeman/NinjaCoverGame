package com.kid.ninjacovergame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StorySelectActivity extends AppCompatActivity {

    private Button btn_story1;
    private Button btn_story2;
    private Button btn_story3;
    private Button btn_story4;
    private Button btn_story5;
    private Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_select);

        btn_story1 = (Button) findViewById(R.id.button1);
        btn_story2 = (Button) findViewById(R.id.button2);
        btn_story3 = (Button) findViewById(R.id.button3);
        btn_story4 = (Button) findViewById(R.id.button4);
        btn_story5 = (Button) findViewById(R.id.button5);
        btn_back = (Button) findViewById(R.id.button_back);

        btn_story1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int level = 1;
                Intent intent = new Intent();
                intent.putExtra("level", level);
                intent.setClass(StorySelectActivity.this, GameActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_story2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int level = 2;
                Intent intent = new Intent();
                intent.putExtra("level", level);
                intent.setClass(StorySelectActivity.this, GameActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_story3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int level = 3;
                Intent intent = new Intent();
                intent.putExtra("level", level);
                intent.setClass(StorySelectActivity.this, GameActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_story4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int level = 4;
                Intent intent = new Intent();
                intent.putExtra("level", level);
                intent.setClass(StorySelectActivity.this, GameActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_story5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int level = 5;
                Intent intent = new Intent();
                intent.putExtra("level", level);
                intent.setClass(StorySelectActivity.this, GameActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(StorySelectActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.setClass(StorySelectActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
