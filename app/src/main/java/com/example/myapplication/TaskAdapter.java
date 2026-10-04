package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task> {
    private Context context;
    private List<Task> tasks;

    public TaskAdapter(@NonNull Context context, @NonNull List<Task> tasks) {
        super(context, 0, tasks);
        this.context = context;
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        }

        Task task = tasks.get(position);

        TextView tvTitle = convertView.findViewById(R.id.tvItemTitle);
        TextView tvSub = convertView.findViewById(R.id.tvItemSubtitle);

        String titleText = (task.isDone() ? "[בוצע] " : "") + task.getTitle();
        tvTitle.setText(titleText);

        String subText = task.getTypeName() + " · " + task.getSubject() + " · הגשה: " + task.getDueDate();
        tvSub.setText(subText);

        return convertView;
    }
}
