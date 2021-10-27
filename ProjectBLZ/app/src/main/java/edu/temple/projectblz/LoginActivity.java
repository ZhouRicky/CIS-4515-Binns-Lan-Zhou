package edu.temple.projectblz;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    SharedPrefs sharedPrefs;

    TextView statusTextView, signUpTextView;
    EditText usernameEditText, passwordEditText;
    Button loginButton;

    boolean isLocationPermissionGranted;
    String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPrefs = new SharedPrefs(this);

        checkPermission();
        viewInitialization();
//        redirectIfLoggedIn(); // TODO: uncomment when logout button is implemented

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
                        if(!isLocationPermissionGranted) {
                            Toast.makeText(LoginActivity.this, "Please enable location permissions", Toast.LENGTH_SHORT).show();
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
                // TODO: when user returns from SignUpActivity, the application will be changed to redirect to MainActivity when php gets implemented
                Intent intent = new Intent(view.getContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
//        redirectIfLoggedIn(); // TODO: uncomment when logout button is implemented
    }

    // uses dexter library to check for permissions at runtime
    private void checkPermission() {
        Dexter.withContext(this).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if(multiplePermissionsReport.areAllPermissionsGranted()) {
                    isLocationPermissionGranted = true;
                    sharedPrefs.setAccessLocationPermissionGranted(true);
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
        // TODO: log in request
        //  - Implement php verifying credential
        //  - Add necessary info to shared preferences (username & session_key if we use it)

        // for now we'll just make it redirect to main activity
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    // redirect user to main activity
    // TODO: uncomment when logout button is implemented
    //  - need to add session_key if used
//    private void redirectIfLoggedIn() {
//        if(!sharedPrefs.getLoggedInUser().equals(Constant.SHARED_PREFS_DEFAULT_STRING)) {
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            startActivity(intent);
//        }
//    }
}