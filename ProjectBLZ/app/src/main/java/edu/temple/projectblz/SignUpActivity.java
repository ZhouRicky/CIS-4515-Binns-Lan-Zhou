package edu.temple.projectblz;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    SharedPrefs sharedPrefs;

    TextView statusTextView, cancelEditText;
    EditText firstNameEditText, lastNameEditText, usernameEditText, passwordEditText, confirmPasswordEditText;
    Button createAccountButton;

    String firstName, lastName, username, password, confirmPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        sharedPrefs = new SharedPrefs(this);

        viewInitialization();

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusTextView.setText("");

                if(firstNameEditText.getText() != null && lastNameEditText.getText() != null && usernameEditText.getText() != null
                        && passwordEditText.getText() != null && confirmPasswordEditText.getText() != null) {
                    firstName = firstNameEditText.getText().toString();
                    lastName = lastNameEditText.getText().toString();
                    username = usernameEditText.getText().toString();
                    password = passwordEditText.getText().toString();
                    confirmPassword = confirmPasswordEditText.getText().toString();

                    if(firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                        statusTextView.setText(Constant.ENTER_ALL_INFO);
                    } else {
                        if(password.equals(confirmPassword)) {
                            createAccount();
                        } else {
                            statusTextView.setText(Constant.PW_DO_NOT_MATCH);
                        }
                    }
                } else {
                    statusTextView.setText(Constant.ENTER_ALL_INFO);
                }
            }
        });

        cancelEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // initialize necessary views
    private void viewInitialization() {
        statusTextView = findViewById(R.id.statusTextView2);
        cancelEditText = findViewById(R.id.cancelTextView);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        usernameEditText = findViewById(R.id.usernameEditText2);
        passwordEditText = findViewById(R.id.passwordEditText2);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        createAccountButton = findViewById(R.id.createAccountButton);
    }

    private void createAccount() {
        // TODO: create account request
        //  - Implement php
        //  - Add necessary info to shared preferences (username & session_key if we use it)
        sharedPrefs.setLoggedInUser(username);
        finish();
    }
}
