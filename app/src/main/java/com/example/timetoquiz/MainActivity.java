package com.example.timetoquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN = 3500;

    //variable
    Animation top_animation,bottom_animation;
    ImageView image_ttq;
    TextView text_WelcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //load animation
        top_animation = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottom_animation = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        //hook
        image_ttq = findViewById(R.id.image_ttq);
        text_WelcomeText = findViewById(R.id.text_WelcomeText);

        //set animation
        image_ttq.setAnimation(top_animation);
        text_WelcomeText.setAnimation(bottom_animation);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, LoginPage.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);
    }
}