package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.database.UserDB;

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class SignUpActivity extends AppCompatActivity {
    EditText edtUsername, edtPassword, edtEmail, edtPhone;
    Button btnRegister, btncanceltosSignin;
    TextView tvLogin;
    UserDB userDb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userDb = new UserDB(SignUpActivity.this);

        edtUsername = findViewById(R.id.edtUser);
        edtPassword = findViewById(R.id.edtPassword);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        btnRegister = findViewById(R.id.btnregister);
        btncanceltosSignin = findViewById(R.id.btncanceltosSignin);
        tvLogin = findViewById(R.id.tvLogin);

        registerUser();

        // Chuyển sang trang SignInActivity khi nhấn nút Cancel
        btncanceltosSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        // Chuyển sang trang SignInActivity khi nhấn vào TextView Login
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerUser() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    edtUsername.setError("Username not empty");
                    return;
                }

                String password = edtPassword.getText().toString().trim();
                if (TextUtils.isEmpty(password)) {
                    edtPassword.setError("Password not empty");
                    return;
                }

                String email = edtEmail.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    edtEmail.setError("Email not empty");
                    return;
                }

                String phone = edtPhone.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    edtPhone.setError("Phone not empty");
                    return;
                }

                boolean checkUsername = userDb.checkUsernameExists(username);
                if (checkUsername) {
                    edtUsername.setError("Username already exists, please choose another");
                    return;
                }

                long insertUser = userDb.insertUserToDatabase(username, password, email, phone);
                if (insertUser == -1) {
                    Toast.makeText(SignUpActivity.this, "Create user failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignUpActivity.this, "Create user success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    // Optional: Ghi tài khoản vào file nếu muốn dùng cách khác
    private void Signup() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if (TextUtils.isEmpty(user)) {
                    edtUsername.setError("Username not empty");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    edtPassword.setError("Password not empty");
                    return;
                }

                try {
                    user += "|";
                    FileOutputStream fileOutputStream = openFileOutput("account.txt", Context.MODE_APPEND);
                    fileOutputStream.write(user.getBytes(StandardCharsets.UTF_8));
                    fileOutputStream.write(password.getBytes(StandardCharsets.UTF_8));
                    fileOutputStream.write('\n');
                    fileOutputStream.close();

                    edtUsername.setText("");
                    edtPassword.setText("");
                    Toast.makeText(SignUpActivity.this, "Register Success", Toast.LENGTH_SHORT).show();

                    Intent intentLogin = new Intent(SignUpActivity.this, SignInActivity.class);
                    startActivity(intentLogin);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
