package edu.temple.projectblz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    SharedPrefs sharedPrefs;

    TextView statusTextView, cancelEditText;
    EditText firstNameEditText, lastNameEditText, usernameEditText, passwordEditText, confirmPasswordEditText, emailEditText;
    Button createAccountButton;

    String firstName, lastName, username, password, confirmPassword, email;

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
                    email = emailEditText.getText().toString();
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
        emailEditText = findViewById(R.id.emailEditText);
        usernameEditText = findViewById(R.id.usernameEditText2);
        passwordEditText = findViewById(R.id.passwordEditText2);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        createAccountButton = findViewById(R.id.createAccountButton);
    }

    private void createAccount() {
        // TODO: create account request
        //  - Implement php
        //  - Add necessary info to shared preferences (username & session_key if we use it)

        final String URL = "http://172.20.10.8/register.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {
                   // Log.d("TAG", "Response: " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String result = jsonObject.getString("status");
                        if(result.equals("success")){
                            sharedPrefs.setLoggedInUser(username);
                            Log.d("TAG", "resultKey " + result);
                            Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(this, LoginActivity.class));
                            finish();
                        }
                        Toast toast =  Toast.makeText(this, result, Toast.LENGTH_LONG);
                        toast.show();
                        Log.d("TAG", "resultKey1 " + result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error, Please try again " + e.toString(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error, Please try again" + error.toString(), Toast.LENGTH_LONG).show();
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("firstname", firstName);
                params.put("lastname", lastName);
                params.put("email", email);
                params.put("username", username);
                params.put("password", password);
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    System.out.println(entry.getKey() + "/" + entry.getValue());
                }
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
        //
       //
    }
}
