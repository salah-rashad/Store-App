package com.bemo.store;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TextInputEditText email, password, passwordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        email = (TextInputEditText)findViewById(R.id.email_edit_text);
        password = (TextInputEditText)findViewById(R.id.password_edit_text);
        passwordConfirm = (TextInputEditText)findViewById(R.id.password_confirm_edit_text);

        TextView loginTextView = (TextView)findViewById(R.id.login_text_view);
        Button loginButton = (Button)findViewById(R.id.register_button);

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerActivity = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(registerActivity);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent mainActivity = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(mainActivity);
            finish();
        }
    }

    public void registerUser() {
        String emailText, passwordText, passwordConfirmText;
        int productAvailability;

        emailText = email.getText().toString();
        passwordText = password.getText().toString();
        passwordConfirmText = passwordConfirm.getText().toString();

        ///////////////////////////

        if (emailText.isEmpty() || emailText.equals(" ")) {
            email.setError("Please fill this input!");
            return;
        }
        if (passwordText.isEmpty() || passwordText.equals(" ")) {
            password.setError("Please fill this input!");
            return;
        }
        if (passwordText.length() < 8) {
            password.setError("Password must be 8 or greater characters.");
            return;
        }
        if (!passwordConfirmText.equals(passwordText)) {
            passwordConfirm.setError("Password not match!");
            return;
        }

        mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent mainActivity = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(mainActivity);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Couldn't register, please try again later!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
