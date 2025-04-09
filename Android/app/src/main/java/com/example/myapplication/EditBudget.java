package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.database.BudgetDB;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditBudget extends AppCompatActivity {
    private EditText edtAmount, edtDate;
    private Spinner spinnerCategory;
    private Button btnConfirm, btnCancel;
    private BudgetDB budgetDB;
    private int budgetId = -1; // -1: Thêm mới, khác -1: Chỉnh sửa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget);

        initViews();
        setupSpinner();

        // Lấy ID từ Intent nếu có (chỉnh sửa)
        if (getIntent().hasExtra("BUDGET_ID")) {
            budgetId = getIntent().getIntExtra("BUDGET_ID", -1);
            loadBudgetData(budgetId);
        }

        edtDate.setOnClickListener(v -> showDatePicker());
        btnConfirm.setOnClickListener(v -> saveBudget());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void initViews() {
        edtAmount = findViewById(R.id.edtAmount);
        edtDate = findViewById(R.id.edtDate);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnCancel = findViewById(R.id.btnHuy);
        budgetDB = new BudgetDB(this);
    }

    private void setupSpinner() {
        String[] categories = {"Family support", "Scholarship", "Overtime pay"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            edtDate.setText(sdf.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void loadBudgetData(int id) {
        SQLiteDatabase db = budgetDB.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name, money, date FROM budgets WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            edtAmount.setText(cursor.getString(1));
            edtDate.setText(cursor.getString(2));

            // Chọn giá trị Spinner
            String category = cursor.getString(0);
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerCategory.getAdapter();
            int position = adapter.getPosition(category);
            spinnerCategory.setSelection(position);
        }
        cursor.close();
        db.close();
    }

    private void saveBudget() {
        String amountStr = edtAmount.getText().toString().trim();
        String date = edtDate.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        if (amountStr.isEmpty() || date.isEmpty()) {
            showToast("Please fill in all fields!");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(amountStr);
        } catch (NumberFormatException e) {
            showToast("Invalid amount!");
            return;
        }

        SQLiteDatabase db = budgetDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", category);
        values.put("money", amount);
        values.put("date", date);

        if (budgetId == -1) {
            long result = db.insert("budgets", null, values);
            showToast(result != -1 ? "Budget saved successfully!" : "Failed to save budget!");
        } else {
            int rows = db.update("budgets", values, "id = ?", new String[]{String.valueOf(budgetId)});
            showToast(rows > 0 ? "Budget updated successfully!" : "Failed to update budget!");
        }
        db.close();
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}