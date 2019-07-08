package com.yy.kaitian.yl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class MyDatabaseHandler extends SQLiteOpenHelper
{
  private static final String DATABASE_NAME = "customerManager";
  private static final int DATABASE_VERSION = 1;
  private static final String KEY_BIRTHDAY = "birthday";
  private static final String KEY_CHECK_TIME = "check_time";
  private static final String KEY_CUSTOMER_ID = "customer_id";
  private static final String KEY_DETECT_TIME = "detect_time";
  private static final String KEY_DIASTOLIC_PRESSURE = "diastolic_pressure";
  private static final String KEY_HEIGHT = "height";
  private static final String KEY_ID = "id";
  private static final String KEY_ITEM_ID = "id";
  private static final String KEY_MARITAL_STATUS = "marital_status";
  private static final String KEY_MEDICAL_HISTORY_1 = "medical_history_1";
  private static final String KEY_MEDICAL_HISTORY_2 = "medical_history_2";
  private static final String KEY_MEDICAL_HISTORY_3 = "medical_history_3";
  private static final String KEY_MEMO = "memo";
  private static final String KEY_NAME = "name";
  private static final String KEY_NATIVE_PLACE = "native_place";
  private static final String KEY_POINT_1 = "point_1";
  private static final String KEY_POINT_10 = "point_10";
  private static final String KEY_POINT_11 = "point_11";
  private static final String KEY_POINT_12 = "point_12";
  private static final String KEY_POINT_13 = "point_13";
  private static final String KEY_POINT_14 = "point_14";
  private static final String KEY_POINT_15 = "point_15";
  private static final String KEY_POINT_16 = "point_16";
  private static final String KEY_POINT_17 = "point_17";
  private static final String KEY_POINT_18 = "point_18";
  private static final String KEY_POINT_19 = "point_19";
  private static final String KEY_POINT_2 = "point_2";
  private static final String KEY_POINT_20 = "point_20";
  private static final String KEY_POINT_21 = "point_21";
  private static final String KEY_POINT_22 = "point_22";
  private static final String KEY_POINT_23 = "point_23";
  private static final String KEY_POINT_24 = "point_24";
  private static final String KEY_POINT_3 = "point_3";
  private static final String KEY_POINT_4 = "point_4";
  private static final String KEY_POINT_5 = "point_5";
  private static final String KEY_POINT_6 = "point_6";
  private static final String KEY_POINT_7 = "point_7";
  private static final String KEY_POINT_8 = "point_8";
  private static final String KEY_POINT_9 = "point_9";
  private static final String KEY_PROFESSION = "profession";
  private static final String KEY_PULSE = "pulse";
  private static final String KEY_REGISTER_TIME = "register_time";
  private static final String KEY_SEX = "sex";
  private static final String KEY_SYSTOLIC_PRESSURE = "systolic_pressure";
  private static final String KEY_WEIGHT = "weight";
  private static final String TABLE_CUSTOMER_PROFILES = "customer_profiles";
  private static final String TABLE_POINT_DATAS = "point_data";

  public MyDatabaseHandler(Context paramContext)
  {
    super(paramContext, "customerManager", null, 1);
  }

  long addCustomerProfiles(CustomerProfilesClass paramCustomerProfilesClass)
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("name", paramCustomerProfilesClass.getName());
    localContentValues.put("birthday", paramCustomerProfilesClass.getBirthday());
    localContentValues.put("native_place", paramCustomerProfilesClass.getNativePlace());
    localContentValues.put("profession", paramCustomerProfilesClass.getProfession());
    localContentValues.put("height", paramCustomerProfilesClass.getHeight());
    localContentValues.put("weight", paramCustomerProfilesClass.getWeight());
    localContentValues.put("systolic_pressure", paramCustomerProfilesClass.getSystolicPressure());
    localContentValues.put("diastolic_pressure", paramCustomerProfilesClass.getDiastolicPressure());
    localContentValues.put("pulse", paramCustomerProfilesClass.getPulse());
    localContentValues.put("sex", paramCustomerProfilesClass.getSex());
    localContentValues.put("marital_status", paramCustomerProfilesClass.getMaritalStatus());
    localContentValues.put("medical_history_1", paramCustomerProfilesClass.getMedicalHistory1());
    localContentValues.put("medical_history_2", paramCustomerProfilesClass.getMedicalHistory2());
    localContentValues.put("medical_history_3", paramCustomerProfilesClass.getMedicalHistory3());
    localContentValues.put("memo", paramCustomerProfilesClass.getMemo());
    localContentValues.put("register_time", paramCustomerProfilesClass.getRegisterTime());
    localContentValues.put("detect_time", paramCustomerProfilesClass.getDetectTime());
    long l = localSQLiteDatabase.insert("customer_profiles", null, localContentValues);
    localSQLiteDatabase.close();
    return l;
  }

  public long addPointData(PointDetectData paramPointDetectData)
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("customer_id", Integer.valueOf(paramPointDetectData.getCustomerID()));
    localContentValues.put("check_time", paramPointDetectData.getDetectTime());
    int i = 0 + 1;
    localContentValues.put("point_1", paramPointDetectData.getPointValue(0));
    int j = i + 1;
    localContentValues.put("point_2", paramPointDetectData.getPointValue(i));
    int k = j + 1;
    localContentValues.put("point_3", paramPointDetectData.getPointValue(j));
    int m = k + 1;
    localContentValues.put("point_4", paramPointDetectData.getPointValue(k));
    int n = m + 1;
    localContentValues.put("point_5", paramPointDetectData.getPointValue(m));
    int i1 = n + 1;
    localContentValues.put("point_6", paramPointDetectData.getPointValue(n));
    int i2 = i1 + 1;
    localContentValues.put("point_7", paramPointDetectData.getPointValue(i1));
    int i3 = i2 + 1;
    localContentValues.put("point_8", paramPointDetectData.getPointValue(i2));
    int i4 = i3 + 1;
    localContentValues.put("point_9", paramPointDetectData.getPointValue(i3));
    int i5 = i4 + 1;
    localContentValues.put("point_10", paramPointDetectData.getPointValue(i4));
    int i6 = i5 + 1;
    localContentValues.put("point_11", paramPointDetectData.getPointValue(i5));
    int i7 = i6 + 1;
    localContentValues.put("point_12", paramPointDetectData.getPointValue(i6));
    int i8 = i7 + 1;
    localContentValues.put("point_13", paramPointDetectData.getPointValue(i7));
    int i9 = i8 + 1;
    localContentValues.put("point_14", paramPointDetectData.getPointValue(i8));
    int i10 = i9 + 1;
    localContentValues.put("point_15", paramPointDetectData.getPointValue(i9));
    int i11 = i10 + 1;
    localContentValues.put("point_16", paramPointDetectData.getPointValue(i10));
    int i12 = i11 + 1;
    localContentValues.put("point_17", paramPointDetectData.getPointValue(i11));
    int i13 = i12 + 1;
    localContentValues.put("point_18", paramPointDetectData.getPointValue(i12));
    int i14 = i13 + 1;
    localContentValues.put("point_19", paramPointDetectData.getPointValue(i13));
    int i15 = i14 + 1;
    localContentValues.put("point_20", paramPointDetectData.getPointValue(i14));
    int i16 = i15 + 1;
    localContentValues.put("point_21", paramPointDetectData.getPointValue(i15));
    int i17 = i16 + 1;
    localContentValues.put("point_22", paramPointDetectData.getPointValue(i16));
    int i18 = i17 + 1;
    localContentValues.put("point_23", paramPointDetectData.getPointValue(i17));
//    (i18 + 1);
    localContentValues.put("point_24", paramPointDetectData.getPointValue(i18));
    long l = localSQLiteDatabase.insert("point_data", null, localContentValues);
    localSQLiteDatabase.close();
    return l;
  }

  public void deletePointData(int paramInt)
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    String[] arrayOfString = new String[1];
    arrayOfString[0] = String.valueOf(paramInt);
    localSQLiteDatabase.delete("point_data", "id = ?", arrayOfString);
    localSQLiteDatabase.close();
  }

  public void deletePointData(PointDetectData paramPointDetectData)
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    String[] arrayOfString = new String[1];
    arrayOfString[0] = String.valueOf(paramPointDetectData.getID());
    localSQLiteDatabase.delete("point_data", "id = ?", arrayOfString);
    localSQLiteDatabase.close();
  }

  public void deletePrifiles(CustomerProfilesClass paramCustomerProfilesClass)
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    String[] arrayOfString = new String[1];
    arrayOfString[0] = String.valueOf(paramCustomerProfilesClass.getID());
    localSQLiteDatabase.delete("customer_profiles", "id = ?", arrayOfString);
    localSQLiteDatabase.close();
  }

  public List<PointDetectData> getAllPointDatas()
  {
    ArrayList<PointDetectData> localArrayList = new ArrayList<>();
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    Cursor localCursor = localSQLiteDatabase.rawQuery("SELECT  * FROM point_data", null);
    PointDetectData localPointDetectData;
    if (localCursor.moveToFirst()){
      do {
        {
          localPointDetectData = new PointDetectData();
          localPointDetectData.setID(Integer.parseInt(localCursor.getString(0)));
          localPointDetectData.setCustomerID(Integer.parseInt(localCursor.getString(1)));
          localPointDetectData.setDetectTime(localCursor.getString(2));
          localArrayList.add(localPointDetectData);
          for (int i = 0; i < 24; i++) {
            localPointDetectData.setPointValue(i, localCursor.getString(3+i));
          }
        }
      } while (localCursor.moveToNext());
    }

    localCursor.close();
    localSQLiteDatabase.close();
//    for (int i = 0; ; i++)
//    {
//      if (i >= 24)
//      {
//        localArrayList.get(i).setPointValue(i, localCursor.getString(3));
////        localArrayList.add(localPointDetectData);
//        if (localCursor.moveToNext())
//          break;
//        localCursor.close();
//        localSQLiteDatabase.close();
//      }
//    }
    return localArrayList;
  }

  public List<CustomerProfilesClass> getAllProfiles()
  {
    ArrayList localArrayList = new ArrayList();
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    Cursor localCursor = localSQLiteDatabase.rawQuery("SELECT  * FROM customer_profiles ORDER BY register_time DESC", null);
    if (localCursor.moveToFirst())
      do
      {
        CustomerProfilesClass localCustomerProfilesClass = new CustomerProfilesClass();
        localCustomerProfilesClass.setID(Integer.parseInt(localCursor.getString(0)));
        localCustomerProfilesClass.setName(localCursor.getString(1));
        localCustomerProfilesClass.setBirthday(localCursor.getString(2));
        localCustomerProfilesClass.setNativePlace(localCursor.getString(3));
        localCustomerProfilesClass.setProfession(localCursor.getString(4));
        localCustomerProfilesClass.setHeight(localCursor.getString(5));
        localCustomerProfilesClass.setWeight(localCursor.getString(6));
        localCustomerProfilesClass.setSystolicPressure(localCursor.getString(7));
        localCustomerProfilesClass.setDiastolicPressure(localCursor.getString(8));
        localCustomerProfilesClass.setPulse(localCursor.getString(9));
        localCustomerProfilesClass.setSex(localCursor.getString(10));
        localCustomerProfilesClass.setMaritalStatus(localCursor.getString(11));
        localCustomerProfilesClass.setMedicalHistory1(localCursor.getString(12));
        localCustomerProfilesClass.setMedicalHistory2(localCursor.getString(13));
        localCustomerProfilesClass.setMedicalHistory3(localCursor.getString(14));
        localCustomerProfilesClass.setMemo(localCursor.getString(15));
        localCustomerProfilesClass.setRegisterTime(localCursor.getString(16));
        localCustomerProfilesClass.setDetectTime(localCursor.getString(17));
        localArrayList.add(localCustomerProfilesClass);
      }
      while (localCursor.moveToNext());
    localCursor.close();
    localSQLiteDatabase.close();
    return localArrayList;
  }

  public PointDetectData getPointDataById(int paramInt)
  {
    SQLiteDatabase localSQLiteDatabase = getReadableDatabase();
    String[] arrayOfString1 = { "id", "customer_id", "check_time", "point_1", "point_2", "point_3", "point_4", "point_5", "point_6", "point_7", "point_8", "point_9", "point_10", "point_11", "point_12", "point_13", "point_14", "point_15", "point_16", "point_17", "point_18", "point_19", "point_20", "point_21", "point_22", "point_23", "point_24" };
    String[] arrayOfString2 = new String[1];
    arrayOfString2[0] = String.valueOf(paramInt);
    Cursor localCursor = localSQLiteDatabase.query("point_data", arrayOfString1, "id=?", arrayOfString2, null, null, null, null);
    if (localCursor != null)
    {
      localCursor.moveToFirst();
      PointDetectData localPointDetectData = new PointDetectData(Integer.parseInt(localCursor.getString(0)));
      localPointDetectData.setCustomerID(Integer.parseInt(localCursor.getString(1)));
      localPointDetectData.setDetectTime(localCursor.getString(2));
      for (int i = 0; ; i++)
      {
        if (i >= 24)
        {
          localCursor.close();
          localSQLiteDatabase.close();
          return localPointDetectData;
        }
        localPointDetectData.setPointValue(i, localCursor.getString(i + 3));
      }
    }
    return null;
  }

  public int getPointDataCount()
  {
    SQLiteDatabase localSQLiteDatabase = getReadableDatabase();
    Cursor localCursor = localSQLiteDatabase.rawQuery("SELECT  * FROM point_data", null);
    int i = localCursor.getCount();
    localCursor.close();
    localSQLiteDatabase.close();
    return i;
  }

  CustomerProfilesClass getProfileById(int paramInt)
  {
    SQLiteDatabase localSQLiteDatabase = getReadableDatabase();
    String[] arrayOfString1 = { "id", "name", "birthday", "native_place", "profession", "height", "weight", "systolic_pressure", "diastolic_pressure", "pulse", "sex", "marital_status", "medical_history_1", "medical_history_2", "medical_history_3", "memo", "register_time", "detect_time" };
    String[] arrayOfString2 = new String[1];
    arrayOfString2[0] = String.valueOf(paramInt);
    Cursor localCursor = localSQLiteDatabase.query("customer_profiles", arrayOfString1, "id=?", arrayOfString2, null, null, null, null);
    if (localCursor != null)
    {
      localCursor.moveToFirst();
      CustomerProfilesClass localCustomerProfilesClass = new CustomerProfilesClass(Integer.parseInt(localCursor.getString(0)), localCursor.getString(1));
      localCustomerProfilesClass.setBirthday(localCursor.getString(2));
      localCustomerProfilesClass.setNativePlace(localCursor.getString(3));
      localCustomerProfilesClass.setProfession(localCursor.getString(4));
      localCustomerProfilesClass.setHeight(localCursor.getString(5));
      localCustomerProfilesClass.setWeight(localCursor.getString(6));
      localCustomerProfilesClass.setSystolicPressure(localCursor.getString(7));
      localCustomerProfilesClass.setDiastolicPressure(localCursor.getString(8));
      localCustomerProfilesClass.setPulse(localCursor.getString(9));
      localCustomerProfilesClass.setSex(localCursor.getString(10));
      localCustomerProfilesClass.setMaritalStatus(localCursor.getString(11));
      localCustomerProfilesClass.setMedicalHistory1(localCursor.getString(12));
      localCustomerProfilesClass.setMedicalHistory2(localCursor.getString(13));
      localCustomerProfilesClass.setMedicalHistory3(localCursor.getString(14));
      localCustomerProfilesClass.setMemo(localCursor.getString(15));
      localCustomerProfilesClass.setRegisterTime(localCursor.getString(16));
      localCustomerProfilesClass.setDetectTime(localCursor.getString(17));
      localCursor.close();
      localSQLiteDatabase.close();
      return localCustomerProfilesClass;
    }
    return null;
  }

  public int getProfilesCount()
  {
    SQLiteDatabase localSQLiteDatabase = getReadableDatabase();
    Cursor localCursor = localSQLiteDatabase.rawQuery("SELECT  * FROM customer_profiles", null);
    int i = localCursor.getCount();
    localCursor.close();
    localSQLiteDatabase.close();
    return i;
  }

  public void onCreate(SQLiteDatabase paramSQLiteDatabase)
  {
    paramSQLiteDatabase.execSQL("CREATE TABLE customer_profiles(id INTEGER PRIMARY KEY,name TEXT,birthday TEXT,native_place TEXT,profession TEXT,height TEXT,weight TEXT,systolic_pressure TEXT,diastolic_pressure TEXT,pulse TEXT,sex TEXT,marital_status TEXT,medical_history_1 TEXT,medical_history_2 TEXT,medical_history_3 TEXT,memo TEXT,register_time TEXT,detect_time TEXT)");
    paramSQLiteDatabase.execSQL("CREATE TABLE point_data(id INTEGER PRIMARY KEY,customer_id INTEGER,check_time TEXT,point_1 TEXT,point_2 TEXT,point_3 TEXT,point_4 TEXT,point_5 TEXT,point_6 TEXT,point_7 TEXT,point_8 TEXT,point_9 TEXT,point_10 TEXT,point_11 TEXT,point_12 TEXT,point_13 TEXT,point_14 TEXT,point_15 TEXT,point_16 TEXT,point_17 TEXT,point_18 TEXT,point_19 TEXT,point_20 TEXT,point_21 TEXT,point_22 TEXT,point_23 TEXT,point_24 TEXT)");
  }

  public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
  {
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS customer_profiles");
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS point_data");
    onCreate(paramSQLiteDatabase);
  }

  public int updatePointData(PointDetectData paramPointDetectData)
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("customer_id", Integer.valueOf(paramPointDetectData.getCustomerID()));
    localContentValues.put("check_time", paramPointDetectData.getDetectTime());
    int i = 0 + 1;
    localContentValues.put("point_1", paramPointDetectData.getPointValue(0));
    int j = i + 1;
    localContentValues.put("point_2", paramPointDetectData.getPointValue(i));
    int k = j + 1;
    localContentValues.put("point_3", paramPointDetectData.getPointValue(j));
    int m = k + 1;
    localContentValues.put("point_4", paramPointDetectData.getPointValue(k));
    int n = m + 1;
    localContentValues.put("point_5", paramPointDetectData.getPointValue(m));
    int i1 = n + 1;
    localContentValues.put("point_6", paramPointDetectData.getPointValue(n));
    int i2 = i1 + 1;
    localContentValues.put("point_7", paramPointDetectData.getPointValue(i1));
    int i3 = i2 + 1;
    localContentValues.put("point_8", paramPointDetectData.getPointValue(i2));
    int i4 = i3 + 1;
    localContentValues.put("point_9", paramPointDetectData.getPointValue(i3));
    int i5 = i4 + 1;
    localContentValues.put("point_10", paramPointDetectData.getPointValue(i4));
    int i6 = i5 + 1;
    localContentValues.put("point_11", paramPointDetectData.getPointValue(i5));
    int i7 = i6 + 1;
    localContentValues.put("point_12", paramPointDetectData.getPointValue(i6));
    int i8 = i7 + 1;
    localContentValues.put("point_13", paramPointDetectData.getPointValue(i7));
    int i9 = i8 + 1;
    localContentValues.put("point_14", paramPointDetectData.getPointValue(i8));
    int i10 = i9 + 1;
    localContentValues.put("point_15", paramPointDetectData.getPointValue(i9));
    int i11 = i10 + 1;
    localContentValues.put("point_16", paramPointDetectData.getPointValue(i10));
    int i12 = i11 + 1;
    localContentValues.put("point_17", paramPointDetectData.getPointValue(i11));
    int i13 = i12 + 1;
    localContentValues.put("point_18", paramPointDetectData.getPointValue(i12));
    int i14 = i13 + 1;
    localContentValues.put("point_19", paramPointDetectData.getPointValue(i13));
    int i15 = i14 + 1;
    localContentValues.put("point_20", paramPointDetectData.getPointValue(i14));
    int i16 = i15 + 1;
    localContentValues.put("point_21", paramPointDetectData.getPointValue(i15));
    int i17 = i16 + 1;
    localContentValues.put("point_22", paramPointDetectData.getPointValue(i16));
    int i18 = i17 + 1;
    localContentValues.put("point_23", paramPointDetectData.getPointValue(i17));
//    (i18 + 1);
    localContentValues.put("point_24", paramPointDetectData.getPointValue(i18));
    String[] arrayOfString = new String[1];
    arrayOfString[0] = String.valueOf(paramPointDetectData.getID());
    int i19 = localSQLiteDatabase.update("point_data", localContentValues, "id = ?", arrayOfString);
    localSQLiteDatabase.close();
    return i19;
  }

  public int updateProfiles(CustomerProfilesClass paramCustomerProfilesClass)
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("name", paramCustomerProfilesClass.getName());
    localContentValues.put("birthday", paramCustomerProfilesClass.getBirthday());
    localContentValues.put("native_place", paramCustomerProfilesClass.getNativePlace());
    localContentValues.put("profession", paramCustomerProfilesClass.getProfession());
    localContentValues.put("height", paramCustomerProfilesClass.getHeight());
    localContentValues.put("weight", paramCustomerProfilesClass.getWeight());
    localContentValues.put("systolic_pressure", paramCustomerProfilesClass.getSystolicPressure());
    localContentValues.put("diastolic_pressure", paramCustomerProfilesClass.getDiastolicPressure());
    localContentValues.put("pulse", paramCustomerProfilesClass.getPulse());
    localContentValues.put("sex", paramCustomerProfilesClass.getSex());
    localContentValues.put("marital_status", paramCustomerProfilesClass.getMaritalStatus());
    localContentValues.put("medical_history_1", paramCustomerProfilesClass.getMedicalHistory1());
    localContentValues.put("medical_history_2", paramCustomerProfilesClass.getMedicalHistory2());
    localContentValues.put("medical_history_3", paramCustomerProfilesClass.getMedicalHistory3());
    localContentValues.put("memo", paramCustomerProfilesClass.getMemo());
    localContentValues.put("register_time", paramCustomerProfilesClass.getRegisterTime());
    localContentValues.put("detect_time", paramCustomerProfilesClass.getDetectTime());
    String[] arrayOfString = new String[1];
    arrayOfString[0] = String.valueOf(paramCustomerProfilesClass.getID());
    int i = localSQLiteDatabase.update("customer_profiles", localContentValues, "id = ?", arrayOfString);
    localSQLiteDatabase.close();
    return i;
  }
}

/* Location:           D:\android studio\fanbianyi\classes-dex2jar (2).jar
 * Qualified Name:     com.tds.test.MyDatabaseHandler
 * JD-Core Version:    0.6.2
 */