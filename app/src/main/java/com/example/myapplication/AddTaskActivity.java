package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddTaskActivity extends AppCompatActivity {

    private Spinner spType, spSubject, spPriority;
    private EditText etTitle, etDueDate, etAmount;
    private TextView tvAmountLabel;
    private Button btnSave, btnCancel;
    private TaskStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        spType = findViewById(R.id.spType);
        spSubject = findViewById(R.id.spSubject);
        spPriority = findViewById(R.id.spPriority);
        etTitle = findViewById(R.id.etTitle);
        etDueDate = findViewById(R.id.etDueDate);
        etAmount = findViewById(R.id.etAmount);
        tvAmountLabel = findViewById(R.id.tvAmountLabel);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        storage = new TaskStorage(this);

        setupSpinners();

        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveTask());
    }

    private void setupSpinners() {
        String[] types = {"שיעורי בית", "מבחן"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(typeAdapter);

        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    tvAmountLabel.setText("מספר תרגילים:");
                    etAmount.setHint("למשל 8");
                } else {
                    tvAmountLabel.setText("מספר נושאים:");
                    etAmount.setHint("למשל 3");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        String[] subjects = {"מתמטיקה", "אנגלית", "מדעי המחשב", "פיזיקה", "היסטוריה", "תנ\"ך", "ספרות", "אחר"};
        ArrayAdapter<String> subAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjects);
        subAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSubject.setAdapter(subAdapter);

        String[] priorities = {"נמוכה", "בינונית", "גבוהה"};
        ArrayAdapter<String> prioAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priorities);
        prioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPriority.setAdapter(prioAdapter);
    }

    private void saveTask() {
        String title = etTitle.getText().toString().trim();
        String dueDate = etDueDate.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();

        if (title.isEmpty() || dueDate.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(amountStr);
            if (amount <= 0) {
                Toast.makeText(this, "הכמות חייבת להיות מספר חיובי", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "אנא הזן מספר תקין", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidDueDate(dueDate)) {
            Toast.makeText(this, "תאריך ההגשה חייב להיות בפורמט יום/חודש, למשל 12/10", Toast.LENGTH_SHORT).show();
            return;
        }

        String type = spType.getSelectedItem().toString();
        String subject = spSubject.getSelectedItem().toString();
        String priority = spPriority.getSelectedItem().toString();
        int newId = storage.nextId();

        Task newTask;
        if (type.equals("שיעורי בית")) {
            newTask = new HomeworkTask(newId, title, subject, priority, dueDate, amount);
        } else {
            newTask = new ExamTask(newId, title, subject, priority, dueDate, amount);
        }

        storage.addTask(newTask);
        Toast.makeText(this, "המשימה נשמרה בהצלחה", Toast.LENGTH_SHORT).show();
        finish();

    }
    private boolean isValidDueDate(String date) {
        String[] parts = date.split("/");
        if (parts.length != 2) {
            return false;
        }
        try {
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            return day >= 1 && day <= 31 && month >= 1 && month <= 12;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
