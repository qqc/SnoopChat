package ucl.hackathon.ssidchat;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;

public class chatRooms extends Activity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room_layout);
        ucl.hackathon.ssidchat.WifiAPActivity prevactivity = new ucl.hackathon.ssidchat.WifiAPActivity();
        ArrayList<String> mRoomList = prevactivity.getmRoomList();
        
        
	}
}
