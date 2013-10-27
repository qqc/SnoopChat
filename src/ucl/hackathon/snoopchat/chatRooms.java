package ucl.hackathon.snoopchat;

import java.util.ArrayList;

import ucl.hackathon.snoopchat.R;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class chatRooms extends Activity
{
    
        ArrayList<String> chatRoomList;
        public void onCreate(Bundle saveInstanceState)
        {
                super.onCreate(saveInstanceState);
                setContentView(R.layout.chat_room_layout);
                
               // Get the reference of ListViewAnimals
                ListView chatroomList=(ListView)findViewById(R.id.chatroomlist);
                
                
                 chatRoomList = new ArrayList<String>();
                 getAlias();
                 // Create The Adapter with passing ArrayList as 3rd parameter
                 ArrayAdapter<String> arrayAdapter =      
                 new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, chatRoomList);
                 // Set The Adapter
                 chatroomList.setAdapter(arrayAdapter); 
                 
                 // register onClickListener to handle click events on each item
                 chatroomList.setOnItemClickListener(new OnItemClickListener()
                    {
                             // argument position gives the index of item which is clicked
                            public void onItemClick(AdapterView<?> arg0, View v,int position, long arg3)
                            {
                                
                                    String selectedAnimal=chatRoomList.get(position);
                                    Toast.makeText(getApplicationContext(), "Animal Selected : "+selectedAnimal,   Toast.LENGTH_LONG).show();
                                 }
                    });
                 
                 
                 
        
       }




void getAlias()
{
	chatRoomList.add("dsa");
	chatRoomList.add("dwe");
	chatRoomList.add("dsada");
	chatRoomList.add("413qw");
   
}
}