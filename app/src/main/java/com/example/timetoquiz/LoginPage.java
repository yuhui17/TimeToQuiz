package com.example.timetoquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class LoginPage extends AppCompatActivity {

    //variable
    Button button_Login;
    Button button_LoginPage_Register;
    EditText text_LoginPassword, text_LoginEmail;
    private Dialog progressloadingDialog;

    private boolean valid = true;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    public static List<String> subjectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        final LoadingDialog loadingDialog = new LoadingDialog(LoginPage.this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //hook
        button_Login = findViewById(R.id.button_Login);
        button_LoginPage_Register = findViewById(R.id.button_LoginPage_Register);
        text_LoginPassword = findViewById(R.id.text_LoginPassword);
        text_LoginEmail = findViewById(R.id.text_LoginEmail);

        //Login button
        button_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkField(text_LoginPassword);
                checkField(text_LoginEmail);

                if(valid == true)
                {
                    loadingDialog.startLoadingDialog();

                    loadDataFromFireStore(); //loading SUBJECT

                    firebaseAuth.signInWithEmailAndPassword(text_LoginEmail.getText().toString(), text_LoginPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(LoginPage.this, "Login Successfully.", Toast.LENGTH_SHORT).show();

                            checkUserAccessLevel(authResult.getUser().getUid());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginPage.this, "Login failed, Please Try Again.", Toast.LENGTH_SHORT).show();

                        }
                    });

                    loadingDialog.dismissDialog();
                }

            }

        });

        //Register button
        button_LoginPage_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPage.this, RegisterPage.class);
                startActivity(intent);
            }
        });

    }

    private void checkUserAccessLevel(String uid)
    {
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(uid);

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG", "onSuccess:" + documentSnapshot.getData());

                if (documentSnapshot.getString("isTeacher") != null)
                {
                    //login as teacher
                    startActivity(new Intent(getApplicationContext(), TeacherDashboardPage.class));
                }
                else
                {
                    //login as student
                    startActivity(new Intent(getApplicationContext(), DashboardPage.class));
                }

//                LoginPage.this.finish();
            }
        });
    }

    private boolean checkField(EditText textField)
    {
        if(textField.getText().toString().isEmpty())
        {
            textField.setError("Required");
            valid = false;
        }
        else
        {
            valid = true;
        }

        return valid;
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if(FirebaseAuth.getInstance().getCurrentUser() != null)
//        {
//            startActivity(new Intent(getApplicationContext(), LoginPage.class));
//            finish();
//        }
//    }

    private void loadDataFromFireStore()
    {
        subjectList.clear();

        firebaseFirestore.collection("QUIZ").document("SUBJECT").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if(documentSnapshot.exists())
                    {
                        long count = (long)documentSnapshot.get("COUNT");

                        //loop to get the subject name
                        for(int i = 1; i <= count; i++)
                        {
                            String subjectName = documentSnapshot.getString("SUB" + String.valueOf(i)); // ex. SUB1, SUB2
                            subjectList.add(subjectName);
                        }
                    }
                    else
                    {
                        //no subject found
                        Toast.makeText(LoginPage.this, "The Subject List Is Empty!", Toast.LENGTH_SHORT).show();
//                        finish();
                    }
                }
                else
                {
                    Toast.makeText(LoginPage.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}