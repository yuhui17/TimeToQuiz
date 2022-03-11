package com.example.timetoquiz;

import static com.example.timetoquiz.LoginPage.subjectList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class TeacherDashboardPage extends AppCompatActivity {

    private RecyclerView recyclerview_Subject;
    private Button button_AddNewSubject;
    private Dialog AddSubjectDialog;
    private EditText text_AddSubjectName;
    private Button button_AddSubject;
    private TeacherDashboardAdapter adapter;
    public static int selected_sub_index = 0;

    final LoadingDialog loadingDialog = new LoadingDialog(TeacherDashboardPage.this);

    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_teacher_dashboard_page);

            Toolbar Toolbar = findViewById(R.id.Toolbar_AddQuestionPage);
            setSupportActionBar(Toolbar);
            getSupportActionBar().setTitle("Teacher Dashboard");

            firebaseFirestore = FirebaseFirestore.getInstance();

            //Add Subject Dialog
            AddSubjectDialog = new Dialog(TeacherDashboardPage.this);
            AddSubjectDialog.setContentView(R.layout.add_subject_dialog);
            AddSubjectDialog.setCancelable(true);
            AddSubjectDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            //toolbar go back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            recyclerview_Subject = findViewById(R.id.recyclerview_QuestionList);
            button_AddNewSubject = findViewById(R.id.button_AddNewQuestion);

            //Add Dialog
            text_AddSubjectName = AddSubjectDialog.findViewById(R.id.text_EditSubjectName);
            button_AddSubject = AddSubjectDialog.findViewById(R.id.button_EditSubject);

//            List<String> subjectList = new ArrayList<>();
//            subjectList.add("subject 1");
//            subjectList.add("subject 2");
//            subjectList.add("subject 3");
//            subjectList.add("subject 4");

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerview_Subject.setLayoutManager(layoutManager);

            adapter = new TeacherDashboardAdapter(subjectList);
            recyclerview_Subject.setAdapter(adapter);

            //this is the dashboard add subject button
            button_AddNewSubject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    text_AddSubjectName.getText().clear();
                    AddSubjectDialog.show();
                }
            });

            //this is the dialog add subject button
            button_AddSubject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(text_AddSubjectName.getText().toString().isEmpty())
                    {
                        text_AddSubjectName.setError("Please Enter A Subject Name");
                        return;
                    }

                    addNewSubject(text_AddSubjectName.getText().toString());
                }
            });
        }
        catch (Exception ex)
        {
            Toast.makeText(TeacherDashboardPage.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

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

                Intent intent = new Intent(TeacherDashboardPage.this, LoginPage.class);
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

    private void addNewSubject(String subjectName)
    {
        AddSubjectDialog.dismiss(); //closing the dialog

        //here start adding subject to the firebase
        loadingDialog.startLoadingDialog();

        Map<String,Object> subData = new ArrayMap<>();
        subData.put("NAME", subjectName);
        subData.put("QUIZZES", 0);
        subData.put("COUNTER", "1");

        String doc_id = firebaseFirestore.collection("QUIZ").document().getId(); //get auto-generated id

        firebaseFirestore.collection("QUIZ").document(doc_id)
                .set(subData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Map<String,Object> subDoc = new ArrayMap<>();
                        subDoc.put("SUB" + String.valueOf(subjectList.size() + 1) + "_NAME", subjectName);
                        subDoc.put("SUB" + String.valueOf(subjectList.size() + 1) + "_ID", doc_id);
                        subDoc.put("COUNT", subjectList.size() + 1);

                        firebaseFirestore.collection("QUIZ").document("SUBJECT")
                                .update(subDoc)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        //success to add subject
                                        Toast.makeText(TeacherDashboardPage.this, "Subject Added Successfully.", Toast.LENGTH_SHORT).show();

                                        subjectList.add(new SubjectModel(doc_id, subjectName, "0", "1"));
                                        adapter.notifyItemInserted(subjectList.size());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //fail to add subject
                                        Toast.makeText(TeacherDashboardPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TeacherDashboardPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        loadingDialog.dismissDialog();
    }
}