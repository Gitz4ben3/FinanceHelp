package com.example.financehelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BankStatementAnalyzer extends AppCompatActivity {

    String CHANNEL_ID = "finance_notification_channel";
    int NOTIFICATION_ID = 123;

    private EditText description;
    private DatePicker calendarView;
    private Button autoBill;
    RadioButton repeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_statement_analyzer);

        createNotificationChannel();
        description = findViewById(R.id.desc);
        calendarView = findViewById(R.id.calenderView);
        autoBill = findViewById(R.id.autoBill);
        repeat = findViewById(R.id.repeat);
        autoBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int year = calendarView.getYear();
                int month = calendarView.getMonth();
                int day = calendarView.getDayOfMonth();

                // Create a Calendar instance and set it to the selected date
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, day);

                // Format the selected date (optional)
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String formattedDate = sdf.format(selectedCalendar.getTime());
                //Toast.makeText(getApplicationContext(), formattedDate, Toast.LENGTH_SHORT).show();
                String desc = description.getText().toString().trim();
                if(desc.isEmpty()){
                    description.requestFocus();
                    description.setError("Reminder description can't be empty");
                }
                else{
                    boolean flag = false;
                    if (repeat.isChecked()){
                        flag = true;
                    }
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    String billId = ref.child("Bill").push().getKey();
                    BillReminder bill = new BillReminder(billId, desc, day, flag);
                    String todaysDate = getTodaysDate();
                    if (formattedDate.equals(todaysDate)) {
                        sendNotification(desc);
                    }
                    ref.child("Bill").child(billId).setValue(bill).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Bill reminder added successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), Home.class));
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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = " ";
            String description = "";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(String title) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.alert);
        builder.setContentTitle("Reminder");
        builder.setContentText("Please be informed that " + title + " is due for payment");
        builder.setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent inten = new Intent(getApplicationContext(), Home.class);
        inten.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
    public void makeNotification(String channelID) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.alert);
        builder.setContentTitle("Payment Reminder");
        builder.setContentText("Today is the due date for your bill.");
        builder.setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(getApplicationContext(), Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
        //builder.setContentTitle(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel= notificationManager.getNotificationChannel(channelID);
            if (notificationChannel == null){
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Some description", importance);
                channel.setLightColor(Color.BLUE);
                channel.enableVibration(true);
                notificationManager.createNotificationChannel(channel);
            }


        }
        notificationManager.notify(0, builder.build());
    }
    public static String getTodaysDate() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.format(new Date()); // Find today's date
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
