package com.example.timetoquiz;

import androidx.annotation.NonNull;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.collection.ArrayMap;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TeacherDashboardAdapter extends RecyclerView.Adapter<TeacherDashboardAdapter.ViewHolder> {

    private List<SubjectModel> subject_list;

    public TeacherDashboardAdapter(List<SubjectModel> subject_list){
        this.subject_list = subject_list;
    }

    @NonNull
    @Override
    public TeacherDashboardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.teacher_dashboard_item_layout, viewGroup,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherDashboardAdapter.ViewHolder viewHolder, int position) {

        String title = subject_list.get(position).getName();

        viewHolder.setData(title, position, this);

    }

    @Override
    public int getItemCount() {
        return subject_list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private Dialog loadingDialog;

        private TextView text_SubjectName;
        private ImageView image_Delete;

        private Dialog editDialog;
        private EditText text_EditSubjectName;
        private Button button_EditSubject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            text_SubjectName = itemView.findViewById(R.id.text_SubjectName);
            image_Delete = itemView.findViewById(R.id.image_Delete);

            //loading dialog
            loadingDialog = new Dialog(itemView.getContext());
            loadingDialog.setContentView(R.layout.custom_loadingdialog);
            loadingDialog.setCancelable(false);
            loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

            //edit dialog
            editDialog = new Dialog(itemView.getContext());
            editDialog.setContentView(R.layout.edit_subject_dialog);
            editDialog.setCancelable(true);
            editDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

            text_EditSubjectName = editDialog.findViewById(R.id.text_EditSubjectName);
            button_EditSubject = editDialog.findViewById(R.id.button_EditSubject);
        }

        private void setData(String title, int position, final TeacherDashboardAdapter adapter)
        {
            text_SubjectName.setText(title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TeacherDashboardPage.selected_sub_index = position;
                    Intent intent = new Intent(itemView.getContext(), TeacherQuizListPage.class);

                    intent.putExtra("SUBJECT", text_SubjectName.getText()); //pass the subject name

                    itemView.getContext().startActivity(intent);
                }
            });

            //long click to change subject name
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    text_EditSubjectName.setText(subject_list.get(position).getName());
                    editDialog.show();

                    return false;
                }
            });

            //edit subject name button click
            button_EditSubject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(text_EditSubjectName.getText().toString().isEmpty())
                    {
                        text_EditSubjectName.setError("Please Enter Subject Name");
                        return;
                    }

                    updateSubject(text_EditSubjectName.getText().toString(), position, itemView.getContext(), adapter);
                }
            });

            image_Delete.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View view) {

                    AlertDialog dialog = new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Delete Subject")
                            .setMessage("Are you sure to delete this subject?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    deleteSubject(position, itemView.getContext(), adapter);
                                }
                            })
                            .setNegativeButton("Cancel",null)
                            .setIcon(R.drawable.icon_delete)
                            .show();

                    dialog.getButton(dialog.BUTTON_POSITIVE).setBackgroundResource(R.drawable.button_rounded_rectangle);
                    dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(R.color.black);

                    dialog.getButton(dialog.BUTTON_NEGATIVE).setBackgroundResource(R.drawable.button_rounded_rectangle);
                    dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(R.color.black);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0,0,50,0);
                    dialog.getButton(dialog.BUTTON_NEGATIVE).setLayoutParams(params);
                }
            });
        }

        private void deleteSubject(final int id, final Context context, final TeacherDashboardAdapter adapter)
        {
            loadingDialog.show();

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            Map<String,Object> subDoc = new ArrayMap<>();
            int index=1;

            for(int i=0; i < subject_list.size(); i++)
            {
                if( i != id)
                {
                    subDoc.put("SUB" + String.valueOf(index) + "_ID", subject_list.get(i).getId());
                    subDoc.put("SUB" + String.valueOf(index) + "_NAME", subject_list.get(i).getName());
                    index++;
                }
            }

            subDoc.put("COUNT", index - 1);

            firestore.collection("QUIZ").document("SUBJECT")
                    .set(subDoc)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(context,"Subject deleted successfully",Toast.LENGTH_SHORT).show();

                            LoginPage.subjectList.remove(id);

                            adapter.notifyDataSetChanged();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });

            //delay a 1.5 sec for showing dialog
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadingDialog.dismiss();
                }
            }, 1500);

        }

        private void updateSubject(final String newSubjectName, final int pos, final Context context, final TeacherDashboardAdapter adapter)
        {
            editDialog.dismiss();

            loadingDialog.show();

            Map<String,Object> catData = new ArrayMap<>();
            catData.put("NAME",newSubjectName);

            final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            firestore.collection("QUIZ").document(subject_list.get(pos).getId())
                    .update(catData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Map<String,Object> catDoc = new ArrayMap<>();
                            catDoc.put("SUB" + String.valueOf(pos + 1) + "_NAME",newSubjectName);

                            firestore.collection("QUIZ").document("SUBJECT")
                                    .update(catDoc)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            Toast.makeText(context,"Subject Name Changed Successfully",Toast.LENGTH_SHORT).show();
                                            LoginPage.subjectList.get(pos).setName(newSubjectName);
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
                            loadingDialog.dismiss();
                        }
                    });

            //delay a 1.5 sec for showing dialog
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadingDialog.dismiss();
                }
            }, 1500);
        }
    }
}
