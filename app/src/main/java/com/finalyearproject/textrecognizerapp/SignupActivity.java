package com.finalyearproject.textrecognizerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.finalyearproject.textrecognizerapp.bll.AuthBLL;
import com.finalyearproject.textrecognizerapp.helper.EditTextValidation;
import com.finalyearproject.textrecognizerapp.helper.Helper;
import com.finalyearproject.textrecognizerapp.models.Errors;
import com.finalyearproject.textrecognizerapp.models.User;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    private TextInputLayout etFirstName, etLastName, etEmail, etPassword, etConfirmPassword;
    private Button btnJoinus;
    private HashMap<String, TextInputLayout> errorMap;
    private AuthBLL authBLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initComponents();
    }
    private void initComponents() {

        authBLL = new AuthBLL();

        etFirstName = findViewById(R.id.et_firstname);
        etLastName = findViewById(R.id.et_lastname);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirmpassword);

        errorMap = new HashMap<>();
        errorMap.put("firstName", etFirstName);
        errorMap.put("lastName", etLastName);
        errorMap.put("email", etEmail);
        errorMap.put("password", etPassword);

        btnJoinus = findViewById(R.id.btn_joinus);

        btnJoinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp() {

        if (isSignUpDetailsValid() && isPasswordConfirmed()) {

            Helper.StrictMode();

            String firstName = etFirstName.getEditText().getText().toString().trim();
            String familyName = etLastName.getEditText().getText().toString().trim();
            String email = etEmail.getEditText().getText().toString().trim();
            String password = etPassword.getEditText().getText().toString().trim();

            User user = new User(firstName, familyName, email, password);

            if (authBLL.registerUser(user)) {

                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                Toast.makeText(this, "Successfully registered !", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(intent);

            }
        } else if (!isPasswordConfirmed()) {
            etConfirmPassword.setError("Password did not match !");
        }
    }
    private boolean isSignUpDetailsValid() {
        if (EditTextValidation.isEmpty(etFirstName) | EditTextValidation.isEmpty(etLastName) |
                EditTextValidation.isEmpty(etEmail) | EditTextValidation.isEmpty(etPassword) |
                EditTextValidation.isEmpty(etConfirmPassword)) {
            return false;
        }
        return true;
    }

    private boolean isPasswordConfirmed() {
        return etPassword.getEditText().getText().toString().trim().equals(etConfirmPassword.getEditText()
                .getText().toString().trim());
    }

    public void showLoginForm(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        authBLL.setAuthListener(new AuthBLL.AuthListener() {

            @Override
            public void onError(Errors errors) {
                for (String key : errorMap.keySet()) {
                    if (errors.getField().equals(key)) {
                        errorMap.get(errors.getField()).setError(errors.getMessage());
                    }
                }

            }
        });
    }
}