package com.yy.kaitian.yl;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class TDSTestActivity extends Activity
{
  private static final boolean D = true;
  public static final String DEVICE_NAME = "device_name";
  public static final int MESSAGE_DEVICE_NAME = 4;
  public static final int MESSAGE_READ = 2;
  public static final int MESSAGE_STATE_CHANGE = 1;
  public static final int MESSAGE_TDS_RESPONSE_CHECK = 6;
  public static final int MESSAGE_TOAST = 5;
  public static final int MESSAGE_WRITE = 3;
  private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
  private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
  private static final int REQUEST_ENABLE_BT = 3;
  private static final String TAG = "TDSTestActivity";
  public static final String TOAST = "toast";
  private BluetoothAdapter mBluetoothAdapter = null;
  private TDSService mBoundService;
  private BluetoothChatService mChatService = null;
  private String mConnectedDeviceName = null;
  private ServiceConnection mConnection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      TDSTestActivity.this.mBoundService = ((TDSService.LocalBinder)paramAnonymousIBinder).getService();
      Toast.makeText(TDSTestActivity.this, "Bind Server TDSTestActivity", Toast.LENGTH_SHORT).show();
      if (TDSTestActivity.this.mBoundService.getState() == 0)
        TDSTestActivity.this.mBoundService.start();
    }

    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      TDSTestActivity.this.mBoundService = null;
      Toast.makeText(TDSTestActivity.this, "Bind Server Disconnected", Toast.LENGTH_SHORT).show();
    }
  };
  private ArrayAdapter<String> mConversationArrayAdapter;
  private ListView mConversationView;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default:
        return;
      case 1:
        Log.i("TDSTestActivity", "MESSAGE_STATE_CHANGE: " + paramAnonymousMessage.arg1);
        switch (paramAnonymousMessage.arg1)
        {
        default:
          return;
        case 0:
        case 1:
          TDSTestActivity.this.mTitle.setText(R.string.title_not_connected);
          return;
        case 3:
          TDSTestActivity.this.mTitle.setText(R.string.title_connected_to);
          TDSTestActivity.this.mTitle.append(TDSTestActivity.this.mConnectedDeviceName);
          TDSTestActivity.this.mConversationArrayAdapter.clear();
          return;
        case 2:
        }
        TDSTestActivity.this.mTitle.setText(R.string.title_connecting);
        return;
      case 3:
        byte[] arrayOfByte = (byte[])paramAnonymousMessage.obj;
        String str3 = "";
        String str4 = TDSUtils.bytesToHexString(arrayOfByte);
        for (int i = 0; ; i = 1 + (i + 1))
        {
          if (i >= str4.length())
          {
            TDSTestActivity.this.mConversationArrayAdapter.add("Me:  " + str3);
            return;
          }
          str3 = new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(str3)).append(str4.charAt(i)).toString())).append(str4.charAt(i + 1)).toString() + " ";
        }
      case 2:
        String str2 = (String)paramAnonymousMessage.obj;
        TDSTestActivity.this.mbTDSResponseCheck = Boolean.valueOf(true);
        TDSTestActivity.this.mConversationArrayAdapter.add(TDSTestActivity.this.mConnectedDeviceName + ":  " + str2);
        return;
      case 4:
        TDSTestActivity.this.mConnectedDeviceName = paramAnonymousMessage.getData().getString("device_name");
        Toast.makeText(TDSTestActivity.this.getApplicationContext(), "Connected to " + TDSTestActivity.this.mConnectedDeviceName, Toast.LENGTH_SHORT).show();
        return;
      case 5:
        Toast.makeText(TDSTestActivity.this.getApplicationContext(), paramAnonymousMessage.getData().getString("toast"), Toast.LENGTH_SHORT).show();
        return;
      case 6:
      }
      if (!TDSTestActivity.this.mbTDSResponseCheck.booleanValue())
      {
        String str1 = "" + "TDS terminal no response";
        Toast.makeText(TDSTestActivity.this.getApplicationContext(), str1, Toast.LENGTH_SHORT).show();
        return;
      }
      new StringBuilder(String.valueOf("")).append("TDS terminal receive response").toString();
      TDSTestActivity.this.mbTDSResponseCheck = Boolean.valueOf(false);
    }
  };
  private boolean mIsBound = false;
  private boolean mIsReceiveRegister = false;
  private boolean mIsStart = false;
  private EditText mOutEditText;
  private StringBuffer mOutStringBuffer;
  private TDSServiceReceiver mReceiver;
  private Button mSendButton;
  private TextView mTitle;
  private OnEditorActionListener mWriteListener = new OnEditorActionListener()
  {
    public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
    {
      if ((paramAnonymousInt == 0) && (paramAnonymousKeyEvent.getAction() == 1))
      {
        String str = paramAnonymousTextView.getText().toString();
        TDSTestActivity.this.sendMessage(str);
      }
      Log.i("TDSTestActivity", "END onEditorAction");
      return true;
    }
  };
  private Boolean mbTDSResponseCheck = Boolean.valueOf(false);

  private void connectDevice(Intent paramIntent, boolean paramBoolean)
  {
    String str = paramIntent.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
    BluetoothDevice localBluetoothDevice = this.mBluetoothAdapter.getRemoteDevice(str);
    this.mBoundService.connect(localBluetoothDevice, paramBoolean);
  }

  private void doBindService()
  {
    bindService(new Intent(this, TDSService.class), this.mConnection, Context.BIND_AUTO_CREATE);
    this.mIsBound = true;
    Log.i("TDSTestActivity", "doBindService start ");
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

  private void doStartTdsServer()
  {
    startService(new Intent(this, TDSService.class));
  }

  private void doStopTdsServer()
  {
    stopService(new Intent(this, TDSService.class));
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

  private void ensureDiscoverable()
  {
    Log.d("TDSTestActivity", "ensure discoverable");
    if (this.mBluetoothAdapter.getScanMode() != 23)
    {
      Intent localIntent = new Intent("android.bluetooth.adapter.action.REQUEST_DISCOVERABLE");
      localIntent.putExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION", 300);
      startActivity(localIntent);
    }
  }

  private void sendMessage(String paramString)
  {
//    if (this.mBoundService.getState() != 3)
//      Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
//    while (paramString.length() <= 0)
//      return;
//    byte[] arrayOfByte1 = paramString.getBytes();
//    if (arrayOfByte1[0] == 49)
//    {
//      this.mbTDSResponseCheck = Boolean.valueOf(false);
//      byte[] arrayOfByte6 = this.mBoundService.mTDSFrameParse.GetVoltagePacket(5);
//      this.mBoundService.write(arrayOfByte6);
//    }
//    while (true)
//    {
//      this.mOutStringBuffer.setLength(0);
//      this.mOutEditText.setText(this.mOutStringBuffer);
//      return;
//      if (arrayOfByte1[0] == 50)
//      {
//        this.mbTDSResponseCheck = Boolean.valueOf(false);
//        byte[] arrayOfByte5 = this.mChatService.mTDSFrameParse.GetDeviceSerialNumberPacket();
//        this.mChatService.write(arrayOfByte5);
//        Message localMessage4 = this.mHandler.obtainMessage(6);
//        this.mHandler.sendMessageDelayed(localMessage4, 1000L);
//      }
//      else if (arrayOfByte1[0] == 51)
//      {
//        this.mbTDSResponseCheck = Boolean.valueOf(false);
//        byte[] arrayOfByte4 = this.mChatService.mTDSFrameParse.SetDeviceSerialNumberPacket("20120711liuzytest");
//        this.mChatService.write(arrayOfByte4);
//        Message localMessage3 = this.mHandler.obtainMessage(6);
//        this.mHandler.sendMessageDelayed(localMessage3, 1000L);
//      }
//      else if (arrayOfByte1[0] == 52)
//      {
//        this.mbTDSResponseCheck = Boolean.valueOf(false);
//        byte[] arrayOfByte3 = this.mChatService.mTDSFrameParse.SetRedLedPacket((byte)1);
//        this.mChatService.write(arrayOfByte3);
//        Message localMessage2 = this.mHandler.obtainMessage(6);
//        this.mHandler.sendMessageDelayed(localMessage2, 1000L);
//      }
//      else if (arrayOfByte1[0] == 53)
//      {
//        this.mbTDSResponseCheck = Boolean.valueOf(false);
//        byte[] arrayOfByte2 = this.mChatService.mTDSFrameParse.SetGreenLedPacket((byte)0);
//        this.mChatService.write(arrayOfByte2);
//        Message localMessage1 = this.mHandler.obtainMessage(6);
//        this.mHandler.sendMessageDelayed(localMessage1, 1000L);
//      }
//    }
  }

  private void setupChat()
  {
    Log.d("TDSTestActivity", "setupChat()");
    this.mConversationArrayAdapter = new ArrayAdapter(this, R.layout.message);
    this.mConversationView = ((ListView)findViewById(R.id.in));
    this.mConversationView.setAdapter(this.mConversationArrayAdapter);
    this.mOutEditText = ((EditText)findViewById(R.id.edit_text_out));
    this.mOutEditText.setOnEditorActionListener(this.mWriteListener);
      this.mSendButton = ((Button)findViewById(R.id.button_send));
    this.mSendButton.setOnClickListener(new OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        String str = ((TextView)TDSTestActivity.this.findViewById(R.id.edit_text_out)).getText().toString();
        TDSTestActivity.this.sendMessage(str);
      }
    });
    doStartTdsServer();
    doBindService();
    doRegisterReceiver();
    this.mIsStart = true;
    this.mOutStringBuffer = new StringBuffer("");
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    Log.d("TDSTestActivity", "onActivityResult " + paramInt2);
//    switch (paramInt1)
//    {
//    default:
//    case 1:
//    case 2:
//      do
//      {
//        do
//          return;
//        while (paramInt2 != -1);
//        connectDevice(paramIntent, true);
//        return;
//      }
//      while (paramInt2 != -1);
//      connectDevice(paramIntent, false);
//      return;
//    case 3:
//    }
//    if (paramInt2 == -1)
//    {
//      setupChat();
//      return;
//    }
    Log.d("TDSTestActivity", "BT not enabled");
    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
    finish();
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Log.e("TDSTestActivity", "+++ ON CREATE +++");
    requestWindowFeature(7);
    setContentView(R.layout.main);
    getWindow().setFeatureInt(7, R.layout.custom_title);
    Log.e("TDSTestActivity", "Liuzy Add 2");
    this.mTitle = ((TextView)findViewById(R.id.title_left_text));
    this.mTitle.setText(R.string.app_name);
    this.mTitle = ((TextView)findViewById(R.id.title_right_text));
    this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (this.mBluetoothAdapter == null)
    {
      Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
      finish();
      return;
    }
    this.mReceiver = new TDSServiceReceiver();
  }

  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    getMenuInflater().inflate(R.menu.option_menu, paramMenu);
    return true;
  }

  public void onDestroy()
  {
    super.onDestroy();
    if (this.mIsStart)
      this.mBoundService.stop();
    Log.e("TDSTestActivity", "--- ON DESTROY ---");
    this.mIsStart = false;
    doUnbindService();
    doStopTdsServer();
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
//    int i = 1;
//    switch (paramMenuItem.getItemId())
//    {
//    default:
//      i = 0;
//    case 2131296298:
//    case 2131296299:
//      do
//      {
//        return i;
//        startActivityForResult(new Intent(this, DeviceListActivity.class), i);
//        return i;
//      }
//      while (!this.mIsBound);
//      Toast.makeText(this, this.mBoundService.testValue(), 0).show();
//      return i;
//    case 2131296311:
//    }
//    startActivity(new Intent(this, DetectMainActivity.class));
//    return i;
    return false;
  }

  public void onPause()
  {
    try
    {
      super.onPause();
      Log.e("TDSTestActivity", "- ON PAUSE -");
      return;
    }
    finally
    {
//      localObject = finally;
//      throw localObject;
    }
  }

  public void onResume()
  {
    try
    {
      super.onResume();
      Log.e("TDSTestActivity", "+ ON RESUME +");
      return;
    }
    finally
    {
//      localObject = finally;
//      throw localObject;
    }
  }

  public void onStart()
  {
    super.onStart();
    Log.e("TDSTestActivity", "++ ON START ++");
    if (!this.mBluetoothAdapter.isEnabled())
    {
      startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 3);
      return;
    }
    if (!this.mIsStart)
    {
      setupChat();
      return;
    }
    doBindService();
    doRegisterReceiver();
  }

  public void onStop()
  {
    super.onStop();
    Log.e("TDSTestActivity", "-- ON STOP --");
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
      default:
        return;
      case 2:
        TDSTestActivity.this.mConnectedDeviceName = TDSTestActivity.this.mBoundService.getConnectedDeviceName();
        Toast.makeText(TDSTestActivity.this.getApplicationContext(), "Connected to " + TDSTestActivity.this.mConnectedDeviceName, Toast.LENGTH_SHORT).show();
        return;
      case 1:
        switch (TDSTestActivity.this.mBoundService.getState())
        {
        default:
          return;
        case 0:
        case 1:
          TDSTestActivity.this.mTitle.setText(R.string.title_not_connected);
          return;
        case 3:
          TDSTestActivity.this.mTitle.setText(R.string.title_connected_to);
          TDSTestActivity.this.mTitle.append(TDSTestActivity.this.mConnectedDeviceName);
          TDSTestActivity.this.mConversationArrayAdapter.clear();
          return;
        case 2:
        }
        TDSTestActivity.this.mTitle.setText(R.string.title_connecting);
        return;
      case 3:
        Toast.makeText(paramContext, "Unable to connect device", Toast.LENGTH_SHORT).show();
        return;
      case 4:
        Toast.makeText(paramContext, "Device connection was lost", Toast.LENGTH_SHORT).show();
        return;
      case 5:
      }
      int i = TDSTestActivity.this.mBoundService.mTDSFrameParse.GetFrameVoltage();
      int j = TDSTestActivity.this.mBoundService.mTDSFrameParse.GetFrameVoltageNum();
      Toast.makeText(paramContext, "GETVOLTAGE_RESPONSE[" + j + "] = " + i, Toast.LENGTH_SHORT).show();
    }
  }
}

/* Location:           D:\android studio\fanbianyi\classes-dex2jar (2).jar
 * Qualified Name:     com.tds.test.TDSTestActivity
 * JD-Core Version:    0.6.2
 */