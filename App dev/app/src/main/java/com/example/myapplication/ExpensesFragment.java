package com.example.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.Adapter.ExpenseAdapter;
import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.databinding.FragmentExpensesBinding;
import com.example.myapplication.model.Expense;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExpensesFragment extends Fragment {
    private FragmentExpensesBinding binding;
    private DatabaseHelper dbHelper;
    private ExpenseAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentExpensesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        dbHelper = new DatabaseHelper(requireContext());
        binding.recyclerViewExpenses.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Setup Spinner with English options directly in code
        List<String> descriptionOptions = new ArrayList<>();
        descriptionOptions.add("Food");
        descriptionOptions.add("Shopping");
        descriptionOptions.add("Utilities");
        descriptionOptions.add("Living");
        descriptionOptions.add("Transport");
        descriptionOptions.add("Others");

        ArrayAdapter<String> adapterDesc = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                descriptionOptions
        );
        adapterDesc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerDescription.setAdapter(adapterDesc);

        // Setup Date Picker
        binding.edtDate.setOnClickListener(v -> showDatePicker());

        loadExpenses();

        binding.btnAddExpense.setOnClickListener(v -> addExpense());
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String dateStr = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    binding.edtDate.setText(dateStr);
                },
                year, month, day
        );
        dialog.show();
    }

    private void addExpense() {
        String description = binding.spinnerDescription.getSelectedItem().toString();
        String amountStr = binding.edtAmount.getText().toString().trim();
        String date = binding.edtDate.getText().toString().trim();

        if (TextUtils.isEmpty(amountStr) || TextUtils.isEmpty(date)) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        SharedPreferences prefs = requireContext().getSharedPreferences("BudgetPrefs", Context.MODE_PRIVATE);
        double currentBudget = prefs.getFloat("totalBudget", 0);

        if (amount > currentBudget) {
            Toast.makeText(requireContext(), "The amount you spend is greater than the balance in your budget.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentBudget <= 15 && amount < 15) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Warning")
                    .setMessage("You are running low on budget, are you sure you can spend this money?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        processExpense(description, amount, date);
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        } else {
            processExpense(description, amount, date);
        }
    }

    private void processExpense(String description, double amount, String date) {
        SharedPreferences prefs = requireContext().getSharedPreferences("BudgetPrefs", Context.MODE_PRIVATE);
        double currentBudget = prefs.getFloat("totalBudget", 0);
        double newBudget = currentBudget - amount;

        prefs.edit().putFloat("totalBudget", (float) newBudget).apply();

        Expense expense = new Expense(0, description, amount, date);
        dbHelper.addExpense(expense);

        binding.spinnerDescription.setSelection(0);
        binding.edtAmount.setText("");
        binding.edtDate.setText("");

        loadExpenses();
    }

    private void loadExpenses() {
        List<Expense> expenseList = dbHelper.getAllExpenses();
        adapter = new ExpenseAdapter(expenseList, new ExpenseAdapter.OnItemActionListener() {
            @Override
            public void onEditClick(int position) {
                showEditDialog(expenseList.get(position));
            }

            @Override
            public void onDeleteClick(int position) {
                deleteExpense(expenseList.get(position));
            }
        });
        binding.recyclerViewExpenses.setAdapter(adapter);
    }

    private void deleteExpense(Expense expense) {
        SharedPreferences prefs = requireContext().getSharedPreferences("BudgetPrefs", Context.MODE_PRIVATE);
        double currentBudget = prefs.getFloat("totalBudget", 0);

        double newBudget = currentBudget + expense.getAmount();
        prefs.edit().putFloat("totalBudget", (float) newBudget).apply();

        dbHelper.deleteExpense(expense.getId());

        loadExpenses();

        Toast.makeText(requireContext(), "Expense deleted and budget updated.", Toast.LENGTH_SHORT).show();
    }

    private void showEditDialog(Expense expense) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Expense");
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_expense, null);
        EditText edtEditDescription = dialogView.findViewById(R.id.edtEditDescription);
        EditText edtEditAmount = dialogView.findViewById(R.id.edtEditAmount);
        EditText edtEditDate = dialogView.findViewById(R.id.edtEditDate);

        edtEditDescription.setText(expense.getDescription());
        edtEditAmount.setText(String.valueOf(expense.getAmount()));
        edtEditDate.setText(expense.getDate());

        edtEditDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        String selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        edtEditDate.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        builder.setView(dialogView);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newDescription = edtEditDescription.getText().toString().trim();
            String newAmountStr = edtEditAmount.getText().toString().trim();
            String newDate = edtEditDate.getText().toString().trim();
            if (TextUtils.isEmpty(newDescription) || TextUtils.isEmpty(newAmountStr) || TextUtils.isEmpty(newDate)) {
                Toast.makeText(requireContext(), "All fields required", Toast.LENGTH_SHORT).show();
                return;
            }
            double newAmount = Double.parseDouble(newAmountStr);
            double oldAmount = expense.getAmount();

            SharedPreferences prefs = requireContext().getSharedPreferences("BudgetPrefs", Context.MODE_PRIVATE);
            double currentBudget = prefs.getFloat("totalBudget", 0);

            double newBudget;
            if (newAmount < oldAmount) {
                double refundAmount = oldAmount - newAmount;
                newBudget = currentBudget + refundAmount;
            } else {
                double additionalExpense = newAmount - oldAmount;
                if (additionalExpense > currentBudget) {
                    Toast.makeText(requireContext(), "Not enough budget to increase expense.", Toast.LENGTH_SHORT).show();
                    return;
                }
                newBudget = currentBudget - additionalExpense;
            }

            prefs.edit().putFloat("totalBudget", (float) newBudget).apply();

            Expense updatedExpense = new Expense(expense.getId(), newDescription, newAmount, newDate);
            dbHelper.updateExpense(updatedExpense);

            loadExpenses();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
