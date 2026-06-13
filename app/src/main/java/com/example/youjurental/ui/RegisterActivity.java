package com.example.youjurental.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.youjurental.R;
import com.example.youjurental.db.UserDBHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText etAccount, etPassword, etConfirm;
    private MaterialButton btnRegister, btnBack;
    private UserDBHelper userDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userDBHelper = UserDBHelper.getInstance(this);

        etAccount = findViewById(R.id.et_account);
        etPassword = findViewById(R.id.et_password);
        etConfirm = findViewById(R.id.et_confirm);
        btnRegister = findViewById(R.id.btn_register);
        btnBack = findViewById(R.id.btn_back);

        btnRegister.setOnClickListener(v -> {
            String account = etAccount.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirm = etConfirm.getText().toString().trim();

            if (TextUtils.isEmpty(account)) {
                Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirm)) {
                Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 6) {
                Toast.makeText(this, "密码至少6位", Toast.LENGTH_SHORT).show();
                return;
            }
            if (userDBHelper.isAccountExists(account)) {
                Toast.makeText(this, "账号已存在", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userDBHelper.register(account, password)) {
                Toast.makeText(this, "注册成功，请登录", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "注册失败", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }
}
