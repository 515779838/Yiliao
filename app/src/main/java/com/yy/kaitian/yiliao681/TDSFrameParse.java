package com.yy.kaitian.yiliao681;

import com.yy.kaitian.yiliao681.utils.AppLog;

public class TDSFrameParse {
    final byte TDS_CMD_GET_SERIAL_NUMBER = 3;
    final byte TDS_CMD_GET_SERIAL_NUMBER_RESPONSE = 4;
    final byte TDS_CMD_GET_TEST_VOLTAGE = 13;
    final byte TDS_CMD_GET_TEST_VOLTAGE_RESPONSE = 14;
    final byte TDS_CMD_GET_VOLTAGE = 1;
    final byte TDS_CMD_GET_VOLTAGE_RESPONSE = 2;
    final byte TDS_CMD_SET_CHECK_MODE = 11;
    final byte TDS_CMD_SET_CHECK_MODE_RESPONSE = 12;
    final byte TDS_CMD_SET_GREEN_LED_STATUS = 9;
    final byte TDS_CMD_SET_GREEN_LED_STATUS_RESPONSE = 10;
    final byte TDS_CMD_SET_RED_LED_STATUS = 7;
    final byte TDS_CMD_SET_RED_LED_STATUS_RESPONSE = 8;
    final byte TDS_CMD_SET_SERIAL_NUMBER = 5;
    final byte TDS_CMD_SET_SERIAL_NUMBER_RESPONSE = 6;
    final int TDS_FRAME_CMD_OFFSET = 0;
    final int TDS_FRAME_CONTENT_OFFSET = 2;
    final int TDS_FRAME_LEN_OFFSET = 1;
    int TDS_FRAME_MAX_DATA_SIZE = 200;
    final byte TDS_HDLC_ESCAPE = 125;
    final byte TDS_HDLC_ESCAPE_MASK = 32;
    final byte TDS_HDLC_FLAG = 126;
    final int TDS_MAX_DEVICE_SERIAL_NUMBER_LEN = 40;
    final int TDS_PKT_STATE_ERROR = 4;
    final int TDS_PKT_STATE_GATHER = 2;
    final int TDS_PKT_STATE_HEAD = 1;
    final int TDS_PKT_STATE_NONE = 0;
    final int TDS_PKT_STATE_RECV = 3;
    int mBufReadPos = 0;
    int mBufWritePos = 0;
    byte[] mPktData = new byte[this.TDS_FRAME_MAX_DATA_SIZE];
    int mPktDataSize = 0;
    int mPktState;
    byte[] mRingBuffer = new byte[1024];
    Boolean mbPktEsc;

    private byte CalculateXor(byte[] paramArrayOfByte, int paramInt) {
        byte b = 0;
        for (int i = 0;i < paramInt ; i++) {
            b = (byte) (b ^ paramArrayOfByte[i]);
        }
        return b;
    }



    public byte GetFrameCmdType() {
        return this.mPktData[0];
    }

    public byte[] GetFrameData() {
        return this.mPktData;
    }

    public int GetFrameDataLen() {
        return this.mPktDataSize;
    }

    public byte[] GetFrameDeviceSerialNumber() {
        // TODO: 2017/11/13
        byte[] arrayOfByte = null;
        if (this.mPktData[0] == 89 && this.mPktData[0] != 0) {
            arrayOfByte = new byte[mPktDataSize];
            for (int i = 0; i < mPktDataSize; i++) {
                arrayOfByte[i] = mPktData[i];
            }
        }
        return arrayOfByte;
    }
//    public byte[] GetFrameDeviceSerialNumber() {
//        // TODO: 2017/11/13
//        int i = this.mPktData[0];
//        byte[] arrayOfByte = null;
//        int j;
//        if (i == 4) {
//            j = this.mPktData[1];
//            if (j <= 39) {
//                arrayOfByte = new byte[j];
//                for (int k = 0; k < j; k++) {
//                    arrayOfByte[k] = this.mPktData[(k + 2)];
//                }
//            }
//        }
//        return arrayOfByte;
//    }

    public int GetFrameSetResponseResult() {
        return this.mPktData[2];
    }

    public int GetFrameTestPowerVoltage() {
        if ((this.mPktData[0] != 14) || (this.mPktData[1] != 5))
            return -1;
        return (0xFF & this.mPktData[5]) + ((0xFF & this.mPktData[6]) << 8);
    }

    public int GetFrameTestVoltage() {
        if ((this.mPktData[0] != 14) || (this.mPktData[1] != 5))
            return -1;
        return (0xFF & this.mPktData[3]) + ((0xFF & this.mPktData[4]) << 8);
    }

    public int GetFrameVoltage() {
        if ((this.mPktData[0] != 2) || (this.mPktData[1] != 3))
            return -1;
        return (0xFF & this.mPktData[3]) + ((0xFF & this.mPktData[4]) << 8);
    }

    public int GetFrameVoltageNum() {
        if ((this.mPktData[0] != 2) || (this.mPktData[1] != 3))
            return -1;
        return this.mPktData[2];
    }

    public byte[] GetTestVoltagePacket(int paramInt) {
//    byte[] arrayOfByte1 = new byte[4];
//    int i = 0 + 1;
//    arrayOfByte1[0] = 13;
//    int j = i + 1;
//    arrayOfByte1[i] = 1;
//    int k = j + 1;
//    arrayOfByte1[j] = ((byte)paramInt);
//    int m = CalculateXor(arrayOfByte1, k);
//    int n = k + 1;
//    arrayOfByte1[k] = m;
//    byte[] arrayOfByte2 = new byte[6];
//    int i1 = 0 + 1;
//    arrayOfByte2[0] = 126;
//    int i2 = 0;
//    while (true)
//    {
//      if (i2 >= n)
//      {
//        (i1 + 1);
//        arrayOfByte2[i1] = 126;
//        TDSUtils.bytesToHexString(arrayOfByte2);
//        return arrayOfByte2;
//      }
//      int i3 = i1 + 1;
//      arrayOfByte2[i1] = arrayOfByte1[i2];
//      i2++;
//      i1 = i3;
//    }
        return null;
    }
    public byte[] GetDeviceSerialNumberPacket() {
        byte[] arrayOfByte1 = new byte[5];
        arrayOfByte1[0] = 126;
        arrayOfByte1[1] = 3;
        arrayOfByte1[2] = 0;
        arrayOfByte1[3] = 3;
        arrayOfByte1[4] = 126;
        return arrayOfByte1;
    }
    public byte[] GetVoltagePacket(int paramInt) {
        AppLog.instance().iToSd("GetVoltagePacket穴位号："+paramInt);
        byte[] arrayOfByteMy = new byte[6];
        arrayOfByteMy[0] = 126;
        arrayOfByteMy[1] = 1;
        arrayOfByteMy[2] = 1;
        arrayOfByteMy[3] = (byte) paramInt;
        arrayOfByteMy[4] = (byte) paramInt;
        arrayOfByteMy[5] = 126;
        return arrayOfByteMy;
//        byte[] arrayOfByte1 = new byte[4];
//        int i = 0 + 1;
//        arrayOfByte1[0] = 1;
//        int j = i + 1;
//        arrayOfByte1[i] = 1;
//        int k = j + 1;
//        arrayOfByte1[j] = ((byte) paramInt);
//        arrayOfByte1[k] = CalculateXor(arrayOfByte1, k);
//        int n = k + 1;
////    arrayOfByte1[k] = m;
//        byte[] arrayOfByte2 = new byte[6];
//        int i1 = 0 + 1;
//        arrayOfByte2[0] = 126;
//        int i2 = 0;
//        while (true) {
//            if (i2 >= n) {
////        (i1 + 1);
//                arrayOfByte2[i1] = 126;
//                Log.d("TDSTestActivity1", TDSUtils.bytesToHexString(arrayOfByte2));
//                return arrayOfByte2;
//            }
//            int i3 = i1 + 1;
//            arrayOfByte2[i1] = arrayOfByte1[i2];
//            i2++;
//            i1 = i3;
//        }
    }

    public Boolean IsFrameValid() {
        if ((this.mPktState == 2) && (this.mPktDataSize > 0))
            return true;
        return false;
    }
    public int ParseFrame(byte  paramByte ){
        AppLog.instance().d(paramByte);
        if(mPktDataSize > TDS_FRAME_MAX_DATA_SIZE){
            TDSFrameInit();
            return 11;
        }
        switch (mPktState) {
            case TDS_PKT_STATE_RECV:
                if(paramByte == 126){
                    mbPktEsc = true;
                    mPktDataSize = 0;
                    mPktState = TDS_PKT_STATE_HEAD;
                    return TDS_PKT_STATE_RECV;
                } else {
                    TDSFrameInit();
                    return TDS_PKT_STATE_RECV;
                }
            case TDS_PKT_STATE_ERROR:
                TDSFrameInit();
                return TDS_PKT_STATE_ERROR;
//            case TDS_PKT_STATE_NONE:
//                if(paramByte != 126){
//                    this.mPktState = TDS_PKT_STATE_NONE;
//                    mbPktEsc = false;
//                    return 1;
//                } else {
//                    this.mPktState = TDS_PKT_STATE_HEAD;
//                    mbPktEsc = true;
//                    return 1;
//                }
            case TDS_PKT_STATE_HEAD:
                if(mPktDataSize == 0 && paramByte == 126){
                    return TDS_PKT_STATE_HEAD;
                }
                if(mPktDataSize == 0 && paramByte==2){
                    mPktData[mPktDataSize] = paramByte;
                    mPktDataSize++;
                    return TDS_PKT_STATE_HEAD;
                }

                if(mPktDataSize == 0 && paramByte==89){
                    mPktData[mPktDataSize] = paramByte;
                    mPktDataSize++;
                    return TDS_PKT_STATE_HEAD;
                }

                if(mPktDataSize == 1 && paramByte==3){
                    mPktData[mPktDataSize] = paramByte;
                    mPktDataSize++;
                    mPktState = TDS_PKT_STATE_GATHER;
                    return TDS_PKT_STATE_HEAD;
                }
                if(mPktDataSize == 1 && paramByte==72){
                    mPktData[mPktDataSize] = paramByte;
                    mPktDataSize++;
                    mPktState = TDS_PKT_STATE_GATHER;
                    return TDS_PKT_STATE_HEAD;
                }
                TDSFrameInit();
                return TDS_PKT_STATE_HEAD;
            case TDS_PKT_STATE_GATHER:
                if(mPktData[0] == 2 && mPktData[1] == 3){
                    if(mPktDataSize <= 5){
                        mPktData[mPktDataSize] = paramByte;
                        mPktDataSize++;
                        return TDS_PKT_STATE_GATHER;
                    } else {
                        return -1;
                    }
                } else if(mPktData[0] == 89 && mPktData[1] == 72){
                    if(mPktDataSize < 15){
                        if(paramByte == 126){
                            return -1;
                        } else {
                            mPktData[mPktDataSize] = paramByte;
                            mPktDataSize++;
                            return TDS_PKT_STATE_GATHER;
                        }
                    } else {
                        TDSFrameInit();
                        return 1;
                    }
                } else {
                    TDSFrameInit();
                    return 1;
                }
            default:
                TDSFrameInit();
                return 1;
        }
    }

//    public int ParseFrame(byte  paramByte ){
//        AppLog.instance().d(paramByte);
//        if(mPktDataSize > TDS_FRAME_MAX_DATA_SIZE){
//            TDSFrameInit();
//            return 11;
//        }
//        switch (mPktState) {
//            case TDS_PKT_STATE_RECV:
//                if(paramByte == 126){
//                    mbPktEsc = true;
//                    mPktDataSize = 0;
//                    mPktState = TDS_PKT_STATE_HEAD;
//                    return TDS_PKT_STATE_RECV;
//                } else {
//                    TDSFrameInit();
//                    return TDS_PKT_STATE_RECV;
//                }
//            case TDS_PKT_STATE_ERROR:
//                TDSFrameInit();
//                return TDS_PKT_STATE_ERROR;
////            case TDS_PKT_STATE_NONE:
////                if(paramByte != 126){
////                    this.mPktState = TDS_PKT_STATE_NONE;
////                    mbPktEsc = false;
////                    return 1;
////                } else {
////                    this.mPktState = TDS_PKT_STATE_HEAD;
////                    mbPktEsc = true;
////                    return 1;
////                }
//            case TDS_PKT_STATE_HEAD:
//                if(mPktDataSize == 0 && paramByte == 126){
//                    return TDS_PKT_STATE_HEAD;
//                }
//                if(mPktDataSize == 0 && paramByte==2){
//                    mPktData[mPktDataSize] = paramByte;
//                    mPktDataSize++;
//                    return TDS_PKT_STATE_HEAD;
//                }
//
//                if(mPktDataSize == 1 && paramByte==3){
//                    mPktData[mPktDataSize] = paramByte;
//                    mPktDataSize++;
//                    mPktState = TDS_PKT_STATE_GATHER;
//                    return TDS_PKT_STATE_HEAD;
//                }
//                TDSFrameInit();
//                return TDS_PKT_STATE_HEAD;
//            case TDS_PKT_STATE_GATHER:
//
//                if(mPktDataSize <= 5){
//                    mPktData[mPktDataSize] = paramByte;
//                    mPktDataSize++;
//                    return TDS_PKT_STATE_GATHER;
//                } else {
//                    return -1;
//                }
//            default:
//                TDSFrameInit();
//                return 1;
//        }
//    }


//    public int ParseFrame(byte  paramByte ){
//        AppLog.instance().d(paramByte);
//        if(mPktDataSize > TDS_FRAME_MAX_DATA_SIZE){
//            return -1;
//        }
//        switch (mPktState) {
//            case TDS_PKT_STATE_ERROR:
//            case TDS_PKT_STATE_RECV:
//            default:
//                return -1;
//            case TDS_PKT_STATE_NONE:
//                if(paramByte != 126){
//                    this.mPktState = TDS_PKT_STATE_NONE;
//                    mbPktEsc = false;
//                    return 1;
//                } else {
//                    this.mPktState = TDS_PKT_STATE_HEAD;
//                    mbPktEsc = true;
//                    return 1;
//                }
//            case TDS_PKT_STATE_HEAD:
//                if(paramByte == 126){
//                    if(mbPktEsc){
//                        mPktState = TDS_PKT_STATE_GATHER;
//                        mbPktEsc = true;
//                        return 1;
//                    }
//                } else {
//                    mPktState = TDS_PKT_STATE_GATHER;
//                    mPktData[mPktDataSize] = paramByte;
//                    mPktDataSize++;
//                    return 1;
////                    mPktState = TDS_PKT_STATE_NONE;
////                    mbPktEsc = false;
//                }
//            case TDS_PKT_STATE_GATHER:
//                if(paramByte != 126){
//                    mPktData[mPktDataSize] = paramByte;
//                    mPktDataSize++;
//                    return 1;
//                } else {
//                    this.mPktState = TDS_PKT_STATE_RECV;
//                    mbPktEsc = false;
//                    return -1;
//                }
//        }
//    }


//    public int ParseFrame(byte paramByte)
//    {
//        if (this.mPktDataSize > this.TDS_FRAME_MAX_DATA_SIZE)
//            return -1;
//        do
//        {
////            return -1;
//            switch (this.mPktState)
//            {
//                default:
//                    return -1;
//                case 0:
//                    if(paramByte != 126){
//                        this.mPktState = TDS_PKT_STATE_HEAD;
//                        return 1;
//                    }
//                    break;
//                case 1:
//                    if(paramByte != 126){
//                        int j;
//                        if(mbPktEsc){
//
//                        }
//                    } else {
//
//                    }
//                    break;
//                case 2:
//
//                    break;
//            }
//        }
//        while (paramByte != 126);
//        this.mPktState = TDS_PKT_STATE_HEAD;
//        return 1;
//        if (paramByte != 126)
//        {
//            int j;
//            if (this.mbPktEsc)
//            {
//                j = (byte)(paramByte ^ 0x20);
//                this.mbPktEsc = false;
//            }
//            while (true)
//            {
//                this.mPktState = TDS_PKT_STATE_GATHER;
//                this.mPktData[this.mPktDataSize] = j;
//                this.mPktDataSize = (1 + this.mPktDataSize);
//                return 1;
//                if (paramByte == 125)
//                {
//                    this.mbPktEsc = true;
//                    return 1;
//                }
//                j = paramByte;
//            }
//        }
//        return 0;
//        if (paramByte == 126)
//        {
//            this.mPktState = TDS_PKT_STATE_RECV;
//            if (CalculateXor(this.mPktData, -1 + this.mPktDataSize) != this.mPktData[(-1 + this.mPktDataSize)])
//                return 0;
//            return 0;
//        }
//        int i;
//        if (this.mbPktEsc)
//        {
//            i = (byte)(paramByte ^ 0x20);
//            this.mbPktEsc = false;
//        }
//        while (true)
//        {
//            this.mPktData[this.mPktDataSize] = i;
//            this.mPktDataSize = (1 + this.mPktDataSize);
//            return 1;
//            if (paramByte == 125)
//            {
//                this.mbPktEsc = Boolean.valueOf(true);
//                return 1;
//            }
//            i = paramByte;
//        }
//    }


    //
    public byte[] SetCheckModePacket(byte paramByte) {
//    byte[] arrayOfByte1 = new byte[5];
//    int i = 0 + 1;
//    arrayOfByte1[0] = 11;
//    int j = i + 1;
//    arrayOfByte1[i] = 1;
//    int k = j + 1;
//    arrayOfByte1[j] = paramByte;
//    int m = CalculateXor(arrayOfByte1, k);
//    int n = k + 1;
//    arrayOfByte1[k] = m;
//    byte[] arrayOfByte2 = new byte[6];
//    int i1 = 0 + 1;
//    arrayOfByte2[0] = 126;
//    int i2 = 0;
//    while (true)
//    {
//      if (i2 >= n)
//      {
//        (i1 + 1);
//        arrayOfByte2[i1] = 126;
//        return arrayOfByte2;
//      }
//      int i3 = i1 + 1;
//      arrayOfByte2[i1] = arrayOfByte1[i2];
//      i2++;
//      i1 = i3;
//    }
        return null;
    }

    //设置设备序列号包
    public byte[] SetDeviceSerialNumberPacket(String paramString) {
//    byte[] arrayOfByte1 = new byte[256];
//    byte[] arrayOfByte2 = paramString.getBytes();
//    if (arrayOfByte2.length > 39)
//      return null;
//    int i = 0 + 1;
//    arrayOfByte1[0] = 5;
//    int j = i + 1;
//    arrayOfByte1[i] = ((byte)arrayOfByte2.length);
//    int k = 0;
//    int i1;
//    byte[] arrayOfByte3;
//    int i2;
//    int i3;
//    if (k >= arrayOfByte2.length)
//    {
//      int n = CalculateXor(arrayOfByte1, j);
//      i1 = j + 1;
//      arrayOfByte1[j] = n;
//      arrayOfByte3 = new byte[i1 + 2];
//      i2 = 0 + 1;
//      arrayOfByte3[0] = 126;
//      i3 = 0;
//    }
//    while (true)
//    {
//      if (i3 >= i1)
//      {
//        (i2 + 1);
//        arrayOfByte3[i2] = 126;
//        return arrayOfByte3;
//        int m = j + 1;
//        arrayOfByte1[j] = arrayOfByte2[k];
//        k++;
//        j = m;
//        break;
//      }
//      int i4 = i2 + 1;
//      arrayOfByte3[i2] = arrayOfByte1[i3];
//      i3++;
//      i2 = i4;
//    }
        return null;
    }

    public byte[] SetGreenLedPacket(byte paramByte) {
//    byte[] arrayOfByte1 = new byte[5];
//    int i = 0 + 1;
//    arrayOfByte1[0] = 9;
//    int j = i + 1;
//    arrayOfByte1[i] = 1;
//    int k = j + 1;
//    arrayOfByte1[j] = paramByte;
//    int m = CalculateXor(arrayOfByte1, k);
//    int n = k + 1;
//    arrayOfByte1[k] = m;
//    byte[] arrayOfByte2 = new byte[6];
//    int i1 = 0 + 1;
//    arrayOfByte2[0] = 126;
//    int i2 = 0;
//    while (true)
//    {
//      if (i2 >= n)
//      {
//        (i1 + 1);
//        arrayOfByte2[i1] = 126;
//        return arrayOfByte2;
//      }
//      int i3 = i1 + 1;
//      arrayOfByte2[i1] = arrayOfByte1[i2];
//      i2++;
//      i1 = i3;
//    }
        return null;
    }

    public byte[] SetRedLedPacket(byte paramByte) {
//    byte[] arrayOfByte1 = new byte[5];
//    int i = 0 + 1;
//    arrayOfByte1[0] = 7;
//    int j = i + 1;
//    arrayOfByte1[i] = 1;
//    int k = j + 1;
//    arrayOfByte1[j] = paramByte;
//    int m = CalculateXor(arrayOfByte1, k);
//    int n = k + 1;
//    arrayOfByte1[k] = m;
//    byte[] arrayOfByte2 = new byte[6];
//    int i1 = 0 + 1;
//    arrayOfByte2[0] = 126;
//    int i2 = 0;
//    while (true)
//    {
//      if (i2 >= n)
//      {
//        (i1 + 1);
//        arrayOfByte2[i1] = 126;
//        return arrayOfByte2;
//      }
//      int i3 = i1 + 1;
//      arrayOfByte2[i1] = arrayOfByte1[i2];
//      i2++;
//      i1 = i3;
//    }
        return null;
    }

    public void TDSFrameInit() {
        this.mPktState = TDS_PKT_STATE_RECV;
        this.mbPktEsc = false;
        this.mPktDataSize = 0;
    }
}

/* Location:           D:\android studio\fanbianyi\classes-dex2jar (2).jar
 * Qualified Name:     com.tds.test.TDSFrameParse
 * JD-Core Version:    0.6.2
 */