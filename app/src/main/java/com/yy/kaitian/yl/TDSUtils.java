package com.yy.kaitian.yl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.regex.Pattern;

public class TDSUtils
{
  public static String bytesToHexString(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null)
      return null;
    StringBuilder localStringBuilder = new StringBuilder(2 * paramArrayOfByte.length);
    for (int i = 0; ; i++)
    {
      if (i >= paramArrayOfByte.length)
        return localStringBuilder.toString();
      localStringBuilder.append("0123456789abcdef".charAt(0xF & paramArrayOfByte[i] >> 4));
      localStringBuilder.append("0123456789abcdef".charAt(0xF & paramArrayOfByte[i]));
    }
  }

  static String getCurrentTimeString()
  {
    Calendar localCalendar = Calendar.getInstance();
    return localCalendar.get(Calendar.YEAR) + "-" + (1 + localCalendar.get(Calendar.MONTH)) + "-" + localCalendar.get(Calendar.DAY_OF_MONTH) + " " + localCalendar.get(Calendar.HOUR_OF_DAY) + ":" + localCalendar.get(Calendar.MINUTE) + ":" + localCalendar.get(Calendar.SECOND);
  }

  static int hexCharToInt(char paramChar)
  {
    if ((paramChar >= '0') && (paramChar <= '9'))
      return paramChar - '0';
    if ((paramChar >= 'A') && (paramChar <= 'F'))
      return 10 + (paramChar - 'A');
    if ((paramChar >= 'a') && (paramChar <= 'f'))
      return 10 + (paramChar - 'a');
    throw new RuntimeException("invalid hex char '" + paramChar + "'");
  }

  public static byte[] hexStringToBytes(String paramString)
  {
    byte[] arrayOfByte = null;
    if (paramString == null);
    while (true)
    {
      int i = paramString.length() % 2;
      arrayOfByte = null;
      if (i == 0)
      {
        int j = paramString.length();
        arrayOfByte = new byte[j / 2];
        for (int k = 0; k < j; k += 2)
          arrayOfByte[(k / 2)] = ((byte)(hexCharToInt(paramString.charAt(k)) << 4 | hexCharToInt(paramString.charAt(k + 1))));
      }
      return arrayOfByte;
    }
  }

  static boolean isDigit(String paramString)
  {
    return Pattern.compile("[0-9]*").matcher(paramString).matches();
  }

  static boolean isLetterDigit(String paramString)
  {
    return Pattern.compile("[A-Za-z0-9]*").matcher(paramString).matches();
  }

  static boolean isStringRegionMatches(String paramString1, String paramString2)
  {
    int i = paramString1.length();
    int j = paramString2.length();
    for (int k = 0; ; k++)
    {
      if (k > i - j)
        return false;
      if (paramString1.regionMatches(k, paramString2, 0, j))
        return true;
    }
  }

  static String md5(String paramString)
  {
    try
    {
      MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
      localMessageDigest.reset();
      localMessageDigest.update(paramString.getBytes());
      byte[] arrayOfByte = localMessageDigest.digest();
      int i = arrayOfByte.length;
      StringBuilder localStringBuilder = new StringBuilder(i << 1);
      for (int j = 0; ; j++)
      {
        if (j >= i)
          return localStringBuilder.toString();
        localStringBuilder.append(Character.forDigit((0xF0 & arrayOfByte[j]) >> 4, 16));
        localStringBuilder.append(Character.forDigit(0xF & arrayOfByte[j], 16));
      }
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      localNoSuchAlgorithmException.printStackTrace();
    }
    return null;
  }
}

/* Location:           D:\android studio\fanbianyi\classes-dex2jar (2).jar
 * Qualified Name:     com.tds.test.TDSUtils
 * JD-Core Version:    0.6.2
 */