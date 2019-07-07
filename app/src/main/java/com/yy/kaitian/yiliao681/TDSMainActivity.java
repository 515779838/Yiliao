package com.yy.kaitian.yiliao681;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.yy.kaitian.yiliao681.bean.UpDate;
import com.yy.kaitian.yiliao681.utils.AppLog;
import com.yy.kaitian.yiliao681.utils.GsonUtils;
import com.yy.kaitian.yiliao681.utils.PathUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import okhttp3.Call;

public class TDSMainActivity extends Activity {
    private SoundPool soundPool;
    public static final int CUSTOMER_DETECT_TYPE_CONTINUE = 1;
    public static final int CUSTOMER_DETECT_TYPE_NEW = 0;
    private static final boolean D = true;
    private static final int MESSAGE_CUSTOMER_SELECT_RESULT = 5;
    private static final int MESSAGE_INTEL_RESULT = 7;
    private static final int MESSAGE_POINT_DATA_SELECT_RESULT = 6;
    private static final int MESSAGE_RIGHT_TITLE_SET = 1;
    private static final int MESSAGE_SERVER_LOGIN_RESULT = 4;
    private static final int REQUEST_ENABLE_BT = 3;
    private static final String TAG = "TDSMainActivity";
    private static final int[] mImages = {R.drawable.user_manager, R.drawable.login, R.drawable.device, R.drawable.connect_device, R.drawable.analysis, R.drawable.report, R.drawable.exit};
    GridView gridView;
    private BluetoothAdapter mBluetoothAdapter = null;
    private TDSService mBoundService;
    private String mConnectedDeviceName = null;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder) {
            TDSMainActivity.this.mBoundService = ((TDSService.LocalBinder) paramAnonymousIBinder).getService();
            if (TDSMainActivity.this.mBoundService.getState() == 0)
                TDSMainActivity.this.mBoundService.start();
        }

        public void onServiceDisconnected(ComponentName paramAnonymousComponentName) {
            TDSMainActivity.this.mBoundService = null;
            Toast.makeText(TDSMainActivity.this, "Bind Server Disconnected", Toast.LENGTH_SHORT).show();
        }
    };
    private int mCustomerID = -1;
    private String mCustomerName = "";
    private int mDetectType;
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message paramAnonymousMessage) {
            switch (paramAnonymousMessage.what) {
                default:
                    return;
                case 1:
            }
            Log.i("TDSMainActivity", "MESSAGE_RIGHT_TITLE_SET : ");
            TDSMainActivity.this.setRightTitle();
        }
    };
    private ImageAdapter mIadapter;
    private int mIntelligentID = -1;
    private boolean mIsBound = false;
    private boolean mIsLoginServer = false;
    private boolean mIsReceiveRegister = false;
    private boolean mIsStart = false;
    private int mLoginPoint = 0;
    private TDSServiceReceiver mReceiver;
    private String mStrPWD = "";
    String[] mStrPointArrays;
    private String mStrUID = "";
    private TextView mTitle;
    private String mStrReportUrl = "";

    private void deviceManage() {
        startActivity(new Intent(this, DeviceManageActivity.class));
    }

    private void doBindService() {
        bindService(new Intent(this, TDSService.class), this.mConnection, BIND_AUTO_CREATE);
        this.mIsBound = true;
        Log.i("TDSTestActivity", "doBindService start ");
    }

    private void doRegisterReceiver() {
        if (!this.mIsReceiveRegister) {
            IntentFilter localIntentFilter = new IntentFilter("com.tds.test.state");
            registerReceiver(this.mReceiver, localIntentFilter);
            this.mIsReceiveRegister = true;
        }
    }

    private void doStartTdsServer() {
        startService(new Intent(this, TDSService.class));
    }

    private void doStopTdsServer() {
        stopService(new Intent(this, TDSService.class));
    }

    private void doUnRegisterReceiver() {
        if (this.mIsReceiveRegister) {
            unregisterReceiver(this.mReceiver);
            this.mIsReceiveRegister = false;
        }
    }

    private void doUnbindService() {
        if (this.mIsBound) {
            unbindService(this.mConnection);
            this.mIsBound = false;
        }
    }

    //智能解读
    private void getIntelligentReading()
            throws UnsupportedEncodingException {
        if (!this.mIsLoginServer) {
            showAlertDlg("您未登录系统，\n请在【登录系统】中登录", 1);
            return;
        }
        if (GetFinishDataCount() <= 0) {
            getReport();
            return;
        }
        startActivityForResult(new Intent(this, PointDatasPreferenceActivity.class), MESSAGE_POINT_DATA_SELECT_RESULT);
    }

    //取得报告
    private void getReport() {
        if (!this.mIsLoginServer) {
            showAlertDlg("您未登录系统，\n请在【登录系统】中登录", 1);
            return;
        }
//        String str2 = "http://www.hsnet.cn/InLight/LoginTo.asp?UID=" + this.mStrUID + "&PWD=" + str1 + "&URL=" + "../do_page/tds3/report_list_cn.asp";
        String str2 = mStrReportUrl;
        Log.i("TDSMainActivity", str2);
        Bundle localBundle = new Bundle();
        localBundle.putString("server_operate_type", "2");
        localBundle.putString("server_operate_url", str2);
        Intent localIntent = new Intent(this, WebViewActivity.class);
        localIntent.putExtras(localBundle);
        startActivity(localIntent);
    }

    private void loginServer() {
        startActivityForResult(new Intent(this, LoginRemoteServerActivity.class), MESSAGE_SERVER_LOGIN_RESULT);
    }

    private void setRightTitle() {
        if (this.mIsBound) ;
        switch (this.mBoundService.getState()) {
            default:
                return;
            case 3:
                this.mConnectedDeviceName = this.mBoundService.getConnectedDeviceName();
                this.mTitle.setText(R.string.title_connected_to);
                this.mTitle.append(this.mConnectedDeviceName);
                return;
            case 2:
                this.mTitle.setText(R.string.title_connecting);
                return;
            case 0:
            case MESSAGE_RIGHT_TITLE_SET:
                this.mTitle.setText(R.string.title_not_connected);
        }
    }

    private void setupTDSServer() {
        doStartTdsServer();
        doBindService();
        doRegisterReceiver();
        this.mIsStart = true;
    }

    private void showAlertDlg(String paramString, int paramInt) {
        Builder localBuilder = new Builder(this);
        localBuilder.setTitle("提示");
        if (paramInt == 0) {
            localBuilder.setMessage(paramString).setCancelable(false).setPositiveButton("OK", new OnClickListener() {
                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                    paramAnonymousDialogInterface.cancel();
                }
            }).setNegativeButton("Cancel", new OnClickListener() {
                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                }
            });
            localBuilder.create().show();
            return;
        }
        localBuilder.setMessage(paramString).setCancelable(false).setPositiveButton("OK", new OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                paramAnonymousDialogInterface.cancel();
            }
        });
        localBuilder.create().show();
    }

    private void startDetectPormpt() {
        startPointDetect();
    }

    private void startDeviceDetect() {

        Intent localIntent = new Intent(this, DetectMainActivity.class);
        Bundle localBundle = new Bundle();
        localBundle.putString("customer_detect_type", this.mDetectType + "");
        localBundle.putString("customer_id", this.mCustomerID + "");
        localBundle.putString("customer_name", this.mCustomerName + "");
        localIntent.putExtras(localBundle);
        startActivity(localIntent);
    }
    //开始检测

    private void startPointDetect() {
        startActivityForResult(new Intent(this, CustomerListPreferenceActivity.class), MESSAGE_CUSTOMER_SELECT_RESULT);
    }

    //提交点数据
    private void submitPointDatas(int paramInt) {
        MyDatabaseHandler localMyDatabaseHandler = new MyDatabaseHandler(this);
        PointDetectData localPointDetectData = localMyDatabaseHandler.getPointDataById(paramInt);
        if (localPointDetectData == null)
            return;
        CustomerProfilesClass localCustomerProfilesClass = localMyDatabaseHandler.getProfileById(localPointDetectData.getCustomerID());
         if(localCustomerProfilesClass == null)
             return;
        this.mIntelligentID = paramInt;
//        String str1 = "" + "ф" + localCustomerProfilesClass.getName();
//
//        String str2;
//        String str3;
////        int i;
//        String str9 = "";
//        if (localCustomerProfilesClass.getSex().equals("Female")) {
//            str2 = str1 + "фF";
//            str3 = String.valueOf(String.valueOf(String.valueOf(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(str2)).append("ф").append(localCustomerProfilesClass.getBirthday()).toString())).append("ф").append(localPointDetectData.getDetectTime()).toString()) + "ф" + localCustomerProfilesClass.getHeight()) + "ф" + localCustomerProfilesClass.getWeight()) + "ф" + localCustomerProfilesClass.getSystolicPressure()) + "ф" + localCustomerProfilesClass.getDiastolicPressure() + "ф" + localCustomerProfilesClass.getPulse();
//
//            String str4 = localCustomerProfilesClass.getMedicalHistory1();
//            if (str4.equals("无"))
//                str4 = "";
//            String str5 = str3 + "ф" + str4;
//            String str6 = localCustomerProfilesClass.getMedicalHistory2();
//            if (str6.equals("无"))
//                str6 = "";
//            String str7 = str5 + "|" + str6;
//
//            String str8 = localCustomerProfilesClass.getMedicalHistory3();
//            if (str8.equals("无"))
//                str8 = "";
////            str9 = ;
//            str9 = new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(str7)).append("|").append(str8).append("|").toString())).append("ф").append(localCustomerProfilesClass.getNativePlace()).toString() + "ф" + localCustomerProfilesClass.getProfession();
//            if (!localCustomerProfilesClass.getMaritalStatus().equals("married"))
//                return;
//        }
////    label774:
//        for (String str10 = str9 + "фM"; ; str10 = str9 + "фC") {
//            String str11 = TDSUtils.md5(this.mStrPWD);
//            String str12 = new StringBuilder(String.valueOf(str10)).append("ф").append(str11).toString() + "ф" + localCustomerProfilesClass.getMemo();
//            Log.i("TDSMainActivity", str12);
//            String str13 = UrlApi.BaseUrl + UrlApi.up_date + "?sDate" + this.mStrUID + str12;
//            Log.i("TDSMainActivity", str13);
//            Bundle localBundle = new Bundle();
//            localBundle.putString("server_operate_type", "3");
//            localBundle.putString("server_operate_url", str13);
//            Intent localIntent = new Intent(this, WebViewActivity.class);
//            localIntent.putExtras(localBundle);
//            startActivityForResult(localIntent, MESSAGE_INTEL_RESULT);
//            return;
//      str2 = str1 + "фM";
//      break;
//      label737: str3 = str3 + "ф" + localPointDetectData.getPointValue(i);
//      i++;
//      break label295;
//            break;
//        }
        StringBuilder builder = new StringBuilder();
        builder.append(localCustomerProfilesClass.getMemo());
        builder.append("ф");
        builder.append(localCustomerProfilesClass.getName());
        builder.append("ф");
        builder.append(localCustomerProfilesClass.getBirthday());
        builder.append("ф");
        builder.append(localCustomerProfilesClass.getNativePlace());
        builder.append("ф");
        builder.append(localCustomerProfilesClass.getProfession());
        builder.append("ф");
        builder.append("Female".equals(localCustomerProfilesClass.getSex())?"F":"M");
        builder.append("ф");
        builder.append("married".equals(localCustomerProfilesClass.getMaritalStatus())?"M":"C");
        builder.append("ф");

        builder.append(localCustomerProfilesClass.getHeight());
        builder.append("ф");
        builder.append(localCustomerProfilesClass.getWeight());
        builder.append("ф");
        builder.append(localCustomerProfilesClass.getSystolicPressure());
        builder.append("ф");
        builder.append(localCustomerProfilesClass.getDiastolicPressure());
        builder.append("ф");
        builder.append(localCustomerProfilesClass.getPulse());
        builder.append("ф");


        builder.append("无".equals(localCustomerProfilesClass.getMedicalHistory1())?"":localCustomerProfilesClass.getMedicalHistory1());
        builder.append("|");
        builder.append("无".equals(localCustomerProfilesClass.getMedicalHistory2())?"":localCustomerProfilesClass.getMedicalHistory2());
        builder.append("|");
        builder.append("无".equals(localCustomerProfilesClass.getMedicalHistory3())?"":localCustomerProfilesClass.getMedicalHistory3());
        builder.append("ф");
        builder.append(mStrUID);
        builder.append("ф");
        builder.append(TDSUtils.md5(this.mStrPWD));
        builder.append("ф");
        builder.append(localPointDetectData.getDetectTime());
//        builder.append(localCustomerProfilesClass.getMemo());
        for (int i = 0; i < 24; i++) {
            builder.append("ф");
            builder.append(localPointDetectData.getPointValue(i));
        }
        AppLog.instance().d(builder.toString());
        OkHttpUtils.get().tag(this)
                .url(UrlApi.BaseUrl+UrlApi.up_date)
                .addParams("sDate",builder.toString())
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }
            @Override
            public void onResponse(String response, int id) {
                response = response.substring(2, response.length() - 2).replace("\\", "");
                UpDate upDate = GsonUtils.INSTANCE.parseToBean(response, UpDate.class);
                if(upDate != null){
                    if(upDate.isResult()){
                            if (mIntelligentID != -1) {
                                new MyDatabaseHandler(getApplicationContext()).deletePointData(mIntelligentID);
                                mIntelligentID = -1;
                            }
                            mLoginPoint = (-1 + mLoginPoint);
                            String str2 = "已登录：" + mLoginPoint;
                            mTitle = ((TextView) findViewById(R.id.title_left_text));
                            mTitle.setText(str2);
                            mTitle = ((TextView) findViewById(R.id.title_right_text));
                            getReport();
                        return;
                    }
                }
                Toast.makeText(TDSMainActivity.this, "失败!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void userManager() {
        startActivity(new Intent(this, ProfilesListActivity.class));
    }

    int GetFinishDataCount() {
        MyDatabaseHandler localMyDatabaseHandler = new MyDatabaseHandler(this);
        int i = localMyDatabaseHandler.getPointDataCount();
        Log.i("TDSMainActivity", "num=" + i);
        if (i <= 0) {
            i = 0;
            return i;
        }
        i = 0;
        List<PointDetectData> localList = localMyDatabaseHandler.getAllPointDatas();
        int k = 0;
        while (k < localList.size()) {
            if (localList.get(k).isPointDetectFinish())
                i++;
            k++;
        }
        return i;
    }

    public void client(String paramString)
            throws UnknownHostException, SocketException {
        InetAddress localInetAddress = InetAddress.getByName("192.168.0.112");
        DatagramSocket localDatagramSocket = new DatagramSocket();
        try {
            byte[] arrayOfByte = paramString.getBytes();
            localDatagramSocket.send(new DatagramPacket(arrayOfByte, arrayOfByte.length, localInetAddress, 6800));
            Log.i("TDSMainActivity", "send finish kkkdkdkdkdkkd==============");
            localDatagramSocket.close();
            return;
        } catch (Exception localException) {
            while (true)
                localException.printStackTrace();
        }
    }

    public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {
        Log.d("TDSMainActivity", "onActivityResult " + paramInt2);
        switch (paramInt1) {
            default:
                setupTDSServer();
                break;
            case REQUEST_ENABLE_BT:
                if (paramInt2 == Activity.RESULT_OK) {

                } else {
                    Log.d("TDSMainActivity", "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case MESSAGE_SERVER_LOGIN_RESULT:
                if (paramInt2 == -1) {
                    Bundle localBundle2 = paramIntent.getExtras();
                    if (localBundle2.getString("login_result").equals("OK")) {
                        this.mIsLoginServer = true;
                        this.mStrUID = localBundle2.getString("login_uid");
                        this.mStrPWD = localBundle2.getString("login_pwd");
                        String str3 = localBundle2.getString("login_point");
                        mStrReportUrl = localBundle2.getString("url");
                        try {
                            this.mLoginPoint = Integer.parseInt(str3 != null ? str3.trim() : "0");
                            String str4 = "已登录：" + this.mLoginPoint;
                            this.mTitle = ((TextView) findViewById(R.id.title_left_text));
                            this.mTitle.setText(str4);
                            this.mTitle = ((TextView) findViewById(R.id.title_right_text));
                            Log.i("TDSMainActivity", "uid=" + this.mStrUID + " pwd=" + this.mStrPWD);
                            return;
                        } catch (Exception e) {
                            this.mIsLoginServer = false;
                            this.mStrUID = "";
                            this.mStrPWD = "";
                            Log.i("TDSMainActivity", str3);
                        }
                    }
                } else {
                    this.mIsLoginServer = false;
                    this.mStrUID = "";
                    this.mStrPWD = "";
                }
                break;
            case MESSAGE_CUSTOMER_SELECT_RESULT:
                if (paramInt2 == -1) {
                    Bundle localBundle1 = paramIntent.getExtras();
                    this.mDetectType = Integer.parseInt(localBundle1.getString("customer_type"));
                    this.mCustomerName = localBundle1.getString("customer_name");
                    this.mCustomerID = Integer.parseInt(localBundle1.getString("customer_id"));
                    Log.i("TDSMainActivity", "name=" + this.mCustomerName + " id=" + this.mCustomerID + " type=" + this.mDetectType);
                    startDeviceDetect();
                }
                break;
            case MESSAGE_POINT_DATA_SELECT_RESULT:
                if (paramInt2 != -1)
                    break;
                final int i = Integer.parseInt(paramIntent.getExtras().getString("point_datas_id"));
                Log.i("TDSMainActivity", "point_datas_id=" + i);
                if (this.mLoginPoint <= 0) {
                    Builder localBuilder1 = new Builder(this);
                    localBuilder1.setMessage("你没有足够的次数可以使用!").setPositiveButton("确定", new OnClickListener() {
                        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                            paramAnonymousDialogInterface.cancel();
                        }
                    });
                    localBuilder1.create().show();
                    return;
                }
                Builder localBuilder2 = new Builder(this);
                localBuilder2.setMessage("本次操作要扣除1个点数，是否要继续？").setCancelable(false).setPositiveButton("确定", new OnClickListener() {
                    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                        TDSMainActivity.this.submitPointDatas(i);
                    }
                }).setNegativeButton("取消", new OnClickListener() {
                    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                        paramAnonymousDialogInterface.cancel();
                    }
                });
                localBuilder2.create().show();
                break;
//            case MESSAGE_INTEL_RESULT:
//
//                break;
        }
    }
//        String str1;
//        do {
//            do {
//                do {
//                    do {
//        return;
//                        if (paramInt2 == -1) {
//                            setupTDSServer();
//                            return;
//                        }
//            Log.d("TDSMainActivity", "BT not enabled");
//            Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//          }
//          while (paramInt2 != -1);
//          Bundle localBundle2 = paramIntent.getExtras();
//          if (localBundle2.getString("login_result").equals("OK"))
//          {
//            this.mIsLoginServer = true;
//            this.mStrUID = localBundle2.getString("login_uid");
//            this.mStrPWD = localBundle2.getString("login_pwd");
//            String str3 = localBundle2.getString("login_point");
//            str3.trim();
//            try
//            {
//              this.mLoginPoint = Integer.parseInt(str3);
//              String str4 = "已登录：" + this.mLoginPoint;
//              this.mTitle = ((TextView)findViewById(R.id.title_left_text));
//              this.mTitle.setText(str4);
//              this.mTitle = ((TextView)findViewById(R.id.title_right_text));
//              Log.i("TDSMainActivity", "uid=" + this.mStrUID + " pwd=" + this.mStrPWD);
//              return;
//            }
//            catch (NumberFormatException localNumberFormatException)
//            {
//              while (true)
//                Log.i("TDSMainActivity", str3);
//            }
//          }
//          this.mIsLoginServer = false;
//          this.mStrUID = "";
//          this.mStrPWD = "";
////          return;
//        }
//

//        while (paramInt2 != -1);
//        final int i = Integer.parseInt(paramIntent.getExtras().getString("point_datas_id"));
//        Log.i("TDSMainActivity", "point_datas_id=" + i);
//        if (this.mLoginPoint <= 0) {
//          Builder localBuilder1 = new Builder(this);
//          localBuilder1.setMessage("你没有足够的次数可以使用!").setPositiveButton("确定", new OnClickListener() {
//            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
//              paramAnonymousDialogInterface.cancel();
//            }
//          });
//          localBuilder1.create().show();
//          return;
//        }
//        Builder localBuilder2 = new Builder(this);
//        localBuilder2.setMessage("本次操作要扣除1个点数，是否要继续？").setCancelable(false).setPositiveButton("确定", new OnClickListener() {
//          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
//            TDSMainActivity.this.submitPointDatas(i);
//          }
//        }).setNegativeButton("取消", new OnClickListener() {
//          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
//            paramAnonymousDialogInterface.cancel();
//          }
//        });
//        localBuilder2.create().show();

//      return;
//        String str1 = paramIntent.getExtras().getString("intelligent_result");
//        Log.i("TDSMainActivity", "intellegent=" + str1);
//        if (paramInt2 == -1) {
//            Log.i("TDSMainActivity", "intellegent ok=" + str1);
//            if (this.mIntelligentID != -1) {
//                new MyDatabaseHandler(this).deletePointData(this.mIntelligentID);
//                this.mIntelligentID = -1;
//            }
//            this.mLoginPoint = (-1 + this.mLoginPoint);
//            String str2 = "已登录：" + this.mLoginPoint;
//            this.mTitle = ((TextView) findViewById(R.id.title_left_text));
//            this.mTitle.setText(str2);
//            this.mTitle = ((TextView) findViewById(R.id.title_right_text));
//            getReport();
//            return;
//        }
//    }

    //                    while (paramInt2 != 0);
//                    Log.i("TDSMainActivity", "intellegent err=" + str1);
//                    Toast.makeText(getApplicationContext(), str1, Toast.LENGTH_SHORT).show();
//                }
    private long openTime = 0;
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        PathUtils.init();
        this.soundPool = new SoundPool(4, 3, 100);
        Log.e("TDSMainActivity", "+++ ON CREATE +++");
        AppLog.instance().iToSd("TDSMainActivity");

        getWindow().setFlags(128, 128);
        requestWindowFeature(7);
        setContentView(R.layout.detect_main);
        getWindow().setFeatureInt(7, R.layout.custom_title);
//        MyPreferManager.getInstance().init(getApplicationContext());
//        long xianzhiTime = 1510904972000L;
//        openTime = System.currentTimeMillis();
//        if(System.currentTimeMillis() - xianzhiTime > 3600 * 1000 *24 *1){
//            MyPreferManager.getInstance().saveIsVYouXiao(false);
//        } else {
//        }
//        if(!MyPreferManager.getInstance().getIsYouXiao()){
//            System.exit(0);
//        }
        this.mTitle = ((TextView) findViewById(R.id.title_left_text));
        this.mTitle.setText(R.string.app_name);
        this.mTitle = ((TextView) findViewById(R.id.title_right_text));
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        this.mReceiver = new TDSServiceReceiver();
        this.gridView = ((GridView) findViewById(R.id.gridView1));
        this.mStrPointArrays = getResources().getStringArray(R.array.main_arrays);
        this.mIadapter = new ImageAdapter(this);

        for (int i = 0; ; i++) {
            if (i >= mImages.length) {
                this.gridView.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong) {
                        switch (paramAnonymousInt) {
                            default:
                                break;
                            case 0:
                                TDSMainActivity.this.loginServer();//登录
                                break;
                            case 1:
                                TDSMainActivity.this.userManager();//用户管理
                                break;
                            case 2:
                                TDSMainActivity.this.deviceManage();//设备管理
                                break;
                            case 3:
                                TDSMainActivity.this.startDetectPormpt();//开始检测
                                break;
                            case 4:
                                try {
                                    TDSMainActivity.this.getIntelligentReading();//智能解读
                                    break;
                                } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
                                    localUnsupportedEncodingException.printStackTrace();
                                    break;
                                }
                            case 5:
                                TDSMainActivity.this.getReport();//获取报告
                                break;
                            case 6:
                                TDSMainActivity.this.finish();//退出系统
                                break;
                        }
                    }
                });
                break;
            }
            this.mIadapter.addItem(mImages[i], this.mStrPointArrays[i], -1);
        }
        this.gridView.setAdapter(this.mIadapter);

    }

    public void onDestroy() {
        super.onDestroy();
        if (this.mIsStart)
            this.mBoundService.stop();
        Log.e("TDSMainActivity", "--- ON DESTROY ---");
        this.mIsStart = false;
        doUnbindService();
        doStopTdsServer();
    }

    public void onPause() {
        try {
            super.onPause();
            Log.e("TDSMainActivity", "- ON PAUSE -");
        }
//    catch (Exception e){
//    }
        finally {
//      localObject = finally;
//      throw localObject;
        }
    }


    public void onStart() {
        super.onStart();
        Log.e("TDSMainActivity", "++ ON START ++");
        if (!this.mBluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), REQUEST_ENABLE_BT);
            return;
        }
        if (!this.mIsStart) {
            setupTDSServer();
            return;
        }
        doBindService();
        doRegisterReceiver();
        Message localMessage = this.mHandler.obtainMessage(1);
        this.mHandler.sendMessageDelayed(localMessage, 1000L);
    }

    public void onStop() {
        super.onStop();
        Log.e("TDSMainActivity", "-- ON STOP --");
        doUnbindService();
        doUnRegisterReceiver();
    }

    public class TDSServiceReceiver extends BroadcastReceiver {
        public TDSServiceReceiver() {
        }

        public void onReceive(Context paramContext, Intent paramIntent) {
            switch (Integer.valueOf(paramIntent.getStringExtra("BT_MESSAGE"))) {
                default:
                    return;
                case 2:
                    TDSMainActivity.this.mConnectedDeviceName = TDSMainActivity.this.mBoundService.getConnectedDeviceName();
                    Toast.makeText(TDSMainActivity.this.getApplicationContext(), "Connected to " + TDSMainActivity.this.mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    return;
                case 1:
                    switch (TDSMainActivity.this.mBoundService.getState()) {
                        default:
                            return;
                        case 0:
                        case 1:
                            TDSMainActivity.this.mTitle.setText(R.string.title_not_connected);
                            return;
                        case 3:
                            TDSMainActivity.this.mTitle.setText(R.string.title_connected_to);
                            TDSMainActivity.this.mTitle.append(TDSMainActivity.this.mConnectedDeviceName);
                            return;
                        case 2:
                            TDSMainActivity.this.mTitle.setText(R.string.title_connecting);
                            return;
                    }
                case 3:
                    Toast.makeText(paramContext, "Unable to connect device", Toast.LENGTH_SHORT).show();

                    return;
                case 4:
//                    int a = soundPool.load(TDSMainActivity.this, R.raw.alarm, 0);
//                    int ii = ((AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE)).getStreamVolume(3);
//                    soundPool.play(a, ii, ii, 1, 0, 1.0F);
//                    Builder builder = new Builder(getApplicationContext());
//                    builder.setTitle("蓝牙链接断开").setPositiveButton("知道了", new OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.dismiss();
//                        }
//                    }).setNegativeButton("取消", new OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.dismiss();
//                        }
//                    }).create().show();

                    Toast.makeText(paramContext, "Device connection was lost", Toast.LENGTH_SHORT).show();
                    return;
                case 5://得到电压响应
                    int i = TDSMainActivity.this.mBoundService.mTDSFrameParse.GetFrameVoltage();
                    int j = TDSMainActivity.this.mBoundService.mTDSFrameParse.GetFrameVoltageNum();
                    Toast.makeText(paramContext, "GETVOLTAGE_RESPONSE[" + j + "] = " + i, Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }

}

/* Location:           D:\android studio\fanbianyi\classes-dex2jar (2).jar
 * Qualified Name:     com.tds.test.TDSMainActivity
 * JD-Core Version:    0.6.2
 */