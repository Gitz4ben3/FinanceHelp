package com.example.financehelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BudgetDisplay extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BudgetAdapter adapter;
    private List<Budget> budgetList;
    private DatabaseReference mRef;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_display);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        budgetList = new ArrayList<>();
        adapter = new BudgetAdapter(this, budgetList);
        recyclerView.setAdapter(adapter);

        mRef = FirebaseDatabase.getInstance().getReference().child("Budget");
        String budgetId = mRef.getKey();
        auth = FirebaseAuth.getInstance();
        // Attach a listener to retrieve data from Firebase
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                budgetList.clear();
                for (DataSnapshot budgetSnapshot : dataSnapshot.getChildren()) {
                    Budget budget = budgetSnapshot.getValue(Budget.class);
                    if (budget != null && budget.getUserId() != null && budget.getUserId().equals(auth.getUid())) {
                        budgetList.add(budget);
                    }
                }
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }
}
