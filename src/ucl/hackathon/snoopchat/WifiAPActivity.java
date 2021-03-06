package ucl.hackathon.snoopchat;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.widget.ProgressBar;
import android.widget.Toast;


public class WifiAPActivity extends Activity {
    private static final String TAG = "WifiAPActivity";
    private static final char MAGIC_CHAR = '%';

    boolean wasAPEnabled = false;
    static WifiAP wifiAp;
    private WifiManager wifi;
//    private static Button mBtnWifiToggle;
    private static ProgressBar mProgressSpinner;
    private static Button mBtnBroadcastMsg;
    private static EditText mInputField;
    private ListView mMessageLog;
    private ArrayAdapter<String> mLogAdapter;
    private String mPrevMsg;
    private String mAlias;
    ArrayList<String> mroomList;
    private String mCurrentRoomID = "aa";
    private long mScanStartTime;
    private long mScanStopTime;
    private boolean firstTime = true;
    List<Pair> macAddressList = new ArrayList<Pair>();
    
    private static class Pair
    {
    	public String alias;
    	public String mac;
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        mroomList = new ArrayList<String>();
//        mBtnWifiToggle = (Button)findViewById(R.id.btn_wifitoggle);
        mBtnBroadcastMsg = (Button)findViewById(R.id.btn_broadcast);
        mInputField = (EditText)findViewById(R.id.txt_ssidinput);
        mMessageLog = (ListView)findViewById(R.id.messagelog);
        mLogAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mMessageLog.setAdapter(mLogAdapter);
        mProgressSpinner = (ProgressBar)findViewById(R.id.progressbar);
        mPrevMsg = "";
        IntentFilter i = new IntentFilter(); 
        i.addAction (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(new BroadcastReceiver(){ 
              public void onReceive(Context c, Intent i) {
            	// Code to execute when SCAN_RESULTS_AVAILABLE_ACTION event occurs 
            	  WifiManager w = (WifiManager) c.getSystemService(Context.WIFI_SERVICE); 
            	  scanResultsHandler(w.getScanResults()); // Handles scan results.
            	  w.startScan();
            	  mScanStartTime = System.currentTimeMillis();
              }
        }, i );

        wifiAp = new WifiAP();
        wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        wifiAp.setAPStatusBlocking(wifi, false, WifiAPActivity.this);
        WifiLock wifiLock = wifi.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY , "MyWifiLock");
        if(!wifiLock.isHeld()){
            wifiLock.acquire();
        }
        wifi.startScan();
        
        final SharedPreferences prefs = this.getSharedPreferences("ucl.hackathon.snoopchat", Context.MODE_PRIVATE);
        prefs.edit().remove("ucl.hackathon.snoopchat.alias").commit();
        final String savedAlias = prefs.getString("ucl.hackathon.snoopchat.alias", "");
        if(savedAlias.isEmpty())
        {
        	AlertDialog.Builder alert = new AlertDialog.Builder(this);

        	alert.setTitle("Welcome");
        	alert.setMessage("Please enter your alias");

        	// Set an EditText view to get user input 
        	final EditText input = new EditText(this);
        	alert.setView(input);

        	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int whichButton) {
	        	mAlias = input.getText().toString();
	        	Log.d("WifiAPActivity", "got alias: " + mAlias);
	        	prefs.edit().putString("ucl.hackathon.snoopchat.alias", mAlias).commit();
	        	mLogAdapter.add("(Broadcasting Alias...)");
	        	wifiAp.setSSID("%&&" + mAlias);
	        	wifiAp.refreshAP(wifi, WifiAPActivity.this);
        	 }
        	});
        	alert.show();
        }
        
        mBtnBroadcastMsg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String ssid = mInputField.getText().toString();
				if(true == ssid.isEmpty())
				{
					Toast.makeText(getApplicationContext(), "dude what the fuck", Toast.LENGTH_LONG).show();
					return; // nope.avi
				}
				mProgressSpinner.setVisibility(View.VISIBLE);
//				mBtnBroadcastMsg.setEnabled(false);
				mBtnBroadcastMsg.setVisibility(View.INVISIBLE);
				mInputField.setText("");
				mInputField.setEnabled(false);
				
				mLogAdapter.add("(Me): " + ssid);
                // [ need code here that recognises what room the user is currently in, if any]
				// change variable mCurrentRoomID here
				
				if ( firstTime)
				{
					// txtbox appears, asks for alias //TODO put user input into a variable. call the string variable 'name'
					String name = new String();
					// bad man code
					name = "IKIRRU"; 	
					firstTime = false;
				}
				wifiAp.setSSID(MAGIC_CHAR + mCurrentRoomID + ssid);
				wifiAp.refreshAP(wifi, WifiAPActivity.this);
			}
		});

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_DIM_BEHIND);       
    }

    @Override
    public void onResume() {
        super.onResume();
        if (wasAPEnabled) {
            if (wifiAp.getWifiAPState()!=wifiAp.WIFI_AP_STATE_ENABLED && wifiAp.getWifiAPState()!=wifiAp.WIFI_AP_STATE_ENABLING){
                wifiAp.toggleAPStatus(wifi, WifiAPActivity.this);
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
            wifiAp.toggleAPStatus(wifi, WifiAPActivity.this);
        } else {
            wasAPEnabled = false;
        }
        updateStatusDisplay();
    }
    
    public void scanResultsHandler(List<ScanResult> scanResults)
    {
    	mScanStopTime = System.currentTimeMillis();
    	Log.d(TAG, "SCAN RESULTS RETURNED IN " + Long.toString(mScanStopTime - mScanStartTime) + " ms");
    	if(null == scanResults)
		{
			Log.d(TAG, "scanResults is null");
			return;
		}
		
		for(ScanResult result : scanResults)
		{
			if(result.SSID.charAt(0) == MAGIC_CHAR) 
			  {
				String msg = "";
				
				// generate alias form mac addresss
				
				Pair pair = new Pair();
				pair.alias = "el";
				pair.mac = result.BSSID;
				
				if (result.SSID.substring(1, 3).equals(mCurrentRoomID))
				{
					msg = result.SSID.substring(3);
				}
				else if (result.SSID.substring(1, 3).equals("&&") && ExistsMacaddwithBlankName(pair))
				{
					macAddressList.add(pair);
				}
				
				if(mPrevMsg.equals(msg) == false)
				{
					mLogAdapter.add(msg);
				}
				mPrevMsg = msg;
				
			}
		}
    }
    
    public boolean ExistsMacaddwithBlankName( Pair pair)
    {
    	
    	for( int index = 0 ; index  <= macAddressList.size();index++)
    	{
    			if ( macAddressList.get(index).mac.equals(pair.mac))
    			{
    				if (macAddressList.get(index).alias.equals(""))
    				{
    					return true;
    				}
    			}
    	}
    	return false;
    }
    
    public void updateRoomList(List <ScanResult> scanResults)
    {
    	for(ScanResult result : scanResults)
		{
		  String id = result.SSID.substring(1, 2);
		  
		  mroomList.clear();
		  if (!mroomList.contains(id))
		  {
			  mroomList.add(id);
		  }
		  
		}
    }
    
    public String generateRoomID()
    {
    	String id = new String();
    	
    	for (char c1 = 'a';c1<='z';c1++)
    	{
    		for (char c2 = 'a'; c2<='z';c2++)
    		{
    			id = "" + c1 + c2;
    			if (!mroomList.contains(id))
    			{
    				mroomList.add(id);
    				return id;
    			}
    		}
    	}
    	
    	return "aa";  // at this point, all possible rooms are in use ( lol, are we dis popular) user redirected to public chat
    	
    }
    
    public ArrayList<String> getmRoomList()
    {
    	return mroomList;
    }

    public static void updateStatusDisplay() {
    	mProgressSpinner.setVisibility(View.INVISIBLE);
//    	mBtnBroadcastMsg.setEnabled(true);
    	mBtnBroadcastMsg.setVisibility(View.VISIBLE);
    	mInputField.setEnabled(true);
    	/*
    	if (wifiAp.getWifiAPState()==wifiAp.WIFI_AP_STATE_ENABLED || wifiAp.getWifiAPState()==wifiAp.WIFI_AP_STATE_ENABLING) {
            mBtnWifiToggle.setText("Turn off");
            //findViewById(R.id.bg).setBackgroundResource(R.drawable.bg_wifi_on);
        } else {
            mBtnWifiToggle.setText("Turn on");
            //findViewById(R.id.bg).setBackgroundResource(R.drawable.bg_wifi_off);
        }*/
    }
    
    public void viewRooms(View view)
    {
    	Intent intent = new Intent(this, chatRooms.class);
    	intent.putStringArrayListExtra("mroomList", (ArrayList<String>) mroomList);
    	startActivity(intent);
    }
}