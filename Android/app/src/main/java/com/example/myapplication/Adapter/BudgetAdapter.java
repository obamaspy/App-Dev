package com.example.myapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.BudgetModel;
import java.util.List;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {
    private List<BudgetModel> budgetList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(BudgetModel budget);
        void onDeleteClick(BudgetModel budget);
    }

    public BudgetAdapter(List<BudgetModel> budgetList, OnItemClickListener listener) {
        this.budgetList = budgetList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_budget, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        BudgetModel budget = budgetList.get(position);
        holder.tvCategory.setText(budget.getName());
        holder.tvAmount.setText(String.valueOf(budget.getMoney()));
        holder.tvDate.setText(budget.getDate());

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(budget);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(budget);
            }
        });
    }

    @Override
    public int getItemCount() {
        return budgetList.size();
    }

    public static class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvAmount, tvDate;
        ImageButton btnEdit, btnDelete;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.BudgetCategory);
            tvAmount = itemView.findViewById(R.id.BudgetAmount);
            tvDate = itemView.findViewById(R.id.BudgetDate);
            btnEdit = itemView.findViewById(R.id.btnEditB);
            btnDelete = itemView.findViewById(R.id.btnDeleteB);
        }
    }
}
