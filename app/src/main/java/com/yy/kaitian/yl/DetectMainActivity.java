package com.yy.kaitian.yl;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class DetectMainActivity extends Activity {
    private static final boolean D = true;
    private static final String TAG = "DetectMainActivity";
    private final int AUTO_DETECT_MODE_ALL = 1;
    private final int AUTO_DETECT_MODE_CONTINUE = 2;
    private final int MAX_DETECT_POINT = 24;
    private final int TDS_FINAL_POINT_SAVE = 4;
    private final int TDS_POINT_AUTO_DETECT = 2;
    private final int TDS_POINT_DETECT_RESULT = 1;
    private final int TDS_POINT_MANUAL_DETECT = 3;
    GridView gridView;
    private int mAutoDetectMode;
    private int mAutoPoint;
    private int mCustomerDetectType;
    private int mCustomerId = -1;
    private String mCustomerName;
    private int mFinalNum;
    private int mFinalValue;
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message paramAnonymousMessage) {
            progressDialog.dismiss();
            switch (paramAnonymousMessage.what) {
                default:
                case TDS_POINT_AUTO_DETECT:
                    DetectMainActivity.this.doPointDetect(DetectMainActivity.this.mAutoPoint);
                    break;
                case TDS_POINT_MANUAL_DETECT:
                    DetectMainActivity.this.doPointDetect(DetectMainActivity.this.mManualPoint);
                    break;
                case TDS_FINAL_POINT_SAVE:
                    double d1 = 192.941D - 0.18861D * DetectMainActivity.this.mFinalValue;
                    double d2 = new Double(new DecimalFormat(".00").format(d1)).doubleValue();
                    String str = d2 + "";
                    DetectMainActivity.this.mPointDatas.setPointValue(DetectMainActivity.this.mFinalNum, str);
                    MyDatabaseHandler localMyDatabaseHandler = new MyDatabaseHandler(DetectMainActivity.this);
                    DetectMainActivity.this.mPointDatas.setDetectTime(TDSUtils.getCurrentTimeString());
                    int i = localMyDatabaseHandler.updatePointData(DetectMainActivity.this.mPointDatas);
                    Log.i("DetectMainActivity", "updatePointData nRet=" + i);
                    DetectMainActivity.this.mUnFinishPointNum = DetectMainActivity.this.mPointDatas.getUnfinishedPointNum();
                    //DetectMainActivity.this.mTitle.setText("未检穴位数:" + DetectMainActivity.this.mUnFinishPointNum + "  客户名称:" + DetectMainActivity.this.mCustomerName);
                    ((TextView) findViewById(R.id.tv_name)).setText("客户名称:" + DetectMainActivity.this.mCustomerName);
                    ((TextView) findViewById(R.id.tv_num)).setText("未检穴位数:" + DetectMainActivity.this.mUnFinishPointNum);
                    break;
            }
//            do {
////        return;
////        DetectMainActivity.this.doPointDetect(DetectMainActivity.this.mAutoPoint);
////        return;
////                DetectMainActivity.this.doPointDetect(DetectMainActivity.this.mManualPoint);
////        return;
//                double d1 = 192.941D - 0.18861D * DetectMainActivity.this.mFinalValue;
//                double d2 = new Double(new DecimalFormat(".00").format(d1)).doubleValue();
//                String str = d2 + "";
//                DetectMainActivity.this.mPointDatas.setPointValue(DetectMainActivity.this.mFinalNum, str);
//                MyDatabaseHandler localMyDatabaseHandler = new MyDatabaseHandler(DetectMainActivity.this);
//                DetectMainActivity.this.mPointDatas.setDetectTime(TDSUtils.getCurrentTimeString());
//                int i = localMyDatabaseHandler.updatePointData(DetectMainActivity.this.mPointDatas);
//                Log.i("DetectMainActivity", "updatePointData nRet=" + i);
//                DetectMainActivity.this.mUnFinishPointNum = DetectMainActivity.this.mPointDatas.getUnfinishedPointNum();
//                DetectMainActivity.this.mTitle.setText("未检穴位数:" + DetectMainActivity.this.mUnFinishPointNum + "  客户名称:" + DetectMainActivity.this.mCustomerName);
//            }
//            while (!DetectMainActivity.this.mbAutoDetect);
//            DetectMainActivity.this.doNextDetect();
        }
    };
    private ImageAdapter mIadapter;
    private final int[] mImages = {R.drawable.m1, R.drawable.m2, R.drawable.m3, R.drawable.m4, R.drawable.m5, R.drawable.m6,
            R.drawable.m7, R.drawable.m8, R.drawable.m9, R.drawable.m10, R.drawable.m11, R.drawable.m12, R.drawable.m13, R.drawable.m14, R.drawable.m15, R.drawable.m16,
            R.drawable.m17, R.drawable.m18, R.drawable.m19, R.drawable.m20, R.drawable.m21, R.drawable.m22, R.drawable.m23, R.drawable.m24};
    private int mManualPoint;
    private PointDetectData mPointDatas = null;
    String[] mStrPointArrays;
    private TextView mTitle;
    private int mUnFinishPointNum = 0;
    private boolean mbAutoDetect;
    private boolean mbDetectFirst = true;

    private ProgressDialog progressDialog;

    private void doNextDetect() {
        this.mAutoPoint = (1 + this.mAutoPoint);
        if (this.mAutoPoint >= 24) {
            finish();
            return;
        }
        if (this.mAutoDetectMode == AUTO_DETECT_MODE_CONTINUE) {
            for (int i = this.mAutoPoint; ; i++) {

                if (i >= 24) ;

                while (this.mPointDatas.getPointValue(i).equals("null")) {
                    if (i < 24) {
                        break;
                    }
                    this.mAutoPoint = i;
                    finish();
                    return;
                }
            }
        }
        Message localMessage = this.mHandler.obtainMessage(TDS_POINT_AUTO_DETECT);
        this.mHandler.sendMessageDelayed(localMessage, 1000L);
    }

    private void insertNewPointDatasItem() {
        MyDatabaseHandler localMyDatabaseHandler = new MyDatabaseHandler(this);
        this.mPointDatas = new PointDetectData();
        this.mPointDatas.setCustomerID(this.mCustomerId);
        this.mPointDatas.setDetectTime(TDSUtils.getCurrentTimeString());
        this.mPointDatas.initPointValues();
        long l = localMyDatabaseHandler.addPointData(this.mPointDatas);
        if (l == -1L) {
            Log.i("DetectMainActivity", "添加失败！");
            return;
        }
        this.mPointDatas.setID((int) l);
        Log.i("DetectMainActivity", "添加成功！" + l + " " + this.mPointDatas._id);
    }

    protected void doPointDetect(int paramInt) {
        Bundle localBundle = new Bundle();
        localBundle.putString("point_id", paramInt + "");
        Intent localIntent = new Intent(this, PointDetectActivity.class);
        localIntent.putExtras(localBundle);
        startActivityForResult(localIntent, TDS_POINT_DETECT_RESULT);
    }

    protected void doSetPointDetected(int paramInt) {
        this.mIadapter.setItemColor(paramInt, Color.RED);
        this.mIadapter.notifyDataSetChanged();
        this.gridView.invalidateViews();
    }

    protected void doStartAutoPointDetect() {
        this.mbAutoDetect = true;
        this.mAutoPoint = 0;
        if ((this.mAutoDetectMode == AUTO_DETECT_MODE_CONTINUE) && (this.mUnFinishPointNum != 24)) {
            for (int i = this.mAutoPoint; ; i++) {
                if (i >= 24) ;
                while (this.mPointDatas.getPointValue(i).equals("null")) {
                    if (i < 24)
                        break;
                    Toast.makeText(getApplicationContext(), "全部穴位检测完成！", Toast.LENGTH_SHORT).show();
                    this.mAutoPoint = i;//// TODO: 2017/11/11
                    return;
                }
            }
        }
        Message localMessage = this.mHandler.obtainMessage(TDS_POINT_AUTO_DETECT);
        this.mHandler.sendMessageDelayed(localMessage, 10L);
    }

    protected void doStartManualPointDetect(int paramInt) {
        this.mManualPoint = paramInt;
        this.mbAutoDetect = false;
        Message localMessage = this.mHandler.obtainMessage(TDS_POINT_MANUAL_DETECT);
        this.mHandler.sendMessageDelayed(localMessage, 100L);
    }

    public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {
        Log.d("DetectMainActivity", "onActivityResult " + paramInt2);
        switch (paramInt1) {
            default:
                break;
            case TDS_POINT_DETECT_RESULT:
                if (paramInt2 == -1) {
                    if (this.mbDetectFirst) {
                        insertNewPointDatasItem();
                        this.mbDetectFirst = false;
                    }
                }
                Bundle localBundle = null;
                try {
                    localBundle = paramIntent.getExtras();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                int i = Integer.parseInt(localBundle.getString("point_num"));
                int j = Integer.parseInt(localBundle.getString("point_value"));
                doSetPointDetected(i);
                this.mUnFinishPointNum = this.mPointDatas.getUnfinishedPointNum();
                if ((this.mUnFinishPointNum == 1) && (i == 23)) {
                    this.mFinalNum = i;
                    this.mFinalValue = j;
                    AlertDialog.Builder localBuilder1 = new AlertDialog.Builder(this);
                    localBuilder1.setTitle("提示");
                    AlertDialog.Builder localBuilder2 = localBuilder1.setMessage("全部穴位检测完成，是否保存最后一个检测数据？").setCancelable(false);
                    DialogInterface.OnClickListener local5 = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                            paramAnonymousDialogInterface.cancel();
                            Message localMessage = DetectMainActivity.this.mHandler.obtainMessage(TDS_FINAL_POINT_SAVE);
                            DetectMainActivity.this.mHandler.sendMessageDelayed(localMessage, 300L);
                        }
                    };
                    AlertDialog.Builder localBuilder3 = localBuilder2.setPositiveButton("OK", local5);
                    DialogInterface.OnClickListener local6 = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                            paramAnonymousDialogInterface.cancel();
                        }
                    };
                    localBuilder3.setNegativeButton("Cancel", local6);
                    localBuilder1.create().show();
                    return;
                }
                double d1 = 192.941D - 0.18861D * j;
                double d2 = new Double(new DecimalFormat(".00").format(d1)).doubleValue();
                String str = d2 + "";
                this.mPointDatas.setPointValue(i, str);
                this.mPointDatas.setDetectTime(TDSUtils.getCurrentTimeString());
                int k = new MyDatabaseHandler(this).updatePointData(this.mPointDatas);
                Log.i("DetectMainActivity", "updatePointData nRet=" + k);
                this.mUnFinishPointNum = this.mPointDatas.getUnfinishedPointNum();
                // this.mTitle.setText("未检穴位数:" + this.mUnFinishPointNum + "  客户名称:" + this.mCustomerName);
                ((TextView) findViewById(R.id.tv_name)).setText("客户名称:" + this.mCustomerName);
                ((TextView) findViewById(R.id.tv_num)).setText("未检穴位数:" + this.mUnFinishPointNum);
                break;
        }
//    do
//    {
//      do
//        return;
//      while (paramInt2 != -1);
//      if (this.mbDetectFirst)
//      {
//        insertNewPointDatasItem();
//        this.mbDetectFirst = false;
//      }
//      Bundle localBundle = paramIntent.getExtras();
//      int i = Integer.parseInt(localBundle.getString("point_num"));
//      int j = Integer.parseInt(localBundle.getString("point_value"));
//      doSetPointDetected(i);
//      this.mUnFinishPointNum = this.mPointDatas.getUnfinishedPointNum();
//      if ((this.mUnFinishPointNum == 1) && (i == 23))
//      {
//        this.mFinalNum = i;
//        this.mFinalValue = j;
//        Builder localBuilder1 = new Builder(this);
//        localBuilder1.setTitle("提示");
//        Builder localBuilder2 = localBuilder1.setMessage("全部穴位检测完成，是否保存最后一个检测数据？").setCancelable(false);
//        DialogInterface.OnClickListener local5 = new DialogInterface.OnClickListener()
//        {
//          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
//          {
//            paramAnonymousDialogInterface.cancel();
//            Message localMessage = DetectMainActivity.this.mHandler.obtainMessage(4);
//            DetectMainActivity.this.mHandler.sendMessageDelayed(localMessage, 300L);
//          }
//        };
//        Builder localBuilder3 = localBuilder2.setPositiveButton("OK", local5);
//        DialogInterface.OnClickListener local6 = new DialogInterface.OnClickListener()
//        {
//          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
//          {
//            paramAnonymousDialogInterface.cancel();
//          }
//        };
//        localBuilder3.setNegativeButton("Cancel", local6);
//        localBuilder1.create().show();
//        return;
//      }
//      double d1 = 192.941D - 0.18861D * j;
//      double d2 = new Double(new DecimalFormat(".00").format(d1)).doubleValue();
//      String str = d2+"";
//      this.mPointDatas.setPointValue(i, str);
//      this.mPointDatas.setDetectTime(TDSUtils.getCurrentTimeString());
//      int k = new MyDatabaseHandler(this).updatePointData(this.mPointDatas);
//      Log.i("DetectMainActivity", "updatePointData nRet=" + k);
//      this.mUnFinishPointNum = this.mPointDatas.getUnfinishedPointNum();
//      this.mTitle.setText("未检穴位数:" + this.mUnFinishPointNum + "  客户名称:" + this.mCustomerName);
//    }
        if (this.mbAutoDetect)
            doNextDetect();

    }

    public void onCreate(Bundle paramBundle) {
        //setTheme(R.style.CustomTitleBarTheme);
        super.onCreate(paramBundle);
        Log.e("DetectMainActivity", "+++ ON CREATE +++");
        // getWindow().setFlags(128, 128);
        // requestWindowFeature(7);
        setContentView(R.layout.detect_main2);
        // getWindow().setFeatureInt(7, R.layout.custom_title_device_manage);
        new Bundle();
        Bundle localBundle = getIntent().getExtras();
        this.mCustomerDetectType = Integer.parseInt(localBundle.getString("customer_detect_type"));
        this.mCustomerId = Integer.parseInt(localBundle.getString("customer_id"));
        this.mCustomerName = localBundle.getString("customer_name");
        Log.i("DetectMainActivity", "customer=" + this.mCustomerName + " id=" + this.mCustomerId + " type=" + this.mCustomerDetectType);
        if (this.mCustomerDetectType == 0) {
            mUnFinishPointNum = 24;
            this.mbDetectFirst = true;
        } else {
            this.mbDetectFirst = false;
            this.mPointDatas = new MyDatabaseHandler(this).getPointDataById(this.mCustomerId);
            mUnFinishPointNum = mPointDatas.getUnfinishedPointNum();
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("准备中...");
        progressDialog.setCancelable(false);

        findViewById(R.id.rl_check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // progressDialog.show();
                DetectMainActivity.this.mAutoDetectMode = AUTO_DETECT_MODE_ALL;
                DetectMainActivity.this.doStartAutoPointDetect();//检测所有穴位
            }
        });
        findViewById(R.id.ll_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ((TextView) findViewById(R.id.tv_name)).setText("客户名称:" + this.mCustomerName);
        ((TextView) findViewById(R.id.tv_num)).setText("未检穴位数:" + this.mUnFinishPointNum);

        this.gridView = ((GridView) findViewById(R.id.gridView1));
        this.mStrPointArrays = getResources().getStringArray(R.array.point_arrays);
        this.mIadapter = new ImageAdapter(this);
        this.gridView.setAdapter(this.mIadapter);
//    i = 0;
//    if (i < this.mImages.length)
        this.gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong) {
                DetectMainActivity.this.doStartManualPointDetect(paramAnonymousInt);
            }
        });


//        int i;
//        for (this.mUnFinishPointNum = 24; ; this.mUnFinishPointNum = this.mPointDatas.getUnfinishedPointNum()) {
//      Button localButton1 = (Button)findViewById(R.id.connect_device);
//      localButton1.setTextSize(12.0F);
//      localButton1.setText(R.string.auto_detect_all);
//      localButton1.setOnClickListener(new View.OnClickListener()
//      {
//        public void onClick(View paramAnonymousView)
//        {
//          DetectMainActivity.this.mAutoDetectMode = 1;
//          DetectMainActivity.this.doStartAutoPointDetect();//检测所有穴位
//        }
//      });
//      Button localButton2 = (Button)findViewById(R.id.disconnect_device);
//      localButton2.setText(" 返回 ");
//      localButton2.setOnClickListener(new View.OnClickListener()
//      {
//        public void onClick(View paramAnonymousView)
//        {
//          DetectMainActivity.this.finish();
//        }
//      });
//      this.mTitle = ((TextView)findViewById(R.id.prompt_text));
//      this.mTitle.setText("未检穴位数:" + this.mUnFinishPointNum + "  客户名称:" + this.mCustomerName);
//      this.gridView = ((GridView)findViewById(R.id.gridView1));
//      this.mStrPointArrays = getResources().getStringArray(R.array.point_arrays);
//      this.mIadapter = new ImageAdapter(this);
//      this.gridView.setAdapter(this.mIadapter);
//      i = 0;
//      if (i < this.mImages.length)
//      this.gridView.setOnItemClickListener(new OnItemClickListener()
//      {
//        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
//        {
//          DetectMainActivity.this.doStartManualPointDetect(paramAnonymousInt);
//        }
//      });
//      this.mbDetectFirst = false;
//      this.mPointDatas = new MyDatabaseHandler(this).getPointDataById(this.mCustomerId);
//    }

        for (int i = 0, a = mImages.length; i < a; i++) {
            if (this.mCustomerDetectType == 0)
                this.mIadapter.addItem(this.mImages[i], this.mStrPointArrays[i], Color.parseColor("#ff666666"));
            else {
                if ("null".equals(this.mPointDatas.getPointValue(i)))
                    this.mIadapter.addItem(this.mImages[i], this.mStrPointArrays[i], Color.parseColor("#ff666666"));
                else
                    this.mIadapter.addItem(this.mImages[i], this.mStrPointArrays[i], Color.RED);
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu paramMenu) {
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
        switch (paramMenuItem.getItemId()) {
            default:
                return false;
            case R.id.detect_auto_all:
                this.mAutoDetectMode = AUTO_DETECT_MODE_ALL;
                doStartAutoPointDetect();
                return true;
            case R.id.detect_auto_undetect:
        }
        this.mAutoDetectMode = AUTO_DETECT_MODE_CONTINUE;
        doStartAutoPointDetect();
        return true;
    }

    protected void onResume() {
        super.onResume();
    }
}

/* Location:           D:\android studio\fanbianyi\classes-dex2jar (2).jar
 * Qualified Name:     com.tds.test.DetectMainActivity
 * JD-Core Version:    0.6.2
 */