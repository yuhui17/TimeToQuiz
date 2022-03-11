package com.example.timetoquiz;

import static com.example.timetoquiz.LoginPage.subjectList;
import static com.example.timetoquiz.TeacherDashboardPage.selected_sub_index;
import static com.example.timetoquiz.TeacherQuizListPage.quizzesIds;

import android.content.Context;
import android.content.Intent;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;

public class QuizListAdapter extends BaseAdapter {

    private int numOfQuiz;
    private int QUIZCOUNT; //use to get the quiz's question count

    public QuizListAdapter(int numOfQuiz) {
        this.numOfQuiz = numOfQuiz;
    }

    @Override
    public int getCount() {
        return numOfQuiz;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;

        if(convertView == null)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quizlist_item_layout, parent, false);
        }
        else
        {
            view = convertView;
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getQuizCountAndRedirectToQuizQuestonPage(position, view.getContext());

//                if (quiz_count > 0)
//                {
//                    Intent intent = new Intent(parent.getContext(), QuizQuestionPage.class);
//                    intent.putExtra("QUIZNO", position);
//                    parent.getContext().startActivity(intent);
//                }
//                else
//                {
//                    Toast.makeText(view.getContext(), "Currently the quiz question is empty!", Toast.LENGTH_SHORT).show();
//                }

            }
        });

        ((TextView)view.findViewById(R.id.textview_quizNo)).setText(String.valueOf(position+1));

        return view;
    }

    //only redirect when the quiz is not empty
    private void getQuizCountAndRedirectToQuizQuestonPage(int pos , Context context)
    {
        QUIZCOUNT = 0;

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("QUIZ").document(subjectList.get(selected_sub_index).getId())
                .collection(quizzesIds.get(pos)).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        Map<String, DocumentSnapshot> docList = new ArrayMap<>();

                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots){

                            docList.put(doc.getId(),doc);
                        }

                        QueryDocumentSnapshot questionListDoc = (QueryDocumentSnapshot) docList.get("QUESTION_LIST");

                        String count =  questionListDoc.getString("COUNT");

                        QUIZCOUNT = Integer.valueOf(count);

                        if (QUIZCOUNT > 0)
                        {
                            Intent intent = new Intent(context, QuizQuestionPage.class);
                            intent.putExtra("QUIZNO", pos);
                            context.startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(context, "Currently the quiz question is empty!", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

}
