package com.example.timetoquiz;

import static com.example.timetoquiz.LoginPage.subjectList;
import static com.example.timetoquiz.TeacherDashboardPage.selected_sub_index;
import static com.example.timetoquiz.TeacherQuizListPage.quizzesIds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;

public class QuizListPage extends AppCompatActivity {

    private GridView quizlist_grid;
    private FirebaseFirestore firestore;
    public static int subject_id;
//    private Dialog progressloadingDialog;
    LoadingDialog loadingDialog = new LoadingDialog(QuizListPage.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list_page);

        Toolbar quizlist_toolbar =  findViewById(R.id.quizlist_toolbar);
        setSupportActionBar(quizlist_toolbar);

//        //get the value passed from DashboardPage
//        String title = getIntent().getStringExtra("SUBJECT");
//        subject_id = getIntent().getIntExtra("SUBJECT_ID", 1);

        getSupportActionBar().setTitle(subjectList.get(selected_sub_index).getName());

        //toolbar go back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        quizlist_grid = findViewById(R.id.quizlist_gridview);

        //show loading progress
//        progressloadingDialog = new Dialog(QuizListPage.this);
//        progressloadingDialog.setContentView(R.layout.loading_progressbar);
//        progressloadingDialog.setCancelable(false);
//        progressloadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
//        progressloadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        progressloadingDialog.show();

        firestore = FirebaseFirestore.getInstance();

        //demo use only
//        QuizListAdapter quizListAdapter =  new QuizListAdapter(3);
//        quizlist_grid.setAdapter(quizListAdapter);

        loadQuizzes();
    }

    //new
    private void loadQuizzes()
    {
        quizzesIds.clear();

        loadingDialog.startLoadingDialog();

//        //demo use
//        quizzesIds.add("1");
//        quizzesIds.add("2");
//        quizzesIds.add("3");
//        quizzesIds.add("4");

        firestore.collection("QUIZ").document(subjectList.get(selected_sub_index).getId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                long numOfQuizzes = (long)documentSnapshot.get("QUIZZES");

                for(int i=1; i<= numOfQuizzes; i++){
                    quizzesIds.add(documentSnapshot.getString("QUIZ" + String.valueOf(i) + "_ID"));
                }

                QuizListAdapter quizListAdapter =  new QuizListAdapter(quizzesIds.size());
                quizlist_grid.setAdapter(quizListAdapter);

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(QuizListPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


        loadingDialog.dismissDialog();
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