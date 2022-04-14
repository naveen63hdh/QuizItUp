package com.example.quizitup.classroom.Home.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizitup.R;
import com.example.quizitup.classroom.Models.Classroom;
import com.example.quizitup.classroom.MyClass.MyClassroomActivity;

import java.util.ArrayList;

public class ClassroomHomeAdapter extends RecyclerView.Adapter<ClassroomHomeAdapter.ClassViewHolder> {


    ArrayList<Classroom> classroomList;
    Context context;

    public ClassroomHomeAdapter(ArrayList<Classroom> classroomList, Context context) {
        this.classroomList = classroomList;
        this.context = context;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_classroom, parent, false);
        ClassViewHolder viewHolder = new ClassViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        Classroom myClass = classroomList.get(position);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MyClassroomActivity.class);
                intent.putExtra("classId",myClass.getKey());
                intent.putExtra("creator",myClass.getCreator());
                context.startActivity(intent);
            }
        });

        holder.cpyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("classroom code", myClass.getKey());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Text Copied", Toast.LENGTH_SHORT).show();
            }
        });

        holder.className.setText(myClass.getName());
        holder.classCode.setText(myClass.getKey());
        holder.createdBy.setText(myClass.getCreator());
    }

    @Override
    public int getItemCount() {
        return classroomList.size();
    }

    class ClassViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linearLayout;
        TextView className, classCode, createdBy;
        ImageButton cpyBtn;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.linear_layout);
            className = itemView.findViewById(R.id.className);
            classCode = itemView.findViewById(R.id.classCode);
            createdBy = itemView.findViewById(R.id.createdBy);
            cpyBtn = itemView.findViewById(R.id.copy_btn);
        }
    }
}
