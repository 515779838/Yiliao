package com.yy.kaitian.yiliao681;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Iterator;
import java.util.Set;

public class DeviceListActivity extends Activity
{
  private static final boolean D = true;
  public static String EXTRA_DEVICE_ADDRESS = "device_address";
  private static final String TAG = "DeviceListActivity";
  private BluetoothAdapter mBtAdapter;
  private OnItemClickListener mDeviceClickListener = new OnItemClickListener()
  {
    public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
    {
      DeviceListActivity.this.mBtAdapter.cancelDiscovery();
      String str1 = ((TextView)paramAnonymousView).getText().toString();
      String str2 = null;
      try {
        str2 = str1.substring(-17 + str1.length());
      } catch (Exception e) {
        e.printStackTrace();
        return;
      }
      Intent localIntent = new Intent();
      localIntent.putExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS, str2);
      DeviceListActivity.this.setResult(-1, localIntent);
      DeviceListActivity.this.finish();
    }
  };
  private ArrayAdapter<String> mNewDevicesArrayAdapter;
  private ArrayAdapter<String> mPairedDevicesArrayAdapter;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      String str1 = paramAnonymousIntent.getAction();
      if (BluetoothDevice.ACTION_FOUND.equals(str1))
      {
        BluetoothDevice localBluetoothDevice = (BluetoothDevice)paramAnonymousIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");

        if (localBluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED)
          DeviceListActivity.this.mNewDevicesArrayAdapter.add(localBluetoothDevice.getName() + "\n" + localBluetoothDevice.getAddress());
      }
      else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(str1))
      {
        DeviceListActivity.this.setProgressBarIndeterminateVisibility(false);
        DeviceListActivity.this.setTitle(R.string.select_device);
      }
       if(DeviceListActivity.this.mNewDevicesArrayAdapter.getCount() != 0){

       } else {
         String str2 = DeviceListActivity.this.getResources().getText(R.string.none_found).toString();
         DeviceListActivity.this.mNewDevicesArrayAdapter.add(str2);
       }
    }
  };

  private void doDiscovery()
  {
    Log.d("DeviceListActivity", "doDiscovery()");
    setProgressBarIndeterminateVisibility(true);
    setTitle(R.string.scanning);
    findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);
    if (this.mBtAdapter.isDiscovering()){
      this.mBtAdapter.cancelDiscovery();
    } else {
      this.mBtAdapter.startDiscovery();
    }
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    requestWindowFeature(5);
    setContentView(R.layout.device_list);
//    getWindow().setFeatureInt(7, R.layout.custom_title_button);

    setResult(0);
    ((Button)findViewById(R.id.button_scan)).setOnClickListener(new OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        DeviceListActivity.this.doDiscovery();
        paramAnonymousView.setVisibility(View.GONE);
      }
    });
    this.mPairedDevicesArrayAdapter = new ArrayAdapter(this, R.layout.device_name);
    this.mNewDevicesArrayAdapter = new ArrayAdapter(this, R.layout.device_name);
    ListView localListView1 = (ListView)findViewById(R.id.paired_devices);
    localListView1.setAdapter(this.mPairedDevicesArrayAdapter);
    localListView1.setOnItemClickListener(this.mDeviceClickListener);
    ListView localListView2 = (ListView)findViewById(R.id.new_devices);
    localListView2.setAdapter(this.mNewDevicesArrayAdapter);
    localListView2.setOnItemClickListener(this.mDeviceClickListener);
    IntentFilter localIntentFilter1 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    registerReceiver(this.mReceiver, localIntentFilter1);
    IntentFilter localIntentFilter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
    registerReceiver(this.mReceiver, localIntentFilter2);
    this.mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    Set localSet = this.mBtAdapter.getBondedDevices();
    if (localSet.size() > 0)
    {
      findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
      Iterator localIterator = localSet.iterator();
      while (true)
      {
        if (!localIterator.hasNext())
          return;
        BluetoothDevice localBluetoothDevice = (BluetoothDevice)localIterator.next();
        this.mPairedDevicesArrayAdapter.add(localBluetoothDevice.getName() + "\n" + localBluetoothDevice.getAddress());
      }
    }
    String str = getResources().getText(R.string.none_paired).toString();
    this.mPairedDevicesArrayAdapter.add(str);
  }

  protected void onDestroy()
  {
    super.onDestroy();
    if (this.mBtAdapter != null)
      this.mBtAdapter.cancelDiscovery();
    unregisterReceiver(this.mReceiver);
  }
}

