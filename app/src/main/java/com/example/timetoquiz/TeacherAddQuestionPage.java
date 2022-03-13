package com.example.timetoquiz;

import static com.example.timetoquiz.LoginPage.subjectList;
import static com.example.timetoquiz.TeacherDashboardPage.selected_sub_index;
import static com.example.timetoquiz.TeacherQuestionPage.questionList;
import static com.example.timetoquiz.TeacherQuizListPage.quizzesIds;
import static com.example.timetoquiz.TeacherQuizListPage.selected_quiz_index;

import android.os.Bundle;
import android.util.ArrayMap;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class TeacherAddQuestionPage extends AppCompatActivity {

    private EditText text_Question, text_Option1, text_Option2, text_Option3, text_Option4, text_Answer;
    private Button button_AddQuestionDetails;

    private  String StrQuestion, StrOption1, StrOption2, StrOption3, StrOption4, StrAnswer;

    LoadingDialog loadingDialog = new LoadingDialog(TeacherAddQuestionPage.this);

    private FirebaseFirestore firestore;
    private boolean validator;
    private String action;
    private  int qid; //use to identify the situation is edit or add

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_add_question_page);

        firestore = FirebaseFirestore.getInstance();

        action = String.valueOf(getIntent().getStringExtra("ACTION"));
        qid = getIntent().getIntExtra("Q_ID",0);

        Toolbar toolbar = findViewById(R.id.Toolbar_AddQuestionPage);
        setSupportActionBar(toolbar);
        //toolbar go back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Question " + String.valueOf(questionList.size()+1));

        text_Question = findViewById(R.id.text_Question);
        text_Option1 = findViewById(R.id.text_Option1);
        text_Option2 = findViewById(R.id.text_Option2);
        text_Option3 = findViewById(R.id.text_Option3);
        text_Option4 = findViewById(R.id.text_Option4);
        text_Answer = findViewById(R.id.text_Answer);
        button_AddQuestionDetails = findViewById(R.id.button_AddQuestionDetails);

        if(action.compareTo("EDIT") == 0)
        {
            loadData(qid);
            getSupportActionBar().setTitle("Question " + String.valueOf(qid+1));
            button_AddQuestionDetails.setText("Update Question");
        }
        else
        {
            getSupportActionBar().setTitle("Question " + String.valueOf(questionList.size()+1));
            button_AddQuestionDetails.setText("Add Question");
        }


        button_AddQuestionDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(action.compareTo("EDIT") == 0)
                {
                    CheckAllFieldIsEmpty();
                    if(validator){
                        CheckValidAnswerFormat();
                        if(validator){
                            EditQuestion();
                        }
                    }
                }
                else
                {
                    CheckAllFieldIsEmpty();
                    if(validator){
                        CheckValidAnswerFormat();
                        if(validator){
                            AddNewQuestion();
                        }
                    }
                }


            }
        });
    }

    private void EditQuestion()
    {
        Map<String,Object> questionData = new ArrayMap<>();
        questionData.put("QUESTION", StrQuestion);
        questionData.put("OPTION1", StrOption1);
        questionData.put("OPTION2", StrOption2);
        questionData.put("OPTION3", StrOption3);
        questionData.put("OPTION4", StrOption4);
        questionData.put("ANSWER", StrAnswer);

        firestore.collection("QUIZ").document(subjectList.get(selected_sub_index).getId())
                .collection(quizzesIds.get(selected_quiz_index)).document(questionList.get(qid).getQuestionID())
                .set(questionData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(TeacherAddQuestionPage.this,"Question updated Successfully",Toast.LENGTH_SHORT).show();

                        questionList.get(qid).setQuestion(StrQuestion);
                        questionList.get(qid).setOption1(StrOption1);
                        questionList.get(qid).setOption2(StrOption2);
                        questionList.get(qid).setOption3(StrOption3);
                        questionList.get(qid).setOption4(StrOption4);
                        questionList.get(qid).setCorrectAnswer(Integer.valueOf(StrAnswer));

                        TeacherAddQuestionPage.this.finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TeacherAddQuestionPage.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void loadData(int qid)
    {
        text_Question.setText(questionList.get(qid).getQuestion());
        text_Option1.setText(questionList.get(qid).getOption1());
        text_Option2.setText(questionList.get(qid).getOption2());
        text_Option3.setText(questionList.get(qid).getOption3());
        text_Option4.setText(questionList.get(qid).getOption4());
        text_Answer.setText(String.valueOf(questionList.get(qid).getCorrectAnswer()));
    }

    private void AddNewQuestion() {
        //no loading dialog for this, because after finish adding will finish this page will cause app crash
//        loadingDialog.startLoadingDialog();
        Map<String,Object> questionData = new ArrayMap<>();

        questionData.put("QUESTION", StrQuestion);
        questionData.put("OPTION1", StrOption1);
        questionData.put("OPTION2", StrOption2);
        questionData.put("OPTION3", StrOption3);
        questionData.put("OPTION4", StrOption4);
        questionData.put("ANSWER", StrAnswer);

        String doc_id = firestore.collection("QUIZ").document(subjectList.get(selected_sub_index).getId())
            .collection(quizzesIds.get(selected_quiz_index)).document().getId();

        firestore.collection("QUIZ").document(subjectList.get(selected_sub_index).getId())
                .collection(quizzesIds.get(selected_quiz_index)).document(doc_id)
                .set(questionData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Map<String, Object> questionDoc = new ArrayMap<>();
                        questionDoc.put("Q" + String.valueOf(questionList.size() + 1) + "_ID", doc_id);
                        questionDoc.put("COUNT", String.valueOf(questionList.size() + 1 ));

                        firestore.collection("QUIZ").document(subjectList.get(selected_sub_index).getId())
                                .collection(quizzesIds.get(selected_quiz_index)).document("QUESTION_LIST")
                                .update(questionDoc)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        Toast.makeText(TeacherAddQuestionPage.this, "Question Added Successfully.", Toast.LENGTH_SHORT).show();

                                        questionList.add(new QuestionModel(
                                                doc_id,
                                                StrQuestion,
                                                StrOption1,
                                                StrOption2,
                                                StrOption3,
                                                StrOption4,
                                                Integer.valueOf(StrAnswer)
                                        ));

                                        TeacherAddQuestionPage.this.finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(TeacherAddQuestionPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TeacherAddQuestionPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

//        loadingDialog.dismissDialog();
    }

    private boolean CheckAllFieldIsEmpty() {
        validator = true;

        StrQuestion = text_Question.getText().toString();
        StrOption1 = text_Option1.getText().toString();
        StrOption2 = text_Option2.getText().toString();
        StrOption3 = text_Option3.getText().toString();
        StrOption4 = text_Option4.getText().toString();
        StrAnswer = text_Answer.getText().toString();

        if(StrQuestion.isEmpty()){
            text_Question.setError("Question cannot be empty!");
            validator = false;
        }
        if(StrOption1.isEmpty()){
            text_Option1.setError("Option cannot be empty!");
            validator = false;
        }
        if(StrOption2.isEmpty()){
            text_Option2.setError("Option cannot be empty!");
            validator = false;
        }
        if(StrOption3.isEmpty()){
            text_Option3.setError("Option cannot be empty!");
            validator = false;
        }
        if(StrOption4.isEmpty()){
            text_Option4.setError("Option cannot be empty!");
            validator = false;
        }
        if(StrAnswer.isEmpty()){
            text_Answer.setError("Answer cannot be empty!");
            validator = false;
        }

        return validator;
    }

    private boolean CheckValidAnswerFormat(){

        StrAnswer = text_Answer.getText().toString();

        if(StrAnswer.compareTo("1") == 0 ||StrAnswer.compareTo("2") == 0 ||StrAnswer.compareTo("3") == 0 ||StrAnswer.compareTo("4") == 0)
        {
            validator = true;
        }
        else
        {
            Toast.makeText(TeacherAddQuestionPage.this, "Answer field should in format of 1/2/3/4 only", Toast.LENGTH_SHORT).show();
            validator = false;
        }

        return validator;
    }


    //if go back is clicked go back to previous page
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            TeacherAddQuestionPage.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}