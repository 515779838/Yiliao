package com.yy.kaitian.yiliao681;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class CustomerListPreferenceActivity extends PreferenceActivity {
    private static final boolean D = true;
    private static final String KEY_CUSTOMER_LIST = "list_customer_key";
    private static final String TAG = "CustomerListPreferenceActivity";
    CheckBoxPreference curSelect = null;
    private PreferenceGroup mCustomerList;
    private Hashtable<CheckBoxPreference, stCustomerList> mGroupTable;

    private void DelSelect() {
        if (this.curSelect == null)
            return;
        new MyDatabaseHandler(this).deletePointData(((stCustomerList) this.mGroupTable.get(this.curSelect)).mID);
        clearPreferenceList();
        initGroupsPreferences();
        myPrompt("删除成功！");
    }

    private void StartDetectSelect() {
        if (this.curSelect == null)
            return;
        stCustomerList localstCustomerList = (stCustomerList) this.mGroupTable.get(this.curSelect);
        Bundle localBundle = new Bundle();
        Intent localIntent = new Intent();
        String str1 = localstCustomerList.mName;
        String str2 = localstCustomerList.mID + "";
        localBundle.putString("customer_type", localstCustomerList.mType + "");
        localBundle.putString("customer_name", str1);
        localBundle.putString("customer_id", str2);
        localIntent.putExtras(localBundle);
        setResult(-1, localIntent);
        finish();
    }

    private void clearPreferenceList() {
        Iterator localIterator = this.mGroupTable.keySet().iterator();
        while (true) {
            if (!localIterator.hasNext()) {
                this.mGroupTable.clear();
                return;
            }
            Preference localPreference = (Preference) localIterator.next();
            this.mCustomerList.removePreference(localPreference);
        }
    }

    private void initGroupsPreferences() {
        getPreferenceScreen().setEnabled(true);
        MyDatabaseHandler localMyDatabaseHandler = new MyDatabaseHandler(this);
        if (localMyDatabaseHandler.getProfilesCount() <= 0) {
            Bundle localBundle = new Bundle();
            Intent localIntent = new Intent();
            localBundle.putString("customer_select", "ERR");
            localIntent.putExtras(localBundle);
            finish();
        }
        List<PointDetectData> localList2;
        int k = 0;
//        while (true) {
        // TODO: 2017/11/11
//            return;
        if (localMyDatabaseHandler.getPointDataCount() > 0) {
            localList2 = localMyDatabaseHandler.getAllPointDatas();
            while (true) {
                PointDetectData localPointDetectData = localList2.get(k);
//                if (localPointDetectData.isPointDetectFinish()){
                    String str2 = localMyDatabaseHandler.getProfileById(localPointDetectData.getCustomerID()).getName();
                    stCustomerList localstCustomerList2 = new stCustomerList(localPointDetectData.getID(), str2, 1);
                    CheckBoxPreference localCheckBoxPreference2 = new CheckBoxPreference(this, null);
                    localCheckBoxPreference2.setTitle(str2);
                    localCheckBoxPreference2.setSummary("未检穴位数：" + localPointDetectData.getUnfinishedPointNum() + "\n检测时间：" + localPointDetectData.getDetectTime());
                    localCheckBoxPreference2.setPersistent(false);
                    localCheckBoxPreference2.setChecked(false);
                    this.mCustomerList.addPreference(localCheckBoxPreference2);
                    this.mGroupTable.put(localCheckBoxPreference2, localstCustomerList2);
//                }
                k++;
                if (k >= localList2.size()){
                    break;
                }
            }
        }
        List<CustomerProfilesClass> localList1 = localMyDatabaseHandler.getAllProfiles();
        for (int i = 0; i < localList1.size(); i++) {
            CustomerProfilesClass localCustomerProfilesClass =  localList1.get(i);
            int j = localCustomerProfilesClass.getID();
            String str1 = localCustomerProfilesClass.getName();
            stCustomerList localstCustomerList1 = new stCustomerList(j, str1, 0);
            CheckBoxPreference localCheckBoxPreference1 = new CheckBoxPreference(this, null);
            localCheckBoxPreference1.setTitle(str1);
            localCheckBoxPreference1.setPersistent(false);
            localCheckBoxPreference1.setChecked(false);
            this.mCustomerList.addPreference(localCheckBoxPreference1);
            this.mGroupTable.put(localCheckBoxPreference1, localstCustomerList1);
        }

//        }
//        PointDetectData localPointDetectData = (PointDetectData) localList2.get(k);
//        if (localPointDetectData.isPointDetectFinish()) ;
//        while (true) {
//            k++;
//            // TODO: 2017/11/11
//            String str2 = localMyDatabaseHandler.getProfileById(localPointDetectData.getCustomerID()).getName();
//            stCustomerList localstCustomerList2 = new stCustomerList(localPointDetectData.getID(), str2, 1);
//            CheckBoxPreference localCheckBoxPreference2 = new CheckBoxPreference(this, null);
//            localCheckBoxPreference2.setTitle(str2);
//            localCheckBoxPreference2.setSummary("未检穴位数：" + localPointDetectData.getUnfinishedPointNum() + "\n检测时间：" + localPointDetectData.getDetectTime());
//            localCheckBoxPreference2.setPersistent(false);
//            localCheckBoxPreference2.setChecked(false);
//            this.mCustomerList.addPreference(localCheckBoxPreference2);
//            this.mGroupTable.put(localCheckBoxPreference2, localstCustomerList2);
//        }
    }

    private void myPrompt(String paramString) {
        Toast.makeText(this, paramString, Toast.LENGTH_SHORT).show();
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        addPreferencesFromResource(R.xml.customer_list);
        PreferenceScreen localPreferenceScreen = getPreferenceScreen();
        this.mGroupTable = new Hashtable();
        this.mCustomerList = ((PreferenceGroup) localPreferenceScreen.findPreference("list_customer_key"));
        initGroupsPreferences();
        setResult(0);
    }

    public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference) {
        CheckBoxPreference localCheckBoxPreference = (CheckBoxPreference) paramPreference;
        if (localCheckBoxPreference.isChecked()) {
            this.curSelect = localCheckBoxPreference;
//            if (localCheckBoxPreference.getSummary() == null);
//            return true;
//                StartDetectSelect();
//            return true;
        }
        Builder localBuilder = new Builder(this);
        localBuilder.setMessage("请选择：").setPositiveButton("继续检测", new OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                paramAnonymousDialogInterface.cancel();
                CustomerListPreferenceActivity.this.StartDetectSelect();
            }
        }).setNegativeButton("删除", new OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                paramAnonymousDialogInterface.cancel();
                CustomerListPreferenceActivity.this.DelSelect();
            }
        }).setNeutralButton("取消", new OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                CustomerListPreferenceActivity.this.curSelect.setChecked(false);
                paramAnonymousDialogInterface.cancel();
            }
        });
        AlertDialog dialog = localBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return true;
    }

    public class stCustomerList {
        public int mID;
        public String mName;
        public int mType;

        public stCustomerList(int paramString, String paramInt1, int arg4) {
            this.mID = paramString;
            this.mName = paramInt1;
//            int i;
            this.mType = arg4;
        }
    }
}

/* Location:           D:\android studio\fanbianyi\classes-dex2jar (2).jar
 * Qualified Name:     com.tds.test.CustomerListPreferenceActivity
 * JD-Core Version:    0.6.2
 */