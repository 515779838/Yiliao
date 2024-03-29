package com.yy.kaitian.yl;

import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ProfilesListActivity extends ListActivity {
    private static final boolean D = true;
    private static final String TAG = "ListActivity";
    private String[] mNameString = null;

    private Hashtable<String, int[]> mProfileTable;
    private boolean mbEmpty;

    private ProfilesArrayAdapter adapter;

    private LinearLayout ll_add, ll_back;

    private void AddCustomerProfile() {
        Bundle localBundle = new Bundle();
        localBundle.putString("customer_profile_edit_type", "NewAdd");
        Intent localIntent = new Intent(this, CustomerProfilesEditActivity.class);
        localIntent.putExtras(localBundle);
        startActivity(localIntent);
    }

    private void CancelAddCustomerProfile() {
        Builder localBuilder = new Builder(this);
        localBuilder.setMessage("确定要退出?").setCancelable(false).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                ProfilesListActivity.this.finish();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                paramAnonymousDialogInterface.cancel();
            }
        });
        localBuilder.create().show();
    }

    private void clearProfileTableList() {
        this.mProfileTable.clear();
//        this.mNameString = new String[1];
//        this.mNameString[0] = "(null)";
        this.mbEmpty = true;
    }

    protected void getProfilesNameList() {
        MyDatabaseHandler localMyDatabaseHandler = new MyDatabaseHandler(this);
        clearProfileTableList();
        int i = localMyDatabaseHandler.getProfilesCount();
        if (i <= 0)
            return;
        this.mNameString = new String[i];
        Log.d("Reading: ", "Reading all contacts..");
        List localList = localMyDatabaseHandler.getAllProfiles();
        int j = 0;
        Iterator localIterator = localList.iterator();
        while (true) {
            if (!localIterator.hasNext()) {
                this.mbEmpty = false;
                return;
            }
            CustomerProfilesClass localCustomerProfilesClass = (CustomerProfilesClass) localIterator.next();
            this.mNameString[j] = localCustomerProfilesClass.getName();
            int[] arrayOfInt = new int[1];
            arrayOfInt[0] = localCustomerProfilesClass.getID();
            this.mProfileTable.put(this.mNameString[j], arrayOfInt);
            j++;
            Log.d("zj Name: ", "Id: " + localCustomerProfilesClass.getID() + " ,Name: " + localCustomerProfilesClass.getName() + " ,Phone: " + localCustomerProfilesClass.getRegisterTime());
        }
    }

    public void onCreate(Bundle paramBundle) {
//    setTheme(R.style.CustomTitleBarTheme);
        super.onCreate(paramBundle);
//    requestWindowFeature(7);
        setContentView(R.layout.profile_list_main);
        getWindow().setFeatureInt(7, R.layout.custom_title_device_manage);
        this.mProfileTable = new Hashtable();
        this.mbEmpty = true;
//    Button localButton1 = (Button)findViewById(R.id.connect_device);
//    localButton1.setText(R.string.new_add);
//    localButton1.setOnClickListener(new View.OnClickListener()
//    {
//      public void onClick(View paramAnonymousView)
//      {
//        ProfilesListActivity.this.AddCustomerProfile();
//      }
//    });
//    Button localButton2 = (Button)findViewById(R.id.disconnect_device);
//    localButton2.setText(" 返回 ");
//    localButton2.setOnClickListener(new View.OnClickListener()
//    {
//      public void onClick(View paramAnonymousView)
//      {
//        ProfilesListActivity.this.finish();
//      }
//    });
        ll_add = (LinearLayout) findViewById(R.id.ll_add);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);

        ll_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle localBundle = new Bundle();
                localBundle.putString("customer_profile_edit_type", "NewAdd");
                Intent localIntent = new Intent(ProfilesListActivity.this, CustomerProfilesEditActivity.class);
                localIntent.putExtras(localBundle);
                startActivity(localIntent);
            }
        });

        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public boolean onCreateOptionsMenu(Menu paramMenu) {
        return false;
    }

    protected void onDestroy() {
        super.onDestroy();
    }

//    protected void onListItemClick(ListView paramListView, View paramView, int paramInt, long paramLong) {
//        Log.e("zj", "111111");
//        String str = (String) getListAdapter().getItem(paramInt);
//        int[] arrayOfInt = (int[]) this.mProfileTable.get(str);
//        if (this.mbEmpty) {
//            Toast.makeText(this, "数据库中没有记录！", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        Bundle localBundle = new Bundle();
//        localBundle.putString("customer_profile_edit_type", "Modify");
//        localBundle.putString("customer_profile_id", arrayOfInt[0] + "");
//        Intent localIntent = new Intent(this, CustomerProfilesEditActivity.class);
//        localIntent.putExtras(localBundle);
//        startActivity(localIntent);
//        finish();
//    }

    public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
        switch (paramMenuItem.getItemId()) {
            default:
                return false;
            case R.id.new_profiles_add:
                Bundle localBundle = new Bundle();
                localBundle.putString("customer_profile_edit_type", "NewAdd");
                Intent localIntent = new Intent(this, CustomerProfilesEditActivity.class);
                localIntent.putExtras(localBundle);
                startActivity(localIntent);
                return true;
            case R.id.new_profiles_cancel:
        }
        Builder localBuilder = new Builder(this);
        localBuilder.setMessage("确定要退出?").setCancelable(false).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                ProfilesListActivity.this.finish();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                paramAnonymousDialogInterface.cancel();
            }
        });
        localBuilder.create().show();
        return true;
    }

    protected void onPause() {
        super.onPause();
        Log.e("ListActivity", "++ ON PAUSE ++");
    }

    protected void onResume() {
        super.onResume();
        Log.e("ListActivity", "++ ON RESUME ++");
    }

    public void onStart() {
        super.onStart();
        Log.e("ListActivity", "++ ON START ++");
        getProfilesNameList();
        Log.e("zj", "1111 = " + mNameString);
        if (this.mNameString == null) {
            return;
        }
        adapter = new ProfilesArrayAdapter(this, this.mNameString);
        setListAdapter(adapter);

        adapter.setOnStartCheckClick(new ProfilesArrayAdapter.OnStartCheckClick() {
            @Override
            public void setOnStartCheckClick(int position) {
                String name = mNameString[position];
                String id = "";
                for (Map.Entry<String, int[]> entry : mProfileTable.entrySet()) {
                    if (entry.getKey().equals(name)) {
                        id = "" + entry.getValue()[0];
                        break;
                    }
                }
                Bundle localBundle = new Bundle();
                Intent localIntent = new Intent();
                localBundle.putString("customer_type", "0");
                localBundle.putString("customer_name", name);
                localBundle.putString("customer_id", id);
                localIntent.putExtras(localBundle);
                setResult(-1, localIntent);
                finish();
            }
        });

        adapter.setonEditClick(new ProfilesArrayAdapter.OnEditClick() {
            @Override
            public void setonEditClick(int position) {
                Log.e("zj", "setonEditClick");

                Log.e("zj", "111111");
                String str = (String) getListAdapter().getItem(position);
                int[] arrayOfInt = (int[]) ProfilesListActivity.this.mProfileTable.get(str);
                if (ProfilesListActivity.this.mbEmpty) {
                    Toast.makeText(ProfilesListActivity.this, "数据库中没有记录！", Toast.LENGTH_SHORT).show();
                    return;
                }
                Bundle localBundle = new Bundle();
                localBundle.putString("customer_profile_edit_type", "Modify");
                localBundle.putString("customer_profile_id", arrayOfInt[0] + "");
                Intent localIntent = new Intent(ProfilesListActivity.this, CustomerProfilesEditActivity.class);
                localIntent.putExtras(localBundle);
                startActivity(localIntent);
                finish();
            }
        });
    }

    public void onStop() {
        super.onStop();
        Log.e("ListActivity", "-- ON STOP --");
    }
}

/* Location:           D:\android studio\fanbianyi\classes-dex2jar (2).jar
 * Qualified Name:     com.tds.test.ProfilesListActivity
 * JD-Core Version:    0.6.2
 */