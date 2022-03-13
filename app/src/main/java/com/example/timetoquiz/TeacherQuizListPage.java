package com.example.timetoquiz;

import static com.example.timetoquiz.LoginPage.subjectList;
import static com.example.timetoquiz.TeacherDashboardPage.selected_sub_index;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeacherQuizListPage extends AppCompatActivity {

    private RecyclerView recyclerview_QuizList;
    private Button button_AddNewQuiz;
    private TeacherQuizListAdapter adapter;
    private FirebaseFirestore firestore;

    LoadingDialog loadingDialog = new LoadingDialog(TeacherQuizListPage.this);

    public static List<String> quizzesIds = new ArrayList<>();
    public static int selected_quiz_index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_quiz_list_page);

        firestore = FirebaseFirestore.getInstance();

        Toolbar Toolbar = findViewById(R.id.Toolbar_AddQuestionPage);
        setSupportActionBar(Toolbar);

        String title = getIntent().getStringExtra("SUBJECT");

        getSupportActionBar().setTitle(title);

        //toolbar go back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerview_QuizList = findViewById(R.id.recyclerview_QuestionList);
        button_AddNewQuiz = findViewById(R.id.button_AddNewQuestion);

        button_AddNewQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                dialog.setTitle("Add New Quiz");
                dialog.setMessage("You want to add a new quiz?");
                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        AddNewQuiz();

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
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview_QuizList.setLayoutManager(layoutManager);

        LoadQuizzes();
    }

    private void LoadQuizzes()
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

                subjectList.get(selected_sub_index).setQuizCounter(documentSnapshot.getString("COUNTER"));
                subjectList.get(selected_sub_index).setNumOfSets(String.valueOf(numOfQuizzes));

                adapter = new TeacherQuizListAdapter(quizzesIds);
                recyclerview_QuizList.setAdapter(adapter);

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(TeacherQuizListPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


        loadingDialog.dismissDialog();
    }

    private void AddNewQuiz()
    {
        loadingDialog.startLoadingDialog();

        String current_sub_id = subjectList.get(selected_sub_index).getId();
        String current_counter = subjectList.get(selected_sub_index).getQuizCounter();

        Map<String,Object> questionData = new ArrayMap<>();
        questionData.put("COUNT", "0");

        firestore.collection("QUIZ").document(current_sub_id)
                .collection(current_counter).document("QUESTION_LIST")
                .set(questionData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Map<String,Object> subDoc = new ArrayMap<>();
                        subDoc.put("COUNTER", String.valueOf(Integer.valueOf(current_counter) + 1));
                        subDoc.put("QUIZ" + String.valueOf(quizzesIds.size() + 1) + "_ID", current_counter);
                        subDoc.put("QUIZZES",quizzesIds.size() + 1);

                        firestore.collection("QUIZ").document(current_sub_id)
                                .update(subDoc)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        Toast.makeText(TeacherQuizListPage.this, "Quiz Added Successfully.", Toast.LENGTH_SHORT).show();

                                        quizzesIds.add(current_counter);
                                        subjectList.get(selected_sub_index).setNumOfSets(String.valueOf(quizzesIds.size()));
                                        subjectList.get(selected_sub_index).setQuizCounter(String.valueOf(Integer.valueOf(current_counter) + 1));

                                        adapter.notifyItemInserted(quizzesIds.size());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(TeacherQuizListPage.this, "Quiz Added Successfully.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(TeacherQuizListPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        loadingDialog.dismissDialog();
    }

    //if go back is clicked go back to previous page
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            TeacherQuizListPage.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
