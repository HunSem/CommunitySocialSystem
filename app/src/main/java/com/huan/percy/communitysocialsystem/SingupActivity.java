package com.huan.percy.communitysocialsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SingupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @InjectView(R.id.input_name) EditText _nameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);
        ButterKnife.inject(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup(){
        Log.d(TAG, "Signup");

        if(!validate()){
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SingupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("正在创建账户...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        //TODO:Implement your own sigup logic here

        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        //On complete call either on SignupSuccess or onSignupFailed
                        //depending on success
                        onSignupSuccess();
                        //onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000
        );
    }

    public void onSignupSuccess(){
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Toast.makeText(getBaseContext(), "创建成功", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onSignupFailed(){
        Toast.makeText(getBaseContext(), "创建失败", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate(){
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if(name.isEmpty() || name.length() < 3){
            _nameText.setError("至少3个字符");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            _emailText.setError("邮件格式不正确");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("请输入4-10个字符");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}

