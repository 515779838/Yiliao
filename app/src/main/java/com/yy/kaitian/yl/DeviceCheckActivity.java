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
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
//未用
public class DeviceCheckActivity extends Activity
{
  private static final boolean D = true;
  private static final String TAG = "DeviceCheckActivity";
  private final int MESSAGE_RIGHT_TITLE_SET = 5;
  private final int TDS_CHECK_RESPONSE = 4;
  private final int TDS_TERMINAL_GET_CHECK_VOLTAGE = 1;
  private final int TDS_TERMINAL_START_CHECK_VOLTAGE = 2;
  private final int TDS_TERMINAL_STOP_CHECK_VOLTAGE = 3;
  private TDSService mBoundService;
  private GraphicalView mChartView;
  private ServiceConnection mConnection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      DeviceCheckActivity.this.mBoundService = ((TDSService.LocalBinder)paramAnonymousIBinder).getService();
    }

    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      DeviceCheckActivity.this.mBoundService = null;
    }
  };
  private TextView mCurDetectValueView;
  private TextView mCurVoltageValueView;
  private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
  private int mDeviceResponseTime;
  private int mDeviceState = 0;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default:
        return;
      case 5:
        Log.i("DeviceCheckActivity", "MESSAGE_RIGHT_TITLE_SET : ");
        DeviceCheckActivity.this.setRightTitle();
        return;
      case 1:
        DeviceCheckActivity.this.getDeviceCheckValue(2);
        return;
      case 2:
        DeviceCheckActivity.this.setDeviceCheckMode(true);
        return;
      case 3:
        DeviceCheckActivity.this.setDeviceCheckMode(false);
        return;
      case 4:
      }
      DeviceCheckActivity.this.checkDeviceResponse();
    }
  };
  private boolean mIsBound = false;
  private boolean mIsReceiveRegister = false;
  private final int mPointValueBufSize = 30;
  private int mPointVauleBufIndex;
  private TDSServiceReceiver mReceiver;
  private XYMultipleSeriesRenderer mRenderer;
  private XYSeries mSeries;
  private XYSeriesRenderer mSeriesRender;
  private final int mSetPointMaxValue = 180;
  private final int mSetPointMinValue = 0;
  private TextView mTitle;
  private boolean mbCheckSet;
  private boolean mbExit;

  private void ResetTDSXYChar()
  {
    this.mSeries.clear();
  }

  private void UpDateXYChar(int paramInt, double paramDouble)
  {
    this.mSeries.add(paramInt, paramDouble);
    if (this.mChartView != null)
      this.mChartView.repaint();
  }

  private void XYCharInit()
  {
    this.mRenderer = buildRenderer(-16776961, PointStyle.CIRCLE);
    int i = this.mRenderer.getSeriesRendererCount();
    for (int j = 0; ; j++)
    {
      if (j >= i)
      {
        this.mRenderer.setChartTitle("Point Value");
        this.mRenderer.setXTitle("num");
        this.mRenderer.setXAxisMin(0.0D);
        this.mRenderer.setXAxisMax(30.0D);
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
        this.mRenderer.setPanLimits(new double[] { -10.0D, 20.0D, -10.0D, 40.0D });
        this.mRenderer.setZoomLimits(new double[] { -10.0D, 20.0D, -10.0D, 40.0D });
        this.mSeries = new XYSeries("Point Voltage", 0);
        this.mDataset.addSeries(this.mSeries);
        return;
      }
      ((XYSeriesRenderer)this.mRenderer.getSeriesRendererAt(j)).setFillPoints(true);
    }
  }

  private Boolean addCheckValue(int paramInt1, int paramInt2)
  {
    double d1 = paramInt2 / 1000.0D;
    double d2 = new Double(new DecimalFormat(".00").format(d1)).doubleValue();
    this.mCurVoltageValueView.setText(d2+"");
    double d3 = 192.941D - 0.18861D * paramInt1;
    double d4 = new Double(new DecimalFormat(".00").format(d3)).doubleValue();
    String str = d4+"";
    if (d4 < 0.0D)
    {
      d4 = 0.0D;
      this.mCurDetectValueView.setText(str);
      if (this.mPointVauleBufIndex >= 29)
//        break label194;
      UpDateXYChar(this.mPointVauleBufIndex, d4);
      this.mPointVauleBufIndex = (1 + this.mPointVauleBufIndex);
    }
    else
    {
//      return Boolean.valueOf(false);
      if (d4 <= 180.0D)
//        break;
        d4 = 180.0D;
//      break;
//      label194:
      this.mPointVauleBufIndex = 0;
      ResetTDSXYChar();
      if (this.mChartView != null)
        this.mChartView.repaint();
    }
    return false;
  }

  private void checkDeviceResponse()
  {
    if (this.mDeviceResponseTime == 0)
    {
      Toast.makeText(this, "终端设备没有回复！", Toast.LENGTH_SHORT).show();
      if (this.mIsBound)
      {
        if (!this.mbCheckSet)
//          break label79;
        getDeviceCheckValue(2);
      }
    }
    else
    {
      this.mDeviceResponseTime = 0;
      if ((!this.mbExit) && (this.mDeviceState == 3))
      {
        Message localMessage = this.mHandler.obtainMessage(4);
        this.mHandler.sendMessageDelayed(localMessage, 5000L);
      }
      setDeviceCheckMode(true);
//      return;
//      label79:
    }
  }

  private void doBindService()
  {
    bindService(new Intent(this, TDSService.class), this.mConnection, Context.BIND_AUTO_CREATE);
    this.mIsBound = true;
    Log.i("DeviceCheckActivity", "doBindService start ");
  }

  private void doRegisterReceiver()
  {
    if (!this.mIsReceiveRegister)
    {
      IntentFilter localIntentFilter = new IntentFilter("com.tds.test.state");
      registerReceiver(this.mReceiver, localIntentFilter);
      this.mIsReceiveRegister = true;
    }
  }

  private void doUnRegisterReceiver()
  {
    if (this.mIsReceiveRegister)
    {
      unregisterReceiver(this.mReceiver);
      this.mIsReceiveRegister = false;
    }
  }

  private void doUnbindService()
  {
    if (this.mIsBound)
    {
      unbindService(this.mConnection);
      this.mIsBound = false;
    }
  }

  private void getDeviceCheckValue(int paramInt)
  {
    if (this.mBoundService.getState() != 3)
    {
      Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
      return;
    }
    byte[] arrayOfByte = this.mBoundService.mTDSFrameParse.GetTestVoltagePacket(paramInt);
    this.mBoundService.write(arrayOfByte);
  }

  private void parseCheckValue()
  {
    int i = this.mBoundService.mTDSFrameParse.GetFrameTestVoltage();
    int j = this.mBoundService.mTDSFrameParse.GetFrameTestPowerVoltage();
    this.mDeviceResponseTime = (1 + this.mDeviceResponseTime);
    if (i != -1)
    {
      Log.e("DeviceCheckActivity", "liuzy =======+=voltage=" + i + "  polwer=" + j);
      addCheckValue(i, j);
      Message localMessage = this.mHandler.obtainMessage(1);
      this.mHandler.sendMessageDelayed(localMessage, 100L);
    }
  }

  private void setDeviceCheckMode(boolean paramBoolean)
  {
    this.mbCheckSet = false;
    if (this.mBoundService.getState() != 3)
    {
      Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
      return;
    }
    if (paramBoolean);
    for (byte b = 1; ; b = 0)
    {
      byte[] arrayOfByte = this.mBoundService.mTDSFrameParse.SetCheckModePacket(b);
      this.mBoundService.write(arrayOfByte);
      return;
    }
  }

  private void setRightTitle()
  {
    if (this.mIsBound)
      this.mDeviceState = this.mBoundService.getState();
    switch (this.mDeviceState)
    {
    default:
      return;
    case 3:
      String str = this.mBoundService.getConnectedDeviceName();
      this.mTitle.setText(R.string.title_connected_to);
      this.mTitle.append(str);
      startDeviceCheck();
      return;
    case 0:
    case 1:
    case 2:
    }
    this.mTitle.setText(R.string.title_not_connected);
    showAlertDlg("设备未连接，\n请在【连接管理】中连接设备");
  }

  private void showAlertDlg(String paramString)
  {
    Builder localBuilder = new Builder(this);
    localBuilder.setTitle("提示");
    localBuilder.setMessage(paramString).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        DeviceCheckActivity.this.finish();
      }
    });
    localBuilder.create().show();
  }

  private void startDeviceCheck()
  {
    ResetTDSXYChar();
    this.mDeviceResponseTime = 0;
    this.mPointVauleBufIndex = 0;
    Message localMessage1 = this.mHandler.obtainMessage(2);
    this.mHandler.sendMessageDelayed(localMessage1, 500L);
    Message localMessage2 = this.mHandler.obtainMessage(4);
    this.mHandler.sendMessageDelayed(localMessage2, 3000L);
  }

  protected XYMultipleSeriesRenderer buildRenderer(int paramInt, PointStyle paramPointStyle)
  {
    XYMultipleSeriesRenderer localXYMultipleSeriesRenderer = new XYMultipleSeriesRenderer();
    localXYMultipleSeriesRenderer.setAxisTitleTextSize(16.0F);
    localXYMultipleSeriesRenderer.setChartTitleTextSize(20.0F);
    localXYMultipleSeriesRenderer.setLabelsTextSize(15.0F);
    localXYMultipleSeriesRenderer.setLegendTextSize(15.0F);
    localXYMultipleSeriesRenderer.setPointSize(5.0F);
    localXYMultipleSeriesRenderer.setMargins(new int[] { 20, 30, 15, 20 });
    this.mSeriesRender = new XYSeriesRenderer();
    this.mSeriesRender.setColor(paramInt);
    this.mSeriesRender.setPointStyle(paramPointStyle);
    localXYMultipleSeriesRenderer.addSeriesRenderer(this.mSeriesRender);
    return localXYMultipleSeriesRenderer;
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Log.e("DeviceCheckActivity", "+++ ON CREATE +++");
    getWindow().setFlags(128, 128);
    this.mbExit = true;
    this.mbCheckSet = false;
    requestWindowFeature(7);
    setContentView(R.layout.device_check);
    getWindow().setFeatureInt(7, R.layout.custom_title);
    this.mTitle = ((TextView)findViewById(R.id.title_left_text));
    this.mTitle.setText("校准设备");
    this.mTitle = ((TextView)findViewById(R.id.title_right_text));
    this.mCurVoltageValueView = ((TextView)findViewById(R.id.device_test_cur_voltage_text));
    this.mCurDetectValueView = ((TextView)findViewById(R.id.device_test_cur_value_text));
    this.mReceiver = new TDSServiceReceiver();
    XYCharInit();
  }

  protected void onDestroy()
  {
    super.onDestroy();
    Log.e("DeviceCheckActivity", "-- ON Destroy --");
    doUnbindService();
  }

  protected void onPause()
  {
    super.onPause();
    Log.e("DeviceCheckActivity", "++ ON PAUSE ++");
  }

  protected void onResume()
  {
    super.onResume();
    Log.e("DeviceCheckActivity", "++ ON Resume ++");
    this.mbExit = false;
    if (this.mChartView == null)
    {
      Message localMessage = this.mHandler.obtainMessage(5);
      this.mHandler.sendMessageDelayed(localMessage, 1000L);
      LinearLayout localLinearLayout = (LinearLayout)findViewById(R.id.check_chart);
      this.mChartView = ChartFactory.getLineChartView(this, this.mDataset, this.mRenderer);
      this.mRenderer.setClickEnabled(true);
      this.mRenderer.setSelectableBuffer(10);
      this.mChartView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
        }
      });
      localLinearLayout.addView(this.mChartView, new ViewGroup.LayoutParams(-1, -1));
      return;
    }
    this.mChartView.repaint();
  }

  public void onStart()
  {
    super.onStart();
    Log.e("DeviceCheckActivity", "++ ON START ++");
    this.mbExit = false;
    doBindService();
    doRegisterReceiver();
  }

  public void onStop()
  {
    super.onStop();
    Log.e("DeviceCheckActivity", "-- ON STOP --");
    Message localMessage = this.mHandler.obtainMessage(3);
    this.mHandler.sendMessageDelayed(localMessage, 100L);
    doUnbindService();
    doUnRegisterReceiver();
    this.mbExit = true;
  }

  public class TDSServiceReceiver extends BroadcastReceiver
  {
    public TDSServiceReceiver()
    {
    }

    public void onReceive(Context paramContext, Intent paramIntent)
    {
      switch (Integer.valueOf(paramIntent.getStringExtra("BT_MESSAGE")).intValue())
      {
      case 2:
      case 5:
      case 6:
      case 8:
      default:
        return;
      case 1:
        DeviceCheckActivity.this.mDeviceState = DeviceCheckActivity.this.mBoundService.getState();
        switch (DeviceCheckActivity.this.mDeviceState)
        {
        default:
          return;
        case 0:
        case 1:
          DeviceCheckActivity.this.mTitle.setText(R.string.title_not_connected);
          DeviceCheckActivity.this.showAlertDlg("设备未连接，\n请在【连接管理】中连接设备");
          return;
        case 3:
          DeviceCheckActivity.this.mTitle.setText(R.string.title_connected_to);
          return;
        case 2:
        }
        DeviceCheckActivity.this.mTitle.setText(R.string.title_connecting);
        DeviceCheckActivity.this.showAlertDlg("连接设备中，请稍候...");
        return;
      case 3:
        DeviceCheckActivity.this.mTitle.setText(R.string.title_not_connected);
        DeviceCheckActivity.this.showAlertDlg("设备连接失败，\n请在【连接管理】中重新连接设备");
        DeviceCheckActivity.this.mDeviceState = 0;
        return;
      case 4:
        DeviceCheckActivity.this.mTitle.setText(R.string.title_not_connected);
        Toast.makeText(paramContext, "Device connection was lost", 0).show();
        DeviceCheckActivity.this.mDeviceState = 0;
        return;
      case 9:
        DeviceCheckActivity.this.parseCheckValue();
        return;
      case 7:
      }
      Message localMessage = DeviceCheckActivity.this.mHandler.obtainMessage(1);
      DeviceCheckActivity.this.mHandler.sendMessageDelayed(localMessage, 500L);
    }
  }
}

/* Location:           D:\android studio\fanbianyi\classes-dex2jar (2).jar
 * Qualified Name:     com.tds.test.DeviceCheckActivity
 * JD-Core Version:    0.6.2
 */