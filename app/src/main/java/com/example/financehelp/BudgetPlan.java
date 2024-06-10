package com.example.financehelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BudgetPlan extends AppCompatActivity {
    EditText netIncome,numDependents;
    double net, needs, entertainment, savings;
    Button submit;
    FirebaseAuth mAuth;
    DatabaseReference mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_plan);
        netIncome = findViewById(R.id.netIncome);
        submit = findViewById(R.id.submit);
        numDependents = findViewById(R.id.numDependents);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                net = Double.parseDouble(netIncome.getText().toString().trim());
                if (Double.toString(net).isEmpty()){
                    netIncome.requestFocus();
                    netIncome.setError("Net income cannot be left empty");
                }else if (numDependents.getText().toString().isEmpty()) {
                    numDependents.requestFocus();
                    numDependents.setError("Field cannot be left empty");
                } else if(net <= 0){
                    netIncome.requestFocus();
                    netIncome.setError("Net income cannot be less than 0");
                }
                else{
                    needs = 0.00;
                    entertainment = 0.00;
                    savings = 0.00;


                    mAuth = FirebaseAuth.getInstance();
                    mRef = FirebaseDatabase.getInstance().getReference();
                    String budgetId = mRef.child("Budget").push().getKey(); // Generating unique budget ID
                    String userId = mAuth.getUid();
                    int numDep = Integer.parseInt(numDependents.getText().toString().trim());
                    Budget budget = new Budget(budgetId,net, needs, entertainment, savings, userId, numDep);
                    mRef.child("Budget").child(budgetId).setValue(budget).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Budget plan created", Toast.LENGTH_SHORT).show();
                                netIncome.getText().clear();
                                numDependents.getText().clear();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Network error, Check internet connection", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }
}
