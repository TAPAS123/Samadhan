package groupmanager.samadhan;

import java.io.ByteArrayInputStream;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageDesk extends AppCompatActivity{
	TextView txtHeadent,txtbody,txtfooter;
	Intent menuIntent;
	String sqlSearch="",Table2Name,Table4Name,Log,logid,Str_user,STRM_ID;
	ImageView imgpersons;
	SQLiteDatabase db;
	Cursor cursorT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.messagedesk);
		imgpersons= (ImageView) findViewById(R.id.imageViewmessage);
	    txtHeadent=(TextView)findViewById(R.id.tvheadmessage);
	    txtbody=(TextView)findViewById(R.id.tvmessagedesk);
	    txtfooter=(TextView)findViewById(R.id.textViewmessageBy);

	    menuIntent = getIntent(); 
		Log =  menuIntent.getStringExtra("Clt_Log");
		logid =  menuIntent.getStringExtra("Clt_LogID");
		Str_user =  menuIntent.getStringExtra("UserClubName");
		STRM_ID =  menuIntent.getStringExtra("Pwd");
        Table2Name="C_"+Str_user+"_2";
		Table4Name="C_"+Str_user+"_4";
		
		sqlSearch="SELECT Text1,Add1,Text2,Photo1 from "+Table4Name+" Where Rtype='MESS' and M_ID="+STRM_ID+" Order By Num1 Desc";
	    calldatabase(sqlSearch);
	}
	

	 private void calldatabase(String sql){
		    db = openOrCreateDatabase("MDA_Club", SQLiteDatabase.CREATE_IF_NECESSARY, null); 
	        cursorT = db.rawQuery(sql, null);
	        String title="",message="",messageby="";
	        byte[] pic=null;
	        System.out.println(cursorT.getCount());
	        if(cursorT.getCount()==0){
	        	txtHeadent.setText("No Message");
	        }else{
	        	 if (cursorT.moveToFirst()) {
	  			   do {
	  				   title=cursorT.getString(0);
	  				   message =cursorT.getString(1);
	  				   messageby =cursorT.getString(2);
	  				   pic =cursorT.getBlob(3);
	  	    		 } while (cursorT.moveToNext());
	  	    	 }
	        	 txtHeadent.setText(title); 
	        	 txtbody.setText(message);
	        	 txtfooter.setText(messageby);
	        	 if(pic==null){
	        		 imgpersons.setVisibility(View.GONE);
	     	     }else{
	     	    	imgpersons.setVisibility(View.VISIBLE);
	     	    	ByteArrayInputStream imageStream = new ByteArrayInputStream(pic);
	     			Bitmap theImage = BitmapFactory.decodeStream(imageStream);
	     			imgpersons.setImageBitmap(theImage);
	     		 }  
	        }
	        cursorT.close();
	        db.close(); 
	        
	}
	 
	 public boolean onKeyDown(int keyCode, KeyEvent event) 
	 {
	   	 if (keyCode == KeyEvent.KEYCODE_BACK) {
	   		  /*Intent MainBtnIntent= new Intent(getBaseContext(),AffiliationAPP.class);
			  MainBtnIntent.putExtra("POstion", 0);
			  MainBtnIntent.putExtra("Count", 888888);
			  MainBtnIntent.putExtra("Clt_Log",Log);
			  MainBtnIntent.putExtra("Clt_LogID",logid);
			  MainBtnIntent.putExtra("UserClubName",Str_user);
	    	  startActivity(MainBtnIntent);*/
	    	  finish();
	   	 return true;
	   	 }
	   	return super.onKeyDown(keyCode, event);
	 }
	
    
}
