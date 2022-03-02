package com.example.timetoquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginPage extends AppCompatActivity {

    //variable
    Button button_Login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        //hook
        button_Login = findViewById(R.id.button_Login);

        //Login button
        button_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPage.this, DashboardPage.class);
                startActivity(intent);
            }
        });
    }
}