package com.example.timetoquiz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.List;

public class QuizListPage extends AppCompatActivity {

    private GridView quizlist_grid;
    private FirebaseFirestore firebaseFirestore;
    public static int subject_id;
    private Dialog progressloadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list_page);

        Toolbar quizlist_toolbar =  findViewById(R.id.quizlist_toolbar);
        setSupportActionBar(quizlist_toolbar);

        //get the value passed from DashboardPage
        String title = getIntent().getStringExtra("SUBJECT");
        subject_id = getIntent().getIntExtra("SUBJECT_ID", 1);

        getSupportActionBar().setTitle(title);

        //toolbar go back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        quizlist_grid = findViewById(R.id.quizlist_gridview);

        //show loading progress
        progressloadingDialog = new Dialog(QuizListPage.this);
        progressloadingDialog.setContentView(R.layout.loading_progressbar);
        progressloadingDialog.setCancelable(false);
        progressloadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        progressloadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressloadingDialog.show();

        firebaseFirestore = FirebaseFirestore.getInstance();

        //demo use only
//        QuizListAdapter quizListAdapter =  new QuizListAdapter(3);
//        quizlist_grid.setAdapter(quizListAdapter);

        loadQuizzes();
    }

    private void loadQuizzes()
    {
        //ex. QUIZ/SUB1 then get QUIZZES count to get the total available quiz
        firebaseFirestore.collection("QUIZ").document("SUB" + String.valueOf(subject_id)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if(documentSnapshot.exists())
                    {
                        long quizzesCount = (long)documentSnapshot.get("QUIZZES");

                        QuizListAdapter quizListAdapter =  new QuizListAdapter((int)quizzesCount);
                        quizlist_grid.setAdapter(quizListAdapter);
                    }
                    else
                    {
                        //no quizzes found
                        Toast.makeText(QuizListPage.this, "The Quizzes List Is Empty!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                }
                else
                {
                    Toast.makeText(QuizListPage.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

                progressloadingDialog.cancel();
            }
        });
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