package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import java.util.List;
import java.util.Locale;

public class BudgetFragment extends Fragment {

    TextView tvBudgetAmount;
    Button btnAddBudget, btnDeleteBudget, btnUpdate;
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
        btnUpdate = view.findViewById(R.id.btnUpdate);
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

        btnUpdate.setOnClickListener(v -> {
            loadBudgets();
            Toast.makeText(getContext(), "Budget updated.", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void showEditDialog(BudgetModel budget) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_budget, null);
        EditText edtCategory = dialogView.findViewById(R.id.edtEditCategory);
        EditText edtAmount = dialogView.findViewById(R.id.edtEditAmount);
        EditText edtDate = dialogView.findViewById(R.id.edtEditDate);

        edtCategory.setText(budget.getName());
        edtAmount.setText(String.valueOf(budget.getMoney()));
        edtAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edtDate.setText(budget.getDate());

        new AlertDialog.Builder(getContext())
                .setTitle("Edit Budget")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newCategory = edtCategory.getText().toString();
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
