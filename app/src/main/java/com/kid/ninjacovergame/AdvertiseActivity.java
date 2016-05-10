package com.kid.ninjacovergame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.purplebrain.adbuddiz.sdk.AdBuddiz;

public class AdvertiseActivity extends AppCompatActivity {

    private Button btn_showAd;
    private Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertise);

        btn_back = (Button) findViewById(R.id.btn_back);
        btn_showAd = (Button) findViewById(R.id.btn_showAd);

        AdBuddiz.setPublisherKey("a5357be7-0a9f-40a8-8cbe-fdccee9dd0b7");

        AdBuddiz.RewardedVideo.fetch(this); // this = current Activity

        btn_showAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdBuddiz.RewardedVideo.show(AdvertiseActivity.this); // this = current Activity
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(AdvertiseActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent();
        intent.setClass(AdvertiseActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
