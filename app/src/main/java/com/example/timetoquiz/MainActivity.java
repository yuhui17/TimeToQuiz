package com.example.timetoquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //SPLASH_SCREEN SHOW UP TIME
    private static int SPLASH_SCREEN = 3500;

    //variable
    Animation top_animation,bottom_animation;
    ImageView image_ttq;
    TextView text_WelcomeText;

//    public static List<String> subjectList = new ArrayList<>();

    //database
//    private FirebaseFirestore fireStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //load animation
        top_animation = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottom_animation = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        //hook
        image_ttq = findViewById(R.id.image_ttq);
        text_WelcomeText = findViewById(R.id.text_WelcomeText);

        //set animation
        image_ttq.setAnimation(top_animation);
        text_WelcomeText.setAnimation(bottom_animation);

//        fireStore = FirebaseFirestore.getInstance();

//        loadDataFromFireStore();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, LoginPage.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);
    }

//    private void loadDataFromFireStore()
//    {
//        subjectList.clear();
//
//        fireStore.collection("QUIZ").document("SUBJECT").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//
//                if(task.isSuccessful())
//                {
//                    DocumentSnapshot documentSnapshot = task.getResult();
//
//                    if(documentSnapshot.exists())
//                    {
//                        long count = (long)documentSnapshot.get("COUNT");
//
//                        //loop to get the subject name
//                        for(int i = 1; i <= count; i++)
//                        {
//                            String subjectName = documentSnapshot.getString("SUB" + String.valueOf(i)); // ex. SUB1, SUB2
//                            subjectList.add(subjectName);
//                        }
//                    }
//                    else
//                    {
//                        //no subject found
//                        Toast.makeText(MainActivity.this, "The Subject List Is Empty!", Toast.LENGTH_SHORT).show();
//                        finish();
//                    }
//                }
//                else
//                {
//                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
}