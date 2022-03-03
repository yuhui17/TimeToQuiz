package com.example.timetoquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScorePage extends AppCompatActivity {

    private TextView textView_Score;
    private Button button_Completed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_page);

        textView_Score = findViewById(R.id.textView_Score);
        button_Completed = findViewById(R.id.button_Completed);

        String score_str = getIntent().getStringExtra("SCORE");
        textView_Score.setText(score_str);

        button_Completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScorePage.this, DashboardPage.class);
                ScorePage.this.startActivity(intent);
                ScorePage.this.finish();
            }
        });
    }
}