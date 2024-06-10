package com.example.financehelp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    ImageView logo;
    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        text = findViewById(R.id.txt);
        logo=findViewById(R.id.logo);
        Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);

        // Apply the animation to the layout
        logo.startAnimation(fadeIn);
        text.startAnimation(fadeIn);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start your main activity here
                Intent intent = new Intent(SplashActivity.this, Login.class);
                startActivity(intent);
                finish(); // Prevent going back to splash screen
            }
        }, 6000);
    }
}