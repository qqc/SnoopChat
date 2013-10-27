package ucl.hackathon.snoopchat;

import java.util.ArrayList;

import ucl.hackathon.snoopchat.R;

import android.app.Activity;
import android.os.Bundle;

public class chatRooms extends Activity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room_layout);
        ucl.hackathon.snoopchat.WifiAPActivity prevactivity = new ucl.hackathon.snoopchat.WifiAPActivity();
        ArrayList<String> mRoomList = prevactivity.getmRoomList();
        
        
	}
}
