package com.example.timetoquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridView;
import androidx.appcompat.widget.Toolbar;

public class QuizListPage extends AppCompatActivity {

    private GridView quizlist_grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list_page);

        Toolbar quizlist_toolbar =  findViewById(R.id.quizlist_toolbar);
        setSupportActionBar(quizlist_toolbar);

        String title = getIntent().getStringExtra("SUBJECT");
        getSupportActionBar().setTitle(title);

        //toolbar go back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        quizlist_grid = findViewById(R.id.quizlist_gridview);

        QuizListAdapter quizListAdapter =  new QuizListAdapter(6);

        quizlist_grid.setAdapter(quizListAdapter);
    }

    //if go back is clicked go back to previous page
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            QuizListPage.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}