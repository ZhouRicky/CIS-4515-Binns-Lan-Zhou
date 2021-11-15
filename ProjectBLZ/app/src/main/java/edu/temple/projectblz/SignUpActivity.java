package edu.temple.projectblz;

import android.content.Intent;
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
import com.android.volley.VolleyLog;
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

                if(firstNameEditText.getText() != null && lastNameEditText.getText() != null &&
                        emailEditText.getText() != null && usernameEditText.getText() != null &&
                        passwordEditText.getText() != null && confirmPasswordEditText.getText() != null) {
                    firstName = firstNameEditText.getText().toString();
                    lastName = lastNameEditText.getText().toString();
                    email = emailEditText.getText().toString();
                    username = usernameEditText.getText().toString();
                    password = passwordEditText.getText().toString();
                    confirmPassword = confirmPasswordEditText.getText().toString();

                    if(firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                            username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
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
        //  - Implement php (need a set url)
        //  - Add necessary info to shared preferences (username & session_key if we use it)
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.REGISTER_URL,
                response -> {

                    // TODO: Refactor code block

                    Log.d("JSON", String.valueOf(response));

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");

                        if(status.equals("success")) {

                            Log.d("JSON", "status: " + status);
                            Toast.makeText(this, status, Toast.LENGTH_SHORT).show();

                            sharedPrefs.setLoggedInUser(username);
                            sharedPrefs.setIsLoggedIn(true);

                            // TODO: Send this back to login screen or main activity?
                            startActivity(new Intent(this, LoginActivity.class));
                            finish();
                        }

                        Toast.makeText(this, status, Toast.LENGTH_SHORT).show();
                        Log.d("JSON", "status1: " + status);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "try/catch error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    VolleyLog.d("Error", "Error: " + error.getMessage());
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
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}
