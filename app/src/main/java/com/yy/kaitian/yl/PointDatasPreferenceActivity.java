package com.yy.kaitian.yl;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class PointDatasPreferenceActivity extends PreferenceActivity {
    private static final boolean D = true;
    private static final String KEY_POINT_DATAS_LIST = "list_point_datas_key";
    private static final String TAG = "PointDatasPreferenceActivity";
    private Hashtable<CheckBoxPreference, stPointDataList> mGroupTable;
    private PreferenceGroup mPointDataList;
    private boolean m_bHaveItem = false;
    private CheckBoxPreference selected;

    private void DeleteIntelege() {
        Builder localBuilder = new Builder(this);
        localBuilder.setMessage("确定要删除用户记录?").setCancelable(false).setPositiveButton("Yes", new OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                PointDatasPreferenceActivity.this.delUserPointData();
            }
        }).setNegativeButton("No", new OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                paramAnonymousDialogInterface.cancel();
                PointDatasPreferenceActivity.this.selected.setChecked(false);
            }
        });
        localBuilder.create().show();
    }

    private stPointDataList GetCheckPointDatas() {
        Enumeration localEnumeration = this.mGroupTable.keys();
        CheckBoxPreference localCheckBoxPreference;
        do {
            if (!localEnumeration.hasMoreElements())
                return null;
            localCheckBoxPreference = (CheckBoxPreference) localEnumeration.nextElement();
        }
        while (!localCheckBoxPreference.isChecked());
        stPointDataList localstPointDataList = (stPointDataList) this.mGroupTable.get(localCheckBoxPreference);
        Log.i("PointDatasPreferenceActivity", " " + localstPointDataList.mID + " " + localstPointDataList.mCustomerID);
        return localstPointDataList;
    }

    private void Intelligence() {
        stPointDataList localstPointDataList = GetCheckPointDatas();
        if (localstPointDataList != null) {
            int i = localstPointDataList.mID;
            PointDetectData localPointDetectData = new MyDatabaseHandler(this).getPointDataById(i);
            String str = "";
            if (localPointDetectData != null) {
                str = localPointDetectData.getDetectTime() + " " + localPointDetectData.getCustomerID();
                for (int j = 0; ; j++) {
                    if (j >= 24) {
                        Log.i("PointDatasPreferenceActivity", str);
                        Bundle localBundle = new Bundle();
                        Intent localIntent = new Intent();
                        localBundle.putString("point_datas_id", localPointDetectData.getID() + "");
                        localIntent.putExtras(localBundle);
                        setResult(-1, localIntent);
                        finish();
                        return;
                    }
                    str = str + " " + localPointDetectData.getPointValue(j);
                }
            }
        }
        myPrompt("请选择要解读的数据！");
    }

    private void clearPreferenceList() {
        Iterator localIterator = this.mGroupTable.keySet().iterator();
        while (true) {
            if (!localIterator.hasNext()) {
                this.mGroupTable.clear();
                return;
            }
            Preference localPreference = (Preference) localIterator.next();
            this.mPointDataList.removePreference(localPreference);
        }
    }

    private void delUserPointData() {
        stPointDataList localstPointDataList = GetCheckPointDatas();
        if (localstPointDataList != null) {
            int i = localstPointDataList.mID;
            new MyDatabaseHandler(this).deletePointData(i);
            clearPreferenceList();
            initGroupsPreferences();
            myPrompt("删除成功！");
            return;
        }
        myPrompt("请选择删除对象！");
    }

    private void initGroupsPreferences() {
        getPreferenceScreen().setEnabled(true);
        MyDatabaseHandler localMyDatabaseHandler = new MyDatabaseHandler(this);
        if (localMyDatabaseHandler.getPointDataCount() == 0)
            return;
        PointDetectData localPointDetectData = null;
        int j = 0;
        List<PointDetectData> localList = localMyDatabaseHandler.getAllPointDatas();
        for (int i = 0; i < localList.size(); i++) {
            localPointDetectData = localList.get(i);
            j = localPointDetectData.getID();
            if (!localPointDetectData.isPointDetectFinish()) {
                localList.remove(i);
            } else {
                int k = localPointDetectData.getCustomerID();
                CustomerProfilesClass localCustomerProfilesClass = localMyDatabaseHandler.getProfileById(k);
                if (localCustomerProfilesClass != null){
                    for (String str = localCustomerProfilesClass.getName(); ; str = "UnKnow") {
                        stPointDataList localstPointDataList = new stPointDataList(j, k, false);
                        CheckBoxPreference localCheckBoxPreference = new CheckBoxPreference(this, null);
                        localCheckBoxPreference.setTitle(str);
                        localCheckBoxPreference.setSummary("检测时间: " + localPointDetectData.getDetectTime());
                        localCheckBoxPreference.setPersistent(false);
                        localCheckBoxPreference.setChecked(localstPointDataList.mbSet);
                        this.mPointDataList.addPreference(localCheckBoxPreference);
                        this.mGroupTable.put(localCheckBoxPreference, localstPointDataList);
                        break;
                    }
                }
            }
        }

//        for (int i = localList.size()-1; i > 0; i--) {
//            int k = localList.get(i).getCustomerID();
//            CustomerProfilesClass localCustomerProfilesClass = localMyDatabaseHandler.getProfileById(k);
//            if (localCustomerProfilesClass != null){
//                for (String str = localCustomerProfilesClass.getName(); ; str = "UnKnow") {
//                    stPointDataList localstPointDataList = new stPointDataList(j, k, false);
//                    CheckBoxPreference localCheckBoxPreference = new CheckBoxPreference(this, null);
//                    localCheckBoxPreference.setTitle(str);
//                    localCheckBoxPreference.setSummary("检测时间: " + localList.get(i).getDetectTime());
//                    localCheckBoxPreference.setPersistent(false);
//                    localCheckBoxPreference.setChecked(localstPointDataList.mbSet);
//                    this.mPointDataList.addPreference(localCheckBoxPreference);
//                    this.mGroupTable.put(localCheckBoxPreference, localstPointDataList);
//                    break;
//                }
//            }
//        }



    }

    private void myPrompt(String paramString) {
        Toast.makeText(this, paramString, Toast.LENGTH_SHORT).show();
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        addPreferencesFromResource(R.xml.point_datas_list);
        PreferenceScreen localPreferenceScreen = getPreferenceScreen();
        this.mGroupTable = new Hashtable();
        this.mPointDataList = ((PreferenceGroup) localPreferenceScreen.findPreference("list_point_datas_key"));
        initGroupsPreferences();
        setResult(0);
    }

    public boolean onCreateOptionsMenu(Menu paramMenu) {
        getMenuInflater().inflate(R.menu.point_datas_list_menu, paramMenu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
        boolean bool = true;
        switch (paramMenuItem.getItemId()) {
            default:
                bool = false;
            case R.id.point_datas_list_reading:
                PointDetectData localPointDetectData = null;
                do {
//        return bool;
                    stPointDataList localstPointDataList2 = GetCheckPointDatas();
                    if (localstPointDataList2 == null)
                        break;
                    int j = localstPointDataList2.mID;
                    localPointDetectData = new MyDatabaseHandler(this).getPointDataById(j);
                }
                while (localPointDetectData == null);
                String str = localPointDetectData.getDetectTime() + " " + localPointDetectData.getCustomerID();
                for (int k = 0; ; k++) {
                    if (k >= 24) {
                        Log.i("PointDatasPreferenceActivity", str);
                        Bundle localBundle = new Bundle();
                        Intent localIntent = new Intent();
                        localBundle.putString("point_datas_id", localPointDetectData.getID() + "");
                        localIntent.putExtras(localBundle);
                        setResult(-1, localIntent);
                        finish();
                        return bool;
                    }
                    str = str + " " + localPointDetectData.getPointValue(k);
                }
//      myPrompt("请选择要解读的数据！");
//      return bool;
            case R.id.point_datas_list_del:
//    }
//    stPointDataList localstPointDataList1 = GetCheckPointDatas();
//    if (localstPointDataList1 != null)
//    {
//      int i = localstPointDataList1.mID;
//      new MyDatabaseHandler(this).deletePointData(i);
//      clearPreferenceList();
//      initGroupsPreferences();
//      myPrompt("删除成功！");
//      return bool;
//    }
//    myPrompt("请选择删除对象！");
                break;
        }
        return bool;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference) {
        this.selected = ((CheckBoxPreference) paramPreference);
        if (this.selected.isChecked()) {
            Builder localBuilder = new Builder(this);
            localBuilder.setMessage("请选择：").setPositiveButton("解读", new OnClickListener() {
                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                    paramAnonymousDialogInterface.cancel();
                    PointDatasPreferenceActivity.this.Intelligence();
                }
            }).setNegativeButton("删除", new OnClickListener() {
                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                    paramAnonymousDialogInterface.cancel();
                    PointDatasPreferenceActivity.this.DeleteIntelege();
                }
            }).setNeutralButton("取消", new OnClickListener() {
                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                    PointDatasPreferenceActivity.this.selected.setChecked(false);
                    paramAnonymousDialogInterface.cancel();
                    PointDatasPreferenceActivity.this.finish();
                }
            });
            localBuilder.create().show();
        }
        return true;
    }

    public class stPointDataList {
        public int mCustomerID;
        public int mID;
        public boolean mbSet;

        public stPointDataList(int paramInt1, int paramBoolean, boolean arg4) {
//      boolean bool;
            this.mbSet = arg4;
            this.mID = paramInt1;
            this.mCustomerID = paramBoolean;
        }
    }
}

/* Location:           D:\android studio\fanbianyi\classes-dex2jar (2).jar
 * Qualified Name:     com.tds.test.PointDatasPreferenceActivity
 * JD-Core Version:    0.6.2
 */