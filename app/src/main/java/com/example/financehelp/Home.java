package com.example.financehelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Home extends AppCompatActivity {
    CardView createBudget, addTransactions, analyzeStatement, extractReport;

    @Override
    protected void onStart() {
        super.onStart();
        newUpdate();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        newUpdate();

        createBudget = findViewById(R.id.createBudget);
        addTransactions = findViewById(R.id.addTransaction);
        analyzeStatement = findViewById(R.id.autoBill);
        extractReport = findViewById(R.id.viewReport);

        createBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), BudgetPlan.class));
            }
        });
        addTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), BudgetDisplay.class));
            }
        });
        analyzeStatement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), BankStatementAnalyzer.class));
            }
        });
        extractReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), GenerateReport.class));
            }
        });
    }
    public void newUpdate(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        String td = getDay();
        ref.child("Bill").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Long newDay = dataSnapshot.child("day").getValue(Long.class); // Use Long.class here
                    if (newDay != null && newDay.toString().equals(td)) {
                        String description = dataSnapshot.child("description").getValue(String.class);
                        sendNotification(description);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static String getDay() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
            Date currentDate = new Date();
            return String.format("%02d", Integer.parseInt(dateFormat.format(currentDate)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private void sendNotification(String description) {
        // Create a notification manager
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel (required for Android 8.0 and higher)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "reminder_channel";
            CharSequence channelName = "Reminder Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription("Channel for reminders");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            notificationManager.createNotificationChannel(channel);
        }

        // Create the notification
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, "reminder_channel");
        } else {
            builder = new Notification.Builder(this);
        }

        builder.setContentTitle("Reminder")
                .setContentText(description)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setAutoCancel(true);

        // Generate a unique notification ID
        int notificationId = (int) System.currentTimeMillis();

        // Show the notification
        notificationManager.notify(notificationId, builder.build());
    }

}