package com.yy.kaitian.yiliao681;

public class PointDetectData
{
  int _customer_id;
  String _detect_time;
  int _id;
  String[] _point_value = new String[24];

  public PointDetectData()
  {
    this._detect_time = "";
  }

  public PointDetectData(int paramInt)
  {
    this._id = paramInt;
    this._detect_time = "";
  }

  public PointDetectData(int paramInt1, int paramInt2)
  {
    this._id = paramInt1;
    this._customer_id = paramInt2;
    this._detect_time = "";
  }

  public int getCustomerID()
  {
    return this._customer_id;
  }

  public String getDetectTime()
  {
    return this._detect_time;
  }

  public int getID()
  {
    return this._id;
  }

  public String getPointValue(int paramInt)
  {
    return this._point_value[paramInt];
  }

  public int getUnfinishedPointNum()
  {
    int i = 0;
    for (int j = 0; ; j++)
    {
      if (j >= 24)
        return i;
      if (this._point_value[j].equals("null"))
        i++;
    }
  }

  public void initPointValues()
  {
    for (int i = 0; ; i++)
    {
      if (i >= 24)
        return;
      this._point_value[i] = "null";
    }
  }

  public boolean isLostPointDetectFinish()
  {
    return !this._point_value[23].equals("null");
  }

  public boolean isPointDetectFinish()
  {
    for (int i = 0; i < 24 ; i++)
    {
      if (this._point_value[i].equals("null")){
        return false;
      }
    }
    return true;
  }

  public void setCustomerID(int paramInt)
  {
    this._customer_id = paramInt;
  }

  public void setDetectTime(String paramString)
  {
    this._detect_time = paramString;
  }

  public void setID(int paramInt)
  {
    this._id = paramInt;
  }

  public void setPointValue(int paramInt, String paramString)
  {
    this._point_value[paramInt] = paramString;
  }
}

/* Location:           D:\android studio\fanbianyi\classes-dex2jar (2).jar
 * Qualified Name:     com.tds.test.PointDetectData
 * JD-Core Version:    0.6.2
 */