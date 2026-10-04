package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etName;
    private Button btnEnter, btnReset;
    private TextView tvWelcome;
    private SharedPreferences prefs;
    private boolean nameLocked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etName = findViewById(R.id.etName);
        btnEnter = findViewById(R.id.btnEnter);
        btnReset = findViewById(R.id.btnReset);
        tvWelcome = findViewById(R.id.tvWelcome);

        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String savedName = prefs.getString("user_name", null);
        if (savedName != null && !savedName.trim().isEmpty()) {
            tvWelcome.setText("ברוך שובך, " + savedName);
            etName.setText(savedName);
            setNameLocked(true);
        } else {
            tvWelcome.setText("מתכנן שיעורי בית");
            setNameLocked(false);
        }

        etName.setOnClickListener(v -> {
            if (nameLocked) {
                showLockedToast();
            }
        });

        btnEnter.setOnClickListener(v -> {
            String currentSavedName = prefs.getString("user_name", null);
            boolean hasSavedUser = currentSavedName != null && !currentSavedName.trim().isEmpty();

            if (hasSavedUser) {
                String typed = etName.getText().toString().trim();
                if (!typed.equals(currentSavedName)) {
                    etName.setText(currentSavedName);
                    showLockedToast();
                    return;
                }
                openMain();
                return;
            }

            String name = etName.getText().toString().trim();
            if (name.length() < 2) {
                Toast.makeText(LoginActivity.this, "השם חייב להיות לפחות 2 תווים", Toast.LENGTH_SHORT).show();
            } else {
                prefs.edit().putString("user_name", name).apply();
                openMain();
            }
        });

        btnReset.setOnClickListener(v -> {
            new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("איפוס נתונים")
                    .setMessage("האם אתה בטוח שברצונך למחוק את כל הנתונים והמשימות?")
                    .setPositiveButton("כן, מחק הכל", (dialog, which) -> {
                        prefs.edit().clear().apply();
                        TaskStorage storage = new TaskStorage(LoginActivity.this);
                        storage.clearAllData();
                        etName.setText("");
                        setNameLocked(false);
                        tvWelcome.setText("מתכנן שיעורי בית");
                        Toast.makeText(LoginActivity.this, "כל הנתונים אופסו בהצלחה", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("ביטול", null)
                    .show();
        });
    }

    private void openMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showLockedToast() {
        Toast.makeText(this,
                "אי אפשר להחליף משתמש עד שמוחקים את הנתונים השמורים",
                Toast.LENGTH_LONG).show();
    }

    private void setNameLocked(boolean locked) {
        nameLocked = locked;
        etName.setFocusable(!locked);
        etName.setFocusableInTouchMode(!locked);
        etName.setCursorVisible(!locked);
        if (locked) {
            etName.clearFocus();
        }
    }
}
