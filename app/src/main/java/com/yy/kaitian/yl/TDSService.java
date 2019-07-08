package com.yy.kaitian.yl;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.yy.kaitian.yl.utils.AppLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class TDSService extends Service {
    public static final String BT_STATE_ACTION = "com.tds.test.state";
    public static final int BT_STATE_MESSAGE_CONNECTFAIL = 3;
    public static final int BT_STATE_MESSAGE_CONNECTLOST = 4;
    public static final int BT_STATE_MESSAGE_DEVICENAME = 2;
    public static final int BT_STATE_MESSAGE_GETSN_RESPONSE = 6;
    public static final int BT_STATE_MESSAGE_GETTEST_RESPONSE = 9;
    public static final int BT_STATE_MESSAGE_GETVOLTAGE_RESPONSE = 5;
    public static final int BT_STATE_MESSAGE_SETCHECK_RESPONSE = 7;
    public static final int BT_STATE_MESSAGE_SETSN_RESPONSE = 8;
    public static final int BT_STATE_MESSAGE_SETSTATE = 1;
    private static final boolean D = true;
    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private static final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String NAME_INSECURE = "BluetoothChatInsecure";
    private static final String NAME_SECURE = "BluetoothChatSecure";
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_NONE = 0;
    public static final String STR_BT_STATE_MESSAGE = "BT_MESSAGE";
    private static final String TAG = "TDSService";
    private BluetoothAdapter mAdapter;
    private final IBinder mBinder = new LocalBinder();
    private ConnectThread mConnectThread;
    private String mConnectedDeviceName = "";
    private ConnectedThread mConnectedThread;
    private AcceptThread mInsecureAcceptThread;
    private AcceptThread mSecureAcceptThread;
    private int mState;
    public TDSDetect mTDSDetect = new TDSDetect();
    public TDSFrameParse mTDSFrameParse = new TDSFrameParse();

    private void connectionFailed() {
        doSendBroadCast("com.tds.test.state", "BT_MESSAGE", "3");
        start();
    }

    private void connectionLost() {
        doSendBroadCast("com.tds.test.state", "BT_MESSAGE", "4");
        start();
    }

    private void doSendBroadCast(String paramString1, String paramString2, String paramString3) {
        Log.d("TDSService", "doSendBroadCast action" + paramString1 + "  msg= " + paramString2 + " type= " + paramString3);
        Intent localIntent = new Intent();
        localIntent.setAction(paramString1);
        localIntent.putExtra(paramString2, paramString3);
        sendBroadcast(localIntent);
    }

    private void setState(int paramInt) {
        Log.d("TDSService", "setState() " + this.mState + " -> " + paramInt);
        this.mState = paramInt;
        doSendBroadCast("com.tds.test.state", "BT_MESSAGE", "2");
    }

    public void connect(BluetoothDevice paramBluetoothDevice, boolean paramBoolean) {
        try {
            Log.d("TDSService", "connect to: " + paramBluetoothDevice);
            if ((this.mState == 2) && (this.mConnectThread != null)) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null) {
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            this.mConnectThread = new ConnectThread(paramBluetoothDevice, paramBoolean);
            this.mConnectThread.start();
            setState(STATE_CONNECTING);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void connected(BluetoothSocket paramBluetoothSocket, BluetoothDevice paramBluetoothDevice, String paramString) {
        try {
            Log.d("TDSService", "connected, Socket Type:" + paramString);
            this.mConnectedThread = new ConnectedThread(paramBluetoothSocket, paramString);
            this.mConnectedThread.start();
            this.mConnectedDeviceName = paramBluetoothDevice.getName();
            setState(STATE_CONNECTED);
//            doSendBroadCast("com.tds.test.state", "BT_MESSAGE", "2");
//            if (this.mConnectThread != null) {
//                this.mConnectThread.cancel();
//                this.mConnectThread = null;
//            }
//            if (this.mConnectedThread != null) {
//                this.mConnectedThread.cancel();
//                this.mConnectedThread = null;
//            }
//            if (this.mSecureAcceptThread != null) {
//                this.mSecureAcceptThread.cancel();
//                this.mSecureAcceptThread = null;
//            }
//            if (this.mInsecureAcceptThread != null) {
//                this.mInsecureAcceptThread.cancel();
//                this.mInsecureAcceptThread = null;
//            }
//            this.mConnectedThread = new ConnectedThread(paramBluetoothSocket, paramString);
//            this.mConnectedThread.start();
//            this.mConnectedDeviceName = paramBluetoothDevice.getName();
////            setState(STATE_CONNECTED);
//            doSendBroadCast("com.tds.test.state", "BT_MESSAGE", "2");
        } catch (Exception e) {
            e.printStackTrace();
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null) {
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            if (this.mSecureAcceptThread != null) {
                this.mSecureAcceptThread.cancel();
                this.mSecureAcceptThread = null;
            }
            if (this.mInsecureAcceptThread != null) {
                this.mInsecureAcceptThread.cancel();
                this.mInsecureAcceptThread = null;
            }
        } finally {


        }
    }

    public String getConnectedDeviceName() {
        return this.mConnectedDeviceName;
    }

    public int getState() {
        return mState;
    }

    public IBinder onBind(Intent paramIntent) {
        return this.mBinder;
    }

    public void onCreate() {
        this.mAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mState = 0;
    }

    public void onDestroy() {
    }

    public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2) {
        Log.i("TDSService", "Received start id " + paramInt2 + ": " + paramIntent);
        return 1;
    }

    public void start() {
        try {
            Log.d("TDSService", "start");
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null) {
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            setState(STATE_LISTEN);
            if (this.mSecureAcceptThread == null) {
                this.mSecureAcceptThread = new AcceptThread(true);
                this.mSecureAcceptThread.start();
            }
            if (this.mInsecureAcceptThread == null) {
                this.mInsecureAcceptThread = new AcceptThread(false);
                this.mInsecureAcceptThread.start();
            }
            return;
        } finally {
        }
    }

    public void stop() {
        try {
            Log.d("TDSService", "stop");
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null) {
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            if (this.mSecureAcceptThread != null) {
                this.mSecureAcceptThread.cancel();
                this.mSecureAcceptThread = null;
            }
            if (this.mInsecureAcceptThread != null) {
                this.mInsecureAcceptThread.cancel();
                this.mInsecureAcceptThread = null;
            }
            setState(STATE_NONE);

            return;
        } finally {
        }
    }

    public int testValue() {
        return 34;
    }

    public void write(byte[] paramArrayOfByte) {
        try {
            if (this.mState != 3)
                return;
            ConnectedThread localConnectedThread = this.mConnectedThread;
            localConnectedThread.write(paramArrayOfByte);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    private class AcceptThread extends Thread {
        private String mSocketType;
        private BluetoothServerSocket mmServerSocket;

        public AcceptThread(boolean arg2) {
//      int i = 0;
            String str = null;
            if (arg2)
                str = "Secure";
            else {
                str = "Insecure";
            }
            this.mSocketType = str;
            try {
                BluetoothServerSocket localBluetoothServerSocket2;
                BluetoothServerSocket localBluetoothServerSocket1;
                if (arg2) {
                    localBluetoothServerSocket2 = TDSService.this.mAdapter.listenUsingRfcommWithServiceRecord("BluetoothChatSecure", TDSService.MY_UUID_SECURE);
                    mmServerSocket = localBluetoothServerSocket2;
                } else {
                    localBluetoothServerSocket1 = TDSService.this.mAdapter.listenUsingInsecureRfcommWithServiceRecord("BluetoothChatInsecure", TDSService.MY_UUID_INSECURE);
                    mmServerSocket = localBluetoothServerSocket1;
                }

            } catch (IOException localIOException) {
                localIOException.printStackTrace();
            }
        }


        public void cancel() {
            Log.d("TDSService", "Socket Type" + this.mSocketType + "cancel " + this);
            try {
                this.mmServerSocket.close();
//        return;
            } catch (IOException localIOException) {
                Log.e("TDSService", "Socket Type" + this.mSocketType + "close() of server failed", localIOException);
            }
        }

        // TODO: 2017/11/14
        public void run() {
            Log.d("TDSService", "Socket Type: " + this.mSocketType + "BEGIN mAcceptThread" + this);
            setName("AcceptThread" + this.mSocketType);
            if (TDSService.this.mState == 3) {
                Log.i("TDSService", "END mAcceptThread, socket Type: " + this.mSocketType);
                return;
            }
//            while (true) {
            BluetoothSocket localBluetoothSocket = null;
            try {
                localBluetoothSocket = this.mmServerSocket.accept();
                TDSService.this.connected(localBluetoothSocket, localBluetoothSocket.getRemoteDevice(), this.mSocketType);

            } catch (IOException localIOException1) {
                Log.e("TDSService", "Socket Type: " + this.mSocketType + "accept() failed", localIOException1);
//                TDSService.this.connected(localBluetoothSocket, localBluetoothSocket.getRemoteDevice(), this.mSocketType);
                try {

                    if (localBluetoothSocket != null) {
                        localBluetoothSocket.close();
                    }
                } catch (IOException localIOException2) {
                    Log.e("TDSService", "Could not close unwanted socket", localIOException2);
                }
            }
        }
    }

    private class ConnectThread extends Thread {
        private String mSocketType;
        private final BluetoothDevice mmDevice;
        private BluetoothSocket mmSocket;
//    private BluetoothSocket localObject;

        public ConnectThread(BluetoothDevice paramBoolean, boolean arg3) {
            this.mmDevice = paramBoolean;
            String str = null;
            if (arg3) {
                str = "Secure";
            } else {
                str = "Insecure";
            }
            this.mSocketType = str;
            try {
                if (arg3) {
                    BluetoothSocket localBluetoothSocket2 = paramBoolean.createRfcommSocketToServiceRecord(TDSService.MY_UUID_SECURE);
                    this.mmSocket = ((BluetoothSocket) localBluetoothSocket2);
                } else {
                    BluetoothSocket localBluetoothSocket1 = paramBoolean.createInsecureRfcommSocketToServiceRecord(TDSService.MY_UUID_INSECURE);
                    this.mmSocket = ((BluetoothSocket) localBluetoothSocket1);
                }
//                setState(STATE_CONNECTED);
            } catch (IOException localIOException) {
                Log.e("TDSService", "Socket Type: " + this.mSocketType + "create() failed", localIOException);
                Object localObject = null;
            }
        }

        public void cancel() {
            try {
                this.mmSocket.close();
//        return;
            } catch (IOException localIOException) {
                Log.e("TDSService", "close() of connect " + this.mSocketType + " socket failed", localIOException);
            }
        }

        public void run() {
            Log.i("TDSService", "BEGIN mConnectThread SocketType:" + this.mSocketType);
            setName("ConnectThread" + this.mSocketType);
            TDSService.this.mAdapter.cancelDiscovery();
            try {
                this.mmSocket.connect();
//                mConnectedDeviceName = mmDevice.getName();
//                setState(3);
//                TDSService.this.mConnectThread = null;
                TDSService.this.connected(this.mmSocket, this.mmDevice, this.mSocketType);
            } catch (IOException localIOException1) {
                localIOException1.printStackTrace();
                synchronized (TDSService.this) {
                    TDSService.this.mConnectThread = null;
                    TDSService.this.connected(this.mmSocket, this.mmDevice, this.mSocketType);
                    try {
                        this.mmSocket.close();
                        TDSService.this.connectionFailed();
                    } catch (IOException localIOException2) {
                        Log.e("TDSService", "unable to close() " + this.mSocketType + " socket during connection failure", localIOException2);
                    }
                }
            }
        }
    }

    private Queue<byte[]> mQueue = new LinkedList<>();
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 11:
                    byte[] arr = new byte[8];
                    arr[0] = 126;
                    arr[1] = 2;
                    arr[2] = 3;
                    arr[3] = 0;
                    arr[4] = 100;
                    arr[5] = 3;
                    arr[6] = 100;
                    arr[7] = 126;
                    if (mQueue.size() < 20) {
                        mQueue.add(arr);
                    }
                    if (!mQueue.isEmpty()) {
                        write(mQueue.poll());
                    }
                    mHandler.sendEmptyMessageDelayed(11, 1000);
                    break;
            }
            return false;
        }
    });

    private class ConnectedThread extends Thread {
        private InputStream mmInStream;
        private OutputStream mmOutStream;
        private BluetoothSocket mmSocket;


        public ConnectedThread(BluetoothSocket bluetoothSocket, String arg3) {
//            mHandler.sendEmptyMessageDelayed(11, 1000);
            Log.d("TDSService", "create ConnectedThread: " + arg3);
            this.mmSocket = bluetoothSocket;
            InputStream localInputStream = null;
            try {
                localInputStream = bluetoothSocket.getInputStream();
                OutputStream localOutputStream2 = bluetoothSocket.getOutputStream();
                this.mmInStream = localInputStream;
                this.mmOutStream = localOutputStream2;
            } catch (IOException localIOException) {
                Log.e("TDSService", "temp sockets not created", localIOException);
                OutputStream localOutputStream1 = null;
            }
        }

        public void cancel() {
            try {
                this.mmSocket.close();
            } catch (IOException localIOException) {
                Log.e("TDSService", "close() of connect socket failed", localIOException);
            }
        }

        public void run() {
            Log.i("TDSService", "BEGIN mConnectedThread");
            byte[] arrayOfByte1 = new byte[1];
            String str1 = null;
            TDSService.this.mTDSFrameParse.TDSFrameInit();
            int i = 0;
            try {
                while (true) {
                    if (mmInStream.read(arrayOfByte1) > 0) {
                        i = TDSService.this.mTDSFrameParse.ParseFrame(arrayOfByte1[0]);
                        AppLog.instance().iToSd(arrayOfByte1[0]);
                        AppLog.instance().iToSd("解析结果："+i);
                        if (i == -1) {
                            if (TDSService.this.mTDSFrameParse.IsFrameValid()) {
//                TDSService.this.mTDSFrameParse.TDSFrameInit();
//                                Log.i("TDSService", "ParseFrame ok1 " + i);
                                TDSService.this.mTDSFrameParse.GetFrameData();
                                TDSService.this.mTDSFrameParse.GetFrameDataLen();
                                switch (TDSService.this.mTDSFrameParse.GetFrameCmdType()) {
                                    case 2:
//                                        TDSService.this.mTDSFrameParse.TDSFrameInit();
                                        int j = TDSService.this.mTDSFrameParse.GetFrameVoltage();
                                        int k = TDSService.this.mTDSFrameParse.GetFrameVoltageNum();
                                        if (j != -1) {
                                            str1 = "voltage[" + k + "] = " + j;
                                            TDSService.this.doSendBroadCast("com.tds.test.state", "BT_MESSAGE", "5");
                                        }
                                        TDSService.this.mTDSFrameParse.TDSFrameInit();
                                        break;
                                    case 0:
                                        TDSService.this.mTDSFrameParse.TDSFrameInit();
                                        break;

                                    case 1:
                                        byte[] arr = new byte[8];
                                        arr[0] = 126;
                                        arr[1] = 2;
                                        arr[2] = 3;
                                        arr[3] = 0;
                                        arr[4] = 100;
                                        arr[5] = 3;
                                        arr[6] = 100;
                                        arr[7] = 126;
                                        if (mQueue.size() < 20) {
                                            mQueue.add(arr);
                                        }
//                                        write(arr);
                                        TDSService.this.mTDSFrameParse.TDSFrameInit();
                                        break;
                                    case 3:
//                                        write("~hehe~".getBytes());
                                        TDSService.this.mTDSFrameParse.TDSFrameInit();
                                        break;
                                    case 89:
                                        byte[] arrayOfByte2 = TDSService.this.mTDSFrameParse.GetFrameDeviceSerialNumber();
                                        if (arrayOfByte2 != null) {
                                            String str2 = new String(arrayOfByte2);
                                            TDSService.this.mTDSDetect.SetSerialNumber(str2);
                                            str1 = str1 + str2;
                                            TDSService.this.doSendBroadCast("com.tds.test.state", "BT_MESSAGE", "6");
                                        }
                                        TDSService.this.mTDSFrameParse.TDSFrameInit();
                                        break;
                                    default:
//                                    case 0:
//                                        TDSService.this.mTDSFrameParse.TDSFrameInit();
//                                        byte[] arrayOfByte2 = TDSService.this.mTDSFrameParse.GetFrameDeviceSerialNumber();
//                                        if (arrayOfByte2 != null) {
//                                            String str2 = new String(arrayOfByte2);
//                                            TDSService.this.mTDSDetect.SetSerialNumber(str2);
//                                            str1 = str1 + str2;
//                                            TDSService.this.doSendBroadCast("com.tds.test.state", "BT_MESSAGE", "6");
//                                        }
                                        TDSService.this.mTDSFrameParse.TDSFrameInit();
                                        break;
                                }

//                                switch (TDSService.this.mTDSFrameParse.GetFrameCmdType()) {
//                                    case 1:
//                                        byte[] arr = new byte[8];
//                                        arr[0] = 126;
//                                        arr[1] = 2;
//                                        arr[2] = 3;
//                                        arr[3] = 0;
//                                        arr[4] = 100;
//                                        arr[5] = 3;
//                                        arr[6] = 100;
//                                        arr[7] = 126;
//                                        if (mQueue.size() < 10) {
//                                            mQueue.add(arr);
//                                        }
////                                        write(arr);
//                                        break;
//                                    case BT_STATE_MESSAGE_CONNECTFAIL:
//                                        write("~hehe~".getBytes());
//                                        break;
//                                    case BT_STATE_MESSAGE_GETVOLTAGE_RESPONSE:
//                                        TDSService.this.mTDSFrameParse.TDSFrameInit();
//                                        int j = TDSService.this.mTDSFrameParse.GetFrameVoltage();
//                                        int k = TDSService.this.mTDSFrameParse.GetFrameVoltageNum();
//                                        if (j != -1) {
//                                            str1 = "voltage[" + k + "] = " + j;
//                                            TDSService.this.doSendBroadCast("com.tds.test.state", "BT_MESSAGE", "5");
//                                        }
//                                        break;
//                                    case BT_STATE_MESSAGE_SETCHECK_RESPONSE:
//                                        if (TDSService.this.mTDSFrameParse.GetFrameSetResponseResult() == 1) {
//                                            TDSService.this.doSendBroadCast("com.tds.test.state", "BT_MESSAGE", "7");
//                                        }
//                                        break;
//                                    case BT_STATE_MESSAGE_GETTEST_RESPONSE:
//                                        TDSService.this.doSendBroadCast("com.tds.test.state", "BT_MESSAGE", "9");
//                                        break;
//                                    case BT_STATE_MESSAGE_GETSN_RESPONSE:
//                                        byte[] arrayOfByte2 = TDSService.this.mTDSFrameParse.GetFrameDeviceSerialNumber();
//                                        if (arrayOfByte2 != null) {
//                                            String str2 = new String(arrayOfByte2);
//                                            TDSService.this.mTDSDetect.SetSerialNumber(str2);
//                                            str1 = str1 + str2;
//                                            TDSService.this.doSendBroadCast("com.tds.test.state", "BT_MESSAGE", "6");
//                                        }
//                                        break;
//                                    case BT_STATE_MESSAGE_SETSN_RESPONSE:
//                                        if (TDSService.this.mTDSFrameParse.GetFrameSetResponseResult() == 1)
//                                            TDSService.this.doSendBroadCast("com.tds.test.state", "BT_MESSAGE", "8");
//                                        break;
//                                    case 11:
//                                    case 13:
//                                    default:
//                                    case 14:
//                                    case 4:
//                                    case 12:
//                                    case 10:
//                                        str1 = "Set Failure!";
//                                        break;
//                                }
                            }
                        }

//                            break;
//                        TDSService.this.mTDSFrameParse.TDSFrameInit();
//                        Log.i("TDSService", "ParseFrame error " + i);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                AppLog.instance().iToSd("断开链接：disconnected+++"+e.getMessage());
//                Log.e("TDSService", "disconnected", e);
//                TDSService.this.connectionLost();
                return;
            }


//            do
//                try {
//                    while (true)
//                        if (this.mmInStream.read(arrayOfByte1) > 0) {
//                            i = TDSService.this.mTDSFrameParse.ParseFrame(arrayOfByte1[0]);
//                            if (i != -1)
//                                break;
//                            TDSService.this.mTDSFrameParse.TDSFrameInit();
//                            Log.i("TDSService", "ParseFrame error " + i);
//                        }
//                } catch (IOException localIOException) {
//                    Log.e("TDSService", "disconnected", localIOException);
//                    TDSService.this.connectionLost();
//                    return;
//                }
//            while (i != 0);
//            if (TDSService.this.mTDSFrameParse.IsFrameValid()) {
////                TDSService.this.mTDSFrameParse.TDSFrameInit();
//                Log.i("TDSService", "ParseFrame ok1 " + i);
//                TDSService.this.mTDSFrameParse.GetFrameData();
//                TDSService.this.mTDSFrameParse.GetFrameDataLen();
//                switch (TDSService.this.mTDSFrameParse.GetFrameCmdType()) {
//                    case 2:
//                        TDSService.this.mTDSFrameParse.TDSFrameInit();
//                        int j = TDSService.this.mTDSFrameParse.GetFrameVoltage();
//                        int k = TDSService.this.mTDSFrameParse.GetFrameVoltageNum();
//                        if (j != -1) {
//                            str1 = "voltage[" + k + "] = " + j;
//                            TDSService.this.doSendBroadCast("com.tds.test.state", "BT_MESSAGE", "5");
//                        }
//                        TDSService.this.mTDSFrameParse.TDSFrameInit();
//                        break;
//                    default:
//                        TDSService.this.mTDSFrameParse.TDSFrameInit();
//                        break;
//                    case 0:
//                        byte[] arrayOfByte2 = TDSService.this.mTDSFrameParse.GetFrameDeviceSerialNumber();
//                        if (arrayOfByte2 != null) {
//                            String str2 = new String(arrayOfByte2);
//                            TDSService.this.mTDSDetect.SetSerialNumber(str2);
//                            str1 = str1 + str2;
//                            TDSService.this.doSendBroadCast("com.tds.test.state", "BT_MESSAGE", "6");
//                        }
//                        TDSService.this.mTDSFrameParse.TDSFrameInit();
//                        return;
//                }
//
//                switch (TDSService.this.mTDSFrameParse.GetFrameCmdType()) {
//                    case 1:
//                        byte[] arr = new byte[8];
//                        arr[0] = 126;
//                        arr[1] = 2;
//                        arr[2] = 3;
//                        arr[3] = 0;
//                        arr[4] = 55;
//                        arr[5] = 03;
//                        arr[6] = 55;
//                        arr[7] = 126;
//                        write(arr);
//                        break;
//                    case BT_STATE_MESSAGE_CONNECTFAIL:
//                        write("hehe".getBytes());
//                        break;
//                    case BT_STATE_MESSAGE_GETVOLTAGE_RESPONSE:
//                        TDSService.this.mTDSFrameParse.TDSFrameInit();
//                        int j = TDSService.this.mTDSFrameParse.GetFrameVoltage();
//                        int k = TDSService.this.mTDSFrameParse.GetFrameVoltageNum();
//                        if (j != -1) {
//                            str1 = "voltage[" + k + "] = " + j;
//                            TDSService.this.doSendBroadCast("com.tds.test.state", "BT_MESSAGE", "5");
//                        }
//                    case BT_STATE_MESSAGE_SETCHECK_RESPONSE:
//                        if (TDSService.this.mTDSFrameParse.GetFrameSetResponseResult() == 1) {
//                            TDSService.this.doSendBroadCast("com.tds.test.state", "BT_MESSAGE", "7");
//                        }
//                        return;
//                    case BT_STATE_MESSAGE_GETTEST_RESPONSE:
//                        TDSService.this.doSendBroadCast("com.tds.test.state", "BT_MESSAGE", "9");
//                        return;
//                    case BT_STATE_MESSAGE_GETSN_RESPONSE:
//                        byte[] arrayOfByte2 = TDSService.this.mTDSFrameParse.GetFrameDeviceSerialNumber();
//                        if (arrayOfByte2 != null) {
//                            String str2 = new String(arrayOfByte2);
//                            TDSService.this.mTDSDetect.SetSerialNumber(str2);
//                            str1 = str1 + str2;
//                            TDSService.this.doSendBroadCast("com.tds.test.state", "BT_MESSAGE", "6");
//                        }
//                        return;
//                    case BT_STATE_MESSAGE_SETSN_RESPONSE:
//                        if (TDSService.this.mTDSFrameParse.GetFrameSetResponseResult() == 1)
//                            TDSService.this.doSendBroadCast("com.tds.test.state", "BT_MESSAGE", "8");
//                        return;
//                    case 11:
//                    case 13:
//                    default:
//                    case 14:
//                    case 4:
//                    case 12:
//                    case 10:
//                        str1 = "Set Failure!";
//                        return;
//                }
//            }
//            AppLog.instance().d(str1);

//            while (true) {
//                TDSService.this.mTDSFrameParse.TDSFrameInit();
////        break;
//                int j = TDSService.this.mTDSFrameParse.GetFrameVoltage();
//                int k = TDSService.this.mTDSFrameParse.GetFrameVoltageNum();
//                if (j != -1) {
//                    str1 = "voltage[" + k + "] = " + j;
//                    TDSService.this.doSendBroadCast("com.tds.test.state", "BT_MESSAGE", "5");
//                    continue;
//                    TDSService.this.doSendBroadCast("com.tds.test.state", "BT_MESSAGE", "9");
//                    continue;
//
//                    byte[] arrayOfByte2 = TDSService.this.mTDSFrameParse.GetFrameDeviceSerialNumber();
//                    if (arrayOfByte2 != null) {
//                        String str2 = new String(arrayOfByte2);
//                        TDSService.this.mTDSDetect.SetSerialNumber(str2);
//                        str1 = str1 + str2;
//                        TDSService.this.doSendBroadCast("com.tds.test.state", "BT_MESSAGE", "6");
//                        continue;
//                        if (TDSService.this.mTDSFrameParse.GetFrameSetResponseResult() == 1) {
//                            TDSService.this.doSendBroadCast("com.tds.test.state", "BT_MESSAGE", "7");
//                            continue;
//                            if (TDSService.this.mTDSFrameParse.GetFrameSetResponseResult() == 1)
//                                TDSService.this.doSendBroadCast("com.tds.test.state", "BT_MESSAGE", "8");
//                            else
//                                str1 = "Set Failure!";
//                        }
//                    }
//                }
//            }
        }

        public void write(byte[] paramArrayOfByte) {
            try {
                this.mmOutStream.write(paramArrayOfByte);
            } catch (IOException localIOException) {
                Log.e("TDSService", "Exception during write", localIOException);
            }
        }
    }

    public class LocalBinder extends Binder {
        public LocalBinder() {
        }

        TDSService getService() {
            return TDSService.this;
        }
    }
}

/* Location:           D:\android studio\fanbianyi\classes-dex2jar (2).jar
 * Qualified Name:     com.tds.test.TDSService
 * JD-Core Version:    0.6.2
 */