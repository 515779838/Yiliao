package com.yy.kaitian.yl;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class ProfileDetailActivity extends PreferenceActivity
{
  private static final boolean D = true;
  private static final String TAG = "ProfileDetailActivity";
  private int mId = -1;

  private boolean delProfileById(int paramInt)
  {
    MyDatabaseHandler localMyDatabaseHandler = new MyDatabaseHandler(this);
    CustomerProfilesClass localCustomerProfilesClass = localMyDatabaseHandler.getProfileById(paramInt);
    if (localCustomerProfilesClass != null)
    {
      localMyDatabaseHandler.deletePrifiles(localCustomerProfilesClass);
      Toast.makeText(this, "删除成功!", Toast.LENGTH_SHORT).show();
      return true;
    }
    Toast.makeText(this, "删除失败!", Toast.LENGTH_SHORT).show();
    return false;
  }

  protected void InitProfileDetailList()
  {
    CustomerProfilesClass localCustomerProfilesClass = new MyDatabaseHandler(this).getProfileById(this.mId);
    if (localCustomerProfilesClass != null)
    {
      findPreference("profile_detail_name").setSummary(localCustomerProfilesClass.getName());
      findPreference("profile_detail_birthday").setSummary(localCustomerProfilesClass.getBirthday());
      findPreference("profile_detail_native_place").setSummary(localCustomerProfilesClass.getNativePlace());
      findPreference("profile_detail_profession").setSummary(localCustomerProfilesClass.getProfession());
      findPreference("profile_detail_height").setSummary(localCustomerProfilesClass.getHeight());
      findPreference("profile_detail_weight").setSummary(localCustomerProfilesClass.getWeight());
      findPreference("profile_detail_systolic_pressure").setSummary(localCustomerProfilesClass.getSystolicPressure());
      findPreference("profile_detail_diastolic_pressure").setSummary(localCustomerProfilesClass.getDiastolicPressure());
      findPreference("profile_detail_pulse").setSummary(localCustomerProfilesClass.getPulse());
      findPreference("profile_detail_sex").setSummary(localCustomerProfilesClass.getSex());
      findPreference("profile_detail_marital_status").setSummary(localCustomerProfilesClass.getMaritalStatus());
      findPreference("profile_detail_medical_history_1").setSummary(localCustomerProfilesClass.getMedicalHistory1());
      findPreference("profile_detail_medical_history_2").setSummary(localCustomerProfilesClass.getMedicalHistory2());
      findPreference("profile_detail_medical_history_3").setSummary(localCustomerProfilesClass.getMedicalHistory3());
      findPreference("profile_detail_memo").setSummary(localCustomerProfilesClass.getMemo());
      findPreference("profile_detail_register_time").setSummary(localCustomerProfilesClass.getRegisterTime());
      findPreference("profile_detail_detect_time").setSummary(localCustomerProfilesClass.getDetectTime());
    }
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(R.xml.profile_detail);
    new Bundle();
    this.mId = Integer.parseInt(getIntent().getExtras().getString("customer_profile_id"));
    InitProfileDetailList();
  }

  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    getMenuInflater().inflate(R.menu.profile_detail_menu, paramMenu);
    return true;
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default:
      return false;
    case R.id.profiles_detail_modify:
      Bundle localBundle = new Bundle();
      localBundle.putString("customer_profile_edit_type", "Modify");
      localBundle.putString("customer_profile_id", this.mId+"");
      Intent localIntent = new Intent(this, CustomerProfilesEditActivity.class);
      localIntent.putExtras(localBundle);
      startActivity(localIntent);
      finish();
      return true;
    case R.id.profiles_detail_delete:
    }
    Builder localBuilder = new Builder(this);
    localBuilder.setMessage("确定要删除?").setCancelable(false).setPositiveButton("确定", new OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        if (ProfileDetailActivity.this.delProfileById(ProfileDetailActivity.this.mId))
          ProfileDetailActivity.this.finish();
      }
    }).setNegativeButton("取消", new OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        paramAnonymousDialogInterface.cancel();
      }
    });
    localBuilder.create().show();
    return true;
  }
}

/* Location:           D:\android studio\fanbianyi\classes-dex2jar (2).jar
 * Qualified Name:     com.tds.test.ProfileDetailActivity
 * JD-Core Version:    0.6.2
 */