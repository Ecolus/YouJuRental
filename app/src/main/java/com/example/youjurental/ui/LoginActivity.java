package com.example.youjurental.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.youjurental.R;
import com.example.youjurental.db.UserDBHelper;
import com.example.youjurental.util.SharedPrefsUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText etAccount, etPassword;
    private MaterialButton btnLogin, btnRegister, btnForget;
    private UserDBHelper userDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userDBHelper = UserDBHelper.getInstance(this);

        if (SharedPrefsUtil.isLoggedIn(this)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        etAccount = findViewById(R.id.et_account);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        btnForget = findViewById(R.id.btn_forget);

        btnLogin.setOnClickListener(v -> {
            String account = etAccount.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(account)) {
                Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userDBHelper.login(account, password)) {
                SharedPrefsUtil.saveLoginInfo(this, account);
                SharedPrefsUtil.savePhone(this, account);
                Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "账号或密码错误", Toast.LENGTH_SHORT).show();
            }
        });

        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        btnForget.setOnClickListener(v ->
                startActivity(new Intent(this, ForgetPasswordActivity.class)));
    }
}
