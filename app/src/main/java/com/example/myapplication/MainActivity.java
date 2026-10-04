package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView tvHello, tvStats, tvEmpty;
    private Spinner spFilter;
    private ListView lvTasks;
    private Button btnAdd, btnLogout;

    private TaskStorage storage;
    private ArrayList<Task> allTasks = new ArrayList<>();
    private ArrayList<Task> filteredTasks = new ArrayList<>();
    private String currentFilter = "הכל";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvHello = findViewById(R.id.tvHello);
        tvStats = findViewById(R.id.tvStats);
        tvEmpty = findViewById(R.id.tvEmpty);
        spFilter = findViewById(R.id.spFilter);
        lvTasks = findViewById(R.id.lvTasks);
        btnAdd = findViewById(R.id.btnAdd);
        btnLogout = findViewById(R.id.btnLogout);

        storage = new TaskStorage(this);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userName = prefs.getString("user_name", "תלמיד");
        tvHello.setText("שלום " + userName + "!");

        setupFilterSpinner();

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        lvTasks.setOnItemClickListener((parent, view, position, id) -> {
            Task selectedTask = filteredTasks.get(position);
            Intent intent = new Intent(MainActivity.this, TaskDetailActivity.class);
            intent.putExtra("TASK_ID", selectedTask.getId());
            startActivity(intent);
        });

        lvTasks.setOnItemLongClickListener((parent, view, position, id) -> {
            Task selectedTask = filteredTasks.get(position);
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("מחיקת משימה")
                    .setMessage("האם למחוק את המשימה '" + selectedTask.getTitle() + "'?")
                    .setPositiveButton("מחק", (dialog, which) -> {
                        storage.deleteById(selectedTask.getId());
                        Toast.makeText(MainActivity.this, "המשימה נמחקה", Toast.LENGTH_SHORT).show();
                        refreshData();
                    })
                    .setNegativeButton("ביטול", null)
                    .show();
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    private void setupFilterSpinner() {
        String[] filters = {"הכל", "מתמטיקה", "אנגלית", "מדעי המחשב", "פיזיקה", "היסטוריה", "תנ\"ך", "ספרות", "אחר"};
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filters);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFilter.setAdapter(filterAdapter);

        spFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentFilter = filters[position];
                applyFilterAndRefreshUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void refreshData() {
        allTasks = storage.loadAll();
        applyFilterAndRefreshUI();
    }

    private void applyFilterAndRefreshUI() {
        filteredTasks = new ArrayList<>();
        int totalCount = allTasks.size();
        int completedCount = 0;
        int totalPoints = 0;

        for (Task t : allTasks) {
            if (t.isDone()) {
                completedCount++;
                totalPoints += t.getPoints();
            }

            if (currentFilter.equals("הכל") || t.getSubject().equalsIgnoreCase(currentFilter)) {
                filteredTasks.add(t);
            }
        }

        tvStats.setText("משימות: " + totalCount + " | הושלמו: " + completedCount + " | נקודות: " + totalPoints);

        if (filteredTasks.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            lvTasks.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            lvTasks.setVisibility(View.VISIBLE);
            TaskAdapter adapter = new TaskAdapter(this, filteredTasks);
            lvTasks.setAdapter(adapter);
        }
    }
}
