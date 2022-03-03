package com.example.timetoquiz;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

public class DashboardGridAdapter extends BaseAdapter {

    public DashboardGridAdapter(List<String> dashboardList) {
        this.DashboardList = dashboardList;
    }

    private List<String> DashboardList;

    @Override
    public int getCount() {
        return DashboardList.size();
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_item_layout, parent,false);
        }
        else
        {
            view = convertView;
        }

        //Trigger When Click the Subject
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(parent.getContext(), QuizListPage.class);
                intent.putExtra("SUBJECT", DashboardList.get(position)); //pass the subject name
                intent.putExtra("SUBJECT_ID", position+1);  //pass  the subject_id
                parent.getContext().startActivity(intent);
            }
        });

        ((TextView) view.findViewById(R.id.quizName)).setText(DashboardList.get(position));

        //color all textview with random color
        Random random = new Random();
        int color = Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
        view.setBackgroundColor(color);

        return view;
    }
}
