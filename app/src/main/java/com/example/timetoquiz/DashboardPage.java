package com.example.timetoquiz;

import static com.example.timetoquiz.MainActivity.subjectList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridView;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DashboardPage extends AppCompatActivity {

    private GridView DashboardGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar Toolbar = findViewById(R.id.Toolbar);
        setSupportActionBar(Toolbar);
        getSupportActionBar().setTitle("Dashboard");

        //toolbar go back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DashboardGrid = findViewById(R.id.DashboardGridView);

//        //Demo Use
//        List<String> QuizList = new ArrayList<>();
//
//        QuizList.add("Subject 1");
//        QuizList.add("Subject 2");
//        QuizList.add("Subject 3");
//        QuizList.add("Subject 4");
//        QuizList.add("Subject 5");
//        QuizList.add("Subject 6");

        DashboardGridAdapter dashboardGridAdapter = new DashboardGridAdapter(subjectList);
        DashboardGrid.setAdapter(dashboardGridAdapter);
    }

    //if go back is clicked go back to previous page
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            DashboardPage.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}