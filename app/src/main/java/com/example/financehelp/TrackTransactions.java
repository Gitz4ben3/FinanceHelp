package com.example.financehelp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financehelp.Budget;
import com.example.financehelp.R;
import com.example.financehelp.Transaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TrackTransactions extends AppCompatActivity {
    Spinner budgetSpinner;
    EditText transactionAmount;
    Button submit;
    DatabaseReference mRef;
    FirebaseAuth auth;
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private List<Transaction> transactionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_transactions);

        budgetSpinner = findViewById(R.id.budgetSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.budget, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        budgetSpinner.setAdapter(adapter);

        transactionAmount = findViewById(R.id.transactionAmount);
        submit = findViewById(R.id.submit);

        String budgetId = getIntent().getStringExtra("budgetId");
        mRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String option = budgetSpinner.getSelectedItem().toString().trim();
                if (!transactionAmount.getText().toString().trim().isEmpty()){
                    if (budgetId != null){
                        mRef.child("Budget").child(budgetId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    Double netIncome = snapshot.child("netIncome").getValue(Double.class);
                                    Double needs = snapshot.child("needs").getValue(Double.class);
                                    Double ent = snapshot.child("entertainment").getValue(Double.class);
                                    Double savings = snapshot.child("savings").getValue(Double.class);
                                    String uId = snapshot.child("userId").getValue(String.class);
                                    String bId = snapshot.child("budgetId").getValue(String.class);
                                    Integer numDep = snapshot.child("numDependents").getValue(Integer.class);
                                    Double amount = Double.parseDouble(transactionAmount.getText().toString().trim());
                                    if (amount > 0){
                                        if (budgetId.equals(bId)){
                                            if (option.equalsIgnoreCase("Savings")){
                                                savings +=amount;
                                            } else if (option.equalsIgnoreCase("Entertainment")) {
                                                ent+=amount;
                                            }
                                            else if(option.equalsIgnoreCase("Necessities")){
                                                needs+=amount;
                                            }
                                            else{
                                                Toast.makeText(getApplicationContext(), "Cant retrieve the selected option", Toast.LENGTH_SHORT).show();
                                            }
                                            Budget budget = new Budget(bId,netIncome, needs, ent, savings, uId, numDep);
                                            mRef.child("Budget").child(budgetId).setValue(budget).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    String date = getCurrentTimeStamp();
                                                    Transaction transaction = new Transaction(option, amount, date, budgetId);
                                                    String transactionId = mRef.child("Transactions").push().getKey();
                                                    mRef.child("Transactions").child(transactionId).setValue(transaction).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                transactionAmount.getText().clear();
                                                                Toast.makeText(getApplicationContext(), "Transaction added successfully", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d("Transaction", "Transction fail");
                                                        }
                                                    });

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d("Budget", "Budget update failed");
                                                }
                                            });
                                        }
                                    }
                                    else{
                                        transactionAmount.requestFocus();
                                        transactionAmount.setError("Enter an amount greater than 0");
                                    }

                                }else{
                                    Log.d("snapshot", "Snapshot does not exist");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d("Budget", error.getMessage());
                            }
                        });
                    }
                    else{
                        Log.d("BudgetId", "budgetId is null");
                    }
                }
                else{
                    transactionAmount.requestFocus();
                    transactionAmount.setError("The transaction amount cannot be empty");
                }

            }
        });
    }
    public static String getCurrentTimeStamp() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dateFormat.format(new Date()); // Find today's date
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
