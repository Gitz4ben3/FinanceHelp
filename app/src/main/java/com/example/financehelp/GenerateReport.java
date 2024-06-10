package com.example.financehelp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

//import org.w3c.dom.Document;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class GenerateReport extends AppCompatActivity {
    ReportAdapter adapter;
    DatabaseReference ref;
    RecyclerView recyclerView;
    List<Budget> budgetList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_report);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        budgetList = new ArrayList<>();
        adapter = new ReportAdapter(this, budgetList);
        recyclerView.setAdapter(adapter);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Budget").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                budgetList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Budget budget = dataSnapshot.getValue(Budget.class);
                    if (budget != null) {
                        budgetList.add(budget);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Budget", error.getMessage());
            }
        });
    }

    public void generatePDFReport(String budgetId) {
        try {
            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
            mRef.child("Budget").child(budgetId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        double netIncome = snapshot.child("netIncome").getValue(Double.class);
                        double needs = snapshot.child("needs").getValue(Double.class);
                        double entertainment = snapshot.child("entertainment").getValue(Double.class);
                        double savings = snapshot.child("savings").getValue(Double.class);
                        int numDependents =  snapshot.child("numDependents").getValue(Integer.class);

                        // Create a new Budget object and populate it with data from Firebase
                        Budget budget = new Budget();
                        budget.setNetIncome(netIncome);
                        budget.setNeeds(needs);
                        budget.setEntertainment(entertainment);
                        budget.setSavings(savings);
                        budget.setNumDependents(numDependents);

                        // Create a new PDF document
                        Document document = new Document();

                        // Get the internal storage directory for the app
                        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "budget_report.pdf");
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(file);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                        // Initialize PDF writer with the file output stream
                        try {
                            PdfWriter.getInstance(document, fos);
                        } catch (DocumentException e) {
                            throw new RuntimeException(e);
                        }

                        // Open the document
                        document.open();

                        // Add title to the PDF
                        try {
                            document.add(new Paragraph("Budget Report"));
                            document.add(new Paragraph("Net income for the budget: " + netIncome));
                            document.add(new Paragraph("Amount spent on necessities: " + needs));
                            document.add(new Paragraph("Amount spent on entertainment: " + entertainment));
                            document.add(new Paragraph("Amount spent on savings: " + savings));
                        } catch (DocumentException e) {
                            throw new RuntimeException(e);
                        }


                        // Add recommendations
                        int count = 1;
                        try {
                            document.add(new Paragraph(""));
                            document.add(new Paragraph("\n\nRecommendations based on your spending"));
                        } catch (DocumentException e) {
                            throw new RuntimeException(e);
                        }
                        if (entertainment > netIncome * 0.3) {
                            try {
                                document.add(new Paragraph(count + ". You've spent more than the recommended amount for entertainment"));
                                count++;
                                document.add(new Paragraph(count + ". The recommended spending percentage for entertainment is 30% of your net income"));
                                count++;
                            } catch (DocumentException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            try {
                                document.add(new Paragraph(count + ". You're spending wisely on entertainment, please keep it up"));
                                count++;
                            } catch (DocumentException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        if (savings > netIncome * 0.2) {
                            try {
                                document.add(new Paragraph(count + ". You're doing great with savings please keep it up"));
                                count++;
                            } catch (DocumentException e) {
                                throw new RuntimeException(e);
                            }

                        } else {
                            try {
                                document.add(new Paragraph(count + ". Please be adviced to spend atleast 20% of your net income per budget"));
                                count++;
                            } catch (DocumentException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if (savings+entertainment+needs > netIncome){
                            try {
                                document.add(new Paragraph(count+". Your total spendings exceed the set budget Amount"));
                                count++;
                            } catch (DocumentException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        //Must display All transactions
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                        // Construct the query to retrieve transactions with a specific budgetId
                        Query query = databaseReference.child("Transactions").orderByChild("budgetId").equalTo(budgetId);
// Attach a listener to retrieve the data
                        try {
                            document.add(new Paragraph("\nList of all the transactions for this budget\n\n"));
                            PdfPTable table = new PdfPTable(3); // 3 columns for Date, Description, and Transaction Amount

                            // Add headers to the table
                            table.addCell("Date");
                            table.addCell("Description");
                            table.addCell("Transaction Amount");

                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // Loop through the dataSnapshot to retrieve each transaction
                                    if (snapshot.exists()) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            Transaction transaction = snapshot.getValue(Transaction.class);

                                            // Extract transaction data
                                            String category = snapshot.child("category").getValue(String.class);
                                            String date = snapshot.child("date").getValue(String.class);
                                            String amount = Double.toString(snapshot.child("amount").getValue(Double.class));

                                            // Add transaction data to the table
                                            table.addCell(date);
                                            table.addCell(category);
                                            table.addCell(amount);
                                        }
                                        // Add the table to the document
                                        try {
                                            document.add(table);
                                        } catch (DocumentException e) {
                                            throw new RuntimeException(e);
                                        }
                                    } else {
                                        // If no transactions found, display a message
                                        try {
                                            document.add(new Paragraph("No transactions found"));
                                        } catch (DocumentException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                    // Close the document
                                    document.close();

                                    // Open the generated PDF using an intent
                                    Uri pdfUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".fileprovider", file);
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setDataAndType(pdfUri, "application/pdf");
                                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Handle any errors that may occur
                                    System.err.println("Database Error: " + databaseError.getMessage());
                                }
                            });
                        } catch (DocumentException e) {
                            throw new RuntimeException(e);
                        }

                        // Notify user that PDF generation is successful
                        Toast.makeText(getApplicationContext(), "Budget report generated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle case where budget data does not exist
                        Toast.makeText(getApplicationContext(), "Budget data does not exist", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("Budget", error.getMessage());
                    // Handle database error
                    Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error generating budget report: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}