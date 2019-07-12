package com.yy.kaitian.yl;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.CheckBoxPreference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PointDatasPreference2Activity extends AppCompatActivity {

    private LinearLayout ll_back;
    private ListView listView;
    private PointDatasPreference2Adapter adapter;
    private List<Map<String, String>> mapList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_datas_preference2);
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
//        if (localMyDatabaseHandler.getPointDataCount() == 0)
//            return;
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
                if (localCustomerProfilesClass != null) {
                    for (String str = localCustomerProfilesClass.getName(); ; str = "UnKnow") {
                        Map<String, String> map = new HashMap<>();
                        map.put("id", "" + j);
                        map.put("name", str);
                        map.put("summary", "检测时间: " + localPointDetectData.getDetectTime());
                        mapList.add(map);
                        break;
                    }
                }
            }
        }

        if (adapter == null) {
            adapter = new PointDatasPreference2Adapter(this, mapList);
            adapter.setonEditClick(new PointDatasPreference2Adapter.OnEditClick() {
                @Override
                public void setonEditClick(final int position) {
                    AlertDialog.Builder localBuilder = new AlertDialog.Builder(PointDatasPreference2Activity.this);
                    localBuilder.setMessage("确定要删除用户记录?").setCancelable(false).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                            paramAnonymousDialogInterface.cancel();
                            int i = Integer.valueOf(mapList.get(position).get("id"));
                            new MyDatabaseHandler(PointDatasPreference2Activity.this).deletePointData(i);
                            initGroupsPreferences();
                            Toast.makeText(PointDatasPreference2Activity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                            paramAnonymousDialogInterface.cancel();
                        }
                    });
                    localBuilder.create().show();
                }
            });
            adapter.setOnStartCheckClick(new PointDatasPreference2Adapter.OnStartCheckClick() {
                @Override
                public void setOnStartCheckClick(int position) {

                    int i = Integer.valueOf(mapList.get(position).get("id"));
                    PointDetectData localPointDetectData = new MyDatabaseHandler(PointDatasPreference2Activity.this).getPointDataById(i);
                    String str = "";
                    if (localPointDetectData != null) {
                        str = localPointDetectData.getDetectTime() + " " + localPointDetectData.getCustomerID();
                        for (int j = 0; ; j++) {
                            if (j >= 24) {
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
            });
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

    }

}
