package ucl.hackathon.snoopchat;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Handle enabling and disabling of WiFi AP
 * @author http://stackoverflow.com/a/7049074/1233435
 */
public class WifiAP extends Activity {
    private static int constant = 0;

    private static final int WIFI_AP_STATE_UNKNOWN = -1;
    private static int WIFI_AP_STATE_DISABLING = 0;
    private static int WIFI_AP_STATE_DISABLED = 1;
    public int WIFI_AP_STATE_ENABLING = 2;
    public int WIFI_AP_STATE_ENABLED = 3;
    private static int WIFI_AP_STATE_FAILED = 4;

    private final String[] WIFI_STATE_TEXTSTATE = new String[] {
            "DISABLING","DISABLED","ENABLING","ENABLED","FAILED"
    };

    private WifiManager wifi;
    private String TAG = "WifiAP";
    private String mSSID;

    private int stateWifiWasIn = -1;

    private boolean alwaysEnableWifi = true; //set to false if you want to try and set wifi state back to what it was before wifi ap enabling, true will result in the wifi always being enabled after wifi ap is disabled

    /**
     * Toggle the WiFi AP state
     * @param wifihandler
     * @author http://stackoverflow.com/a/7049074/1233435
     */
    public void toggleAPStatus(WifiManager wifihandler, Context context) {
        if (wifi==null){
            wifi = wifihandler;
        }

        boolean wifiApIsOn = getWifiAPState()==WIFI_AP_STATE_ENABLED || getWifiAPState()==WIFI_AP_STATE_ENABLING;
        new SetWifiAPTask(!wifiApIsOn,false,context).execute();
    }
    
    /**
     * Set the WiFi AP state
     * @param wifihandler
     * @param false = disabled, true = enabled
     * @author http://stackoverflow.com/a/7049074/1233435
     */
    public void setAPStatusBlocking(WifiManager wifihandler, boolean status, Context context) {
        if (wifi==null){
            wifi = wifihandler;
        }
        setWifiApEnabled(status);
    }
    
    /**
     * Reset the WiFi AP state
     * @param wifihandler
     * @author http://stackoverflow.com/a/7049074/1233435
     */
    public void refreshAP(WifiManager wifihandler, Context context) {
        if (wifi==null){
            wifi = wifihandler;
        }
        
        new RefreshWifiAPTask(false,context).execute();
    }

    /**
     * Enable/disable wifi
     * @param true or false
     * @return WifiAP state
     * @author http://stackoverflow.com/a/7049074/1233435
     */
    private int setWifiApEnabled(boolean enabled) {
        Log.d(TAG, "*** setWifiApEnabled CALLED **** " + enabled);

        WifiConfiguration config = new WifiConfiguration();
        config.SSID = mSSID;
        config.preSharedKey = "\"lemonadekeyfaces\"";
        //config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

        //remember wirelesses current state
        if (enabled && stateWifiWasIn==-1){
            stateWifiWasIn=wifi.getWifiState();
        }

        //disable wireless
        if (enabled && wifi.getConnectionInfo() !=null) {
            Log.d(TAG, "disable wifi: calling");
            wifi.setWifiEnabled(false);
            int loopMax = 10;
            while(loopMax>0 && wifi.getWifiState()!=WifiManager.WIFI_STATE_DISABLED){
                Log.d(TAG, "disable wifi: waiting, pass: " + (10-loopMax));
                try {
                    Thread.sleep(500);
                    loopMax--;
                } catch (Exception e) {

                }
            }
            Log.d(TAG, "disable wifi: done, pass: " + (10-loopMax));
        }

        //enable/disable wifi ap
        int state = WIFI_AP_STATE_UNKNOWN;
        try {
            Log.d(TAG, (enabled?"enabling":"disabling") +" wifi ap: calling");
            wifi.setWifiEnabled(false);
            Method method1 = wifi.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            //method1.invoke(wifi, null, enabled); // true
            method1.invoke(wifi, config, enabled); // true
            Method method2 = wifi.getClass().getMethod("getWifiApState");
            state = (Integer) method2.invoke(wifi);
        } catch (Exception e) {
            Log.e(WIFI_SERVICE, e.getMessage());
            // toastText += "ERROR " + e.getMessage();
        }


        //hold thread up while processing occurs
        if (!enabled) {
            int loopMax = 10;
            while (loopMax>0 && (getWifiAPState()==WIFI_AP_STATE_DISABLING || getWifiAPState()==WIFI_AP_STATE_ENABLED || getWifiAPState()==WIFI_AP_STATE_FAILED)) {
                Log.d(TAG, (enabled?"enabling":"disabling") +" wifi ap: waiting, pass: " + (10-loopMax));
                try {
                    Thread.sleep(500);
                    loopMax--;
                } catch (Exception e) {

                }
            }
            Log.d(TAG, (enabled?"enabling":"disabling") +" wifi ap: done, pass: " + (10-loopMax));

            //enable wifi if it was enabled beforehand
            //this is somewhat unreliable and app gets confused and doesn't turn it back on sometimes so added toggle to always enable if you desire
            if(stateWifiWasIn==WifiManager.WIFI_STATE_ENABLED || stateWifiWasIn==WifiManager.WIFI_STATE_ENABLING || stateWifiWasIn==WifiManager.WIFI_STATE_UNKNOWN || alwaysEnableWifi){
                Log.d(TAG, "enable wifi: calling");
                wifi.setWifiEnabled(true);
                //don't hold things up and wait for it to get enabled
            }

            stateWifiWasIn = -1;
        } else if (enabled) {
            int loopMax = 10;
            while (loopMax>0 && (getWifiAPState()==WIFI_AP_STATE_ENABLING || getWifiAPState()==WIFI_AP_STATE_DISABLED || getWifiAPState()==WIFI_AP_STATE_FAILED)) {
                Log.d(TAG, (enabled?"enabling":"disabling") +" wifi ap: waiting, pass: " + (10-loopMax));
                try {
                    Thread.sleep(500);
                    loopMax--;
                } catch (Exception e) {

                }
            }
            Log.d(TAG, (enabled?"enabling":"disabling") +" wifi ap: done, pass: " + (10-loopMax));
        }
        return state;
    }

    /**
     * Get the wifi AP state
     * @return WifiAP state
     * @author http://stackoverflow.com/a/7049074/1233435
     */
    public int getWifiAPState() {
        int state = WIFI_AP_STATE_UNKNOWN;
        try {
            Method method2 = wifi.getClass().getMethod("getWifiApState");
            state = (Integer) method2.invoke(wifi);
        } catch (Exception e) {

        }

        if(state>=10){
            //using Android 4.0+ (or maybe 3+, haven't had a 3 device to test it on) so use states that are +10
            constant=10;
        }

        //reset these in case was newer device
        WIFI_AP_STATE_DISABLING = 0+constant;
        WIFI_AP_STATE_DISABLED = 1+constant;
        WIFI_AP_STATE_ENABLING = 2+constant;
        WIFI_AP_STATE_ENABLED = 3+constant;
        WIFI_AP_STATE_FAILED = 4+constant;

        Log.d(TAG, "getWifiAPState.state " + (state==-1?"UNKNOWN":WIFI_STATE_TEXTSTATE[state-constant]));
        return state;
    }
    
    public void setSSID(String ssid)
    {
    	mSSID = ssid;
    }

    /**
     * the AsyncTask to enable/disable the wifi ap
     * @author http://stackoverflow.com/a/7049074/1233435
     */
    class SetWifiAPTask extends AsyncTask<Void, Void, Void> {
        boolean mMode; //enable or disable wifi AP
        boolean mFinish; //finalize or not (e.g. on exit)
        //ProgressDialog d;

        /**
         * enable/disable the wifi ap
         * @param mode enable or disable wifi AP
         * @param finish finalize or not (e.g. on exit)
         * @param context the context of the calling activity
         * @author http://stackoverflow.com/a/7049074/1233435
         */
        public SetWifiAPTask(boolean mode, boolean finish, Context context) {
            mMode = mode;
            mFinish = finish;
            //d = new ProgressDialog(context);
        }

        /**
         * do before background task runs
         * @author http://stackoverflow.com/a/7049074/1233435
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            d.setTitle("Turning WiFi AP " + (mMode?"on":"off") + "...");
//            d.setMessage("...please wait a moment.");
//            d.show();
        }

        /**
         * do after background task runs
         * @param aVoid
         * @author http://stackoverflow.com/a/7049074/1233435
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                //d.dismiss();
                WifiAPActivity.updateStatusDisplay();
            } catch (IllegalArgumentException e) {

            };
            if (mFinish){
                finish();
            }
        }

        /**
         * the background task to run
         * @param params
         * @author http://stackoverflow.com/a/7049074/1233435
         */
        @Override
        protected Void doInBackground(Void... params) {
            setWifiApEnabled(mMode);
            Log.d("SetWiFiAPTask", "WIFI AP SET TO: " + Boolean.toString(mMode));
            return null;
        }
    }
    
    /**
     * the AsyncTask to enable/disable the wifi ap
     * @author http://stackoverflow.com/a/7049074/1233435
     */
    class RefreshWifiAPTask extends AsyncTask<Void, Void, Void> {
        boolean mFinish; //finalize or not (e.g. on exit)
        //ProgressDialog d;

        /**
         * enable/disable the wifi ap
         * @param mode enable or disable wifi AP
         * @param finish finalize or not (e.g. on exit)
         * @param context the context of the calling activity
         * @author http://stackoverflow.com/a/7049074/1233435
         */
        public RefreshWifiAPTask(boolean finish, Context context) {
            mFinish = finish;
            //d = new ProgressDialog(context);
        }

        /**
         * do before background task runs
         * @author http://stackoverflow.com/a/7049074/1233435
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * do after background task runs
         * @param aVoid
         * @author http://stackoverflow.com/a/7049074/1233435
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                WifiAPActivity.updateStatusDisplay();
            } catch (IllegalArgumentException e) {

            };
            if (mFinish){
                finish();
            }
        }

        /**
         * the background task to run
         * @param params
         * @author http://stackoverflow.com/a/7049074/1233435
         */
        @Override
        protected Void doInBackground(Void... params) {
            setWifiApEnabled(true);
        	try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	setWifiApEnabled(false);
        	wifi.startScan();
            return null;
        }
    }
    
    
}