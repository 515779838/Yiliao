package com.yy.kaitian.yl;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class ImageViewActivity extends Activity
{
  private static int[] COLORS = { -16711936, -16776961, -65281, -16711681 };
  private static final boolean D = true;
  private static final String TAG = "ImageViewActivity";
  public static final String TYPE = "type";
  private final int MAX_DETECT_POINT = 23;
  private final int TDS_TERMINAL_GET_VOLTAGE = 1;
  private Button button;
  private ImageView image;
  private TDSService mBoundService;
  private GraphicalView mChartView;
  private ServiceConnection mConnection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      ImageViewActivity.this.mBoundService = ((TDSService.LocalBinder)paramAnonymousIBinder).getService();
      Toast.makeText(ImageViewActivity.this, "Bind Server ImageViewActivity", Toast.LENGTH_SHORT).show();
    }

    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      ImageViewActivity.this.mBoundService = null;
      Toast.makeText(ImageViewActivity.this, "Bind Server Disconnected", Toast.LENGTH_SHORT).show();
    }
  };
  private int mCurrentPoint = 0;
  private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
  private String mDateFormat;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default:
        return;
      case 1:
      }
      ImageViewActivity.this.sendGetVoltageMessage(ImageViewActivity.this.mCurrentPoint);
    }
  };
  private boolean mIsBound = false;
  private boolean mIsReceiveRegister = false;
  private TDSServiceReceiver mReceiver;
  private XYMultipleSeriesRenderer mRenderer;
  private XYSeries mSeries;
  private XYSeriesRenderer mSeriesRender;
  private int xValue;
  private double yValue;

  private void ResetTDSXYChar()
  {
    this.xValue = 0;
    this.mSeries.clear();
  }

  private void UpDateImage(int paramInt)
  {
//    int i;
//    switch (paramInt)
//    {
//    default:
//      return;
//    case 0:
//      i = 2130837512;
//    case 1:
//    case 2:
//    case 3:
//    case 4:
//    case 5:
//    case 6:
//    case 7:
//    case 8:
//    case 9:
//    case 10:
//    case 11:
//    case 12:
//    case 13:
//    case 14:
//    case 15:
//    case 16:
//    case 17:
//    case 18:
//    case 19:
//    case 20:
//    case 21:
//    case 22:
//    case 23:
//    }
//    while (true)
//    {
//      this.image.setImageResource(i);
//      return;
//      i = 2130837523;
//      continue;
//      i = 2130837529;
//      continue;
//      i = 2130837530;
//      continue;
//      i = 2130837531;
//      continue;
//      i = 2130837532;
//      continue;
//      i = 2130837533;
//      continue;
//      i = 2130837534;
//      continue;
//      i = 2130837535;
//      continue;
//      i = 2130837513;
//      continue;
//      i = 2130837514;
//      continue;
//      i = 2130837515;
//      continue;
//      i = 2130837516;
//      continue;
//      i = 2130837517;
//      continue;
//      i = 2130837518;
//      continue;
//      i = 2130837519;
//      continue;
//      i = 2130837520;
//      continue;
//      i = 2130837521;
//      continue;
//      i = 2130837522;
//      continue;
//      i = 2130837524;
//      continue;
//      i = 2130837525;
//      continue;
//      i = 2130837526;
//      continue;
//      i = 2130837527;
//      continue;
//      i = 2130837528;
//    }
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
        this.mRenderer.setChartTitle("Average Voltage");
        this.mRenderer.setXTitle("num");
        this.mRenderer.setYTitle("Voltage");
        this.mRenderer.setXAxisMin(0.5D);
        this.mRenderer.setXAxisMax(12.5D);
        this.mRenderer.setYAxisMin(-10.0D);
        this.mRenderer.setYAxisMax(100.0D);
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

  private void doBindService()
  {
    bindService(new Intent(this, TDSService.class), this.mConnection, Context.BIND_AUTO_CREATE);
    this.mIsBound = true;
    Log.i("ImageViewActivity", "doBindService start ");
  }

  private void doDetectNext()
  {
    this.mCurrentPoint = (1 + this.mCurrentPoint);
    if (this.mCurrentPoint > 23)
    {
      Toast.makeText(this, "Detect Finish ", Toast.LENGTH_SHORT).show();
      return;
    }
    UpDateImage(this.mCurrentPoint);
    ResetTDSXYChar();
    Message localMessage = this.mHandler.obtainMessage(1);
    this.mHandler.sendMessageDelayed(localMessage, 100L);
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

  private void sendGetVoltageMessage(int paramInt)
  {
    if (this.mBoundService.getState() != 3)
    {
      Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
      return;
    }
    byte[] arrayOfByte = this.mBoundService.mTDSFrameParse.GetVoltagePacket(paramInt);
    this.mBoundService.write(arrayOfByte);
  }

  private void startAutoDetect()
  {
    Message localMessage = this.mHandler.obtainMessage(1);
    this.mCurrentPoint = 0;
    ResetTDSXYChar();
    UpDateImage(this.mCurrentPoint);
    this.mHandler.sendMessageDelayed(localMessage, 100L);
  }

  public void addListenerOnButton()
  {
    this.image = ((ImageView)findViewById(R.id.imageView1));
    this.button = ((Button)findViewById(R.id.btnChangeImage));
    this.button.setOnClickListener(new OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        ImageViewActivity.this.startAutoDetect();
      }
    });
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
    setContentView(R.layout.point_detect);
    this.mReceiver = new TDSServiceReceiver();
    XYCharInit();
    this.mCurrentPoint = 0;
    this.xValue = 1;
    this.yValue = 0.0D;
  }

  protected void onDestroy()
  {
    super.onDestroy();
    doUnbindService();
  }

  protected void onPause()
  {
    super.onPause();
    Log.e("ImageViewActivity", "++ ON PAUSE ++");
  }

  protected void onResume()
  {
    super.onResume();
    if (this.mChartView == null)
    {
      LinearLayout localLinearLayout = (LinearLayout)findViewById(R.id.chart);
      this.mChartView = ChartFactory.getLineChartView(this, this.mDataset, this.mRenderer);
      this.mRenderer.setClickEnabled(true);
      this.mRenderer.setSelectableBuffer(10);
      this.mChartView.setOnClickListener(new OnClickListener()
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
    Log.e("ImageViewActivity", "++ ON START ++");
    doBindService();
    doRegisterReceiver();
  }

  public void onStop()
  {
    super.onStop();
    Log.e("ImageViewActivity", "-- ON STOP --");
    doUnbindService();
    doUnRegisterReceiver();
  }

  public class TDSServiceReceiver extends BroadcastReceiver
  {
    public TDSServiceReceiver()
    {
    }

    public void onReceive(Context paramContext, Intent paramIntent)
    {
//      switch (Integer.valueOf(paramIntent.getStringExtra("BT_MESSAGE")).intValue())
//      {
//      case 1:
//      case 2:
//      case 3:
//      default:
//      case 4:
//      case 5:
//      }
//      int i;
//      do
//      {
//        return;
//        Toast.makeText(paramContext, "Device connection was lost", 0).show();
//        return;
//        i = ImageViewActivity.this.mBoundService.mTDSFrameParse.GetFrameVoltage();
//        ImageViewActivity.this.mBoundService.mTDSFrameParse.GetFrameVoltageNum();
//      }
//      while (i == -1);
//      double d = i % 100 / 3.0D;
//      ImageViewActivity.this.UpDateXYChar(ImageViewActivity.this.xValue, d);
//      ImageViewActivity localImageViewActivity = ImageViewActivity.this;
//      localImageViewActivity.xValue = (1 + localImageViewActivity.xValue);
//      if (ImageViewActivity.this.xValue > 12)
//      {
//        ImageViewActivity.this.doDetectNext();
//        return;
//      }
//      Message localMessage = ImageViewActivity.this.mHandler.obtainMessage(1);
//      ImageViewActivity.this.mHandler.sendMessageDelayed(localMessage, 100L);
    }
  }
}

/* Location:           D:\android studio\fanbianyi\classes-dex2jar (2).jar
 * Qualified Name:     com.tds.test.ImageViewActivity
 * JD-Core Version:    0.6.2
 */