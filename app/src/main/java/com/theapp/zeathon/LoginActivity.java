package com.theapp.zeathon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText userName, pwrd;
    Button btnLogin;
    TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userName = findViewById(R.id.userName);
        pwrd = findViewById(R.id.pwrd);
        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.register).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void userLogin() {
        String email = userName.getText().toString();
        String password = pwrd.getText().toString();

        if(email.isEmpty()) {
            userName.setError("email is required");
            userName.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            userName.setError("enter a valid email");
            userName.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            pwrd.setError("password is required");
            pwrd.requestFocus();
            return;
        }

        if(password.length() > 6 ){
            pwrd.setError("password should be atleast 6 characters long");
            pwrd.requestFocus();
            return;
        }

        Call<LoginResponse> call = RetrofitClient
                .getInstance()
                .getNaMarkets()
                .userLogin(email, password);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse loginResponse = response.body();
                if(!loginResponse.isError()){
                    // proceed with the login
                    // save user
                    // open profile
                   // Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                    SharedPrefManager.getInstance(LoginActivity.this)
                            .userLogin(loginResponse.getUser());
                    Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLogin:
                userLogin();
            break;
            case R.id.register:
                startActivity(new Intent(this, MainActivity.class));
                break;
        }

    }


}
