package com.yy.kaitian.yiliao681;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.yy.kaitian.yiliao681.bean.Login;
import com.yy.kaitian.yiliao681.utils.GsonUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import okhttp3.Call;

public class LoginRemoteServerActivity extends Activity {
    private static final boolean D = true;
    private static final String TAG = "LoginRemoteServerActivity";
    private AutoCompleteTextView mAccountEdit;
    private Button mCancelButton;
    private Button mLoginButton;
    private SharedPreferences mLoginSharePre;
    private EditText mPwdEdit;
    private CheckBox mSavePwdCheckBox;
    private String mStrAccount = "";
    private String mStrMD5Pwd = "";
    private String mStrPwd = "";

    private void loginTDSServer(String paramString) {
        HttpGet localHttpGet = new HttpGet(paramString);
        try {
            HttpResponse localHttpResponse = new DefaultHttpClient().execute(localHttpGet);
            if (localHttpResponse.getStatusLine().getStatusCode() == 200) {
                Log.i("LoginRemoteServerActivity", EntityUtils.toString(localHttpResponse.getEntity()));
                return;
            }
            Log.i("LoginRemoteServerActivity", "Error Response: " + localHttpResponse.getStatusLine().toString());
            return;
        } catch (ClientProtocolException localClientProtocolException) {
            localClientProtocolException.printStackTrace();
            return;
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
            return;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public boolean isNetworkAvailable() {
        NetworkInfo localNetworkInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return (localNetworkInfo != null) && (localNetworkInfo.isConnected());
    }

    public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {
//        Log.d("LoginRemoteServerActivity", "onActivityResult " + paramInt2);
//        switch (paramInt1) {
//            default:
//            case 1:
//        }
//        Bundle localBundle1;
//        Intent localIntent;
//    do
//    {
//      return;
//      localBundle1 = new Bundle();
//      localIntent = new Intent();
//      if (paramInt2 == -1)
//      {
//        Bundle localBundle3 = paramIntent.getExtras();
//        Toast.makeText(getApplicationContext(), "登陆系统成功！", 0).show();
//        if (this.mSavePwdCheckBox.isChecked())
//          this.mLoginSharePre.edit().putString(this.mStrAccount, this.mStrPwd).commit();
//        localBundle1.putString("login_result", "OK");
//        localBundle1.putString("login_uid", this.mStrAccount);
//        localBundle1.putString("login_pwd", this.mStrPwd);
//        localBundle1.putString("login_point", localBundle3.getString("login_result"));
//        localIntent.putExtras(localBundle1);
//        setResult(-1, localIntent);
//        finish();
//        return;
//      }
//    }
//    while (paramInt2 != 0);
//        Bundle localBundle2 = paramIntent.getExtras();
//        Toast.makeText(getApplicationContext(), localBundle2.getString("login_result"), Toast.LENGTH_SHORT).show();
//    localBundle1.putString("login_result", "ERR");
//    localIntent.putExtras(localBundle1);
//    setResult(-1, localIntent);
//        finish();
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.login);
        this.mPwdEdit = ((EditText) findViewById(R.id.login_pwd_edit));
        this.mAccountEdit = ((AutoCompleteTextView) findViewById(R.id.login_account_edit));
        this.mSavePwdCheckBox = ((CheckBox) findViewById(R.id.login_save_pwd_checkbox));
        this.mLoginButton = ((Button) findViewById(R.id.login_login_button));
        this.mCancelButton = ((Button) findViewById(R.id.login_cancel_button));
        this.mSavePwdCheckBox.setChecked(true);
        this.mAccountEdit.setThreshold(1);
        this.mPwdEdit.setInputType(129);
        this.mLoginSharePre = getSharedPreferences("login_pwd", 0);
        this.mAccountEdit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable paramAnonymousEditable) {
                LoginRemoteServerActivity.this.mPwdEdit.setText(LoginRemoteServerActivity.this.mLoginSharePre.getString(LoginRemoteServerActivity.this.mAccountEdit.getText().toString(), ""));
            }

            public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {
            }

            public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {
//        new String[LoginRemoteServerActivity.this.mLoginSharePre.getAll().size()];
//        String[] arrayOfString = (String[])LoginRemoteServerActivity.this.mLoginSharePre.getAll().keySet().toArray(new String[0]);
//        ArrayAdapter localArrayAdapter = new ArrayAdapter(LoginRemoteServerActivity.this, 17367050, arrayOfString);
//        LoginRemoteServerActivity.this.mAccountEdit.setAdapter(localArrayAdapter);
            }
        });
        setResult(0);
        Log.i("LoginRemoteServerActivity", this.mLoginSharePre.getString("account", ""));
        if (!isNetworkAvailable())
            new AlertDialog.Builder(this).setTitle("没有可用的网络").setMessage("是否对网络进行设置？").setPositiveButton("是", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                    if (Build.VERSION.SDK_INT > 10) {
                        LoginRemoteServerActivity.this.startActivity(new Intent("android.settings.SETTINGS"));
                        return;
                    }
                    LoginRemoteServerActivity.this.startActivity(new Intent("android.settings.WIRELESS_SETTINGS"));
                }
            }).setNeutralButton("否", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                    paramAnonymousDialogInterface.cancel();
                }
            }).show();
        this.mLoginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                LoginRemoteServerActivity.this.mStrAccount = LoginRemoteServerActivity.this.mAccountEdit.getText().toString();
                LoginRemoteServerActivity.this.mStrPwd = LoginRemoteServerActivity.this.mPwdEdit.getText().toString();
                LoginRemoteServerActivity.this.mStrMD5Pwd = TDSUtils.md5(LoginRemoteServerActivity.this.mStrPwd);
                String str = "http://www.hsnet.cn/InLight/PinCheck.asp?ID=" + LoginRemoteServerActivity.this.mStrAccount + "&PWD=" + LoginRemoteServerActivity.this.mStrMD5Pwd;
                Log.i("LoginRemoteServerActivity", str);
                login(mStrAccount, mStrPwd, mStrMD5Pwd);
//                LoginRemoteServerActivity.this.setResult(0);
//                Bundle localBundle = new Bundle();
//                localBundle.putString("server_operate_type", "1");
//                localBundle.putString("server_operate_url", str);
//                Intent localIntent = new Intent(LoginRemoteServerActivity.this, WebViewActivity.class);
//                localIntent.putExtras(localBundle);
//                LoginRemoteServerActivity.this.startActivityForResult(localIntent, 1);
            }
        });
        this.mCancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                LoginRemoteServerActivity.this.finish();
            }
        });
    }

    private void login(final String strAccount, final String strPwd, String strMD5Pwd) {
        OkHttpUtils.get().tag(this)
                .url(UrlApi.BaseUrl + UrlApi.Login)
                .addParams("name", strAccount)
                .addParams("pwd", strMD5Pwd)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                response = response.substring(2,response.length() - 2).replace("\\", "");
                Login login = GsonUtils.INSTANCE.parseToBean(response, Login.class);
                if(login != null){
                    if(login.isState()){
                        Bundle localBundle1 = new Bundle();
                        Intent localIntent = new Intent();
                        Toast.makeText(getApplicationContext(), "登陆系统成功！", Toast.LENGTH_SHORT).show();
                        setResult(0);
                        if (mSavePwdCheckBox.isChecked())
                            mLoginSharePre.edit().putString(strAccount, strPwd).commit();
                        localBundle1.putString("login_result", "OK");
                        localBundle1.putString("login_uid", strAccount);
                        localBundle1.putString("login_pwd", strPwd);
                        localBundle1.putString("login_point", login.getNum()+"");
                        localBundle1.putString("url",login.getLink());
                        localIntent.putExtras(localBundle1);
                        setResult(-1, localIntent);
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "登录失败！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
    }
}



/* Location:           D:\android studio\fanbianyi\classes-dex2jar (2).jar
 * Qualified Name:     com.tds.test.LoginRemoteServerActivity
 * JD-Core Version:    0.6.2
 */