package com.example.timetoquiz;

import static com.example.timetoquiz.LoginPage.subjectList;
import static com.example.timetoquiz.TeacherDashboardPage.selected_sub_index;
import static com.example.timetoquiz.TeacherQuizListPage.selected_quiz_index;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.collection.ArrayMap;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;
import java.util.Map;

public class TeacherQuizListAdapter extends RecyclerView.Adapter<TeacherQuizListAdapter.ViewHolder>
{
    private List<String> quizIDs;

    public TeacherQuizListAdapter(List<String> quizIDs) {
        this.quizIDs = quizIDs;
    }


    @NonNull
    @Override
    public TeacherQuizListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.teacher_dashboard_item_layout,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherQuizListAdapter.ViewHolder viewHolder, int i) {

        String setID = quizIDs.get(i);
        viewHolder.setData(i, setID, this);
    }

    @Override
    public int getItemCount() {
        return quizIDs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //here reuse the teacher dashboard item layout
        private TextView text_QuizName;
        private ImageView image_Delete;
        private Dialog loadingDialog;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            text_QuizName = itemView.findViewById(R.id.text_SubjectName);
            image_Delete = itemView.findViewById(R.id.image_Delete);

            loadingDialog = new Dialog(itemView.getContext());
            loadingDialog.setContentView(R.layout.loading_progressbar);
            loadingDialog.setCancelable(false);
            loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        private void setData(final int pos, final String quizID, final TeacherQuizListAdapter adapter)
        {
            text_QuizName.setText("QUIZ " + String.valueOf(pos + 1));


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    selected_quiz_index = pos;

                    Intent intent = new Intent(itemView.getContext(),TeacherQuestionPage.class);
                    itemView.getContext().startActivity(intent);
                }
            });

            image_Delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog dialog = new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Delete Quiz")
                            .setMessage("Are you sure to delete this quiz?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    deleteQuiz(pos, quizID,itemView.getContext(), adapter);
                                }
                            })
                            .setNegativeButton("Cancel",null)
                            .setIcon(R.drawable.icon_delete)
                            .show();

                    dialog.getButton(dialog.BUTTON_POSITIVE).setBackgroundResource(R.drawable.button_rounded_rectangle);
                    dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);

                    dialog.getButton(dialog.BUTTON_NEGATIVE).setBackgroundResource(R.drawable.button_rounded_rectangle);
                    dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0,0,50,0);
                    dialog.getButton(dialog.BUTTON_NEGATIVE).setLayoutParams(params);
                }
            });
        }


        private void deleteQuiz(final int pos, String quizID, final Context context, final TeacherQuizListAdapter adapter)
        {
            loadingDialog.show();

            final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            firestore.collection("QUIZ").document(subjectList.get(selected_sub_index).getId())
                    .collection(quizID).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            WriteBatch batch = firestore.batch();

                            for(QueryDocumentSnapshot doc : queryDocumentSnapshots)
                            {
                                batch.delete(doc.getReference());
                            }

                            batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Map<String, Object> subDoc = new ArrayMap<>();

                                    int index=1;
                                    for(int i=0; i< quizIDs.size();  i++)
                                    {
                                        if(i != pos)
                                        {
                                            subDoc.put("QUIZ" + String.valueOf(index) + "_ID", quizIDs.get(i));
                                            index++;
                                        }
                                    }

                                    subDoc.put("QUIZZES", index-1);

                                    firestore.collection("QUIZ").document(subjectList.get(selected_sub_index).getId())
                                            .update(subDoc)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void v) {
                                                    Toast.makeText(context,"Quiz deleted Successfully",Toast.LENGTH_SHORT).show();

                                                    TeacherQuizListPage.quizzesIds.remove(pos);

                                                    subjectList.get(selected_sub_index).setNumOfSets(String.valueOf(TeacherQuizListPage.quizzesIds.size()));

                                                    adapter.notifyDataSetChanged();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context,e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

            loadingDialog.dismiss();
        }
    }
}

