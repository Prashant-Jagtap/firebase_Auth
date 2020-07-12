package com.example.firebase_auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth;
    EditText edittext_email,edittext_password;
    ProgressBar progressBar;
    TextView forgot_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        edittext_email = (EditText) findViewById(R.id.username_login);
        edittext_password = (EditText) findViewById(R.id.password_login);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

    forgot_password = findViewById(R.id.forgot);

        findViewById(R.id.textView_l).setOnClickListener(this);
        findViewById(R.id.button_login).setOnClickListener(this);

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText resetmail = new EditText(view.getContext());
                AlertDialog.Builder passwordResetdailogue = new AlertDialog.Builder(view.getContext());
                passwordResetdailogue.setTitle("Reset Password");
                passwordResetdailogue.setMessage("Enter your email to recieve reset Link :");
                passwordResetdailogue.setView(resetmail);

                passwordResetdailogue.setPositiveButton("Send" , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //to reset the password
                        //mAuth.sendPasswordResetEmail(resetmail.getText().toString());
                        String mail = resetmail.getText().toString();
                        mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(),"Reset link is send to mail",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"Mail not sent" + e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

                passwordResetdailogue.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //nothing to provide as clivking this will go to same page
                    }
                });

                passwordResetdailogue.create().show();


            }
        });


    }
    private void userlogin()
    {
        String email = edittext_email.getText().toString().trim();
        String password = edittext_password.getText().toString().trim();

        if(email.isEmpty()){
            edittext_email.setError("Email is required");
            edittext_email.requestFocus();
            return;
        }

        //android.util.Pattern.EMAIL_ADDRESS
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            edittext_email.setError("Enter valid Email");
            edittext_email.requestFocus();
            return;

        }


        if(password.isEmpty())
        {
            edittext_password.setError("Password is required");
            edittext_password.requestFocus();
            return;
        }

        if (password.length() <6)
        {
            edittext_password.setError("Minimum length of password is 6");
            edittext_password.requestFocus();
            return;

        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful())
                {//if login successfull

                    finish();
      Intent intent = new Intent(getApplicationContext(),dashboard.class);
                        // also be written :- Intent intent = new Intent(MainActivity.this,dashboard.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //clear all activity on top of stack
                    //if not done the user will again come back to login after pressing back button
                    startActivity(intent);

                }
                else
                {
                    //error in login
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();


                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser()!=null)
        {
            finish();
            startActivity(new Intent(this,dashboard.class));

        }
    }



    @Override
    public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.textView_l:
                    finish();
                        startActivity(new Intent(this,MainActivity2.class));

                        break;

                case R.id.button_login:
                        userlogin();
                    break;

            }
    }
}
