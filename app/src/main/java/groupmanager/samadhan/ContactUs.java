package groupmanager.samadhan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ContactUs extends Fragment {

	String ClientId,Table4Name,Website="";
	TextView textView1,txtName,txtAddr,txtMob,txtMob2,txtFax,txtEmail,txtWebsite;

	public ContactUs() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		final View rootView = inflater.inflate(R.layout.contactus, container, false);
		
		LinearLayout LLName=(LinearLayout)rootView.findViewById(R.id.LLName);
		LinearLayout LLAddr=(LinearLayout)rootView.findViewById(R.id.LLAddr);
		LinearLayout LLMob=(LinearLayout)rootView.findViewById(R.id.LLMob);
		LinearLayout LLFax=(LinearLayout)rootView.findViewById(R.id.LLFax);
		LinearLayout LLEmail=(LinearLayout)rootView.findViewById(R.id.LLEmail);
		LinearLayout LLWebsite=(LinearLayout)rootView.findViewById(R.id.LLWebsite);

		setHasOptionsMenu(true);///To Show Action Bar Menu
		textView1=(TextView)rootView.findViewById(R.id.textView1);
		txtName=(TextView)rootView.findViewById(R.id.txtName);
		txtAddr=(TextView)rootView.findViewById(R.id.txtAddr);
		txtMob=(TextView)rootView.findViewById(R.id.txtMob);
		txtMob2=(TextView)rootView.findViewById(R.id.txtMob2);
		txtFax=(TextView)rootView.findViewById(R.id.txtFax);
		txtEmail=(TextView)rootView.findViewById(R.id.txtEmail);
		txtWebsite=(TextView)rootView.findViewById(R.id.txtWebsite);
		Typeface face=Typeface.createFromAsset(getContext().getAssets(), "calibri.ttf");
		textView1.setTypeface(face);
		txtName.setTypeface(face);
		txtAddr.setTypeface(face);
		txtMob.setTypeface(face);
		txtMob2.setTypeface(face);
		txtFax.setTypeface(face);
		txtEmail.setTypeface(face);
		txtWebsite.setTypeface(face);

		
		WebView webVw1=(WebView)rootView.findViewById(R.id.webVw1);

		ClientId =  this.getArguments().getString("UserClubName");
		
		Table4Name="C_"+ClientId+"_4";
		
		LLName.setVisibility(View.GONE);
		LLAddr.setVisibility(View.GONE);
		LLMob.setVisibility(View.GONE);
		LLFax.setVisibility(View.GONE);
		LLEmail.setVisibility(View.GONE);
		LLWebsite.setVisibility(View.GONE);
		
		SQLiteDatabase db = getActivity().openOrCreateDatabase("MDA_Club", SQLiteDatabase.CREATE_IF_NECESSARY, null);
		String Qry="SELECT Text1,Cond1,Text2,Text3,Text4,Text5,Text6,Text7 FROM "+Table4Name +" where Rtype='Contact'";
		Cursor cursorT = db.rawQuery(Qry, null);
		
		String Name="",Addr="",Phone1="",Phone2="",Fax="",Email="",Loc="";
		
		if (cursorT.moveToFirst()) {
			Name=ChkVal(cursorT.getString(0));
			Addr=ChkVal(cursorT.getString(1));
			Phone1=ChkVal(cursorT.getString(2));
			Phone2=ChkVal(cursorT.getString(3));
			Fax=ChkVal(cursorT.getString(4));
			Email=ChkVal(cursorT.getString(5));
			Website=ChkVal(cursorT.getString(6));
			Loc=ChkVal(cursorT.getString(7));
	    }
	    cursorT.close();
	    db.close();
	    
	    
	    if(Name.length()>0){
	    	LLName.setVisibility(View.VISIBLE);
	    	txtName.setText(Name);
	    }
	    
	    if(Addr.length()>0){
	    	LLAddr.setVisibility(View.VISIBLE);
	    	txtAddr.setText(Addr);
	    }
	    
	    if(Phone1.length()>0 || Phone2.length()>0){
	    	LLMob.setVisibility(View.VISIBLE);
	    	txtMob.setText(Phone1);
	    	txtMob2.setText(Phone2);
	    }
	    
	    if(Fax.length()>0){
	    	LLFax.setVisibility(View.VISIBLE);
	    	txtFax.setText(Fax);
	    }
	    
	    if(Email.length()>0){
	    	LLEmail.setVisibility(View.VISIBLE);
	    	txtEmail.setText(Email);
	    }
	    
	    if(Website.length()>0){
	    	LLWebsite.setVisibility(View.VISIBLE);
	    	txtWebsite.setText(Website);
	    }
	    
	    if(Loc.length()>0)
	    {
	      String url = "http://maps.google.com/maps/api/staticmap?center=" + Loc + "&zoom=15&size=400x400&sensor=true";
	      //url = "http://maps.google.com/maps?q="+Loc;
	      
	      String text = "<html><body><p align=\"justify\">";
      	  text+= "</p><iframe width='100%' height='100%' src='https://maps.google.com/maps?q="+Loc+"&hl=es;z=15&amp;output=embed'></iframe></body></html>";
      	  webVw1.loadDataWithBaseURL(null, text, "text/html", "UTF-8", null);
	      
      	  webVw1.getSettings().setJavaScriptEnabled(true);
	      //webVw1.loadUrl(url);
	    }
	    
	    //Phone1 Click Event
	    txtMob.setOnClickListener(new View.OnClickListener() {
		     public void onClick(View v) {
		        String Ph1= txtMob.getText().toString().trim();
		        Show_LandLine_Dialog(Ph1.trim());
		     }
		});
	    
	    //Phone2 Click Event
	    txtMob2.setOnClickListener(new View.OnClickListener() {
		     public void onClick(View v) {
		        String Ph2= txtMob2.getText().toString().trim();
		        Show_LandLine_Dialog(Ph2.trim());
		     }
		});
	    
	    
	    //Email1 Click Event
	    txtEmail.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	String Email1= txtEmail.getText().toString().trim();
	        	Show_Email_Dialog(Email1);
	         }
	    });


		//Website Click Event
		txtWebsite.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Website));
				startActivity(intent);
			}
		});



		return rootView;
    }



	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		MenuItem item = menu.findItem(R.id.action_globalSearch);
		item.setVisible(false);///To hide Global Search Menu button
	}

	
	// Show Dialog to Call On Landline Number
		private void Show_LandLine_Dialog(final String PhoneNo)
		{
			AlertDialog.Builder AdBuilder = new AlertDialog.Builder(getContext());
			if((PhoneNo.length()==0)||(PhoneNo.contains("+"))){
			   AdBuilder
	   		    .setTitle( Html.fromHtml("<font color='#E32636'>Wrong Landline Number !</font>"))
	   		    .setMessage(PhoneNo)
	            .setPositiveButton("OK",new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog,int id) {
	                   	dialog.dismiss();
	                   }
	             });	
	   	   }else{
	   		   AdBuilder
	            .setPositiveButton("CALL",new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog,int id) {
	                	   
	                	   int ZeroIndex=PhoneNo.indexOf("0");
	                	   if(ZeroIndex==0)
	                   	     callOnphone(PhoneNo);
	                	   else
	                		 callOnphone("0"+PhoneNo);
	                	  
	                   }
	            });
	   	   }
		   AdBuilder.show();
	  }
	
	
		
	//Show Email Dialog
	private void Show_Email_Dialog(String Email)
	{      	
   	    Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        String[] TO = {Email};// Email Address in String Array 'TO'
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);                                    
        startActivity(Intent.createChooser(emailIntent, "Email:"));
	}
	
	public void callOnphone(String MobCall) {
		try {
	        Intent callIntent = new Intent(Intent.ACTION_CALL);
	        callIntent.setData(Uri.parse("tel:"+MobCall));
	        startActivity(callIntent);
	    } catch (ActivityNotFoundException activityException) {
	    	Toast.makeText(getContext(), "Call failed", Toast.LENGTH_SHORT).show();
	    	//System.out.println("Call failed");
	    }
	 }
	 
	
		
	
	//call function for initialise blank if null is there
	private String ChkVal(String DVal)
	{
		if((DVal==null)||(DVal.equalsIgnoreCase("null"))){
			DVal="";
		}
		return DVal;
	}




 
}
