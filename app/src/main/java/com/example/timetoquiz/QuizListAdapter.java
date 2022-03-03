package com.example.timetoquiz;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class QuizListAdapter extends BaseAdapter {

    private int numOfQuiz;

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
                Intent intent = new Intent(parent.getContext(), QuizQuestionPage.class);
                parent.getContext().startActivity(intent);
            }
        });

        ((TextView)view.findViewById(R.id.textview_quizNo)).setText(String.valueOf(position+1));

        return view;
    }
}
