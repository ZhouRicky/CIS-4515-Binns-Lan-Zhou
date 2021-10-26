package edu.temple.projectblz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    TextView LoginTextView,QuestionTextView,SignUpTextView,Status;
    Button LoginButton;
    EditText UsernameEditText,PasswordEditText;
    String Username,Password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewInitialization();


        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(UsernameEditText.getText()!=null&&PasswordEditText.getText()!=null){
                    Username = UsernameEditText.getText().toString();
                    Password = UsernameEditText.getText().toString();
                    //TODO Implement php verifying credential
                }
                else{
                    Status.setText(Constant.InvalidInputMessage);
                    //Might be better with alertDialog
                    //InvalidInputMessage is defined null currenly in Constant.java
                }
            }
        });
    }

    private void ViewInitialization(){
        LoginTextView       = findViewById(R.id.Login);
        QuestionTextView    = findViewById(R.id.Question);
        SignUpTextView      = findViewById(R.id.SignUp);
        UsernameEditText    = findViewById(R.id.Username);
        PasswordEditText    = findViewById(R.id.Password);
        LoginButton         = findViewById(R.id.LoginButton);
        Status              = findViewById(R.id.Status);
    }
}