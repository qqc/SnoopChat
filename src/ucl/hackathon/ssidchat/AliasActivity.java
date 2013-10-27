package ucl.hackathon.ssidchat;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

public class AliasActivity extends Activity{
	
	static WifiAP wifiAp;
	private WifiManager wifi;
	private static final String TAG = "WifiAPActivity";
    private static final char MAGIC_CHAR = '%';

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		AliasActivity start = new AliasActivity();
		
		start.broadcastAlias(MAGIC_CHAR + "&&" + "MYALIAS"); //TODO replace "MY ALIAS" with text from UI object

	}
	
	
	public void broadcastAlias(String myAlias)
	{
		String ssid = myAlias;
		
		wifiAp = new WifiAP();
        wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        wifiAp.setAPStatusBlocking(wifi, false, AliasActivity.this);
	
		wifiAp.setSSID(ssid);
		if(wifi.isWifiEnabled() == false)
		{
			Log.d(TAG, "Toggling WiFiAP");
			wifiAp.toggleAPStatus(wifi, AliasActivity.this);
		}
		else
		{
			Log.d(TAG, "Resetting WiFiAP");
			wifiAp.resetAPStatus(wifi, AliasActivity.this);
		}
		
	}

}
