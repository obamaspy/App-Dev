//package com.example.myapplication;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.DatePickerDialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Spinner;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.myapplication.database.BudgetDB;
//
//import java.text.DecimalFormat;
//import java.text.NumberFormat;
//import java.util.Calendar;
//import java.util.Locale;
//
//public class EditBudget2 extends AppCompatActivity {
//
//    EditText edtAmount2, edtDate2;
//    Spinner spinnerCategory2;
//    Button btnConfirm2, btnHuy2;
//    Calendar calendar;
//    double currentBudget;
//    BudgetDB budgetDB;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_edit_budget2);
//
//        edtAmount2 = findViewById(R.id.edtAmount2);
//        edtDate2 = findViewById(R.id.edtDate2);
//        spinnerCategory2 = findViewById(R.id.spinnerCategory2);
//        btnConfirm2 = findViewById(R.id.btnConfirm2);
//        btnHuy2 = findViewById(R.id.btnHuy2);
//
//        budgetDB = new BudgetDB(this);
//        currentBudget = getIntent().getDoubleExtra("CURRENT_BUDGET", 0);
//
//        String[] categories = {"Food", "Electric", "Water", "Housing", "Shopping", "Gas", "Tuition"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
//        spinnerCategory2.setAdapter(adapter);
//
//        btnConfirm2.setOnClickListener(v -> handleConfirm());
//        btnHuy2.setOnClickListener(v -> finish());
//
//        edtDate2.setOnClickListener(v -> showDatePicker());
//    }
//
//    private void handleConfirm() {
//        String amountStr = edtAmount2.getText().toString().trim();
//        String category = spinnerCategory2.getSelectedItem().toString();
//        String selectedDate = edtDate2.getText().toString().trim();
//
//        if (amountStr.isEmpty() || selectedDate.isEmpty()) {
//            Toast.makeText(this, "Please enter an amount and select a date", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        try {
//            double amount = Double.parseDouble(amountStr);
//
//            if (amount > currentBudget) {
//                Toast.makeText(this, "The amount you want to spend is over your budget.", Toast.LENGTH_LONG).show();
//                return;
//            }
//
//            if (currentBudget <= 20) {
//                showLowBudgetWarning(amount, category, selectedDate);
//            } else {
//                confirmExpense(amount, category, selectedDate);
//            }
//
//        } catch (NumberFormatException e) {
//            Toast.makeText(this, "Invalid amount entered", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void showLowBudgetWarning(double amount, String category, String date) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Low Budget Warning")
//                .setMessage("You are running low on budget, are you sure you will use this money wisely?")
//                .setPositiveButton("Yes", (dialog, which) -> confirmExpense(amount, category, date))
//                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
//                .show();
//    }
//
//    private void confirmExpense(double amount, String category, String date) {
//        String formattedAmount = formatCurrency(amount);
//        long insertId = saveToDatabase(category, (int) amount, date);
//
//        if (insertId != -1) {
//            Toast.makeText(this, "Budget saved successfully!", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "Failed to save budget.", Toast.LENGTH_SHORT).show();
//        }
//
//        Intent intent = new Intent();
//        intent.putExtra("BUDGET_AMOUNT", formattedAmount);
//        intent.putExtra("BUDGET_CATEGORY", category);
//        setResult(Activity.RESULT_OK, intent);
//        finish();
//    }
//
//    private long saveToDatabase(String category, int amount, String date) {
//        return budgetDB.insertBudget(category, amount, date);
//    }
//
//    private String formatCurrency(double amount) {
//        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.GERMANY);
//        formatter.applyPattern("#,###");
//        return formatter.format(amount);
//    }
//
//    private void showDatePicker() {
//        calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
//            String selectedDate = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
//            edtDate2.setText(selectedDate);
//        }, year, month, day);
//        datePickerDialog.show();
//    }
//}
