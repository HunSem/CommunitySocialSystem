package com.huan.percy.communitysocialsystem;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Percy on 2016/6/27.
 */
public class AddBroadcastActivity extends AppCompatActivity {

    private final String IP = "http://192.168.23.178:8003";
    private final String ADD_REQUEST = "/LingliServer/AddBroadcast";

    private String location;
    private String email;
    private boolean sendResult = false;
    private EditText inputTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_broadcast);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        inputTxt = (EditText) findViewById(R.id.content);
    }

    @Override
    public void onBackPressed() {

        if (checkIsEmpty()){
            this.finish();
        } else {
            showDialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_send:
                if (!checkIsEmpty()){
                    final ProgressDialog progressDialog = new ProgressDialog(AddBroadcastActivity.this,
                            R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("正在发送...");
                    progressDialog.show();

                    sendNewBroadcast();

                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    //On complete call either onLoginSuccess or onLoginFailed
                                    if (sendResult){ sendSuccess();}
                                    else { sendFailed();}
                                    //onLoginSuccess();
                                    //onLoginFailed
                                    progressDialog.dismiss();
                                }
                            }
                            , 3000);
                } else {
                    Toast.makeText(getApplicationContext(), "好像什么都没写欸 ╮(╯▽╰)╭", Toast.LENGTH_LONG).show();
                }

                return true;
            case android.R.id.home:
                if(!checkIsEmpty()){
                    showDialog();
                } else {
                    this.finish();
                }

                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendNewBroadcast(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(IP+ADD_REQUEST);//服务器地址，指向Servlet
                    List<NameValuePair> params = new ArrayList<NameValuePair>();

                    String content = inputTxt.getText().toString();
                    //获取本地Cookie
                    SharedPreferences pref = getSharedPreferences("Cookie", MODE_PRIVATE);
                    location  = pref.getString("location", "null");
                    email = pref.getString("email", "null");

                    Log.d("email", email);
                    params.add(new BasicNameValuePair("id", email));
                    params.add(new BasicNameValuePair("article", content));
                    params.add(new BasicNameValuePair("location", location));


                    final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");//以UTF-8格式发送
                    httpPost.setEntity(entity);
                    HttpResponse httpResponse = httpclient.execute(httpPost);
                    if (httpResponse.getStatusLine().getStatusCode() == 200)//在200毫秒之内接收到返回值
                    {
                        HttpEntity entity1 = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity1, "utf-8");//以UTF-8格式解析
                        // parsing JSON
                        JSONObject result = new JSONObject(response); //Convert String to JSON Object

                        sendResult = result.getBoolean("result");
                        //Log.d("json", "login:"+login + " name:"+name+" location:"+location);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void sendSuccess(){
        this.finish();
    }

    private void sendFailed(){
        Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_LONG).show();
    }

    private boolean checkIsEmpty(){
        return inputTxt.getText().length() == 0;
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddBroadcastActivity.this);
        builder.setMessage("写过的东西不要了吗？");
        builder.setTitle("Σ(っ °Д °;)っ");
        builder.setPositiveButton("就不要", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                AddBroadcastActivity.this.finish();
            }
        });
        builder.setNegativeButton("要要要", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


}
