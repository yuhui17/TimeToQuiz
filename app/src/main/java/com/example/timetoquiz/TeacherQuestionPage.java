package com.example.timetoquiz;

import static com.example.timetoquiz.LoginPage.subjectList;
import static com.example.timetoquiz.TeacherDashboardPage.selected_sub_index;
import static com.example.timetoquiz.TeacherQuizListPage.quizzesIds;
import static com.example.timetoquiz.TeacherQuizListPage.selected_quiz_index;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeacherQuestionPage extends AppCompatActivity {

    private RecyclerView recyclerview_QuestionList;
    private Button button_AddNewQuestion;

    public static List<QuestionModel> questionList = new ArrayList<>();
    private TeacherQuestionAdapter adapter;

    FirebaseFirestore firestore;

    LoadingDialog loadingDialog = new LoadingDialog(TeacherQuestionPage.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_question_page);

        Toolbar Toolbar = findViewById(R.id.Toolbar_AddQuestionPage);
        setSupportActionBar(Toolbar);
        getSupportActionBar().setTitle("Question");

        recyclerview_QuestionList = findViewById(R.id.recyclerview_QuestionList);
        button_AddNewQuestion = findViewById(R.id.button_AddNewQuestion);

        //toolbar go back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        button_AddNewQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(TeacherQuestionPage.this, TeacherAddQuestionPage.class);
                    startActivity(intent);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview_QuestionList.setLayoutManager(layoutManager);

        firestore = FirebaseFirestore.getInstance();

        loadQuestion();
    }

    private void loadQuestion()
    {
        questionList.clear();
        loadingDialog.startLoadingDialog();

//        //demo use
//        questionList.add(new QuestionModel("1", "Q1", "A", "B", "C", "D", 2));
//        questionList.add(new QuestionModel("2", "Q2", "A", "B", "C", "D", 4));
//        questionList.add(new QuestionModel("3", "Q3", "A", "B", "C", "D", 4));
//        questionList.add(new QuestionModel("4", "Q4", "A", "B", "C", "D", 2));
//        questionList.add(new QuestionModel("5", "Q5", "A", "B", "C", "D", 1));

        firestore.collection("QUIZ").document(subjectList.get(selected_sub_index).getId())
                .collection(quizzesIds.get(selected_quiz_index)).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        Map<String, DocumentSnapshot> docList = new ArrayMap<>();

                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots){

                            docList.put(doc.getId(),doc);
                        }

                        QueryDocumentSnapshot questionListDoc = (QueryDocumentSnapshot) docList.get("QUESTION_LIST");

                        String count =  questionListDoc.getString("COUNT");

                        for(int i=0; i < Integer.valueOf(count); i++){

                            String questionID= questionListDoc.getString("Q" + String.valueOf(i + 1) + "_ID");

                            QueryDocumentSnapshot questionDoc = (QueryDocumentSnapshot) docList.get(questionID);

                            questionList.add(new QuestionModel(
                                    questionID,
                                    questionDoc.getString("QUESTION"),
                                    questionDoc.getString("OPTION1"),
                                    questionDoc.getString("OPTION2"),
                                    questionDoc.getString("OPTION3"),
                                    questionDoc.getString("OPTION4"),
                                    Integer.valueOf(questionDoc.getString("ANSWER"))
                                    ));

                        }

                        adapter = new TeacherQuestionAdapter(questionList);
                        recyclerview_QuestionList.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(TeacherQuestionPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });



        adapter = new TeacherQuestionAdapter(questionList);
        recyclerview_QuestionList.setAdapter(adapter);

        loadingDialog.dismissDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(adapter != null)
        {
            adapter.notifyDataSetChanged();
        }
    }

    //if go back is clicked go back to previous page
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            TeacherQuestionPage.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}