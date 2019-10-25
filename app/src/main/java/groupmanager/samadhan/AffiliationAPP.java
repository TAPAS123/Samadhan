package groupmanager.samadhan;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class AffiliationAPP extends Fragment {
	List<RowEnvt> rowItems;
	CustomAffil adapter;
	CustomNews adapt;
	Customfamily1 adptfamily1;
	ListView lvEvent;
	SQLiteDatabase db;
	Cursor cursorT;
	String sqlSearch,Table2Name,Table4Name,Log,logid,Str_user,StrClubName,StrCity,StrM_id,Strsrch,TableFamilyName,PName="";
	Intent menuIntent;
	String [] temp;
	RowEnvt item;
	TextView txtHeadent;
	int StrCount,post;
	EditText myFilter;
	ImageView imgsrchaff,Ivaddmember;
	String UserType="";


	public AffiliationAPP() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		final View rootView = inflater.inflate(R.layout.eventcalendar, container, false);

        lvEvent = (ListView) rootView.findViewById(R.id.listViewEvnt);
        txtHeadent=(TextView)rootView.findViewById(R.id.txtevnthead);
        myFilter = (EditText) rootView.findViewById(R.id.edCitysechaff);
        imgsrchaff= (ImageView) rootView.findViewById(R.id.imgSerchAff);
        Ivaddmember= (ImageView) rootView.findViewById(R.id.ivupdateMember);

		setHasOptionsMenu(true);///To Show Action Bar Menu

		Log =  this.getArguments().getString("Clt_Log");
		logid =  this.getArguments().getString("Clt_LogID");
		//ClubName =  this.getArguments().getString("Clt_ClubName");
		Str_user =  this.getArguments().getString("UserClubName");
		StrCount =  this.getArguments().getInt("Count", StrCount);
		post =  this.getArguments().getInt("POstion", post);

		Typeface face= Typeface.createFromAsset(getContext().getAssets(), "calibri.ttf");
		txtHeadent.setTypeface(face);
		
        Table2Name="C_"+Str_user+"_2";
		Table4Name="C_"+Str_user+"_4";
		TableFamilyName="C_"+Str_user+"_Family";
		
		Get_SharedPref_Values();// Get Stored Shared Pref Values of Login
		
		if(StrCount==0)
		{
			imgsrchaff.setVisibility(View.VISIBLE);
			sqlSearch="SELECT Text2,Text1,m_id from "+Table4Name+" Where Rtype='AFFI' Order By Text2,Text1";
		}
		else if(StrCount==999999 || StrCount==111 || StrCount==222)
		{
			txtHeadent.setText("News");
			imgsrchaff.setVisibility(View.GONE);
			String Cond="";//News Condition 12-05-2016 Updated by Tapas
			if(StrCount==999999)
			{
			   if(UserType.equals("SPOUSE"))
				  Cond=" AND (COND2 is NULL OR COND2='ALL' OR LENGTH(COND2)=0 OR COND2 like '%,"+logid+",%')";//News Condition 12-05-2016 Updated by Tapas
	    	   else 
	    		  Cond=" AND (COND1 is NULL OR COND1='ALL' OR LENGTH(COND1)=0 OR COND1 like '%,"+logid+",%')";//News Condition 12-05-2016 Updated by Tapas
			}
			sqlSearch="SELECT Text2,Text1,M_ID from "+Table4Name+" Where Rtype='News' "+Cond+" Order By Date1_1 DESC";
		}
		else if(StrCount==22)
		{
			txtHeadent.setText("Pending News");
			imgsrchaff.setVisibility(View.GONE);
			sqlSearch="SELECT Text2,Text1,m_id from "+Table4Name+" Where Rtype='Add_News' Order By m_id DESC";
		}
		else if(StrCount==888888)
		{
			txtHeadent.setText("Messages");
			imgsrchaff.setVisibility(View.GONE);
			sqlSearch="SELECT Text2,Text1,m_id from "+Table4Name+" Where Rtype='MESS' Order By Num1 Desc";
		}
		else if(StrCount==888222||StrCount==888333)
		{
			if(StrCount==888222)
				PName =  menuIntent.getStringExtra("PName");
			else
				PName=Log;
			
			txtHeadent.setText(PName+" (Family)");
			imgsrchaff.setVisibility(View.GONE);
			sqlSearch="SELECT Name,Relation,Mob_1,M_id,DOB_D,DOB_M,DOB_Y,Pic from "+TableFamilyName+" Where MemId="+post+" Order By Name";
		}
		
		if(StrCount==888333)
			 Ivaddmember.setVisibility(View.VISIBLE);
		else
			 Ivaddmember.setVisibility(View.GONE); 
		 
		calldatabase(sqlSearch);
		
           myFilter.addTextChangedListener(new TextWatcher() {
        	  public void afterTextChanged(Editable s) {}
        	  public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        	  public void onTextChanged(CharSequence s, int start, int before, int count) {
        		  //imgsrchaff.setVisibility(View.VISIBLE);
        		   Strsrch=myFilter.getText().toString();
     				System.out.println(Strsrch);
     				sqlSearch="SELECT Text2,Text1,m_id from "+Table4Name+" Where Rtype='AFFI' AND TEXT2 like '"+Strsrch+"%'  Order By Text2,Text1";
     				calldatabase(sqlSearch);
        	  }
        	  });
           
           imgsrchaff.setOnClickListener(new OnClickListener(){ 
   			@Override
   			public void onClick(View arg0) {
   				myFilter.setVisibility(View.VISIBLE);
   		        myFilter.setHint("Search city here.");
   		        myFilter.requestFocus();
   		       ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(myFilter, InputMethodManager.SHOW_FORCED);
   		     	myFilter.setFocusable(true);
   		     	
   			}
           });
           
           Ivaddmember.setOnClickListener(new OnClickListener() { 
   			@Override
   			  public void onClick(View arg0) {
   				/*menuIntent= new Intent(context,UpdateMemberProfile.class);
    	    	menuIntent.putExtra("Clt_LogID",logid);
 				menuIntent.putExtra("Clt_Log",Log);
 				menuIntent.putExtra("UserClubName",Str_user);
	    	    menuIntent.putExtra("Pwd",0);
	    	    menuIntent.putExtra("POstion",-1);
		        startActivity(menuIntent);
		  		finish();*/
   			  }
           });

		return rootView;
        
	}



	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		MenuItem item = menu.findItem(R.id.action_globalSearch);
		item.setVisible(false);///To hide Global Search Menu button
	}

	 
	 private void Get_SharedPref_Values()
	 {
		SharedPreferences ShPref = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

	     if (ShPref.contains("UserType")){
	    	  UserType=ShPref.getString("UserType", "");
	     }
	 }
	
	private void calldatabase(String sql){
		 db = getActivity().openOrCreateDatabase("MDA_Club", SQLiteDatabase.CREATE_IF_NECESSARY, null);
		  System.out.println(sql);
	        cursorT = db.rawQuery(sql, null);
	        System.out.println(cursorT.getCount());
	        if(cursorT.getCount()==0){
	        	if(StrCount==999999 || StrCount==111){
	        		txtHeadent.setText("");
	        	}else if(StrCount==888888){
	        		//txtHeadent.setText("No Message");
	        	}else{
	        		txtHeadent.setText("No Record");	
	        	}
	        	//Toast.makeText(getBaseContext(), "No Record", Toast.LENGTH_SHORT).show();
	        }else{
	        	String mid;
	        	rowItems = new ArrayList<RowEnvt>();
	        	 if (cursorT.moveToFirst()) {
	  			   do {
	  				   StrCity=cursorT.getString(0);
	  				   StrClubName =cursorT.getString(1);
	  				   StrM_id =cursorT.getString(2);
	  				   
		  			   if(StrCount==888222||StrCount==888333)
		  			   {
		  				  mid=cursorT.getString(3); 
		  				  String StrDD =ChkVal(cursorT.getString(4));
		  				  String StrMM=ChkVal(cursorT.getString(5));
		  				  String StrYY =ChkVal(cursorT.getString(6));
		  				  byte[] imgP=cursorT.getBlob(7);
		  				  
		  				  String DOB="";
		  				  if(StrDD.length()>0 && StrMM.length()>0)
		  			      {
		  			    	if(StrYY.length()==0){
		  			    		StrMM= getMonthForInt(Integer.parseInt(StrMM));
		  			    		DOB=StrDD+" "+StrMM;
		  			    	}else{
		  			    		DOB=StrDD+"-"+StrMM+"-"+StrYY; 
		  			    	}
		  			      }
		  				  item = new RowEnvt(StrCity,StrClubName,StrM_id,mid,DOB,imgP);
		  				}else{
		  				  item = new RowEnvt(StrCity,StrClubName,StrM_id,"");	
		  				}
	        	       rowItems.add(item); 
	  	    		 } while (cursorT.moveToNext());
	  	    	 }
	        	 
	        	 if((StrCount==999999) || StrCount==111 ||(StrCount==888888)||(StrCount==22)||(StrCount==222))
				 {
					 if(StrCount==888888){
							 adapt = new CustomNews(getContext(), R.layout.newslist, rowItems);
							 lvEvent.setAdapter(adapt);
							 lvEvent.setSelection(post);
					 }
					 else {
						 adapt = new CustomNews(getContext(), R.layout.newslist, rowItems);
						 lvEvent.setAdapter(adapt);
						 lvEvent.setSelection(post);
					 }
	        	 }else if(StrCount==888222||StrCount==888333){
	        		 adptfamily1 = new Customfamily1(getContext(),R.layout.familylist1, rowItems);
		             lvEvent.setAdapter(adptfamily1);  
	        	 }
	        	 else{
	        		 adapter = new CustomAffil(getContext(),R.layout.affiliationlist, rowItems);
		             lvEvent.setAdapter(adapter);  
		             lvEvent.setSelection(post);
	        	 }
	        	 
	        	 
	        	 //ListView Click Event
	             lvEvent.setOnItemClickListener(new OnItemClickListener() 
	             {
	    	    	 public void onItemClick(AdapterView<?> parent, View view, int position, long id){
	    	    	    String Pwd=rowItems.get(position).getEvtdate();
	    	    	    String mid=rowItems.get(position).getEvtVenue();
	    	    	    if(StrCount==888888)
	    	    	    {
	    	    	    	menuIntent= new Intent(getContext(),MessageDesk.class);
		    	    	    menuIntent.putExtra("Pwd",Pwd);
	    	    	    }
	    	    	    else if(StrCount==888222)
	    	    	    {
	    	    	    	menuIntent= new Intent(getContext(),FamilyDetailvalue.class);
		    	    	    menuIntent.putExtra("Pwd",mid);
		    	    	    menuIntent.putExtra("POstion",post);
		    	    	    menuIntent.putExtra("PName",PName);
	    	    	    }
	    	    	    else if(StrCount==888333)
	    	    	    {
	    	    	    	/*menuIntent= new Intent(context,UpdateMemberProfile.class);
		    	    	    menuIntent.putExtra("Pwd",mid);
		    	    	    menuIntent.putExtra("POstion",post);*/
	    	    	    }
	    	    	    else if(StrCount==111 || StrCount==222)
	    	    	    {   
	    	    	    	//Read/UnRead News for Admin
	    	    	    	
	    	    	    	/*String V4=rowItems.get(position).getEvtName();//Get News Title
	    	    	    	String V3=rowItems.get(position).getEvtDesc();//Get News date
	    	    	    	
	    	    	    	if(StrCount==111)
	    	    	    	  menuIntent= new Intent(context,News_EventReadUnread.class);
	    	    	    	else if(StrCount==222)
	    	    	    	  menuIntent= new Intent(context,Resend_Notification.class);
		    	    	    menuIntent.putExtra("PType","News");
		    	    	    menuIntent.putExtra("MID",Pwd);
		    	    	    menuIntent.putExtra("VAL2","");//Event/News (Venue)
		    	    	    menuIntent.putExtra("VAL3",V3);//Event/News (Date)
		    	    	    menuIntent.putExtra("VAL4",V4);//Event/News (Title)*/
	    	    	    }
	    	    	    else
	    	    	    {
	    	    	        /*menuIntent= new Intent(context,ShowAffiliation.class);
	    	    	        if(StrCount==0){
	    	    	    	   menuIntent.putExtra("CHKg","1");	
	    	    	        }else {
	    	    	    	   menuIntent.putExtra("CHKg","@@@");
	    	    	        }
		    	    	    menuIntent.putExtra("Pwd",Pwd);
		    	    	    menuIntent.putExtra("Count", StrCount);
		    	    	    menuIntent.putExtra("Positn", position);*/
	    	  	        }
	    	    	    menuIntent.putExtra("Clt_Log",Log);
    	    	    	menuIntent.putExtra("Clt_LogID",logid);
	     				menuIntent.putExtra("UserClubName",Str_user);
	     				startActivity(menuIntent);
	    		  		//getActivity().finish();
	    	    	    
	    	    	    
	    	    	  }
	    	       });
	        }
	        cursorT.close();
	        db.close(); 
	        
	}
	




	private String getMonthForInt(int num) {
        String month = "wrong";
        num=num-1;
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
    }
	
	
	//call function for initialise blank if null is there
	private String ChkVal(String DVal)
	{
		if((DVal==null)||(DVal.equalsIgnoreCase("null"))){
			DVal="";
		}
		return DVal.trim();
	}
	
	
	/*public boolean onKeyDown(int keyCode, KeyEvent event)
	 {
	   	 if (keyCode == KeyEvent.KEYCODE_BACK) {
	   		if(StrCount==22)
	   		{
	   		   MainBtnIntent= new Intent(getBaseContext(),Add_News.class);
	   		   MainBtnIntent.putExtra("Clt_Log", Log);
	   		   MainBtnIntent.putExtra("Clt_LogID", logid);
	   		   MainBtnIntent.putExtra("Clt_ClubName", ClubName);
	   		   MainBtnIntent.putExtra("UserClubName", Str_user);
	   		   MainBtnIntent.putExtra("AppLogo", AppLogo);
	   		   MainBtnIntent.putExtra("addchk", "2");
	   		   startActivity(MainBtnIntent);
			   finish();
	   		}else if(StrCount==888222||StrCount==888333){
	   		   finish();
	   		}
	   		else{
	   		   MainBtnIntent= new Intent(getBaseContext(),MenuPage.class);
		   	   MainBtnIntent.putExtra("AppLogo", AppLogo);
			   startActivity(MainBtnIntent);
			   finish();
	   		}

	   	    return true;
	   	 }
	   	return super.onKeyDown(keyCode, event);
	 }*/
	
	
}
