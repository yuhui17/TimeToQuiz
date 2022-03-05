package com.example.timetoquiz;

//import static com.example.timetoquiz.MainActivity.subjectList;

import static com.example.timetoquiz.LoginPage.subjectList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

        if(item.getItemId() == android.R.id.home)
        {
//            FirebaseAuth.getInstance().signOut();
//
//            DashboardPage.this.finish();

            showAlertDialogLogOut();
        }

        return super.onOptionsItemSelected(item);
    }


    private void showAlertDialogLogOut() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Logout Confirmation");
        dialog.setMessage("You want to logout now?");
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //logout process
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(DashboardPage.this, LoginPage.class);
                startActivity(intent);

//                DashboardPage.this.finish();
                finishAffinity();

                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        showAlertDialogLogOut();
    }
}