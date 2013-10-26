package ucl.hackathon.ssidchat;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


public class WifiAPActivity extends Activity {
    //private String TAG = "WifiAPActivity";

    boolean wasAPEnabled = false;
    static WifiAP wifiAp;
    private WifiManager wifi;
    private static Button mBtnWifiToggle;
    private Button mBtnChangeSSID;
    private EditText mSSIDInput;
    private ListView mMessageLog;
    private ArrayAdapter<String> mLogAdapter;
    private String mPrevMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        mBtnWifiToggle = (Button)findViewById(R.id.btn_wifitoggle);
        mBtnChangeSSID = (Button)findViewById(R.id.btn_changessid);
        mSSIDInput = (EditText)findViewById(R.id.txt_ssidinput);
        mMessageLog = (ListView)findViewById(R.id.messagelog);
        mLogAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mMessageLog.setAdapter(mLogAdapter);
        mPrevMsg = "";
        
        IntentFilter i = new IntentFilter(); 
        i.addAction (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION); 
        registerReceiver(new BroadcastReceiver(){ 
              public void onReceive(Context c, Intent i) {
            	// Code to execute when SCAN_RESULTS_AVAILABLE_ACTION event occurs 
            	  WifiManager w = (WifiManager) c.getSystemService(Context.WIFI_SERVICE); 
            	  scanResultsHandler(w.getScanResults()); // your method to handle Scan results
            	  w.startScan();
              }
        }, i );

        wifiAp = new WifiAP();
        wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        WifiLock wifiLock = wifi.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY , "MyWifiLock");
        if(!wifiLock.isHeld()){
            wifiLock.acquire();
        }
        wifi.startScan();

        mBtnWifiToggle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	wifiAp.setSSID("Hello World");
                wifiAp.toggleWiFiAP(wifi, WifiAPActivity.this);
            }
        });
        
        mBtnChangeSSID.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String ssid = mSSIDInput.getText().toString();
				mLogAdapter.add("(Me): " + ssid);
				wifiAp.setSSID("%" + ssid);
				wifiAp.resetWiFiAP(wifi, WifiAPActivity.this);
			}
		});

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_DIM_BEHIND);       
    }

    @Override
    public void onResume() {
        super.onResume();
        if (wasAPEnabled) {
            if (wifiAp.getWifiAPState()!=wifiAp.WIFI_AP_STATE_ENABLED && wifiAp.getWifiAPState()!=wifiAp.WIFI_AP_STATE_ENABLING){
                wifiAp.toggleWiFiAP(wifi, WifiAPActivity.this);
            }
        }
        updateStatusDisplay();
    }

    @Override
    public void onPause() {
        super.onPause();
        boolean wifiApIsOn = wifiAp.getWifiAPState()==wifiAp.WIFI_AP_STATE_ENABLED || wifiAp.getWifiAPState()==wifiAp.WIFI_AP_STATE_ENABLING;
        if (wifiApIsOn) {
            wasAPEnabled = true;
            wifiAp.toggleWiFiAP(wifi, WifiAPActivity.this);
        } else {
            wasAPEnabled = false;
        }
        updateStatusDisplay();
    }
    
    public void scanResultsHandler(List<ScanResult> scanResults)
    {
    	Log.d("WifiAPActivity", "SCAN RESULTS RETURNED");
    	if(null == scanResults)
		{
			Log.d("WifiAPActivity", "getScanResults() returned null");
			return;
		}
		
		for(ScanResult result : scanResults)
		{
			if(result.SSID.charAt(0) == '%')
			{
				String msg = result.SSID.substring(1);
				if(mPrevMsg.equals(msg) == false)
				{
					mLogAdapter.add(msg);
				}
				mPrevMsg = msg;
			}
		}
    }

    public static void updateStatusDisplay() {
        if (wifiAp.getWifiAPState()==wifiAp.WIFI_AP_STATE_ENABLED || wifiAp.getWifiAPState()==wifiAp.WIFI_AP_STATE_ENABLING) {
            mBtnWifiToggle.setText("Turn off");
            //findViewById(R.id.bg).setBackgroundResource(R.drawable.bg_wifi_on);
        } else {
            mBtnWifiToggle.setText("Turn on");
            //findViewById(R.id.bg).setBackgroundResource(R.drawable.bg_wifi_off);
        }
    }
}