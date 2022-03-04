package com.example.timetoquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterPage extends AppCompatActivity {

    EditText text_Nickname, text_Email, text_Password;
    CheckBox checkbox_RegisterAsTeacher;
    Button button_Register;
    private Dialog progressloadingDialog;

    private boolean valid = true;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        final LoadingDialog loadingDialog = new LoadingDialog(RegisterPage.this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        text_Nickname = findViewById(R.id.text_Nickname);
        text_Email = findViewById(R.id.text_Email);
        text_Password = findViewById(R.id.text_Password);

        checkbox_RegisterAsTeacher = findViewById(R.id.checkbox_RegisterAsTeacher);

        button_Register = findViewById(R.id.button_Register);

        button_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkField(text_Nickname);
                checkField(text_Email);
                checkField(text_Password);

                if(valid)
                {
                    loadingDialog.startLoadingDialog();

                    firebaseAuth.createUserWithEmailAndPassword(text_Email.getText().toString(), text_Password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            //register successful
                            Toast.makeText(RegisterPage.this, "Account Register Successfully", Toast.LENGTH_SHORT).show();

                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            DocumentReference documentReference = firebaseFirestore.collection("Users").document(user.getUid());
                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("NICKNAME", text_Nickname.getText().toString());
                            userInfo.put("EMAIL", text_Nickname.getText().toString());

                            if(checkbox_RegisterAsTeacher.isChecked())
                            {
                                //specify "is teacher"
                                userInfo.put("isTeacher", "1");
                            }
                            else
                            {
                                //specify "is student"
                                userInfo.put("isStudent", "1");
                            }

                            documentReference.set(userInfo);

                            startActivity(new Intent(getApplicationContext(), LoginPage.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //register failed
                            Toast.makeText(RegisterPage.this, "Account Register Failed", Toast.LENGTH_SHORT).show();
                        }
                    });

                    loadingDialog.dismissDialog();
                }
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
}