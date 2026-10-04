package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class TaskDetailActivity extends AppCompatActivity {

    private TextView tvDetailTitle, tvDetailType, tvDetailSubject, tvDetailPriority;
    private TextView tvDetailDueDate, tvDetailAmount, tvDetailStatus, tvDetailPoints;
    private Button btnToggleDone, btnDelete, btnBack;

    private TaskStorage storage;
    private Task currentTask;
    private int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        taskId = getIntent().getIntExtra("TASK_ID", -1);
        storage = new TaskStorage(this);

        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailType = findViewById(R.id.tvDetailType);
        tvDetailSubject = findViewById(R.id.tvDetailSubject);
        tvDetailPriority = findViewById(R.id.tvDetailPriority);
        tvDetailDueDate = findViewById(R.id.tvDetailDueDate);
        tvDetailAmount = findViewById(R.id.tvDetailAmount);
        tvDetailStatus = findViewById(R.id.tvDetailStatus);
        tvDetailPoints = findViewById(R.id.tvDetailPoints);

        btnToggleDone = findViewById(R.id.btnToggleDone);
        btnDelete = findViewById(R.id.btnDelete);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(TaskDetailActivity.this)
                    .setTitle("מחיקת משימה")
                    .setMessage("האם אתה בטוח שברצונך למחוק משימה זו?")
                    .setPositiveButton("מחק", (dialog, which) -> {
                        storage.deleteById(taskId);
                        Toast.makeText(TaskDetailActivity.this, "המשימה נמחקה", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("ביטול", null)
                    .show();
        });

        btnToggleDone.setOnClickListener(v -> {
            if (currentTask != null) {
                currentTask.setDone(!currentTask.isDone());
                storage.updateTask(currentTask);
                displayTaskDetails();
            }
        });

        loadTask();
    }

    private void loadTask() {
        currentTask = storage.findById(taskId);
        if (currentTask == null) {
            Toast.makeText(this, "משימה לא נמצאה", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        displayTaskDetails();
    }

    private void displayTaskDetails() {
        tvDetailTitle.setText(currentTask.getTitle());
        tvDetailType.setText("סוג: " + currentTask.getTypeName());
        tvDetailSubject.setText("מקצוע: " + currentTask.getSubject());
        tvDetailPriority.setText("עדיפות: " + currentTask.getPriority());
        tvDetailDueDate.setText("תאריך הגשה: " + currentTask.getDueDate());

        if (currentTask instanceof HomeworkTask) {
            tvDetailAmount.setText("תרגילים: " + ((HomeworkTask) currentTask).getExercises());
        } else if (currentTask instanceof ExamTask) {
            tvDetailAmount.setText("נושאים: " + ((ExamTask) currentTask).getTopics());
        }

        if (currentTask.isDone()) {
            tvDetailStatus.setText("סטטוס: בוצע");
            btnToggleDone.setText("בטל סימון");
        } else {
            tvDetailStatus.setText("סטטוס: טרם בוצע");
            btnToggleDone.setText("סמן כבוצע");
        }

        tvDetailPoints.setText("שווה " + currentTask.getPoints() + " נקודות");
    }
}
