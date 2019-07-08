package com.yy.kaitian.yl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.util.regex.Pattern;

public class WebViewActivity extends Activity
{
  private static final boolean D = true;
  private static final String TAG = "WebViewActivity";
  public static final int TDS_SERVER_GET_REPORT_RESULT = 2;
  public static final int TDS_SERVER_LOGIN_RESULT = 1;
  public static final int TDS_SERVER_OPERATE_TYPE_GET_REPORT = 2;
  public static final int TDS_SERVER_OPERATE_TYPE_INTELLIGENT_READING = 3;
  public static final int TDS_SERVER_OPERATE_TYPE_LOGIN = 1;
  public static final String mGetReportUri = "../do_page/tds3/report_list_cn.asp";
  public static final String mGetReportUrl = "/InLight/LoginTo.asp";
  public static final String mIntelligentReadingUrl = "/utf/utf.aspx";
  public static final String mLoginUrl = "/InLight/PinCheck.asp";
  public static final String mRemoteServerAddr = "http://www.hsnet.cn";
  private int mOperateType = 0;
  private String mStrIntelError = "ERROR";
  private String mStrIntelNopass = "NOPASS";
  private String mStrIntelNotime = "NOTIME";
  private String mStrIntelOK = "NOASK";
  private String mStrIntelOutScope = "OUTSCOPE";
  private String mStrLoginIDErr = "ERROR_ID";
  private String mStrLoginOK = "Return Ok!";
  private String mStrLoginPWDErr = "ERROR_PWD";
  private String mUrl = "";
  private WebView webView;

  private String GetAccountPoint(String paramString)
  {
    String str = replaceBlank(paramString);
    if (str != "")
    {
      String[] arrayOfString = str.split("==");
      if (arrayOfString.length > 1)
        return arrayOfString[1];
    }
    return null;
  }

  private void doServerReturnResult(String paramString)
  {
    switch (this.mOperateType)
    {
    case 2:
    default:
      return;
    case 1:
      Bundle localBundle2 = new Bundle();
      Intent localIntent2 = new Intent();
      if (TDSUtils.isStringRegionMatches(paramString, this.mStrLoginPWDErr))
      {
        localBundle2.putString("login_result", "密码错误！");
        localIntent2.putExtras(localBundle2);
        setResult(0, localIntent2);
        finish();
        return;
      }
      if (TDSUtils.isStringRegionMatches(paramString, this.mStrLoginIDErr))
      {
        localBundle2.putString("login_result", "账号错误！");
        localIntent2.putExtras(localBundle2);
        setResult(0, localIntent2);
        finish();
        return;
      }
      if (TDSUtils.isStringRegionMatches(paramString, this.mStrLoginOK))
      {
        String str2 = "";
        int k = paramString.indexOf(this.mStrLoginOK);
        int m = paramString.indexOf("</body>");
        if ((k != -1) && (m != -1))
        {
          str2 = GetAccountPoint(paramString.substring(1 + (k + this.mStrLoginOK.length()), m));
          if (str2 == null)
            str2 = "";
        }
        localBundle2.putString("login_result", str2);
        localIntent2.putExtras(localBundle2);
        setResult(-1, localIntent2);
        finish();
        return;
      }
      localBundle2.putString("login_result", "找不到网页！");
      localIntent2.putExtras(localBundle2);
      setResult(0, localIntent2);
      finish();
      return;
    case 3:
    }
    Bundle localBundle1 = new Bundle();
    Intent localIntent1 = new Intent();
    if (TDSUtils.isStringRegionMatches(paramString, this.mStrIntelOK))
    {
      String str1 = "";
      int i = paramString.indexOf("R");
      int j = paramString.indexOf("</body>");
      if ((i != -1) && (j != -1))
      {
        str1 = paramString.substring(i + 4, j);
        Log.i("WebViewActivity", str1);
      }
      localBundle1.putString("intelligent_result", str1);
      localIntent1.putExtras(localBundle1);
      setResult(-1, localIntent1);
      finish();
      return;
    }
    if (TDSUtils.isStringRegionMatches(paramString, this.mStrIntelNotime))
    {
      localBundle1.putString("intelligent_result", "没有可用的检测次数！");
      localIntent1.putExtras(localBundle1);
      setResult(0, localIntent1);
      finish();
      return;
    }
    if (TDSUtils.isStringRegionMatches(paramString, this.mStrIntelNopass))
    {
      localBundle1.putString("intelligent_result", "登录不成功! 可能使用的密码已经更改！");
      localIntent1.putExtras(localBundle1);
      setResult(0, localIntent1);
      finish();
      return;
    }
    if (TDSUtils.isStringRegionMatches(paramString, this.mStrIntelError))
    {
      localBundle1.putString("intelligent_result", "系统执行错误！");
      localIntent1.putExtras(localBundle1);
      setResult(0, localIntent1);
      finish();
      return;
    }
    if (TDSUtils.isStringRegionMatches(paramString, this.mStrIntelOutScope))
    {
      localBundle1.putString("intelligent_result", "检测体能值超出系统范围！");
      localIntent1.putExtras(localBundle1);
      setResult(0, localIntent1);
      finish();
      return;
    }
    localBundle1.putString("intelligent_result", "报告已经提交！");
    localIntent1.putExtras(localBundle1);
    setResult(0, localIntent1);
    finish();
  }

  private String replaceBlank(String paramString)
  {
    String str = "";
    if (paramString != null)
      str = Pattern.compile("\r|\n").matcher(paramString).replaceAll("==");
    return str;
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(R.layout.web_view);
    ((Button)findViewById(R.id.webCancel)).setOnClickListener(new OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        WebViewActivity.this.finish();
      }
    });
    this.webView = ((WebView)findViewById(R.id.webView1));
    this.webView.getSettings().setJavaScriptEnabled(true);
    this.webView.getSettings().setSupportZoom(true);
    this.webView.getSettings().setBuiltInZoomControls(true);
    this.webView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
    this.webView.setWebViewClient(new WebViewClient()
    {
      public void onPageFinished(WebView paramAnonymousWebView, String paramAnonymousString)
      {
        WebViewActivity.this.webView.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
        super.onPageFinished(paramAnonymousWebView, paramAnonymousString);
      }

      public boolean shouldOverrideUrlLoading(WebView paramAnonymousWebView, String paramAnonymousString)
      {
        paramAnonymousWebView.loadUrl(paramAnonymousString);
        return true;
      }
    });
    setResult(0);
    new Bundle();
    Bundle localBundle = getIntent().getExtras();
    this.mOperateType = Integer.parseInt(localBundle.getString("server_operate_type"));
    this.mUrl = localBundle.getString("server_operate_url");
    this.webView.loadUrl(this.mUrl);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
      webView.goBack();// 返回前一个页面
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  class MyJavaScriptInterface
  {
    MyJavaScriptInterface()
    {
    }

    public void processHTML(String paramString)
    {
      Log.i("WebViewActivity", paramString);
      WebViewActivity.this.doServerReturnResult(paramString);
    }
  }
}

/* Location:           D:\android studio\fanbianyi\classes-dex2jar (2).jar
 * Qualified Name:     com.tds.test.WebViewActivity
 * JD-Core Version:    0.6.2
 */