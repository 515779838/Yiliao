package com.yy.kaitian.yiliao681;

public class CustomerProfilesClass
{
  String _birthday;
  String _detect_time;
  String _diastolic_pressure;
  String _height;
  int _id;
  String _marital_status;
  String _medical_history_1;
  String _medical_history_2;
  String _medical_history_3;
  String _memo;
  String _name;
  String _native_place;
  String _profession;
  String _pulse;
  String _register_time;
  String _sex;
  String _systolic_pressure;
  String _weight;

  public CustomerProfilesClass()
  {
  }

  public CustomerProfilesClass(int paramInt, String paramString)
  {
    this._id = paramInt;
    this._name = paramString;
    this._detect_time = "";
  }

  public CustomerProfilesClass(String paramString)
  {
    this._name = paramString;
    this._detect_time = "";
  }

  public String getBirthday()
  {
    return this._birthday;
  }

  public String getDetectTime()
  {
    return this._detect_time;
  }

  public String getDiastolicPressure()
  {
    return this._diastolic_pressure;
  }

  public String getHeight()
  {
    return this._height;
  }

  public int getID()
  {
    return this._id;
  }

  public String getMaritalStatus()
  {
    return this._marital_status;
  }

  public String getMedicalHistory1()
  {
    return this._medical_history_1;
  }

  public String getMedicalHistory2()
  {
    return this._medical_history_2;
  }

  public String getMedicalHistory3()
  {
    return this._medical_history_3;
  }

  public String getMemo()
  {
    return this._memo;
  }

  public String getName()
  {
    return this._name;
  }

  public String getNativePlace()
  {
    return this._native_place;
  }

  public String getProfession()
  {
    return this._profession;
  }

  public String getPulse()
  {
    return this._pulse;
  }

  public String getRegisterTime()
  {
    return this._register_time;
  }

  public String getSex()
  {
    return this._sex;
  }

  public String getSystolicPressure()
  {
    return this._systolic_pressure;
  }

  public String getWeight()
  {
    return this._weight;
  }

  public void setBirthday(String paramString)
  {
    this._birthday = paramString;
  }

  public void setDetectTime(String paramString)
  {
    this._detect_time = paramString;
  }

  public void setDiastolicPressure(String paramString)
  {
    this._diastolic_pressure = paramString;
  }

  public void setHeight(String paramString)
  {
    this._height = paramString;
  }

  public void setID(int paramInt)
  {
    this._id = paramInt;
  }

  public void setMaritalStatus(String paramString)
  {
    this._marital_status = paramString;
  }

  public void setMedicalHistory1(String paramString)
  {
    this._medical_history_1 = paramString;
  }

  public void setMedicalHistory2(String paramString)
  {
    this._medical_history_2 = paramString;
  }

  public void setMedicalHistory3(String paramString)
  {
    this._medical_history_3 = paramString;
  }

  public void setMemo(String paramString)
  {
    this._memo = paramString;
  }

  public void setName(String paramString)
  {
    this._name = paramString;
  }

  public void setNativePlace(String paramString)
  {
    this._native_place = paramString;
  }

  public void setProfession(String paramString)
  {
    this._profession = paramString;
  }

  public void setPulse(String paramString)
  {
    this._pulse = paramString;
  }

  public void setRegisterTime(String paramString)
  {
    this._register_time = paramString;
  }

  public void setSex(String paramString)
  {
    this._sex = paramString;
  }

  public void setSystolicPressure(String paramString)
  {
    this._systolic_pressure = paramString;
  }

  public void setWeight(String paramString)
  {
    this._weight = paramString;
  }
}

/* Location:           D:\android studio\fanbianyi\classes-dex2jar (2).jar
 * Qualified Name:     com.tds.test.CustomerProfilesClass
 * JD-Core Version:    0.6.2
 */