package edu.temple.projectblz;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class LoginActivity extends AppCompatActivity {

    SharedPrefs sharedPrefs;

    TextView statusTextView, signUpTextView;
    EditText usernameEditText, passwordEditText;
    Button loginButton;

    String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPrefs = new SharedPrefs(this);
        handleSSLHandshake();
        checkPermission();
        viewInitialization();

        // log in button functionality
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusTextView.setText("");

                if(usernameEditText.getText() != null && passwordEditText.getText() != null) {
                    username = usernameEditText.getText().toString();
                    password = passwordEditText.getText().toString();

                    if(username.isEmpty() || password.isEmpty()) {
                        statusTextView.setText(Constant.INCORRECT_INFO);
                    } else {
                        if(!sharedPrefs.getIsPermissionGranted()) {
                            Toast.makeText(LoginActivity.this, "Please enable all necessary permissions", Toast.LENGTH_SHORT).show();
                        } else {
                            login();
                        }
                    }
                } else {
                    statusTextView.setText(Constant.INVALID_INPUT);
                }
            }
        });

        // sign up button functionality
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), SignUpActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
        redirectIfLoggedIn();
    }

    // uses dexter library to check for permissions at runtime
    private void checkPermission() {
        Dexter.withContext(this).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if(multiplePermissionsReport.areAllPermissionsGranted()) {
                    sharedPrefs.setIsPermissionGranted(true);
                }

                if(multiplePermissionsReport.getDeniedPermissionResponses().size() > 0){
                    Toast.makeText(LoginActivity.this, "All permissions are required to continue", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), "");
                    intent.setData(uri);
                    startActivity(intent);
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();

        // checks for WRITE_SETTINGS permission
        if(!Settings.System.canWrite(this)) {
            Toast.makeText(LoginActivity.this, "Modify system settings must be allowed", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), "");
            intent.setData(uri);
            startActivity(intent);
        }
    }

    // initialize necessary views
    private void viewInitialization() {
        statusTextView = findViewById(R.id.statusTextView);
        signUpTextView = findViewById(R.id.signUpTextView);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
    }


    private void login() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.LOGIN_URL,
                response -> {

                    Log.d("JSON", String.valueOf(response));

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        Log.d("JSONObject", jsonObject.toString());

                        if(jsonObject.getString("status").equals("success")) {
                            Log.d("JSON", "success: " + jsonObject.getString("message"));

                            sharedPrefs.setLoggedInUser(username);
                            sharedPrefs.setPassword(password);
                            sharedPrefs.setDriverId(jsonObject.getString("driverId"));
                            sharedPrefs.setIsLoggedIn(true);

                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        } else if(jsonObject.getString("status").equals("error")) {
                            Log.d("JSON", "error: " + jsonObject.getString("message"));
                        }
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
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }

    // redirect user to main activity if not explicitly logged out
    // TODO: check if working properly
    private void redirectIfLoggedIn() {
        if(!sharedPrefs.getLoggedInUser().equals(Constant.SHARED_PREFS_DEFAULT_STRING)
                && !sharedPrefs.getDriverId().equals(Constant.SHARED_PREFS_DEFAULT_STRING)
                && sharedPrefs.getIsLoggedIn().equals(true)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}