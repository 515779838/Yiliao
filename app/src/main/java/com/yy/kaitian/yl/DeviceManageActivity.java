package com.yy.kaitian.yl;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Calendar;

public class DeviceManageActivity extends Activity
{
  private static final boolean D = true;
  public static final String DEVICE_NAME = "device_name";
  private static final int MESSAGE_GET_DEVICE_SN = 2;
  private static final int MESSAGE_RIGHT_TITLE_SET = 1;
  private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
  private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
  private static final int REQUEST_ENABLE_BT = 3;
  private static final String TAG = "DeviceManageActivity";
  public static final String TOAST = "toast";
  private BluetoothAdapter mBluetoothAdapter = null;
  private TDSService mBoundService;
  private String mConnectedDeviceName = null;
  private ServiceConnection mConnection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      DeviceManageActivity.this.mBoundService = ((TDSService.LocalBinder)paramAnonymousIBinder).getService();
      if (DeviceManageActivity.this.mBoundService.getState() == 0)
        DeviceManageActivity.this.mBoundService.start();
    }

    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      DeviceManageActivity.this.mBoundService = null;
      Toast.makeText(DeviceManageActivity.this, "Bind Server Disconnected", Toast.LENGTH_SHORT).show();
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
      case MESSAGE_RIGHT_TITLE_SET:
        Log.i("DeviceManageActivity", "MESSAGE_RIGHT_TITLE_SET : ");
        DeviceManageActivity.this.setRightTitle();
        return;
      case MESSAGE_GET_DEVICE_SN:
        Log.i("DeviceManageActivity", "MESSAGE_GET_DEVICE_SN : ");
        DeviceManageActivity.this.getDeviceSN();
      }
    }
  };
  private boolean mIsBound = false;
  private boolean mIsReceiveRegister = false;
  private TDSServiceReceiver mReceiver;

  private void connectDevice(Intent paramIntent, boolean paramBoolean)
  {
    String str = paramIntent.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
    BluetoothDevice localBluetoothDevice = this.mBluetoothAdapter.getRemoteDevice(str);
    this.mBoundService.connect(localBluetoothDevice, paramBoolean);
  }

  private void disConnectDevice()
  {
    if (this.mIsBound)
      this.mBoundService.stop();
  }

  private void doBindService()
  {
    bindService(new Intent(this, TDSService.class), this.mConnection, Context.BIND_AUTO_CREATE);
    this.mIsBound = true;
    Log.i("DeviceManageActivity", "doBindService start ");
  }

  private void doDeviceTest()
  {
    startActivity(new Intent(this, DeviceCheckActivity.class));
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

  private void doWriteDeviceSN()
  {
    startActivity(new Intent(this, WriteSNActivity.class));
  }

  private void ensureDiscoverable()
  {
    Log.d("DeviceManageActivity", "ensure discoverable");
    if (this.mBluetoothAdapter.getScanMode() != 23)
    {
      Intent localIntent = new Intent("android.bluetooth.adapter.action.REQUEST_DISCOVERABLE");
      localIntent.putExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION", 300);
      startActivity(localIntent);
    }
  }

  private String getCurrentTime()
  {
    Calendar localCalendar = Calendar.getInstance();
    int i = localCalendar.get(Calendar.HOUR);
    int j = localCalendar.get(Calendar.MINUTE);
    int k = localCalendar.get(Calendar.SECOND);
    String str1;
    if (i < 10)
    {
      str1 = "" + "0" + i + ":";
    } else {
      str1 = i + ":";
    }
    if(j < 10){
      str1 = str1 + "0" + j + ":";
    } else {
      str1 = str1 + j + ":";
    }

    if(k < 10){
      str1 = str1 + "0" + k + ":";
    } else {
      str1 = str1 + k + ":";
    }
    return str1;
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

  private void setRightTitle()
  {
    String str1 = null;
    if (this.mIsBound)
      str1 = getCurrentTime() + "    ";
    switch (this.mBoundService.getState())
    {
    default:
      return;
    case 3:
      this.mConnectedDeviceName = this.mBoundService.getConnectedDeviceName();
      String str3 = getResources().getString(R.string.title_connected_to);
      this.mConversationArrayAdapter.add(str1 + str3 + this.mConnectedDeviceName);
      String str4 = "SN=" + this.mBoundService.mTDSDetect.GetSerialNumber();
      this.mConversationArrayAdapter.add(str1 + str4);
      return;
    case 2:
      String str2 = getResources().getString(R.string.title_connecting);
      this.mConversationArrayAdapter.add(str1 + str2);
      return;
    case 0:

    case 1:
      this.mConversationArrayAdapter.add(str1 + "请连接设备");
    }
  }

  private void showAlertDlg(String paramString)
  {
    Builder localBuilder = new Builder(this);
    localBuilder.setTitle("提示");
    localBuilder.setMessage(paramString).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
      }
    });
    localBuilder.create().show();
  }

  private void startDeviceConnect()
  {
    startActivityForResult(new Intent(this, DeviceListActivity.class), REQUEST_CONNECT_DEVICE_SECURE);
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if(paramIntent == null){
      return;
    }
    Log.d("DeviceManageActivity", "onActivityResult " + paramInt2);
    switch (paramInt1) {
      case REQUEST_CONNECT_DEVICE_SECURE:
        if(paramInt2 == -1){
          connectDevice(paramIntent, true);
        } else {
          connectDevice(paramIntent, false);
        }
        break;
      default:
        connectDevice(paramIntent, false);
        break;
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    setTheme(R.style.CustomTitleBarTheme);
    super.onCreate(paramBundle);
    Log.e("DeviceManageActivity", "+++ ON CREATE +++");
    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    setContentView(R.layout.device_manage);
    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_button);
    this.mConversationArrayAdapter = new ArrayAdapter(this, R.layout.message);
    this.mConversationView = ((ListView)findViewById(R.id.in));
    this.mConversationView.setAdapter(this.mConversationArrayAdapter);
    this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    this.mReceiver = new TDSServiceReceiver();
    Button localButton1 = (Button)findViewById(R.id.customer_modify);
    localButton1.setText("连接设备");
    localButton1.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        DeviceManageActivity.this.startDeviceConnect();
      }
    });
    Button localButton2 = (Button)findViewById(R.id.disconnect_device);
    localButton2.setText("断开连接");
    localButton2.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        DeviceManageActivity.this.disConnectDevice();
      }
    });
    Button localButton3 = (Button)findViewById(R.id.customer_delete);
    localButton3.setText("  返回  ");
    localButton3.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        DeviceManageActivity.this.finish();
      }
    });
  }

  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    return false;
  }

  public void onDestroy()
  {
    super.onDestroy();
    Log.e("DeviceManageActivity", "--- ON DESTROY ---");
    doUnbindService();
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
//      this.mBoundService.stop();
//      return i;
//    case 2131296300:
//    }
//    doDeviceTest();
//    return i;
    return false;
  }

  public void onPause()
  {
    try
    {
      super.onPause();
      Log.e("DeviceManageActivity", "- ON PAUSE -");
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
      Log.e("DeviceManageActivity", "+ ON RESUME +");
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
    Log.e("DeviceManageActivity", "++ ON START ++");
    doBindService();
    doRegisterReceiver();
    Message localMessage = this.mHandler.obtainMessage(MESSAGE_RIGHT_TITLE_SET);
    this.mHandler.sendMessageDelayed(localMessage, 1000L);
  }

  public void onStop()
  {
    super.onStop();
    Log.e("DeviceManageActivity", "-- ON STOP --");
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
      int i = Integer.valueOf(paramIntent.getStringExtra("BT_MESSAGE")).intValue();
      String str1 = DeviceManageActivity.this.getCurrentTime() + "    ";
      switch (i)
      {
        case 0:
          String strno_found = DeviceManageActivity.this.getResources().getString(R.string.none_found);
          DeviceManageActivity.this.mConversationArrayAdapter.add(str1 + strno_found);
          break;
        case 5:
        default:
          return;
        case 1:
          break;
        case 2:
          DeviceManageActivity.this.mConnectedDeviceName = DeviceManageActivity.this.mBoundService.getConnectedDeviceName();
          switch (DeviceManageActivity.this.mBoundService.getState())
          {
            case 0:
              DeviceManageActivity.this.mConversationArrayAdapter.add(str1 + DeviceManageActivity.this.getResources().getString(R.string.title_connect_lost));
              break;
            case 1:
//              String str6 = DeviceManageActivity.this.getResources().getString(R.string.title_not_connected);
//              DeviceManageActivity.this.mConversationArrayAdapter.add(str1 + str6);
              break;
            case 2:
              String str6 = DeviceManageActivity.this.getResources().getString(R.string.title_connecting);
              DeviceManageActivity.this.mConversationArrayAdapter.add(str1 + str6);
              break;
            case 3:
              String str5 = DeviceManageActivity.this.getResources().getString(R.string.title_connected_to);
              DeviceManageActivity.this.mConversationArrayAdapter.add(str1 + str5 + DeviceManageActivity.this.mConnectedDeviceName);
              Message localMessage = DeviceManageActivity.this.mHandler.obtainMessage(MESSAGE_GET_DEVICE_SN);
              DeviceManageActivity.this.mHandler.sendMessageDelayed(localMessage, 1000L);
              break;
            default:
              String strFail = DeviceManageActivity.this.getResources().getString(R.string.title_can_not_connect);
              DeviceManageActivity.this.mConversationArrayAdapter.add(str1 + strFail);
              break;
          }

          return;
      case 3:
        String str4 = DeviceManageActivity.this.getResources().getString(R.string.title_can_not_connect);
        DeviceManageActivity.this.mConversationArrayAdapter.add(str1 + str4);
        return;
      case 4:
        String str3 = DeviceManageActivity.this.getResources().getString(R.string.title_connect_lost);
        DeviceManageActivity.this.mConversationArrayAdapter.add(str1 + str3);
        return;
      case 6:
//        DeviceManageActivity.this.mConnectedDeviceName = DeviceManageActivity.this.mBoundService.getConnectedDeviceName();
//        String str5 = DeviceManageActivity.this.getResources().getString(R.string.title_connected_to);
//        DeviceManageActivity.this.mConversationArrayAdapter.add(str1 + str5 + DeviceManageActivity.this.mConnectedDeviceName);
//        Message localMessage = DeviceManageActivity.this.mHandler.obtainMessage(2);
//        DeviceManageActivity.this.mHandler.sendMessageDelayed(localMessage, 1000L);
        String str2 = "SN=" + DeviceManageActivity.this.mBoundService.mTDSDetect.GetSerialNumber();
        DeviceManageActivity.this.mConversationArrayAdapter.add(str1 + str2);
      }
    }
  }
}

