package com.yy.kaitian.yl;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CustomerListPreference2Activity extends AppCompatActivity {

    private LinearLayout ll_back;
    private ListView listView;
    private CustomerListPreference2Adapter adapter;
    private List<Map<String, String>> mapList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list_preference2);
        listView = (ListView) findViewById(R.id.listView);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initGroupsPreferences();
    }

    private void initGroupsPreferences() {
        mapList.clear();
        MyDatabaseHandler localMyDatabaseHandler = new MyDatabaseHandler(this);
        if (localMyDatabaseHandler.getProfilesCount() <= 0) {
            Bundle localBundle = new Bundle();
            Intent localIntent = new Intent();
            localBundle.putString("customer_select", "ERR");
            localIntent.putExtras(localBundle);
            Toast.makeText(this, "请先新增用户！", Toast.LENGTH_SHORT).show();
            finish();
        }
        List<PointDetectData> localList2;
        int k = 0;
        if (localMyDatabaseHandler.getPointDataCount() > 0) {
            localList2 = localMyDatabaseHandler.getAllPointDatas();
            while (true) {
                PointDetectData localPointDetectData = localList2.get(k);
                Map<String, String> map = new HashMap<>();
                map.put("id", "" + localPointDetectData.getID());
                map.put("name", localMyDatabaseHandler.getProfileById(localPointDetectData.getCustomerID()).getName());
                map.put("type", "1");
                map.put("summary", "未检穴位数：" + localPointDetectData.getUnfinishedPointNum() + "\n检测时间：" + localPointDetectData.getDetectTime());
                mapList.add(map);
                k++;
                if (k >= localList2.size()) {
                    break;
                }
            }
        }
        List<CustomerProfilesClass> localList1 = localMyDatabaseHandler.getAllProfiles();
        for (int i = 0; i < localList1.size(); i++) {
            CustomerProfilesClass localCustomerProfilesClass = localList1.get(i);
            Map<String, String> map = new HashMap<>();
            map.put("id", "" + localCustomerProfilesClass.getID());
            map.put("name", localCustomerProfilesClass.getName());
            map.put("type", "0");
            map.put("summary", "");
            mapList.add(map);
        }
        if (adapter == null) {
            adapter = new CustomerListPreference2Adapter(this, mapList);
            adapter.setonEditClick(new CustomerListPreference2Adapter.OnEditClick() {
                @Override
                public void setonEditClick(final int position) {
                    AlertDialog.Builder localBuilder = new AlertDialog.Builder(CustomerListPreference2Activity.this);
                    localBuilder.setMessage("确定要删除用户记录?").setCancelable(false).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                            paramAnonymousDialogInterface.cancel();
                            new MyDatabaseHandler(CustomerListPreference2Activity.this).deletePointData(Integer.valueOf(mapList.get(position).get("id")));
                            initGroupsPreferences();
                            Toast.makeText(CustomerListPreference2Activity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                            paramAnonymousDialogInterface.cancel();
                        }
                    });
                    localBuilder.create().show();
                }
            });
            adapter.setOnStartCheckClick(new CustomerListPreference2Adapter.OnStartCheckClick() {
                @Override
                public void setOnStartCheckClick(int position) {
                    Bundle localBundle = new Bundle();
                    Intent localIntent = new Intent();
                    localBundle.putString("customer_type", mapList.get(position).get("type"));
                    localBundle.putString("customer_name", mapList.get(position).get("name"));
                    localBundle.putString("customer_id", mapList.get(position).get("id"));
                    localIntent.putExtras(localBundle);
                    setResult(-1, localIntent);
                    finish();
                }
            });
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

    }

}
