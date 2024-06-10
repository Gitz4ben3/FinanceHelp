package com.example.financehelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Create_Account extends AppCompatActivity {
    FirebaseAuth auth;
    private Button submit;
    private EditText name, surname, phone, email, pswd, c_pswd;
    private String firstname, lastname, contact, em, pass, c_pass;
    DatabaseReference dataref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        submit = findViewById(R.id.submit);
        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        phone = findViewById(R.id.Phone);
        email = findViewById(R.id.email);
        pswd = findViewById(R.id.password);
        c_pswd = findViewById(R.id.c_password);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstname = name.getText().toString().trim();
                lastname = surname.getText().toString().trim();
                contact = phone.getText().toString().trim();
                em = email.getText().toString().trim();
                pass = pswd.getText().toString().trim();
                c_pass = c_pswd.getText().toString().trim();

                if (firstname.isEmpty()){
                    name.requestFocus();
                    name.setError("Please input your first name");
                }
                else if (lastname.isEmpty()){
                    surname.requestFocus();
                    surname.setError("Please input your last name");
                }
                else if (contact.isEmpty()){
                    phone.requestFocus();
                    phone.setError("Please input your phone number");
                }
                else if (em.isEmpty()){
                    email.requestFocus();
                    email.setError("Please input your email address");
                }
                else if (pass.isEmpty()){
                    pswd.requestFocus();
                    pswd.setError("Please input your password");
                }
                else if (c_pass.isEmpty()){
                    c_pswd.requestFocus();
                    c_pswd.setError("Please confirm your password");
                }
                else{
                    if(!c_pass.equals(pass)){
                        c_pswd.requestFocus();
                        c_pswd.setError("Password do not match");
                    }
                    else{
                        auth = FirebaseAuth.getInstance();
                        auth.createUserWithEmailAndPassword(em, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                dataref = FirebaseDatabase.getInstance().getReference();
                                Registration profile = new Registration(firstname, lastname, contact, em);
                                String userId = auth.getUid();
                                if (userId != null){
                                    dataref.child("Users").child(userId).setValue(profile);
                                    Toast.makeText(getApplicationContext(), "User successfully registered.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), Login.class));
                                    finish();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "User registration failed.", Toast.LENGTH_SHORT).show();
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
            }
        });

    }
}