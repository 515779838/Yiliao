package com.yy.kaitian.yiliao681;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothChatService
{
  private static final boolean D = true;
  private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
  private static final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
  private static final String NAME_INSECURE = "BluetoothChatInsecure";
  private static final String NAME_SECURE = "BluetoothChatSecure";
  public static final int STATE_CONNECTED = 3;
  public static final int STATE_CONNECTING = 2;
  public static final int STATE_LISTEN = 1;
  public static final int STATE_NONE = 0;
  private static final String TAG = "BluetoothChatService";
  private final BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
  private ConnectThread mConnectThread;
  private ConnectedThread mConnectedThread;
  private final Handler mHandler;
  private AcceptThread mInsecureAcceptThread;
  private AcceptThread mSecureAcceptThread;
  private int mState = 0;
  public TDSDetect mTDSDetect = new TDSDetect();
  public TDSFrameParse mTDSFrameParse = new TDSFrameParse();

  public BluetoothChatService(Context paramContext, Handler paramHandler)
  {
    this.mHandler = paramHandler;
  }

  private void connectionFailed()
  {
    Message localMessage = this.mHandler.obtainMessage(5);
    Bundle localBundle = new Bundle();
    localBundle.putString("toast", "Unable to connect device");
    localMessage.setData(localBundle);
    this.mHandler.sendMessage(localMessage);
    start();
  }

  private void connectionLost()
  {
    Message localMessage = this.mHandler.obtainMessage(5);
    Bundle localBundle = new Bundle();
    localBundle.putString("toast", "Device connection was lost");
    localMessage.setData(localBundle);
    this.mHandler.sendMessage(localMessage);
    start();
  }

  private void setState(int paramInt)
  {
    try
    {
      Log.d("BluetoothChatService", "setState() " + this.mState + " -> " + paramInt);
      this.mState = paramInt;
      this.mHandler.obtainMessage(1, paramInt, -1).sendToTarget();
      return;
    }
    finally
    {
//      localObject = finally;
//      throw localObject;
    }
  }

  public void connect(BluetoothDevice paramBluetoothDevice, boolean paramBoolean)
  {
    try
    {
      Log.d("BluetoothChatService", "connect to: " + paramBluetoothDevice);
      if ((this.mState == 2) && (this.mConnectThread != null))
      {
        this.mConnectThread.cancel();
        this.mConnectThread = null;
      }
      if (this.mConnectedThread != null)
      {
        this.mConnectedThread.cancel();
        this.mConnectedThread = null;
      }
      this.mConnectThread = new ConnectThread(paramBluetoothDevice, paramBoolean);
      this.mConnectThread.start();
      setState(2);
      return;
    }
    finally
    {
    }
  }

  public void connected(BluetoothSocket paramBluetoothSocket, BluetoothDevice paramBluetoothDevice, String paramString)
  {
    try
    {
      Log.d("BluetoothChatService", "connected, Socket Type:" + paramString);
      if (this.mConnectThread != null)
      {
        this.mConnectThread.cancel();
        this.mConnectThread = null;
      }
      if (this.mConnectedThread != null)
      {
        this.mConnectedThread.cancel();
        this.mConnectedThread = null;
      }
      if (this.mSecureAcceptThread != null)
      {
        this.mSecureAcceptThread.cancel();
        this.mSecureAcceptThread = null;
      }
      if (this.mInsecureAcceptThread != null)
      {
        this.mInsecureAcceptThread.cancel();
        this.mInsecureAcceptThread = null;
      }
      this.mConnectedThread = new ConnectedThread(paramBluetoothSocket, paramString);
      this.mConnectedThread.start();
      Message localMessage = this.mHandler.obtainMessage(4);
      Bundle localBundle = new Bundle();
      localBundle.putString("device_name", paramBluetoothDevice.getName());
      localMessage.setData(localBundle);
      this.mHandler.sendMessage(localMessage);
      setState(3);
      return;
    }
    finally
    {
    }
  }

  public int getState()
  {
    try
    {
      int i = this.mState;
      return i;
    }
    finally
    {
//      localObject = finally;
//      throw localObject;
    }
  }

  public void start()
  {
    try
    {
      Log.d("BluetoothChatService", "start");
      if (this.mConnectThread != null)
      {
        this.mConnectThread.cancel();
        this.mConnectThread = null;
      }
      if (this.mConnectedThread != null)
      {
        this.mConnectedThread.cancel();
        this.mConnectedThread = null;
      }
      setState(1);
      if (this.mSecureAcceptThread == null)
      {
        this.mSecureAcceptThread = new AcceptThread(true);
        this.mSecureAcceptThread.start();
      }
      if (this.mInsecureAcceptThread == null)
      {
        this.mInsecureAcceptThread = new AcceptThread(false);
        this.mInsecureAcceptThread.start();
      }
      return;
    }
    finally
    {
    }
  }

  public void stop()
  {
    try
    {
      Log.d("BluetoothChatService", "stop");
      if (this.mConnectThread != null)
      {
        this.mConnectThread.cancel();
        this.mConnectThread = null;
      }
      if (this.mConnectedThread != null)
      {
        this.mConnectedThread.cancel();
        this.mConnectedThread = null;
      }
      if (this.mSecureAcceptThread != null)
      {
        this.mSecureAcceptThread.cancel();
        this.mSecureAcceptThread = null;
      }
      if (this.mInsecureAcceptThread != null)
      {
        this.mInsecureAcceptThread.cancel();
        this.mInsecureAcceptThread = null;
      }
      setState(0);
      return;
    }
    finally
    {
    }
  }

  public void write(byte[] paramArrayOfByte)
  {
    try
    {
      if (this.mState != 3)
        return;
      ConnectedThread localConnectedThread = this.mConnectedThread;
      localConnectedThread.write(paramArrayOfByte);
      return;
    }
    finally
    {
    }
  }

  private class AcceptThread extends Thread
  {
    private String mSocketType;
    private BluetoothServerSocket mmServerSocket;
    private BluetoothServerSocket localObject;

    public AcceptThread(boolean arg2)
    {
      int i = 0;
      String str = "";
      if (i != 0)
        str = "Secure";
      while (true)
      {
        this.mSocketType = str;
        if (i != 0);
        try
        {
          BluetoothServerSocket localBluetoothServerSocket2 = BluetoothChatService.this.mAdapter.listenUsingRfcommWithServiceRecord("BluetoothChatSecure", BluetoothChatService.MY_UUID_SECURE);
          BluetoothServerSocket localBluetoothServerSocket1;
          for (localObject = localBluetoothServerSocket2; ; localObject = localBluetoothServerSocket1)
          {
            this.mmServerSocket = ((BluetoothServerSocket)localObject);
//            break;
            str = "Insecure";
//            return;
            localBluetoothServerSocket1 = BluetoothChatService.this.mAdapter.listenUsingInsecureRfcommWithServiceRecord("BluetoothChatInsecure", BluetoothChatService.MY_UUID_INSECURE);
          }
        }
        catch (IOException localIOException)
        {
          while (true)
          {
            Log.e("BluetoothChatService", "Socket Type: " + this.mSocketType + "listen() failed", localIOException);
            Object localObject = null;
          }
        }
      }
    }

    public void cancel()
    {
      Log.d("BluetoothChatService", "Socket Type" + this.mSocketType + "cancel " + this);
      try
      {
        this.mmServerSocket.close();
        return;
      }
      catch (IOException localIOException)
      {
        Log.e("BluetoothChatService", "Socket Type" + this.mSocketType + "close() of server failed", localIOException);
      }
    }

    public void run()
    {
      Log.d("BluetoothChatService", "Socket Type: " + this.mSocketType + "BEGIN mAcceptThread" + this);
      setName("AcceptThread" + this.mSocketType);
      if (BluetoothChatService.this.mState == 3)
      {
        label68: Log.i("BluetoothChatService", "END mAcceptThread, socket Type: " + this.mSocketType);
        return;
      }
      while (true)
      {
        BluetoothSocket localBluetoothSocket = null;
        try
        {
          while (true)
          {
            localBluetoothSocket = this.mmServerSocket.accept();
            BluetoothChatService.this.connected(localBluetoothSocket, localBluetoothSocket.getRemoteDevice(), this.mSocketType);

            if (localBluetoothSocket == null)
              break;
            synchronized (BluetoothChatService.this)
            {
              switch (BluetoothChatService.this.mState)
              {
              default:
              case 1:
              case 2:
              case 0:
              case 3:
              }
            }
          }
        }
        catch (IOException localIOException1)
        {
          Log.e("BluetoothChatService", "Socket Type: " + this.mSocketType + "accept() failed", localIOException1);
        }
//        break label68;
//        continue;
        try
        {
          localBluetoothSocket.close();
        }
        catch (IOException localIOException2)
        {
          Log.e("BluetoothChatService", "Could not close unwanted socket", localIOException2);
        }
      }
    }
  }

  private class ConnectThread extends Thread
  {
    private String mSocketType;
    private final BluetoothDevice mmDevice;
    private BluetoothSocket mmSocket;
    private BluetoothSocket localObject;

    public ConnectThread(BluetoothDevice paramBoolean, boolean arg3)
    {
      this.mmDevice = paramBoolean;
      int i = 0;
      String str = null;
      if (i != 0)
        str = "Secure";
      while (true)
      {
        this.mSocketType = str;
        if (i != 0);
        try
        {
          BluetoothSocket localBluetoothSocket2 = paramBoolean.createRfcommSocketToServiceRecord(BluetoothChatService.MY_UUID_SECURE);
          BluetoothSocket localBluetoothSocket1;
          for (localObject = localBluetoothSocket2; ; localObject = localBluetoothSocket1)
          {
            this.mmSocket = ((BluetoothSocket)localObject);
            str = "Insecure";
//            return;
//            break;
            localBluetoothSocket1 = paramBoolean.createInsecureRfcommSocketToServiceRecord(BluetoothChatService.MY_UUID_INSECURE);
          }
        }
        catch (IOException localIOException)
        {
          while (true)
          {
            Log.e("BluetoothChatService", "Socket Type: " + this.mSocketType + "create() failed", localIOException);
            Object localObject = null;
          }
        }
      }
    }

    public void cancel()
    {
      try
      {
        this.mmSocket.close();
        return;
      }
      catch (IOException localIOException)
      {
        Log.e("BluetoothChatService", "close() of connect " + this.mSocketType + " socket failed", localIOException);
      }
    }

    public void run()
    {
      Log.i("BluetoothChatService", "BEGIN mConnectThread SocketType:" + this.mSocketType);
      setName("ConnectThread" + this.mSocketType);
      BluetoothChatService.this.mAdapter.cancelDiscovery();
      try
      {
        this.mmSocket.connect();
      }
      catch (IOException localIOException1)
      {
        synchronized (BluetoothChatService.this)
        {
          BluetoothChatService.this.mConnectThread = null;
          BluetoothChatService.this.connected(this.mmSocket, this.mmDevice, this.mSocketType);
//          return;
//          localIOException1 = localIOException1;
          try
          {
            this.mmSocket.close();
            BluetoothChatService.this.connectionFailed();
            return;
          }
          catch (IOException localIOException2)
          {
            while (true)
              Log.e("BluetoothChatService", "unable to close() " + this.mSocketType + " socket during connection failure", localIOException2);
          }
        }
      }
    }
  }

  private class ConnectedThread extends Thread
  {
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final BluetoothSocket mmSocket;

    public ConnectedThread(BluetoothSocket paramString, String arg3)
    {
      String str = "";
      Log.d("BluetoothChatService", "create ConnectedThread: " + str);
      this.mmSocket = paramString;
      InputStream localInputStream = null;
      try
      {
        localInputStream = paramString.getInputStream();
        OutputStream localOutputStream2 = paramString.getOutputStream();
//        localOutputStream1 = localOutputStream2;
        this.mmInStream = localInputStream;
        this.mmOutStream = localOutputStream2;
        return;
      }
      catch (IOException localIOException)
      {
        while (true)
        {
          Log.e("BluetoothChatService", "temp sockets not created", localIOException);
          OutputStream localOutputStream1 = null;
        }
      }
    }

    public void cancel()
    {
      try
      {
        this.mmSocket.close();
        return;
      }
      catch (IOException localIOException)
      {
        Log.e("BluetoothChatService", "close() of connect socket failed", localIOException);
      }
    }

    public void run()
    {
      Log.i("BluetoothChatService", "BEGIN mConnectedThread");
      byte[] arrayOfByte1 = new byte[1];
      BluetoothChatService.this.mTDSFrameParse.TDSFrameInit();
      int i;
      do
        try
        {
          while (true)
            if (this.mmInStream.read(arrayOfByte1) > 0)
            {
              i = BluetoothChatService.this.mTDSFrameParse.ParseFrame(arrayOfByte1[0]);
              if (i != -1)
                break;
              BluetoothChatService.this.mTDSFrameParse.TDSFrameInit();
              Log.i("BluetoothChatService", "ParseFrame error " + i);
            }
        }
        catch (IOException localIOException)
        {
          Log.e("BluetoothChatService", "disconnected", localIOException);
          BluetoothChatService.this.connectionLost();
          return;
        }
      while (i != 0);
      if (BluetoothChatService.this.mTDSFrameParse.IsFrameValid().booleanValue())
      {
        Log.i("BluetoothChatService", "ParseFrame ok1 " + i);
        BluetoothChatService.this.mTDSFrameParse.GetFrameData();
        BluetoothChatService.this.mTDSFrameParse.GetFrameDataLen();
      }
      switch (BluetoothChatService.this.mTDSFrameParse.GetFrameCmdType())
      {
      case 3:
      case 5:
      case 7:
      case 9:
      default:
      case 2:
      case 4:
        while (true)
        {
          BluetoothChatService.this.mTDSFrameParse.TDSFrameInit();
//          break;
          int j = BluetoothChatService.this.mTDSFrameParse.GetFrameVoltage();
          int k = BluetoothChatService.this.mTDSFrameParse.GetFrameVoltageNum();
          if (j != -1)
          {
            String str4 = "voltage[" + k + "] = " + j;
            BluetoothChatService.this.mHandler.obtainMessage(2, str4.length(), -1, str4).sendToTarget();
//            continue;
            byte[] arrayOfByte2 = BluetoothChatService.this.mTDSFrameParse.GetFrameDeviceSerialNumber();
            if (arrayOfByte2 != null)
            {
              String str2 = new String(arrayOfByte2);
              BluetoothChatService.this.mTDSDetect.SetSerialNumber(str2);
              String str3 = "SN = " + str2;
              BluetoothChatService.this.mHandler.obtainMessage(2, str3.length(), -1, str3).sendToTarget();
            }
          }
        }
      case 6:
      case 8:
      case 10:
      }
      if (BluetoothChatService.this.mTDSFrameParse.GetFrameSetResponseResult() == 1);
      for (String str1 = "Set Success!"; ; str1 = "Set Failure!")
      {
        BluetoothChatService.this.mHandler.obtainMessage(2, str1.length(), -1, str1).sendToTarget();
        break;
      }
    }

    public void write(byte[] paramArrayOfByte)
    {
      try
      {
        this.mmOutStream.write(paramArrayOfByte);
        BluetoothChatService.this.mHandler.obtainMessage(3, -1, -1, paramArrayOfByte).sendToTarget();
        return;
      }
      catch (IOException localIOException)
      {
        Log.e("BluetoothChatService", "Exception during write", localIOException);
      }
    }
  }
}

/* Location:           D:\android studio\fanbianyi\classes-dex2jar (2).jar
 * Qualified Name:     com.tds.test.BluetoothChatService
 * JD-Core Version:    0.6.2
 */