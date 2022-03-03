package com.example.timetoquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class QuizQuestionPage extends AppCompatActivity implements View.OnClickListener{

    private TextView quiz_QuestionNumber, quiz_Question, quiz_Timer;
    private Button quiz_option1, quiz_option2, quiz_option3, quiz_option4;
    private List<Question> questionList;
    private CountDownTimer countDownTimer;
    int questionNumber;

    //***if any changes made need to change both of this***
    String TimerSec = "25";    //set time text in counter
    int ActualTimerSec = 25000;    //set the actual counter


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_question_page);

        quiz_QuestionNumber = findViewById(R.id.quiz_QuestionNumber);
        quiz_Question = findViewById(R.id.quiz_Question);
        quiz_Timer = findViewById(R.id.quiz_Timer);

        quiz_option1 = findViewById(R.id.quiz_option1);
        quiz_option2 = findViewById(R.id.quiz_option2);
        quiz_option3 = findViewById(R.id.quiz_option3);
        quiz_option4 = findViewById(R.id.quiz_option4);

        quiz_option1.setOnClickListener(this);
        quiz_option2.setOnClickListener(this);
        quiz_option3.setOnClickListener(this);
        quiz_option4.setOnClickListener(this);

        quiz_Timer.setText(String.valueOf(TimerSec));
        getQuizQuestionsList();
    }

    private void getQuizQuestionsList()
    {
        questionList = new ArrayList<>();

        //add question
        questionList.add(new Question("Question 1", "A", "B", "C","C", 2));
        questionList.add(new Question("Question 2", "halo", "B", "ta","C", 2));
        questionList.add(new Question("Question 3", "A", "ni", "C","wo", 2));
        questionList.add(new Question("Question 4", "A", "lol", "C","777", 2));
        questionList.add(new Question("Question 5", "A", "B", "test","C", 2));

        setQuestion();
    }

    private void setQuestion()
    {
        quiz_Timer.setText(String.valueOf(25));

        quiz_Question.setText(questionList.get(0).getQuestion());
        quiz_option1.setText(questionList.get(0).getOption1());
        quiz_option2.setText(questionList.get(0).getOption2());
        quiz_option3.setText(questionList.get(0).getOption3());
        quiz_option4.setText(questionList.get(0).getOption4());

        //set the question number [ex. 1/10]
        quiz_QuestionNumber.setText(String.valueOf(1) + "/" + String.valueOf(questionList.size()));

        startQuizTimer();

        questionNumber = 0;
    }

    private void startQuizTimer()
    {
        int delayTime = 2000;
        countDownTimer = new CountDownTimer(ActualTimerSec+delayTime, 1000) {
            @Override
            public void onTick(long l) {
                if(l < ActualTimerSec){
                    quiz_Timer.setText(String.valueOf(l/1000));
                }
            }

            @Override
            public void onFinish() {

            }
        };

        countDownTimer.start();
    }

    @Override
    public void onClick(View view) {

        int selectedOption = 0;

        switch (view.getId())
        {
            case R.id.quiz_option1:
                selectedOption = 1;
                break;

            case R.id.quiz_option2:
                selectedOption = 2;
                break;

            case R.id.quiz_option3:
                selectedOption = 3;
                break;

            case R.id.quiz_option4:
                selectedOption = 4;
                break;

            default:
        }

        countDownTimer.cancel();
        checkCorrectAnswer(selectedOption, view);
    }

    private void checkCorrectAnswer(int selectedOption, View view)
    {
        if(selectedOption == questionList.get(questionNumber).getCorrectOption())
        {
            //Correct Answer
            //color green correct answer
            ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
        }
        else
        {
            //Wrong Answer
            //color red wrong answer
            ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.RED));

            //color green correct answer
            switch (questionList.get(questionNumber).getCorrectOption())
            {
                case 1:
                    quiz_option1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 2:
                    quiz_option2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 3:
                    quiz_option3.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 4:
                    quiz_option4.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
            }
        }

        //change the question to next after [2secs]
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeQuestion();
            }
        }, 2000);
    }

    private void changeQuestion()
    {
         if(questionNumber < questionList.size()-1)
         {
             questionNumber++;

             playAnim(quiz_Question, 0, 0); //color the select option [true or false]
             playAnim(quiz_option1, 0, 1); //color the select option [true or false]
             playAnim(quiz_option2, 0, 2); //color the select option [true or false]
             playAnim(quiz_option3, 0, 3); //color the select option [true or false]
             playAnim(quiz_option4, 0, 4); //color the select option [true or false]

             quiz_QuestionNumber.setText(String.valueOf(questionNumber+1) + "/" + String.valueOf(questionList.size()));

             //reset the timer time
             quiz_Timer.setText(String.valueOf(TimerSec));
             startQuizTimer();
         }
         else
         {
             //Go to Score Activity
             Intent intent = new Intent(QuizQuestionPage.this, ScorePage.class);
             startActivity(intent);
             QuizQuestionPage.this.finish();
         }
    }

    private void playAnim(View view, final int value, int viewNum)
    {
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500)
                .setStartDelay(100).setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if(value == 0)
                        {
                            switch(viewNum)
                            {
                                case 0:
                                    ((TextView)view).setText(questionList.get(questionNumber).getQuestion());
                                    break;
                                case 1:
                                    ((Button)view).setText(questionList.get(questionNumber).getOption1());
                                case 2:
                                    ((Button)view).setText(questionList.get(questionNumber).getOption2());
                                case 3:
                                    ((Button)view).setText(questionList.get(questionNumber).getOption3());
                                case 4:
                                    ((Button)view).setText(questionList.get(questionNumber).getOption4());
                            }

                            //color back all the option [Current color: #FFA521]
                            if(viewNum != 0)
                            {
                                ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFA521")));
                            }

                            playAnim(view, 1, viewNum);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });

    }
}