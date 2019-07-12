package com.yy.kaitian.yl;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.DecimalFormat;
import java.util.HashMap;

public class PointDetectActivity extends Activity {
    private static final boolean D = true;
    private static final String TAG = "PointDetectActivity";
    public static final String TYPE = "type";
    private final int MESSAGE_RIGHT_TITLE_SET = 4;
    private final int TDS_CHECK_RESPONSE = 3;
    private final int TDS_POINT_DETECT_FINISH = 2;
    private final int TDS_TERMINAL_GET_VOLTAGE = 1;
    private ImageView image;
    private TDSService mBoundService;
    private GraphicalView mChartView;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder) {
            PointDetectActivity.this.mBoundService = ((TDSService.LocalBinder) paramAnonymousIBinder).getService();
        }

        public void onServiceDisconnected(ComponentName paramAnonymousComponentName) {
            PointDetectActivity.this.mBoundService = null;
        }
    };
    private int mCurrentPoint = 0;
    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    private int mDeviceResponseTime;
    private int mDeviceState = 0;
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message paramAnonymousMessage) {
            switch (paramAnonymousMessage.what) {
                default:
                    return;
                case 4:
                    Log.i("PointDetectActivity", "MESSAGE_RIGHT_TITLE_SET : ");
                    PointDetectActivity.this.setRightTitle();
                    return;
                case 1:
                    PointDetectActivity.this.getPointValue(PointDetectActivity.this.mCurrentPoint);
                    return;
                case 2:
                    PointDetectActivity.this.doResultFinish();
                    return;
                case 3:
                    PointDetectActivity.this.checkDeviceResponse();
            }
        }
    };


    private boolean mIsBound = false;
    private boolean mIsReceiveRegister = false;
    private int mPointAverageValue;//点 平均值
    private int mPointEqualTime;
    private int mPointTotalValue;//点总和
    private int[] mPointValueBuf;
    private final int mPointValueBufSize = 20;
    private int mPointVauleBufIndex;
    private TDSServiceReceiver mReceiver;
    private XYMultipleSeriesRenderer mRenderer;
    private XYSeries mSeries;
    private XYSeriesRenderer mSeriesRender;
    private final int mSetPointDetectTime = 10;
    private final int mSetPointMaxValue = 180;
    private final int mSetPointMinValue = 0;
    private final int mSetStablePercent = 5;
    private TextView mTitle;
    private boolean mbExit;
    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundPoolMap;

    private void ResetTDSXYChar() {
        this.mSeries.clear();
    }

    private final int[] mImages = {R.drawable.m1, R.drawable.m2, R.drawable.m3, R.drawable.m4, R.drawable.m5, R.drawable.m6,
            R.drawable.m7, R.drawable.m8, R.drawable.m9, R.drawable.m10, R.drawable.m11, R.drawable.m12, R.drawable.m13, R.drawable.m14, R.drawable.m15, R.drawable.m16,
            R.drawable.m17, R.drawable.m18, R.drawable.m19, R.drawable.m20, R.drawable.m21, R.drawable.m22, R.drawable.m23, R.drawable.m24};

    private void UpDateImage(int paramInt) {
        try {
            this.image.setImageResource(mImages[paramInt]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void UpDateXYChar(int paramInt, double paramDouble) {
        this.mSeries.add(paramInt, paramDouble);
        if (this.mChartView != null)
            this.mChartView.repaint();
    }

    private void XYCharInit() {
        this.mRenderer = buildRenderer(-16776961, PointStyle.CIRCLE);
        int i = this.mRenderer.getSeriesRendererCount();
        for (int j = 0; ; j++) {
            if (j >= i) {
                this.mRenderer.setChartTitle("Point Value");
                this.mRenderer.setXTitle("num");
                this.mRenderer.setXAxisMin(0.0D);
                this.mRenderer.setXAxisMax(20.0D);
                this.mRenderer.setYAxisMin(0.0D);
                this.mRenderer.setYAxisMax(180.0D);
                this.mRenderer.setAxesColor(-3355444);
                this.mRenderer.setLabelsColor(-3355444);
                this.mRenderer.setXLabels(12);
                this.mRenderer.setYLabels(10);
                this.mRenderer.setShowGrid(true);
                this.mRenderer.setXLabelsAlign(Paint.Align.RIGHT);
                this.mRenderer.setYLabelsAlign(Paint.Align.RIGHT);
                this.mRenderer.setZoomButtonsVisible(true);
                this.mRenderer.setPanLimits(new double[]{-10.0D, 20.0D, -10.0D, 40.0D});
                this.mRenderer.setZoomLimits(new double[]{-10.0D, 20.0D, -10.0D, 40.0D});
                this.mSeries = new XYSeries("Point Voltage", 0);
                this.mDataset.addSeries(this.mSeries);
                return;
            }
            ((XYSeriesRenderer) this.mRenderer.getSeriesRendererAt(j)).setFillPoints(true);
        }
    }

    private Boolean addPointValue(int paramInt) {
        double d1 = 192.941D - 0.18861D * paramInt;
        double d2 = Double.valueOf(new DecimalFormat(".00").format(d1));
        if ((d2 <= 3.0D) || (d2 >= 180.0D)) {
            this.mPointEqualTime = 0;
            this.mPointTotalValue = 0;
            this.mSeries.clear();
            mPointVauleBufIndex = 0;
            return false;
        }
        this.mPointValueBuf[this.mPointVauleBufIndex] = paramInt;
        if (this.mPointVauleBufIndex == 0)
            this.mPointAverageValue = paramInt;
        int i;
        if (paramInt > this.mPointAverageValue) {
            i = paramInt - this.mPointAverageValue;
        } else {
            i = this.mPointAverageValue - paramInt;
        }
        if (i * 100 / this.mPointAverageValue >= 5) {
            this.mPointEqualTime = 0;
            this.mPointTotalValue = 0;
            this.mSeries.clear();
            mPointVauleBufIndex = 0;
            return false;
        }
        this.mPointEqualTime = (1 + this.mPointEqualTime);
        this.mPointTotalValue = (paramInt + this.mPointTotalValue);
        this.mPointAverageValue = (this.mPointTotalValue / this.mPointEqualTime);
        if (this.mPointVauleBufIndex < 19) {
            UpDateXYChar(this.mPointVauleBufIndex, d2);
            this.mPointVauleBufIndex = (1 + this.mPointVauleBufIndex);
            if (this.mChartView != null)
                this.mChartView.repaint();
            return true;
        } else {
            return false;
        }
//        boolean bool = false;
//        if (paramInt > this.mPointAverageValue) {
//            i = paramInt - this.mPointAverageValue;
//            if (i * 100 / this.mPointAverageValue >= 5)
//                this.mPointEqualTime = (1 + this.mPointEqualTime);
//            this.mPointTotalValue = (paramInt + this.mPointTotalValue);
//            this.mPointAverageValue = (this.mPointTotalValue / this.mPointEqualTime);
//            bool = true;
//            if (this.mPointVauleBufIndex >= 19)
//                UpDateXYChar(this.mPointVauleBufIndex, d2);
//            this.mPointVauleBufIndex = (1 + this.mPointVauleBufIndex);
//        }

//    while (true)
//    {
//      Log.e("PointDetectActivity", "liuzy =======+=mPointEqualTime=" + this.mPointEqualTime);
//      return Boolean.valueOf(bool);
//      i = this.mPointAverageValue - paramInt;
//      break;
//      label230: this.mPointEqualTime = 0;
//      this.mPointTotalValue = 0;
//      this.mPointAverageValue = paramInt;
//      bool = false;
//      break label158;
//      label251: this.mPointVauleBufIndex = 0;
//      this.mSeries.clear();
//      if (this.mChartView != null)
//        this.mChartView.repaint();
//    }
    }

    private int mError = 0;

    private void checkDeviceResponse() {
        if ((this.mDeviceResponseTime == 0) && (!this.mbExit)) {
//            Toast.makeText(this, "终端设备没有回复！", Toast.LENGTH_SHORT).show();
            mError++;
            if (this.mIsBound)
                getPointValue(this.mCurrentPoint);
        }
        if (mError >= 5) {
            Toast.makeText(this, "终端设备没有回复！", Toast.LENGTH_SHORT).show();
            mError = 0;
        }
        this.mDeviceResponseTime = 0;
        if ((!this.mbExit) && (this.mDeviceState == 3)) {
            Message localMessage = this.mHandler.obtainMessage(3);
            this.mHandler.sendMessageDelayed(localMessage, 500L);//源代码5000 客户要求 改短
        }
    }

    private void doBindService() {
        bindService(new Intent(this, TDSService.class), this.mConnection, BIND_AUTO_CREATE);
        this.mIsBound = true;
    }

    private void doRegisterReceiver() {
        if (!this.mIsReceiveRegister) {
            IntentFilter localIntentFilter = new IntentFilter("com.tds.test.state");
            registerReceiver(this.mReceiver, localIntentFilter);
            this.mIsReceiveRegister = true;
        }
    }

    private void doResultFinish() {
        playSound(1);
        playSound(1);
        Bundle localBundle = new Bundle();
        localBundle.putString("point_num", this.mCurrentPoint + "");
        localBundle.putString("point_value", this.mPointAverageValue + "");
        Intent localIntent = new Intent();
        localIntent.putExtras(localBundle);
        setResult(-1, localIntent);
        finish();
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

    private void getPointValue(int paramInt) {
        if (this.mBoundService.getState() != 3) {
            showAlertDlg("设备未连接，\n请在【设备管理】中连接设备");
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        byte[] arrayOfByte = this.mBoundService.mTDSFrameParse.GetVoltagePacket(paramInt);
        this.mBoundService.write(arrayOfByte);
    }

    private void initSounds() {
        this.soundPool = new SoundPool(4, 3, 100);
        this.soundPoolMap = new HashMap();
        this.soundPoolMap.put(1, this.soundPool.load(this, R.raw.alarm, 0));
        this.soundPoolMap.put(2, this.soundPool.load(this, R.raw.ding, 0));
    }

    private void parsePointValue() {
        int i = this.mBoundService.mTDSFrameParse.GetFrameVoltage();
        this.mDeviceResponseTime = (1 + this.mDeviceResponseTime);
        if (i != -1) {
            Log.e("PointDetectActivity", "liuzy =======+=voltage=" + i);
            addPointValue(i);
            if (this.mPointEqualTime > 10)
                doResultFinish();
        } else {
//            return;
        }
        Message localMessage = this.mHandler.obtainMessage(1);
        this.mHandler.sendMessageDelayed(localMessage, 100L);
    }

    private void setRightTitle() {
        if (this.mIsBound)
            this.mDeviceState = this.mBoundService.getState();
        switch (this.mDeviceState) {
            default:
                return;
            case 3:
                String str = this.mBoundService.getConnectedDeviceName();
                this.mTitle.setText(R.string.title_connected_to);
                this.mTitle.append(str);
                startPointDetect();
                return;
            case 0:
            case 1:
            case 2:
        }
        this.mTitle.setText(R.string.title_not_connected);
        showAlertDlg("设备未连接，\n请在【设备管理】中连接设备");
    }

    private void showAlertDlg(String paramString) {
        Builder localBuilder = new Builder(this);
        playSound(2);
        localBuilder.setTitle("提示");
        localBuilder.setMessage(paramString).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                PointDetectActivity.this.finish();
            }
        });
        localBuilder.create().show();
    }

    private void startPointDetect() {
        ResetTDSXYChar();
        this.mPointAverageValue = 0;
        this.mPointTotalValue = 0;
        this.mPointEqualTime = 0;
        this.mPointVauleBufIndex = 0;
        this.mDeviceResponseTime = 0;
        Message localMessage1 = this.mHandler.obtainMessage(1);
        this.mHandler.sendMessageDelayed(localMessage1, 500L);
        Message localMessage2 = this.mHandler.obtainMessage(3);
        this.mHandler.sendMessageDelayed(localMessage2, 3000L);
    }

    protected XYMultipleSeriesRenderer buildRenderer(int paramInt, PointStyle paramPointStyle) {
        XYMultipleSeriesRenderer localXYMultipleSeriesRenderer = new XYMultipleSeriesRenderer();
        localXYMultipleSeriesRenderer.setAxisTitleTextSize(16.0F);
        localXYMultipleSeriesRenderer.setChartTitleTextSize(20.0F);
        localXYMultipleSeriesRenderer.setLabelsTextSize(15.0F);
        localXYMultipleSeriesRenderer.setLegendTextSize(15.0F);
        localXYMultipleSeriesRenderer.setPointSize(5.0F);
        localXYMultipleSeriesRenderer.setMargins(new int[]{20, 30, 15, 20});
        this.mSeriesRender = new XYSeriesRenderer();
        this.mSeriesRender.setColor(paramInt);
        this.mSeriesRender.setPointStyle(paramPointStyle);
        localXYMultipleSeriesRenderer.addSeriesRenderer(this.mSeriesRender);
        return localXYMultipleSeriesRenderer;
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.e("PointDetectActivity", "+++ ON CREATE +++");
        //getWindow().setFlags(128, 128);
        // requestWindowFeature(7);
        setContentView(R.layout.point_detect);
        // getWindow().setFeatureInt(7, R.layout.custom_title);
        String[] arrayOfString = getResources().getStringArray(R.array.point_detail_arrays);
        this.image = ((ImageView) findViewById(R.id.imageView1));
        this.mReceiver = new TDSServiceReceiver();
        XYCharInit();
        this.mCurrentPoint = 0;
        this.mPointValueBuf = new int[20];
        this.mbExit = true;
        new Bundle();
        this.mCurrentPoint = Integer.parseInt(getIntent().getExtras().getString("point_id"));
//        this.mTitle = ((TextView) findViewById(R.id.title_left_text));
//        this.mTitle.setText(arrayOfString[this.mCurrentPoint]);
        this.mTitle = ((TextView) findViewById(R.id.tv_right));
        findViewById(R.id.ll_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ((TextView) findViewById(R.id.tv_name)).setText(arrayOfString[this.mCurrentPoint]);
        ((TextView) findViewById(R.id.tv_id)).setText("" + (this.mCurrentPoint + 1) + "/24");

        UpDateImage(this.mCurrentPoint);
        initSounds();
    }

    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
        soundPool.release();
        this.mbExit = true;
    }

    protected void onPause() {
        super.onPause();
        Log.e("PointDetectActivity", "++ ON PAUSE ++");
        this.mbExit = true;
    }

    protected void onResume() {
        super.onResume();
        this.mbExit = false;
        if (this.mChartView == null) {
            Message localMessage = this.mHandler.obtainMessage(4);
            this.mHandler.sendMessageDelayed(localMessage, 1000L);
            LinearLayout localLinearLayout = (LinearLayout) findViewById(R.id.chart);
            this.mChartView = ChartFactory.getLineChartView(this, this.mDataset, this.mRenderer);
            this.mRenderer.setClickEnabled(true);
            this.mRenderer.setSelectableBuffer(10);
            this.mChartView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View paramAnonymousView) {
                }
            });
            localLinearLayout.addView(this.mChartView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return;
        }
        this.mChartView.repaint();
    }

    public void onStart() {
        super.onStart();
        Log.e("PointDetectActivity", "++ ON START ++");
        this.mbExit = false;
        doBindService();
        doRegisterReceiver();
    }

    public void onStop() {
        super.onStop();
        Log.e("PointDetectActivity", "-- ON STOP --");
        doUnbindService();
        doUnRegisterReceiver();
        this.mbExit = true;
    }

    public void playSound(int paramInt) {
//    int i = ((AudioManager)getBaseContext().getSystemService("audio")).getStreamVolume(3);
        int i = ((AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE)).getStreamVolume(3);
        this.soundPool.play((Integer) this.soundPoolMap.get(paramInt), i, i, 1, 0, 1.0F);
    }

    public class TDSServiceReceiver extends BroadcastReceiver {
        public TDSServiceReceiver() {
        }

        public void onReceive(Context paramContext, Intent paramIntent) {
            switch (Integer.valueOf(paramIntent.getStringExtra("BT_MESSAGE")).intValue()) {
                case 2:
                default:
                    return;
                case 1:
                    PointDetectActivity.this.mDeviceState = PointDetectActivity.this.mBoundService.getState();
                    switch (PointDetectActivity.this.mDeviceState) {
                        default:
                            return;
                        case 0:
                        case 1:
                            PointDetectActivity.this.mTitle.setText(R.string.title_not_connected);
                            PointDetectActivity.this.showAlertDlg("设备未连接，\n请在【设备管理】中连接设备");
                            return;
                        case 3:
                            PointDetectActivity.this.mTitle.setText(R.string.title_connected_to);
                            return;
                        case 2:
                    }
                    PointDetectActivity.this.mTitle.setText(R.string.title_connecting);
                    PointDetectActivity.this.showAlertDlg("连接设备中，请稍候...");
                    return;
                case 3:
                    PointDetectActivity.this.mTitle.setText(R.string.title_not_connected);
                    PointDetectActivity.this.showAlertDlg("设备连接失败，\n请在【设备管理】中重新连接设备");
                    PointDetectActivity.this.mDeviceState = 0;
                    return;
                case 4:
                    PointDetectActivity.this.mTitle.setText(R.string.title_not_connected);
                    playSound(2);
//                    int a = soundPool.load(TDSMainActivity.this, R.raw.alarm, 0);
//                    int ii = ((AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE)).getStreamVolume(3);
//                    soundPool.play(a, ii, ii, 1, 0, 1.0F);
                    showAlertDlg("设备未连接，\n请在【设备管理】中连接设备");
                    Toast.makeText(paramContext, "Device connection was lost", Toast.LENGTH_SHORT).show();
                    PointDetectActivity.this.mDeviceState = 0;
                    return;
                case 5:
                    PointDetectActivity.this.parsePointValue();
            }
        }
    }
}

/* Location:           D:\android studio\fanbianyi\classes-dex2jar (2).jar
 * Qualified Name:     com.tds.test.PointDetectActivity
 * JD-Core Version:    0.6.2
 */