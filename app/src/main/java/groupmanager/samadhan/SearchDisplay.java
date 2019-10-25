package groupmanager.samadhan;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DateFormatSymbols;
import java.util.ArrayList;

import groupmanager.samadhan.SimpleGestureFilter.SimpleGestureListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchDisplay extends AppCompatActivity implements SimpleGestureListener{
	TextView txtName,txtMAX;
	String s,Str_user,Log,logid,sqlSearch="",finalresult,Table2Name,Table4Name,quey,SelectStrquey,STRFinalQury="";
	String [] temp;
	ImageView BtnNxt,BtnPrev,ImgVw_Ad;
	Intent menuIntent;
	AlertDialog ad;
	private SimpleGestureFilter detect;
	SQLiteDatabase dbObj;
	Cursor cursorT;
	int Cnt,i=0;
	Integer tempsize;
	int[] CodeArr;
	Dialog dialog;
	String Additional_Data,Additional_Data2,TableMiscName,TableFamilyName;
	final Context context=this;
	ArrayAdapter<String> listAdapter=null,listAdp_NewOpTitle=null;
	String MemDir="Member",JoinFinalqry="";
	int DOB_Disp=0;
	int Chkval=0;
	ArrayList<String> stock_list;
	String Dir_Filter_Condition,Special_Dir_Condition;
	String New_Op_Title="",DbName;
	ListView LV1;
	ArrayList<RowEnvt> ListObj;
	
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.directory_new);
	        ad=new AlertDialog.Builder(SearchDisplay.this).create();
	        detect = new SimpleGestureFilter(this,this);

			txtName=(TextView)findViewById(R.id.txtName);
			LV1 = (ListView)findViewById(R.id.Lv1);
			BtnNxt=(ImageView)findViewById(R.id.iVNext);
			BtnPrev=(ImageView)findViewById(R.id.iVPrev);
			txtMAX=(TextView)findViewById(R.id.tvMinofMax);
			ImgVw_Ad=(ImageView)findViewById(R.id.imgVw_Ad); // ImageView for Ad

			
			menuIntent = getIntent(); 
			Log =  menuIntent.getStringExtra("Clt_Log");
			logid =  menuIntent.getStringExtra("Clt_LogID");
			Str_user =  menuIntent.getStringExtra("UserClubName");
			quey =  menuIntent.getStringExtra("Qury");
			//StrShrdCrival =  menuIntent.getStringExtra("StrCriteria");
			SelectStrquey =  menuIntent.getStringExtra("STRslct");
			Additional_Data=menuIntent.getStringExtra("AddData1");
			Additional_Data2=menuIntent.getStringExtra("AddData2");
			Dir_Filter_Condition =  menuIntent.getStringExtra("Dir_Filter_Condition");//Directory Filter Condition
			Special_Dir_Condition=menuIntent.getStringExtra("Special_Dir_Condition");//Special Directory Condition with DirName
			New_Op_Title=menuIntent.getStringExtra("New_Op_Title");/////////Get New Option Caption or Title which is used to display some list in popup screen
			DbName=menuIntent.getStringExtra("DbName");//Connected Database Name

			stock_list = new ArrayList<String>();
			stock_list = menuIntent.getStringArrayListExtra("stock_list");
			
			if(stock_list!=null){
				Chkval=1;
				System.out.println("size:::T "+stock_list.size());
				for(int j=0;j<stock_list.size();j++){
					String val =stock_list.get(j).toString();
					String [] tt=val.split("#@");
					JoinFinalqry=JoinFinalqry+" Upper(cm."+tt[1].trim()+") like '%"+tt[0].trim().toUpperCase()+"%' And";
				}
				if(JoinFinalqry.length()!=0){
					JoinFinalqry=JoinFinalqry.substring(0, JoinFinalqry.length()-3);
				}
				System.out.println(JoinFinalqry);
			}
			
	        Table2Name="C_"+Str_user+"_2";
	        Table4Name="C_"+Str_user+"_4";
	        TableMiscName="C_"+Str_user+"_MISC";
	        TableFamilyName="C_"+Str_user+"_Family";
			
			Get_SharedPref_Values(); // Get Shared Pref Values of MemDir(Display Member/Spouse)
			
			/////////////////////////////////////////////////////	
	        if(quey.length()==0){
	           Toast.makeText(getBaseContext(), quey.length()+"  null ....", Toast.LENGTH_SHORT).show();
       	 	}else{
	       	   if(SelectStrquey.equals("1")){	
	       	 	  /*String LikeClause="",WhClause,OrderByClause;
	       	 	  String[] AArr=quey.split(" "); // Split with Space in name(quey)

	       		  if(MemDir.equals("Member"))
	       		  {
	       			 OrderByClause=" Order by c2.M_Name";// Order By Clause
	       			 
	       			 LikeClause="c2.M_Name like '%"+AArr[0].trim()+"%'";
	        		 for(int i=1;i<AArr.length;i++)
	        		 {
	        			LikeClause=LikeClause+" AND c2.M_Name like '%"+AArr[i].trim()+"%'";
	        		 }
	       		  }
	       		  else
	       		  {
	       			 OrderByClause=" Order by c2.S_Name"; // Order By Clause
	       			 
	       			 LikeClause="c2.S_Name like '%"+AArr[0].trim()+"%'";
	        		 for(int i=1;i<AArr.length;i++)
	        		 {
	        			LikeClause=LikeClause+" AND c2.S_Name like '%"+AArr[i].trim()+"%'";
	        		 }
	       		  }
	       		   
	       		  if(StrSqlRec.length()!=0){
	       			WhClause=" Where "+LikeClause+" AND "+StrSqlRec+StrShrdCrival+OrderByClause;
	       	 	  }else{
	       	 		WhClause=" Where "+LikeClause+StrShrdCrival+OrderByClause;	
	       	 	  }
	       		  
	       		sqlSearch="select M_id from "+Table2Name+WhClause;// Select Query
	       		//String pp="";*/
	       		
       	 	   }else if(SelectStrquey.equals("2")){	
	       	 	    quey=quey.replace("#", "#")+" "; 
		       	 	temp=quey.split("#");
		       	    String Name=temp[0].toString().trim();//Name
		       	    String Mob=temp[1].toString().trim();//Mobile OR Landline
	  				String MemNo=temp[2].toString().trim();//MemN0
	  				String Addr=temp[3].toString().trim();//Address
		  				
	  			    //Make Query For Name
	  				if(Name.length()!=0){
		       	 	  String[] AArr=Name.split(" "); // Split with Space in name
		       		  if(MemDir.equals("Member")){
		       			STRFinalQury="c2.M_Name like '%"+AArr[0].trim()+"%'";
		        	    for(int i=1;i<AArr.length;i++)
		        	    {
		        		   STRFinalQury=STRFinalQury+" AND c2.M_Name like '%"+AArr[i].trim()+"%'";
		        	    }
		       		  }
		       		  else{
		       			STRFinalQury="c2.S_Name like '%"+AArr[0].trim()+"%'";
		        	    for(int i=1;i<AArr.length;i++)
		        	    {
		        		   STRFinalQury=STRFinalQury+" AND c2.S_Name like '%"+AArr[i].trim()+"%'";
		        	    }
		       		  }
	  				}
		       		   
	  			    //Make Query For Mobile	/ Landline  				
		  			if(Mob.length()!=0){
		  				if(STRFinalQury.length()!=0){
			  				if(MemDir.equals("Member"))
					  		  STRFinalQury=STRFinalQury+" AND (c2.M_Mob like '%"+Mob+"%' OR  c2.M_SndMob like '%"+Mob+"%' OR  c2.M_Land1 like '%"+Mob+"%' OR  c2.M_Land2 like '%"+Mob+"%') ";
					  		else
					  		  STRFinalQury=STRFinalQury+" AND c2.S_Mob like '%"+Mob+"%'";
			  			}else{
			  				if(MemDir.equals("Member"))
				  			  STRFinalQury="(c2.M_Mob like '%"+Mob+"%' OR  c2.M_SndMob like '%"+Mob+"%' OR  c2.M_Land1 like '%"+Mob+"%' OR  c2.M_Land2 like '%"+Mob+"%') ";
				  			else
				  		      STRFinalQury="c2.S_Mob like '%"+Mob+"%'";
			  			}
		  			}
		  			
		  		    //Make Query For MemNo
		  			if(MemNo.length()!=0 && MemDir.equals("Member")){
		  				if(STRFinalQury.length()!=0){
		  				  STRFinalQury=STRFinalQury+" AND  c2.MemNo like '%"+MemNo+"%'";	
		  				}else{
		  				  STRFinalQury="c2.MemNo like '%"+MemNo+"%'";
		  				}
		       	 	}
		  			
		  		    //Make Query For Addr
		  			if(Addr.length()!=0){	
		  				if(STRFinalQury.length()!=0){
		  				  STRFinalQury=STRFinalQury+" AND  (c2.M_Add1  like '%"+Addr+"%' OR  c2.M_Add2  like '%"+Addr+"%'  OR  c2.M_Add3  like '%"+Addr+"%' OR c2.M_City like '%"+Addr+"%')";	
		  				}else{
		  				  STRFinalQury="(c2.M_Add1  like '%"+Addr+"%' OR  c2.M_Add2  like '%"+Addr+"%'  OR  c2.M_Add3  like '%"+Addr+"%' OR c2.M_City like '%"+Addr+"%')";
		  				}
		       	 	}
		  			
	  				if(Chkval==0)
	  				{
	  					/////20-02-2017 Added Special_Dir_Condition Qry Condition
	  					if(Special_Dir_Condition.contains("A.M_Id"))
	  					{
	  						STRFinalQury=" AND "+STRFinalQury;
	  						STRFinalQury=STRFinalQury.replace("c2.", "A.");
	  						sqlSearch=Special_Dir_Condition.replace("ORDER BY B.num2",STRFinalQury);
	  					}
	  					else{
	  						STRFinalQury=" Where "+STRFinalQury;
	  					}
	  					
	  				}else
	  				{
	  					if(STRFinalQury.length()>1){
	  						STRFinalQury=" join "+TableMiscName+"  cm on c2.M_id=cm.MemId Where "+JoinFinalqry+" And "+STRFinalQury;
	  					}else{
	  						STRFinalQury=" join "+TableMiscName+"  cm on c2.M_id=cm.MemId Where "+JoinFinalqry;
	  					}
	  				}
	  				
	  				if(sqlSearch.length()==0)
	  				{
		  			   //Make Final Search Query
		  			   if(MemDir.equals("Member"))
		  				sqlSearch="select c2.M_id  from "+Table2Name+" c2" +STRFinalQury+Dir_Filter_Condition+Special_Dir_Condition+" Order by C4_LName,Upper(c2.M_Name)";
		  		       else
		  				sqlSearch="select c2.M_id  from "+Table2Name+"  c2" +STRFinalQury+Dir_Filter_Condition+Special_Dir_Condition+" Order by Upper(c2.S_Name)";
		               ///////////////////////////////
	  				}
		  			
	       	 	 }
	       	     String tt=sqlSearch;
	       	     System.out.println(sqlSearch);
	       	     //String tt=sqlSearch;
	       	     callNoRecord();
       	 	  }



			LV1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					String Type = ListObj.get(position).getEvtDesc();
					String Val=ListObj.get(position).getEvtName();

					if(Type.equals("Mob1") || Type.equals("Mob2")){
						Show_Mob_Dialog(Val.trim());
					}
					else if(Type.equals("Phone1") || Type.equals("Phone2")){
						Show_LandLine_Dialog(Val.trim());
					}
					else if(Type.equals("Email1") || Type.equals("Email2")){
						Show_Email_Dialog(Val.trim());
					}
					else if(Type.equals("New_Op_Title")){
						// Display Popup Screen of ListView
						final Dialog dialog = new Dialog(context);
						dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);// For Hide the title of the dialog box
						dialog.setContentView(R.layout.additional_data);
						dialog.setCancelable(false);
						dialog.show();
						TextView txtHead=(TextView)dialog.findViewById(R.id.tvTt);
						ListView LV=(ListView)dialog.findViewById(R.id.Lv1);
						Button btnBack=(Button)dialog.findViewById(R.id.btnBack);

						txtHead.setText(New_Op_Title);

						// Set ListAdapter
						LV.setAdapter(listAdp_NewOpTitle);

						btnBack.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
							}
						});
					}

				}
			});
	       	 	


				
				
				BtnPrev.setOnClickListener(new OnClickListener(){ 
			         @Override public void onClick(View arg0){
			            Prev();
			         }
				});
					
			    BtnNxt.setOnClickListener(new OnClickListener(){ 
				     @Override public void onClick(View arg0){
				        Next();
				     }
			    });
			    
			    ImgVw_Ad.setOnClickListener(new OnClickListener(){ 
			        @Override public void onClick(View arg0){
			        	byte[] ImgAdg=null;
						Open_Database();//Open Database
			        	String Qry="Select photo1 from "+Table4Name+" Where Rtype='FullAdg'";
			        	cursorT = dbObj.rawQuery(Qry, null);
			        	while(cursorT.moveToNext())
			    		{
			        	   ImgAdg=cursorT.getBlob(0);
			    		   break;
			    		}
			    		cursorT.close();
						Close_Database();//Close DataBase
			    		
			    		// Sent Image for full AD
			    		if(ImgAdg!=null){
		    			 /* menuIntent= new Intent(getBaseContext(),FullAdvertisement.class);
			    		  menuIntent.putExtra("Type","9");
			    		  menuIntent.putExtra("Clt_Log",Log);
			    		  menuIntent.putExtra("Clt_LogID",logid);
			    		  menuIntent.putExtra("Clt_ClubName",ClubName);
			    		  menuIntent.putExtra("UserClubName",Str_user);
			    		  menuIntent.putExtra("AppLogo", AppLogo);
			    		  menuIntent.putExtra("Photo1", ImgAdg);
			    	      startActivity(menuIntent);*/
			    	      //finish();
			    		}
				       }
					 });
	}



	private void Open_Database()
	{
		dbObj = openOrCreateDatabase(DbName, SQLiteDatabase.CREATE_IF_NECESSARY, null);
	}

	private void Close_Database()
	{
		dbObj.close();
	}


		
    //Get Shared Pref Values
	private void Get_SharedPref_Values() {
	   SharedPreferences sharedpreferences = context.getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);
		if (sharedpreferences.contains("MemDir"))
		{
		   MemDir=sharedpreferences.getString("MemDir", "");
		}
		if (sharedpreferences.contains("DOB_Disp"))
	    {
		   DOB_Disp=Integer.parseInt(sharedpreferences.getString("DOB_Disp", ""));
	    }
		//NOTE DOB_Disp 0=DOB Visible, 1=DOB not Visible,2=DOB Visible without Year
    }	
		

		
	 @Override
	public boolean dispatchTouchEvent(MotionEvent m1){
		  // Call onTouchEvent of SimpleGestureFilter class
		  this.detect.onTouchEvent(m1);
		  return super.dispatchTouchEvent(m1);
	 }

	 
	//Show Mobile Call/Sms Dialog
	private void Show_Mob_Dialog(final String MobNo)
	{
		AlertDialog.Builder AdBuilder = new AlertDialog.Builder(context);
    	if((MobNo==null)||(MobNo.length()!=10)||(MobNo.contains("+"))){
    		AdBuilder
    		 .setTitle( Html.fromHtml("<font color='#E32636'>Wrong Mobile Number !</font>"))
    		 .setMessage(MobNo)
             .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                    	dialog.dismiss();
                    }
              });
    	}else{
    	  AdBuilder
            .setPositiveButton("CALL",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                    	callOnphone("0"+MobNo);
                    }
            })
            .setNegativeButton("SMS",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                    	callOnSms("0"+MobNo);
                    }
             });
    	}
    	AdBuilder.show();
	}
	
	
	// Show Dialog to Call On Landline Number
	private void Show_LandLine_Dialog(final String PhoneNo)
	{
		AlertDialog.Builder AdBuilder = new AlertDialog.Builder(context);
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
		    	System.out.println("Call failed");
		    }
	 }
	 
	public void callOnSms(String MobCall) {
			try {
				String uri= "smsto:"+MobCall;
	            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
	            intent.putExtra("compose_mode", true);
	            startActivity(intent);
		    } catch (ActivityNotFoundException activityException) {
		    	System.out.println("Sms failed");
		    }
	 }

	public boolean onKeyDown(int keyCode, KeyEvent event) 
	 {
	   	 if (keyCode == KeyEvent.KEYCODE_BACK) {
	   		back();
	   	 return true;
	   	 }
	   	return super.onKeyDown(keyCode, event);
	 }
	
	public void back(){
    	finish();
	}

	@Override
	public void onSwipe(int direction) {
		// TODO Auto-generated method stub
		switch (direction) {
	      case SimpleGestureFilter.SWIPE_RIGHT :
	    	   Prev();
	           break;
	      case SimpleGestureFilter.SWIPE_LEFT :  
	    	   Next();
	           break;
	      case SimpleGestureFilter.SWIPE_DOWN :  
	           break;
	      case SimpleGestureFilter.SWIPE_UP : 
	           break;
	      }
	}
	
	public void Next(){
		if(Cnt+1==tempsize){
    		Toast.makeText(getBaseContext(), "No Further Record", Toast.LENGTH_SHORT).show();
    	}else{
    		Cnt=Cnt+1;
    		FillMainData();
		    txtMAX.setText(Cnt+1+" of "+tempsize);
    	}
	}
	
	public void Prev(){
		if(Cnt==0){
    		Toast.makeText(getBaseContext(), "No Previous Record", Toast.LENGTH_SHORT).show();
    	}else{
    		Cnt=Cnt-1;
    		FillMainData();
		    txtMAX.setText(Cnt+1+" of "+tempsize);
    	}
	}
	
	@Override
	public void onDoubleTap() {
		// TODO Auto-generated method stub
	}

	
	public void callNoRecord() {
		// TODO Auto-generated method stub
		 Open_Database();//Open Database
		 Display_Image_Ad();// Display Ad (Advertisement)
		 
		 cursorT = dbObj.rawQuery(sqlSearch, null);
    	 tempsize=cursorT.getCount();
    	 if(tempsize==0){
    		 cursorT.close();
			 Close_Database();//Close DataBase
    		 AlertDialog.Builder AdBuilder = new AlertDialog.Builder(context);
    		 AdBuilder
    		 .setTitle( Html.fromHtml("<font color='#E32636'>Result!</font>"))
    		 .setMessage("No Record Found.")
    		 .setCancelable(false)
             .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                    	back();
                    }
             });
    		AdBuilder.show();	 
    	 }else{
    	   Cnt=0;
    	   txtMAX.setText(Cnt+1+" of "+tempsize);
    	   CodeArr=new int[tempsize];
    	  if (cursorT.moveToFirst()) {
    		 do {
	    		 CodeArr[i]=cursorT.getInt(0);
	    		 i++;
    		 } while (cursorT.moveToNext());
    	  }
    	  cursorT.close();
			 Close_Database();//Close DataBase
    	  FillMainData();
	    }
	}
	
	
	public void FillMainData() {
		// TODO Auto-generated method stub
	  try{
		 int MemId=0;
		 String sql="";
		 String[] AddDATA=new String[10];// Array For Additional Data
		  Open_Database();//Open Database
 		 
 		 if(MemDir.equals("Member")) 
			 sql="select M_id,M_Name,M_Add1,M_Add2,M_Add3,(M_City || \" \" || M_Pin),M_Email,M_Mob,MemNo,M_BG,M_Pic,(C4_BG || \"\" || ifnull(C4_DOB_Y,'')),M_DOB_D,M_DOB_M,M_DOB_Y,M_MrgAnn_D,M_MrgAnn_M,M_MrgAnn_Y,C4_DOB_D,M_BussNm,M_BussCate,M_MemSince_D,C4_Mob,M_SndMob,M_Email1,M_Land1,M_Land2 from "+Table2Name+" where M_id="+CodeArr[Cnt] ;
		 else 
			 sql="select M_id,S_Name,M_Add1,M_Add2,M_Add3,(M_City || \" \" || M_Pin),S_Email,S_Mob,C4_Gender,S_BG,S_Pic,C3_BG,S_DOB_D,S_DOB_M,S_DOB_Y,M_MrgAnn_D,M_MrgAnn_M,M_MrgAnn_Y,C4_DOB_D,M_BussNm,M_BussCate,M_MemSince_D,C3_FName,C3_MName from "+Table2Name+" where M_id="+CodeArr[Cnt] ;
 		 
		 cursorT = dbObj.rawQuery(sql, null);
		 if(cursorT.moveToFirst()){ 
			 MemId=cursorT.getInt(0);
			 finalresult=MemId+"^"+Chkval(cursorT.getString(1))+"^"+Chkval(cursorT.getString(2))+"^"+Chkval(cursorT.getString(3))+"^"+Chkval(cursorT.getString(4))+"^"+
			    		 Chkval(cursorT.getString(5))+"^"+Chkval(cursorT.getString(6))+"^"+Chkval(cursorT.getString(7))+"^"+Chkval(cursorT.getString(8))+"^"+
						 Chkval(cursorT.getString(9))+"^"+Chkval(cursorT.getString(11))+"^"+Chkval(cursorT.getString(12))+"^"+Chkval(cursorT.getString(13))+"^"+
			    		 Chkval(cursorT.getString(14))+"^"+Chkval(cursorT.getString(15))+"^"+Chkval(cursorT.getString(16))+"^"+Chkval(cursorT.getString(17))+"^"+
					     Chkval(cursorT.getString(18))+"^"+Chkval(cursorT.getString(19))+"^"+Chkval(cursorT.getString(20))+"^"+Chkval(cursorT.getString(21));

			 //imgP=cursorT.getBlob(10);
			 if(MemDir.equals("Member"))
			 {
				 finalresult=finalresult+"^"+Chkval(cursorT.getString(22))+"^"+Chkval(cursorT.getString(23))+"^"+Chkval(cursorT.getString(24))+"^"+Chkval(cursorT.getString(25))+"^"+Chkval(cursorT.getString(26));
			 }
			 else
			 {
			    String Spouse_Company=Chkval(cursorT.getString(22));
			    String Spouse_Profession=Chkval(cursorT.getString(23));
			    if(Spouse_Company.trim().length()>0)
			       AddDATA[0]="Company :  "+ Spouse_Company;
			    if(Spouse_Profession.trim().length()>0)
			       AddDATA[1]="Profession :  "+ Spouse_Profession;
			 }
		 }
		 cursorT.close();

		  
		  //Check Data is in Any "New Option Title" or Not(10-02-2017)
		  if(New_Op_Title.length()>1)
			 Check_New_Option(MemId);
		  /////////////////////////////////
		 
		 //Get Records Additional Data 1 (for Main Screen)
		 if(!Additional_Data.equals("NODATA") && Additional_Data.contains("#") && MemDir.equals("Member"))
		 {
			 String[] Arr1=Additional_Data.split("#");
			 String[] Arr2=Arr1[0].replace("^", "#").split("#");
			 sql="Select "+Arr1[1]+" from "+TableMiscName+" Where Rtype='DATA' And Memid="+MemId;
			 cursorT = dbObj.rawQuery(sql, null);
			 if(cursorT.moveToFirst()){
				   for(int i=0;i<Arr2.length;i++)
				   {
					   String data=cursorT.getString(i);
					   if(data!=null)
					   {
						   if(data.trim().length()>0)
						   {
							   AddDATA[i]=Arr2[i]+" :  "+ cursorT.getString(i);
						   }
					   }
				   }
		     }
			 cursorT.close();
		 }
		 
		 //Get Records Additional Data 2 (for PopUp Screen)
		 /*if(!Additional_Data2.equals("NODATA") && Additional_Data2.contains("#") && MemDir.equals("Member"))
		 {
			 ArrayList<String> arrList = new ArrayList<String>();
			 String[] Arr_1=Additional_Data2.split("#");
			 String[] Arr_2=Arr_1[0].replace("^", "#").split("#");
			 sql="Select "+Arr_1[1]+" from "+TableMiscName+" Where Rtype='DATA' And Memid="+MemId;
			 cursorT = dbObj.rawQuery(sql, null);
			 if(cursorT.moveToFirst()){
				   for(int i=0;i<Arr_2.length;i++)
				   {
					   String data=cursorT.getString(i);
					   if(data!=null)
					   {
						   if(data.trim().length()>0)
						   {
							   data=Arr_2[i]+" :  "+ cursorT.getString(i);
							   arrList.add(data); // Add data in Array List
						   }
					   }
				   }
		     }
			 if(arrList.size()!=0)
			 {
				 btnMoreDetails.setVisibility(View.GONE);//Display Addition Data Button
				 listAdapter = new ArrayAdapter<String>(this, R.layout.listitem_additionaldata, arrList); 
			 }
			 else
			 {
				 btnMoreDetails.setVisibility(View.GONE);//Hide Addition Data Button
				 listAdapter=null;
			 }
			 cursorT.close();
		 }
		 else
		 {
			 btnMoreDetails.setVisibility(View.GONE);//Hide Addition Data Button
			 listAdapter=null;
		 }*/

		  Close_Database();//Close DataBase
		 Fill_Main(finalresult,AddDATA); // Set Display Data of Directory after search
	   }catch(Exception ex){
   		 System.out.println(ex.getMessage());
   	   }
	}
	
	
	 //Check Member Has Spouse or Not
	/* private void CheckSpouse(int M_Id)
	 {
		 String sql="";
		 SpouseDetails="";
		 imgSpouse=null;
		 Spouse_Addition_Data=new String[10];
		 if(MemDir.equals("Member"))
			  sql="Select M_id,S_Name,M_Add1,M_Add2,M_Add3,(M_City || \" \" || M_Pin),S_Email,S_Mob,C4_Gender,S_BG,S_Pic,C3_BG,S_DOB_D,S_DOB_M,S_DOB_Y,M_MrgAnn_D,M_MrgAnn_M,M_MrgAnn_Y,C4_DOB_D,C3_Mob,C3_FName,C3_MName from "+Table2Name+" where M_id="+M_Id;
		 else
			  sql="select M_id,M_Name,M_Add1,M_Add2,M_Add3,(M_City || \" \" || M_Pin),M_Email,M_Mob,MemNo,M_BG,M_Pic,(C4_BG || \"\" || ifnull(C4_DOB_Y,'')),M_DOB_D,M_DOB_M,M_DOB_Y,M_MrgAnn_D,M_MrgAnn_M,M_MrgAnn_Y,C4_DOB_D,C4_Mob from "+Table2Name+" where M_id="+M_Id;
			   
		 cursorT = dbObj.rawQuery(sql, null);
	     if(cursorT.moveToFirst()){
			String Name=Chkval(cursorT.getString(1));
			if(Name.trim().length()>0)
			{
			   SpouseDetails=cursorT.getInt(0)+"^"+Name+"^"+Chkval(cursorT.getString(2))+"^"+Chkval(cursorT.getString(3))+"^"+Chkval(cursorT.getString(4))+"^"+
					Chkval(cursorT.getString(5))+"^"+Chkval(cursorT.getString(6))+"^"+Chkval(cursorT.getString(7))+"^"+Chkval(cursorT.getString(8))+"^"+
					Chkval(cursorT.getString(9))+"^"+Chkval(cursorT.getString(11))+"^"+Chkval(cursorT.getString(12))+"^"+Chkval(cursorT.getString(13))+"^"+
					Chkval(cursorT.getString(14))+"^"+Chkval(cursorT.getString(15))+"^"+Chkval(cursorT.getString(16))+"^"+Chkval(cursorT.getString(17))+"^"+
					Chkval(cursorT.getString(18))+"^"+Chkval(cursorT.getString(19));
				   
			   imgSpouse=cursorT.getBlob(10);//Get Spouse Image
			   if(MemDir.equals("Member"))
			   {
				  String Spouse_Company=Chkval(cursorT.getString(20));
			      String Spouse_Profession=Chkval(cursorT.getString(21));
				  if(Spouse_Company.trim().length()>0)
					  Spouse_Addition_Data[0]="Company :  "+ Spouse_Company;
				  if(Spouse_Profession.trim().length()>0)
					  Spouse_Addition_Data[1]="Profession :  "+ Spouse_Profession;
			   }
		     }
		  }
		  cursorT.close();
			      
		  //Get Records Additional Data 1 (for Main Screen) Only For Member
		  if(!Additional_Data.equals("NODATA") && Additional_Data.contains("#") && !MemDir.equals("Member"))
		  {
			String[] Arr1=Additional_Data.split("#");
			String[] Arr2=Arr1[0].replace("^", "#").split("#");
			sql="Select "+Arr1[1]+" from "+TableMiscName+" Where Rtype='DATA' And Memid="+M_Id;
			cursorT = dbObj.rawQuery(sql, null);
			if(cursorT.moveToFirst()){
				for(int i=0;i<Arr2.length;i++)
				{
				  String data=cursorT.getString(i);
				  if(data!=null)
				  {
					 if(data.trim().length()>0)
					 {
						Spouse_Addition_Data[i]=Arr2[i]+" :  "+ cursorT.getString(i);
					 }
				  }
				}
			 }
			 cursorT.close();
		   }
			 
		   if(SpouseDetails.trim().length()>0)
			   btnSpouseDetails.setVisibility(View.VISIBLE);//Display Addition Data Button
		   else
			   btnSpouseDetails.setVisibility(View.GONE);//Display Addition Data Button
	 }
	 
	 
	 //Check Member Has Spouse or Not
	 private void CheckFamily(int M_Id)
	 {
	   String sql="";
	   int familycount=0;
	   sql="Select Count(M_Id) from "+TableFamilyName+" where MemId="+M_Id;
	    cursorT = dbObj.rawQuery(sql, null);
		if(cursorT.moveToFirst()){
			familycount=cursorT.getInt(0);
		 }
		 cursorT.close();
		 if(familycount>0){
			 String ValFamilyShow="";
			 sql="Select Text1 from "+Table4Name+" where Rtype='FAMILY'";
			 cursorT = dbObj.rawQuery(sql, null);
			  if(cursorT.moveToFirst()){
				ValFamilyShow=cursorT.getString(0);
			   }
			  cursorT.close();
			  if(ValFamilyShow.equalsIgnoreCase("YES")){
				 MId_family=M_Id;
				 btnFamilyDetails.setVisibility(View.VISIBLE);//Display Addition Data Button
			  }else{
				 MId_family=0;
				 btnFamilyDetails.setVisibility(View.GONE);//Display Addition Data Button
			  }
		 }else{
			 MId_family=0;
			 btnFamilyDetails.setVisibility(View.GONE);//Display Addition Data Button
		 }
	 }
	
	 
	//Check Member is in Any Committee or Not
	private void CheckCommittee(int M_Id)
	{
	   ArrayList<String> arrList = new ArrayList<String>();
	   String sql="Select Text2,Text4 from "+Table4Name+" Where Rtype='ICAI' AND Text1='Committee' AND Num1="+M_Id+" Order by Text2";
	   cursorT = dbObj.rawQuery(sql, null);
	   while(cursorT.moveToNext()){
			String data=Chkval(cursorT.getString(0));
			String Desig=Chkval(cursorT.getString(1));
		    if(data.trim().length()>1)
			{
		       if(Desig.trim().length()>1)
		    	   data=Desig+", "+data;
			   arrList.add(data); // Add data in Array List
			}
	   }
	   if(arrList.size()!=0)
	   {
		  btnCommittee.setVisibility(View.VISIBLE);//Display Addition Data Button
		  listAdpCommitee = new ArrayAdapter<String>(this, R.layout.listitem_additionaldata, arrList); 
	   }
	   else
	   {
		   btnCommittee.setVisibility(View.GONE);//Hide Addition Data Button
		   listAdpCommitee=null;
	   }
	   cursorT.close();
	}*/
	
	
	
	//Check Data is in Any New Option title or Not (10-02-2017)
	private void Check_New_Option(int M_Id)
	{
		ArrayList<String> arrList = new ArrayList<String>();
		String sql="Select Text1 from "+TableMiscName+" Where Rtype='ADDL' AND MemId="+M_Id+" Order by Text1";
		cursorT = dbObj.rawQuery(sql, null);
		while(cursorT.moveToNext()){
			String data=Chkval(cursorT.getString(0));
		    if(data.trim().length()>1)
			{
			   arrList.add(data); // Add data in Array List
			}
		}
		if(arrList.size()!=0)
		{
			AddData_in_List(New_Op_Title,"New_Op_Title",R.drawable.rounded_tranparent_listbackgroud);//Set region
			listAdp_NewOpTitle = new ArrayAdapter<String>(this, R.layout.listitem_additionaldata, arrList);
		}

		cursorT.close();
	}
	
	
	//Fill Values Main Data Display
	public void Fill_Main(String WResult,String[] AddData)
	{
		ListObj = new ArrayList<RowEnvt>();

		s = WResult.replace("^", "##")+" ";
		temp = s.split("##");  

		String MemId=temp[0].toString();
		String Name=temp[1].toString();
		String Add1=temp[2].toString().trim();
		String Add2=temp[3].toString().trim();
		String Add3=temp[4].toString().trim();
		String Add4_City=temp[5].toString().trim();
		String Email1=temp[6].toString();
		String Mob1=temp[7].toString();
		String MemNo=temp[8].toString();
		String BloodGrp=temp[9].toString().trim();
		String Prefix_Name=temp[10].toString();
		String Dob_dd=temp[11].toString().trim();
		String Dob_mm=temp[12].toString();
		String Dob_yy=temp[13].toString();
		String MarAnni_dd=temp[14].toString().trim();
		String MarAnni_mm=temp[15].toString();
		String MarAnni_yy=temp[16].toString();
		String C4_DOD_D="Y";//temp[17].toString();//Special Condition Value
		String Website=temp[18].toString();//Website(Added 05-01-2018)
		String BusiType=temp[19].toString();//Business Type(Added 05-01-2018)
		String BusiDetails=temp[20].toString();//Business Details(Added 05-01-2018)
		
		String DOB="",Ann_Date="",Region="",Mob2="",Email2="",Phone1="",Phone2="";
		if(MemDir.equals("Member"))
		{
			Region=temp[21].toString().trim();// New Field 15-03-2016
			Mob2=temp[22].toString().trim();// New Field 16-03-2016
			Email2=temp[23].toString().trim();// New Field 16-03-2016
			Phone1=temp[24].toString().trim();// New Field 16-03-2016
			Phone2=temp[25].toString().trim();// New Field 16-03-2016
		}
		
		///Added 29-12-2016 ////
		////Special Condition C4_DOD_D (Mob1,Mob2,Land1,Land2,Email1,Email2) is Displayed or Not
		if(C4_DOD_D.equalsIgnoreCase("Y"))
		{
			Mob1="";//Mob1
			Mob2="";//Mob2
			//Email1="";//Email1
			//Email2="";//Email2
			Phone1="";//Phone1
			Phone2="";//Phone2
		}
		//////////////////////////////////////////

		Name=Prefix_Name.trim()+" "+Name.trim();
		txtName.setText(Name);//Set Name
		//AddData_in_List(Name,"Name",R.drawable.directory_user);//Set Name
		AddData_in_List(Region,"Region",R.drawable.rounded_tranparent_listbackgroud);//Set region
		AddData_in_List(MemNo,"MemNo",R.drawable.rounded_tranparent_listbackgroud);//Set Member No
		AddData_in_List(Mob1,"Mob1",R.drawable.director_mobile_icon);//Set Mobile 1
		AddData_in_List(Mob2,"Mob2",R.drawable.director_mobile_icon);//Set Mobile 2
		AddData_in_List(Phone1,"Phone1",R.drawable.directory_phone_icon);//Set Phone 1
		AddData_in_List(Phone2,"Phone2",R.drawable.directory_phone_icon);//Set Phone 1


		String Address=Add1+Add2+Add3+Add4_City;
		AddData_in_List(Address,"Address",R.drawable.location);//Set Address

		AddData_in_List(Email1,"Email1",R.drawable.directory_email_icon);//Set Email 1
		AddData_in_List(Email2,"Email2",R.drawable.directory_email_icon);//Set Email 2
		AddData_in_List(BloodGrp,"BG",R.drawable.blood2);//Set Blood Group

		//Set DOB (Updated at 07-02-2015 Tapas)
		if((Dob_dd.trim().length()==0 && Dob_mm.trim().length()==0) || (DOB_Disp==1)){
			DOB="";
		}
		else if(Dob_yy.trim().length()==0 || DOB_Disp==2){
			Dob_mm= getMonthForInt(Integer.parseInt(Dob_mm));
			DOB=Dob_dd+" "+Dob_mm;
		}
		else{
			DOB=Dob_dd+"-"+Dob_mm+"-"+Dob_yy;
		}
		AddData_in_List(DOB,"DOB",R.drawable.cake_birthday);// set DOB

		//Set Marriage Anniversary Date(Updated at 07-02-2015 Tapas)
		if(MarAnni_dd.trim().length()==0 && MarAnni_mm.trim().length()==0){
			Ann_Date="";
		}
		else if(MarAnni_yy.trim().length()==0)
		{
			MarAnni_mm= getMonthForInt(Integer.parseInt(MarAnni_mm));
			Ann_Date=MarAnni_dd+" "+MarAnni_mm;
		}
		else{
			Ann_Date=MarAnni_dd+"-"+MarAnni_mm+"-"+MarAnni_yy;
		}
		AddData_in_List(Ann_Date,"Anniversary",R.drawable.wedd_anni);// set Anniversary Date


		AddData_in_List(Website,"Website",R.drawable.website);// set Website
		AddData_in_List(BusiType,"BusiType",R.drawable.rounded_tranparent_listbackgroud);// set Business Type
		AddData_in_List(BusiDetails,"BusiDetails",R.drawable.rounded_tranparent_listbackgroud);// set Business Details


		//Set Additional Data
		AddData_in_List(AddData[0],"Addional1",R.drawable.rounded_tranparent_listbackgroud);
		AddData_in_List(AddData[1],"Addional2",R.drawable.rounded_tranparent_listbackgroud);
		AddData_in_List(AddData[2],"Addional3",R.drawable.rounded_tranparent_listbackgroud);
		AddData_in_List(AddData[3],"Addional4",R.drawable.rounded_tranparent_listbackgroud);
		AddData_in_List(AddData[4],"Addional5",R.drawable.rounded_tranparent_listbackgroud);
		AddData_in_List(AddData[5],"Addional6",R.drawable.rounded_tranparent_listbackgroud);
		AddData_in_List(AddData[6],"Addional7",R.drawable.rounded_tranparent_listbackgroud);
		AddData_in_List(AddData[7],"Addional8",R.drawable.rounded_tranparent_listbackgroud);
		AddData_in_List(AddData[8],"Addional9",R.drawable.rounded_tranparent_listbackgroud);
		AddData_in_List(AddData[9],"Addional10",R.drawable.rounded_tranparent_listbackgroud);
		////////////////////////////////

		Adapter_Directory_New adp = new Adapter_Directory_New(this, R.layout.directory_new_list_cell, ListObj);
		LV1.setAdapter(adp);
    } 

    public void filloremptyData(String str,LinearLayout ll,TextView txt){
	   if(str==null){
		  ll.setVisibility(View.GONE);
	   }else if((str!=null)&&(str.trim().length()!=0)){
		  ll.setVisibility(View.VISIBLE);
		  txt.setText(str);
	   }else{
		  ll.setVisibility(View.GONE);
	   }
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


	public void AddData_in_List(String Name,String Type,int DrawableId){
		Name=Chkval(Name);
		if((Name.length()>0)){
			RowEnvt item = new RowEnvt(Name,Type, DrawableId);
			ListObj.add(item);// Adding contact to list
		}
	}
    
    private String Chkval(String DVal)
	{
    	if((DVal==null)||(DVal.equalsIgnoreCase("null"))){
			DVal="";
		}
		return DVal.trim();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	  switch (item.getItemId()) {
    	    // action with ID action_back was selected
    	    case R.id.action_back:
    	    	back();
    	        break;
    	    default:
    	        break;
    	    }
    	return true;
    }
	
	 private void Display_Image_Ad()
		{
	    	//Rtype=Ad2 for Directory
	    	String sql ="Select Photo1 from "+Table4Name+" WHERE Rtype='Ad2'";
	    	cursorT = dbObj.rawQuery(sql, null);
			byte[] ImgAd=null;
	    	while(cursorT.moveToNext())
			{
	    	   ImgAd=cursorT.getBlob(0);
			   break;
			}
			cursorT.close();
			
			// Set Image for AD
			if(ImgAd==null)
			{
				ImgVw_Ad.setVisibility(View.GONE);
			}
			else
			{
			  Bitmap bitmap = BitmapFactory.decodeByteArray(ImgAd , 0, ImgAd.length);
			  ImgVw_Ad.setVisibility(View.VISIBLE);
			  ImgVw_Ad.setImageBitmap(bitmap);
			}
		}
	
}