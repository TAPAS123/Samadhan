package groupmanager.samadhan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class ShowPastPresSec extends AppCompatActivity {
	String Str_user,Year,Name,mobile="",email="",Chk;
	Intent menuIntent;
	Context context=this;
	TextView txtHead,Txtname,TxtYear,Txtemail,Txtphone,TxtShowname,TxtShowYear,TxtShowemail,TxtShowphone;
	AlertDialog ad;
	String MTitle;
	AlertDialog.Builder alertDialogBuilder3;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showpastpressecretary);
        Txtname=(TextView)findViewById(R.id.textView1Past);
        TxtYear=(TextView)findViewById(R.id.textView2Past);
        Txtphone=(TextView)findViewById(R.id.textView3Past);
        Txtemail=(TextView)findViewById(R.id.textView4Past);
        txtHead=(TextView)findViewById(R.id.tvheadPast);  
        TxtShowname=(TextView)findViewById(R.id.tvNamePast);
        TxtShowYear=(TextView)findViewById(R.id.tvYearPast);
        TxtShowemail=(TextView)findViewById(R.id.tvEamilPast);
        TxtShowphone=(TextView)findViewById(R.id.tvContactPast);

        ad=new AlertDialog.Builder(this).create();

        menuIntent = getIntent();
		Str_user=menuIntent.getStringExtra("UserClubName");
		Year=menuIntent.getStringExtra("Pyear");
		Name=menuIntent.getStringExtra("Pname");
		mobile=menuIntent.getStringExtra("Pmob");
		email=menuIntent.getStringExtra("Pemail");
		Chk=menuIntent.getStringExtra("StrChk");
		MTitle =  menuIntent.getStringExtra("MTitle");

		
		if(Chk.equals("2")){
			txtHead.setText("Past Secretray Detail");
		}else{
			txtHead.setText(MTitle+" Detail");
		}
		
		checklenth(Name,Txtname,TxtShowname);
		checklenth(Year,TxtYear,TxtShowYear);
		checklenth(mobile,Txtphone,TxtShowphone);
		checklenth(email,Txtemail,TxtShowemail);
		
		if(mobile.length()!=0){
			TxtShowphone.setTextColor(Color.BLUE);
			TxtShowphone.setOnClickListener(new OnClickListener(){ 
	            public void onClick(View arg0){ 
		        alertDialogBuilder3 = new AlertDialog.Builder(ShowPastPresSec.this);
		        if(mobile.length()==10){
		        	mobile="0"+mobile;
		        }
		                 
             alertDialogBuilder3
             .setPositiveButton("Call",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                	callOnphone(mobile);
                }
             })
             .setNegativeButton("Sms",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                	callOnSms(mobile);
                }
             });
             ad = alertDialogBuilder3.create();
             ad.show();
	         }
	       });  
		}
		
		if(email.length()!=0){
			TxtShowemail.setTextColor(Color.BLUE);
			TxtShowemail.setOnClickListener(new OnClickListener(){ 
	            public void onClick(View arg0){ 
	            	alertDialogBuilder3 = new AlertDialog.Builder(ShowPastPresSec.this);
	            	 alertDialogBuilder3
		                .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog,int id) {
		                    	dialog.dismiss();
		                    }
		                })
		                .setPositiveButton("Email",new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog,int id) {
		                    	callemail(email);
		                    }
		                });
		                ad = alertDialogBuilder3.create();
		                ad.show();
	            }
			});
		}
	}
	
	private void checklenth(String Strget,TextView txtcaption,TextView txtvalue){
		if(Strget.length()==0){
			txtcaption.setVisibility(View.GONE);
			txtvalue.setVisibility(View.GONE);
		}else{
			txtcaption.setVisibility(View.VISIBLE);
			txtvalue.setVisibility(View.VISIBLE);
			txtvalue.setText(Strget);
		}
	}
	

	
	
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	 {
	   	 if (keyCode == KeyEvent.KEYCODE_BACK) {
			   finish();
	   	 return true;
	   	 }
	   	return super.onKeyDown(keyCode, event);
	 }
	
	public void callOnphone(String MobCall) {
		try {
	        Intent callIntent = new Intent(Intent.ACTION_CALL);
	        callIntent.setData(Uri.parse("tel:"+MobCall));
	        startActivity(callIntent);
	    } catch (ActivityNotFoundException activityException) {
	    	Toast.makeText(getBaseContext(), "Call failed", Toast.LENGTH_SHORT).show();
	    	//System.out.println("Call failed");
	    }
    }
 
   public void callOnSms(String MobCall) {
		try {
			String uri= "smsto:"+MobCall;
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
            intent.putExtra("compose_mode", true);
            startActivity(intent);
	       } catch (ActivityNotFoundException activityException) {
	    	Toast.makeText(getBaseContext(), "Sms failed", Toast.LENGTH_SHORT).show();
	       }
   }

   public void callemail(String tomail){
	 try{
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		String aEmailList[] = {tomail};
		emailIntent.putExtra(Intent.EXTRA_EMAIL, aEmailList);
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Club manager");
		emailIntent.setType("plain/text");
		emailIntent.putExtra(Intent.EXTRA_TEXT, "My message body.");
		startActivity(Intent.createChooser(emailIntent, "Send your email in:"));
	 }catch(Exception ex){	
		 Toast.makeText(getBaseContext(), "Emailfailed", Toast.LENGTH_SHORT).show();
	 }
   }
}

