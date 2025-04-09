package com.example.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.Adapter.BudgetAdapter;
import com.example.myapplication.database.BudgetDB;
import com.example.myapplication.model.BudgetModel;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BudgetFragment extends Fragment {

    TextView tvBudgetAmount;
    Button btnAddBudget, btnDeleteBudget;
    RecyclerView recyclerBudget;
    BudgetAdapter budgetAdapter;
    List<BudgetModel> budgetList;
    BudgetDB budgetDB;
    private double totalBudget = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        tvBudgetAmount = view.findViewById(R.id.tvBudgetAmount);
        btnAddBudget = view.findViewById(R.id.btnAddBudget);
        btnDeleteBudget = view.findViewById(R.id.btnDeleteBudget);
        recyclerBudget = view.findViewById(R.id.recyclerBudget);

        budgetDB = new BudgetDB(requireContext());
        budgetList = new ArrayList<>();

        budgetAdapter = new BudgetAdapter(budgetList, new BudgetAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(BudgetModel budget) {
                showEditDialog(budget);
            }

            @Override
            public void onDeleteClick(BudgetModel budget) {
                budgetDB.deleteBudget(budget.getId());
                totalBudget -= budget.getMoney();
                saveBudgetToPrefs();
                updateBudgetDisplay();
                loadBudgets();
                Toast.makeText(getContext(), "Budget deleted.", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerBudget.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerBudget.setAdapter(budgetAdapter);

        loadBudgets();

        btnAddBudget.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), EditBudget.class);
            startActivityForResult(intent, 1);
        });

        btnDeleteBudget.setOnClickListener(v -> {
            budgetDB.clearAllBudgets();
            budgetList.clear();
            budgetAdapter.notifyDataSetChanged();
            totalBudget = 0;
            saveBudgetToPrefs();
            updateBudgetDisplay();
            Toast.makeText(getContext(), "All budgets deleted.", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void showEditDialog(BudgetModel budget) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_budget, null);
        Spinner spinnerCategory = dialogView.findViewById(R.id.spinnerCategory);
        EditText edtAmount = dialogView.findViewById(R.id.edtEditAmount);
        EditText edtDate = dialogView.findViewById(R.id.edtDate);
        ImageView imgCalendar = dialogView.findViewById(R.id.imgCalendar);

        // Thiết lập dữ liệu cho Spinner
        String[] categories = {"Family support", "Scholarship", "Overtime pay"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Chọn vị trí danh mục hiện tại
        int selectedIndex = 0;
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equalsIgnoreCase(budget.getName())) {
                selectedIndex = i;
                break;
            }
        }
        spinnerCategory.setSelection(selectedIndex);

        edtAmount.setText(String.valueOf(budget.getMoney()));
        edtAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edtDate.setText(budget.getDate());

        // Hiển thị DatePicker khi nhấn vào EditText hoặc biểu tượng lịch
        View.OnClickListener dateClickListener = v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, year, month, dayOfMonth) -> {
                        String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        edtDate.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        };

        edtDate.setOnClickListener(dateClickListener);
        imgCalendar.setOnClickListener(dateClickListener);

        new AlertDialog.Builder(getContext())
                .setTitle("Edit Budget")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newCategory = spinnerCategory.getSelectedItem().toString();
                    String newDate = edtDate.getText().toString();
                    double newAmount = Double.parseDouble(edtAmount.getText().toString());

                    double oldAmount = budget.getMoney();
                    double diff = newAmount - oldAmount;

                    budget.setName(newCategory);
                    budget.setMoney((int) newAmount);
                    budget.setDate(newDate);

                    budgetDB.updateBudget(budget);
                    totalBudget += diff;
                    saveBudgetToPrefs();
                    updateBudgetDisplay();
                    loadBudgets();

                    Toast.makeText(getContext(), "Budget updated.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadBudgets() {
        budgetList.clear();
        budgetList.addAll(budgetDB.getAllBudgets());

        double totalBudgetFromDB = 0;
        for (BudgetModel budget : budgetList) {
            totalBudgetFromDB += budget.getMoney();
        }

        double totalExpenses = budgetDB.getTotalExpenses();
        totalBudget = totalBudgetFromDB - totalExpenses;

        saveBudgetToPrefs();
        updateBudgetDisplay();
        budgetAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBudgets();
    }

    private void updateBudgetDisplay() {
        tvBudgetAmount.setText(formatCurrency(totalBudget) + " $");
    }

    private String formatCurrency(double amount) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.GERMANY);
        formatter.applyPattern("#,###");
        return formatter.format(amount);
    }

    private void saveBudgetToPrefs() {
        SharedPreferences prefs = requireContext().getSharedPreferences("BudgetPrefs", Context.MODE_PRIVATE);
        prefs.edit().putFloat("totalBudget", (float) totalBudget).apply();
    }
}
