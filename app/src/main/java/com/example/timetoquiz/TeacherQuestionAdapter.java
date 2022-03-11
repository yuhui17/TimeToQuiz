package com.example.timetoquiz;

import static com.example.timetoquiz.LoginPage.subjectList;
import static com.example.timetoquiz.TeacherDashboardPage.selected_sub_index;
import static com.example.timetoquiz.TeacherQuizListPage.quizzesIds;
import static com.example.timetoquiz.TeacherQuizListPage.selected_quiz_index;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class TeacherQuestionAdapter extends RecyclerView.Adapter<TeacherQuestionAdapter.ViewHolder> {

    private List<QuestionModel> question_List;

    public TeacherQuestionAdapter(List<QuestionModel> question_List) {
        this.question_List = question_List;
    }

    @NonNull
    @Override
    public TeacherQuestionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.teacher_dashboard_item_layout, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherQuestionAdapter.ViewHolder holder, int position) {
        holder.setData(position, this);
    }

    @Override
    public int getItemCount() {
        return question_List.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView text_QuestionName;
        private ImageView image_Delete;
        private Dialog loadingDialog;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            text_QuestionName = itemView.findViewById(R.id.text_SubjectName);
            image_Delete = itemView.findViewById(R.id.image_Delete);

            loadingDialog = new Dialog(itemView.getContext());
            loadingDialog.setContentView(R.layout.loading_progressbar);
            loadingDialog.setCancelable(false);
            loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        private void setData(final int pos, TeacherQuestionAdapter adapter)
        {
            text_QuestionName.setText("QUESTION " +  String.valueOf(pos+1));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(),TeacherAddQuestionPage.class);
                    intent.putExtra("ACTION","EDIT");
                    intent.putExtra("Q_ID", pos);

                    itemView.getContext().startActivity(intent);
                }
            });

            image_Delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog dialog = new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Delete Question")
                            .setMessage("Are you sure to delete this question?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    deleteQuestion(pos, itemView.getContext(), adapter);
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

        private void deleteQuestion(int pos, Context context, TeacherQuestionAdapter adapter){
            loadingDialog.show();

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            firestore.collection("QUIZ").document(subjectList.get(selected_sub_index).getId())
                    .collection(quizzesIds.get(selected_quiz_index)).document(question_List.get(pos).getQuestionID())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Map<String, Object> questionDoc = new ArrayMap<>();

                            int index = 1;
                            for(int i=0;i<question_List.size(); i++){
                                if(i != pos){
                                    questionDoc.put("Q" + String.valueOf(index) + "_ID", question_List.get(i).getQuestionID());
                                    index++;
                                }
                            }

                            questionDoc.put("COUNT", String.valueOf(index - 1));

                            firestore.collection("QUIZ").document(subjectList.get(selected_sub_index).getId())
                                    .collection(quizzesIds.get(selected_quiz_index)).document("QUESTION_LIST")
                                    .set(questionDoc)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(context,"Question deleted Successfully",Toast.LENGTH_SHORT).show();

                                            question_List.remove(pos);
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

                        }
                    });

            loadingDialog.dismiss();
        }
    }
}
