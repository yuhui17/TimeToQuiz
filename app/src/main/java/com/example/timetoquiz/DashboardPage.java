package com.example.timetoquiz;

//import static com.example.timetoquiz.MainActivity.subjectList;

import static com.example.timetoquiz.LoginPage.subjectList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

        builder.setTitle("Logout Confirmation");
        builder.setMessage("Are you sure you want to logout???");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                //Do Logout Process
                if(item.getItemId() == android.R.id.home)
                {
                    FirebaseAuth.getInstance().signOut();

                    startActivity(new Intent(getApplicationContext(), DashboardPage.class));
                    DashboardPage.this.finish();
                }

                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

        return super.onOptionsItemSelected(item);
    }

}