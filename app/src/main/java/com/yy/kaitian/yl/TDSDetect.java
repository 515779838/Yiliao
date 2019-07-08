package com.yy.kaitian.yl;

import java.util.ArrayList;

public class TDSDetect
{
  private ArrayList<MeasureValueInfo> mMeasureValueList = new ArrayList();
  String mSerialNum = "";

  public void AddMeasureValue(int paramInt1, int paramInt2)
  {
    MeasureValueInfo localMeasureValueInfo = new MeasureValueInfo(paramInt1, paramInt2);
    this.mMeasureValueList.add(localMeasureValueInfo);
  }

  public void ClearAllMeasureValue()
  {
    this.mMeasureValueList.clear();
  }

  public int GetMeasureData(int paramInt)
  {
    for (int i = 0; ; i++)
    {
      if (i >= this.mMeasureValueList.size())
        return -1;
      MeasureValueInfo localMeasureValueInfo = (MeasureValueInfo)this.mMeasureValueList.get(i);
      if (localMeasureValueInfo.mPosition == paramInt)
        return localMeasureValueInfo.mData;
    }
  }

  public String GetSerialNumber()
  {
    return this.mSerialNum;
  }

  public void SetSerialNumber(String paramString)
  {
    this.mSerialNum = paramString;
  }

  private static class MeasureValueInfo
  {
    final int mData;
    final int mPosition;

    MeasureValueInfo(int paramInt1, int paramInt2)
    {
      this.mPosition = paramInt1;
      this.mData = paramInt2;
    }
  }
}

/* Location:           D:\android studio\fanbianyi\classes-dex2jar (2).jar
 * Qualified Name:     com.tds.test.TDSDetect
 * JD-Core Version:    0.6.2
 */