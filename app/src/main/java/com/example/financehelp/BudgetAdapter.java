package com.example.financehelp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {
    private Context context;
    private List<Budget> budgetList;

    public BudgetAdapter(Context context, List<Budget> budgetList) {
        this.context = context;
        this.budgetList = budgetList;
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.budget_item, parent, false);
        return new BudgetViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        Budget budget = budgetList.get(position);
        holder.budgetAmt.setText("Budget Amount: R" + budget.getNetIncome());
    }

    @Override
    public int getItemCount() {
        return budgetList != null ? budgetList.size() : 0;
    }

    public class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView budgetAmt;
        CardView cardView;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            budgetAmt = itemView.findViewById(R.id.budgetAmt);
            cardView = itemView.findViewById(R.id.cardView);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Retrieve the clicked budget
                    Budget budget = budgetList.get(getAdapterPosition());
                    String budgetId = budget.getBudgetId();

                    // Create intent and pass budgetId to the next activity
                    Intent intent = new Intent(context, TrackTransactions.class);
                    intent.putExtra("budgetId", budgetId);
                    context.startActivity(intent);
                }
            });
        }
    }

}
