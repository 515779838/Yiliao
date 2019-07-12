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
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WriteSNActivity extends Activity
{
  private static final boolean D = true;
  private static final String TAG = "WriteSNActivity";
  private final int MESSAGE_RIGHT_TITLE_SET = 5;
  private final int TDS_CHECK_RESPONSE = 4;
  private final int TDS_TERMINAL_GET_SN = 2;
  private final int TDS_TERMINAL_SET_SN = 1;
  private TDSService mBoundService;
  private Button mCancelButton;
  private ServiceConnection mConnection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      WriteSNActivity.this.mBoundService = ((TDSService.LocalBinder)paramAnonymousIBinder).getService();
    }

    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      WriteSNActivity.this.mBoundService = null;
    }
  };
  private int mDeviceResponseTime;
  private int mDeviceState = 0;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      case 3:
      default:
        return;
      case 5:
        Log.i("WriteSNActivity", "MESSAGE_RIGHT_TITLE_SET : ");
        WriteSNActivity.this.setRightTitle();
        return;
      case 2:
        WriteSNActivity.this.getDeviceSN();
        return;
      case 1:
        WriteSNActivity.this.writeDeviceSN(WriteSNActivity.this.mStrSN);
        return;
      case 4:
      }
      WriteSNActivity.this.checkDeviceResponse();
    }
  };
  private boolean mIsBound = false;
  private boolean mIsReceiveRegister = false;
  private TDSServiceReceiver mReceiver;
  private EditText mSNEdit;
  private String mStrSN = "";
  private TextView mTitle;
  private Button mWriteButton;
  private boolean mbCheckSet;

  private void checkDeviceResponse()
  {
    if (this.mDeviceResponseTime == 0)
      Toast.makeText(this, "终端设备没有回复！", Toast.LENGTH_SHORT).show();
  }

  private void doBindService()
  {
    bindService(new Intent(this, TDSService.class), this.mConnection, BIND_AUTO_CREATE);
    this.mIsBound = true;
    Log.i("WriteSNActivity", "doBindService start ");
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

  private void getDeviceSN()
  {
    if (this.mBoundService.getState() != 3)
    {
      Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
      return;
    }
    byte[] arrayOfByte = this.mBoundService.mTDSFrameParse.GetDeviceSerialNumberPacket();
    this.mBoundService.write(arrayOfByte);
  }

  private void parseCheckValue()
  {
    this.mDeviceResponseTime = (1 + this.mDeviceResponseTime);
    Log.e("WriteSNActivity", "parseCheckValue");
    Message localMessage = this.mHandler.obtainMessage(2);
    this.mHandler.sendMessageDelayed(localMessage, 500L);
    showAlertDlg("序列号设置成功!");
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
      return;
    case 0:
    case 1:
    case 2:
    }
    this.mTitle.setText(R.string.title_not_connected);
    showAlertDlg("设备未连接，\n请在【设备管理】中连接设备");
  }

  private void showAlertDlg(String paramString)
  {
    Builder localBuilder = new Builder(this);
    localBuilder.setTitle("提示");
    localBuilder.setMessage(paramString).setCancelable(false).setPositiveButton("确定", new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
      }
    });
    localBuilder.create().show();
  }

  private void startWriteSN()
  {
    this.mDeviceResponseTime = 0;
    Message localMessage1 = this.mHandler.obtainMessage(1);
    this.mHandler.sendMessageDelayed(localMessage1, 500L);
    Message localMessage2 = this.mHandler.obtainMessage(4);
    this.mHandler.sendMessageDelayed(localMessage2, 3000L);
  }

  private void writeDeviceSN(String paramString)
  {
    if (this.mBoundService.getState() != 3)
    {
      Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
      return;
    }
    byte[] arrayOfByte = this.mBoundService.mTDSFrameParse.SetDeviceSerialNumberPacket(paramString);
    this.mBoundService.write(arrayOfByte);
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Log.e("WriteSNActivity", "+++ ON CREATE +++");
    getWindow().setFlags(128, 128);
    this.mbCheckSet = false;
    requestWindowFeature(7);
    setContentView(R.layout.write_sn);
    getWindow().setFeatureInt(7, R.layout.custom_title);
    this.mTitle = ((TextView)findViewById(R.id.title_left_text));
    this.mTitle.setText("写设备SN");
    this.mTitle = ((TextView)findViewById(R.id.title_right_text));
    this.mReceiver = new TDSServiceReceiver();
    this.mSNEdit = ((EditText)findViewById(R.id.write_sn_edit));
    this.mWriteButton = ((Button)findViewById(R.id.write_sn_button));
    this.mCancelButton = ((Button)findViewById(R.id.cancel_sn_button));
    this.mWriteButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        WriteSNActivity.this.mStrSN = WriteSNActivity.this.mSNEdit.getText().toString();
        if ((WriteSNActivity.this.mStrSN.length() < 4) || (WriteSNActivity.this.mStrSN.length() > 30))
        {
          Toast.makeText(WriteSNActivity.this, "序列号长度错误！", Toast.LENGTH_SHORT).show();
          return;
        }
        WriteSNActivity.this.startWriteSN();
      }
    });
    this.mCancelButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        WriteSNActivity.this.finish();
      }
    });
  }

  protected void onDestroy()
  {
    super.onDestroy();
    Log.e("WriteSNActivity", "-- ON Destroy --");
    doUnbindService();
  }

  protected void onPause()
  {
    super.onPause();
    Log.e("WriteSNActivity", "++ ON PAUSE ++");
  }

  protected void onResume()
  {
    super.onResume();
    Log.e("WriteSNActivity", "++ ON Resume ++");
    if (!this.mbCheckSet)
    {
      Message localMessage = this.mHandler.obtainMessage(5);
      this.mHandler.sendMessageDelayed(localMessage, 500L);
      this.mbCheckSet = true;
    }
  }

  public void onStart()
  {
    super.onStart();
    Log.e("WriteSNActivity", "++ ON START ++");
    doBindService();
    doRegisterReceiver();
  }

  public void onStop()
  {
    super.onStop();
    Log.e("WriteSNActivity", "-- ON STOP --");
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
      switch (Integer.valueOf(paramIntent.getStringExtra("BT_MESSAGE")).intValue())
      {
      case 2:
      case 5:
      case 6:
      case 7:
      default:
        return;
      case 1:
        WriteSNActivity.this.mDeviceState = WriteSNActivity.this.mBoundService.getState();
        switch (WriteSNActivity.this.mDeviceState)
        {
        default:
          return;
        case 0:
        case 1:
          WriteSNActivity.this.mTitle.setText(R.string.title_not_connected);
          WriteSNActivity.this.showAlertDlg("设备未连接，\n请在【设备管理】中连接设备");
          return;
        case 3:
          WriteSNActivity.this.mTitle.setText(R.string.title_connected_to);
          return;
        case 2:
        }
        WriteSNActivity.this.mTitle.setText(R.string.title_connecting);
        WriteSNActivity.this.showAlertDlg("连接设备中，请稍候...");
        return;
      case 3:
        WriteSNActivity.this.mTitle.setText(R.string.title_not_connected);
        WriteSNActivity.this.showAlertDlg("设备连接失败，\n请在【设备管理】中重新连接设备");
        WriteSNActivity.this.mDeviceState = 0;
        return;
      case 4:
        WriteSNActivity.this.mTitle.setText(R.string.title_not_connected);
        Toast.makeText(paramContext, "Device connection was lost", Toast.LENGTH_SHORT).show();
        WriteSNActivity.this.mDeviceState = 0;
        return;
      case 8:
      }
      WriteSNActivity.this.parseCheckValue();
    }
  }
}

/* Location:           D:\android studio\fanbianyi\classes-dex2jar (2).jar
 * Qualified Name:     com.tds.test.WriteSNActivity
 * JD-Core Version:    0.6.2
 */