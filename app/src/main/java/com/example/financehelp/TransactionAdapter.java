package com.example.financehelp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private Context context;
    private List<Transaction> transactionList;

    public TransactionAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.transaction_item, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.amountTextView.setText(String.valueOf(transaction.getAmount()));
        holder.categoryTextView.setText(transaction.getCategory());
        holder.timestampTextView.setText(transaction.getDate());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView amountTextView, categoryTextView, timestampTextView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
        }
    }
}

