package com.huan.percy.communitysocialsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SINGUP = 0;
    private String location = null;
    private String name = null;
    private boolean loginResult = false;
    private final String IP = "http://192.168.23.178:8003";
    private final String REQUEST_URL = "/LingliServer/SignIn";

    public static final int HANDLE_RESPOND = 1;

    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_login) Button _loginButton;
    @InjectView(R.id.link_signup) TextView _signupLink;

    private VideoView myVideoView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        if (checkCookie()){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }
        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start the Singup activity
                Intent intent = new Intent(getApplicationContext(), SingupActivity.class);
                startActivity(intent);
                finish();
            }
        });

        initView();

        final String videoPath = Uri.parse("android.resource://" + getPackageName() + "/"
                + R.raw.falldown).toString();
        myVideoView.setVideoPath(videoPath);
        myVideoView.start();
        myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.setLooping(true);

            }
        });

        myVideoView
                .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        myVideoView.setVideoPath(videoPath);
                        myVideoView.start();

                    }
                });
    }

    public void login(){
        Log.d(TAG, "Login");

        if (!validate()){
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("正在登录...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        //TODO: Implement your own anthentication logic here
        verifyAccount(email, password);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        //On complete call either onLoginSuccess or onLoginFailed
                        if (loginResult){ onLoginSuccess();}
                        else { onLoginFailed();}
                        //onLoginSuccess();
                        //onLoginFailed
                        progressDialog.dismiss();
                    }
                }
        , 3000);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_SINGUP){
            if (resultCode == RESULT_OK){
                //TODO: Implement successful singup logic here
                //By default we just finish the Activity and log them in automatical

                //this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(){
        _loginButton.setEnabled(true);
        Toast.makeText(getBaseContext(), "登录成功", Toast.LENGTH_LONG).show();
        saveCookie();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void onLoginFailed(){
        Toast.makeText(getBaseContext(), "登录失败", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate(){
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password  = _passwordText.getText().toString();

        //if the email is invalid
        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            _emailText.setError("邮件格式不正确");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        //if the password is inalid
        if (password.isEmpty() || password.length() < 4 || password.length() > 10){
            _passwordText.setError("请输入4-10个字符");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public boolean checkCookie(){
        SharedPreferences pref = getSharedPreferences("Cookie", MODE_PRIVATE);
        return pref.getBoolean("logined", false);
    }

    public void saveCookie(){
        SharedPreferences.Editor editor = getSharedPreferences("Cookie",
                MODE_PRIVATE).edit();

        loginResult = true;
        editor.putBoolean("logined", loginResult);
        editor.putString("email", _emailText.getText().toString());
        editor.putString("pwd", _passwordText.getText().toString());
        editor.putString("name", name);
        editor.putString("location",location);
        editor.apply();
    }

    public void verifyAccount(final String id, final String pw) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(IP+REQUEST_URL);//服务器地址，指向Servlet
                    List<NameValuePair> params = new ArrayList<NameValuePair>();//将id和pw装入list
                    params.add(new BasicNameValuePair("id", id));
                    params.add(new BasicNameValuePair("pw", pw));
                    final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");//以UTF-8格式发送
                    httpPost.setEntity(entity);
                    HttpResponse httpResponse = httpclient.execute(httpPost);
                    if (httpResponse.getStatusLine().getStatusCode() == 200)//在200毫秒之内接收到返回值
                    {
                        HttpEntity entity1 = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity1, "utf-8");//以UTF-8格式解析
                        // parsing JSON
                        JSONObject result = new JSONObject(response); //Convert String to JSON Object
                        loginResult = result.getBoolean("result");
                        name = result.getString("name");
                        location = result.getString("location");

                        Log.d("json", "login:"+loginResult + " name:"+name+" location:"+location);

                        Message message = new Message();
                        message.what = HANDLE_RESPOND;
                        message.obj = response;
                        handler.sendMessage(message);//使用Message传递消息给线程
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_RESPOND:
                    String response = (String) msg.obj;
                    //Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    private void initView() {

        myVideoView = (VideoView) findViewById(R.id.videoView);

    }

}
