package com.example.financehelp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
    private Context context;
    private List<Budget> budgetList;

    public ReportAdapter(Context context, List<Budget> budgetList) {
        this.context = context;
        this.budgetList = budgetList;
    }

    @NonNull
    @Override
    public ReportAdapter.ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.report_item, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportAdapter.ReportViewHolder holder, int position) {
        Budget budget = budgetList.get(position);
        holder.budgetAmount.setText("Budget: R" + budget.getNetIncome());

        // Pass the budgetId along with the Budget object
        holder.extract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GenerateReport) context).generatePDFReport(budget.getBudgetId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return budgetList.size();
    }

    public class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView budgetAmount;
        Button extract;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            budgetAmount = itemView.findViewById(R.id.budgetAmount);
            extract = itemView.findViewById(R.id.extractReport);
        }
    }
}

