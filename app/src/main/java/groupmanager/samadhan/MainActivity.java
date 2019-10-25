package groupmanager.samadhan;

import java.util.ArrayList;
import java.util.List;

//import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


public class MainActivity extends RuntimePermissionsActivity {

	Context context=this;
	ListView Lv;
	SharedPreferences sharedpreferences;
	Editor editor;
	String Login="";
	List<RowItem_AddGrp> rowItems_AddGrp;
	
	public static final String cLid = "clientid";
	public static final String uid = "userid"; 
	public static final String pass = "passwordKey"; 
	public static final String datetm = "DateTme"; 
	public static final String value2 = "name"; 
	public static final String value3 = "cltid"; 
	public static final String value4 = "clubname"; 
	public static final String ValueMenuList = "MenuList";
	byte[] AppLogo;
	String[] Arr_Grp_Data;
	UnCommonProperties UnComObj;
	private static final int REQUEST_PERMISSIONS = 20;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_main);
        
        UnComObj=new UnCommonProperties();// Make An Object
		String AppTitle=UnComObj.GET_AppTitle();
		
        setTitle(AppTitle);
        Lv=(ListView)findViewById(R.id.lV);

		sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        
        Get_LoginMain_Data(); // Get All Group Details
        
        if(Arr_Grp_Data!=null)
        {
           // For Multi Group Check
           if(Arr_Grp_Data.length>1)
           {
             rowItems_AddGrp = new ArrayList<RowItem_AddGrp>();
             for(int i=0;i<Arr_Grp_Data.length;i++)
             {
            	 RowItem_AddGrp item;
            	 Bitmap bitmap = null;
            	 String GrpName=Arr_Grp_Data[i].split("~")[6];
            	 String GrpName1=Arr_Grp_Data[i].split("~")[0];
                if((GrpName==null)||(GrpName.length()==0)){
                    item = new RowItem_AddGrp(GrpName1,null);
                }else{
                	DbHandler dbObj = new DbHandler(context,"");
            		AppLogo=dbObj.Get_AppLogo("C_"+GrpName1+"_4");
            		dbObj.close();
            		if(AppLogo==null){
            			bitmap=null;
            		}else{
            			bitmap = BitmapFactory.decodeByteArray(AppLogo,0, AppLogo.length);	
            		}
                	item = new RowItem_AddGrp(GrpName,bitmap);
                }
                rowItems_AddGrp.add(item);
             }
             Adapter_AddGrp adapter = new Adapter_AddGrp(context,R.layout.list_item_multi_grp, rowItems_AddGrp);
             Lv.setAdapter(adapter);
            
             // ListView Click Event//
             Lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parentAdapter, View view, int position,long id) {
                	String GrpValues=Arr_Grp_Data[position];
                	Edit_SharedPref_Values(GrpValues);
                	GoToLogin("Multi");
                }
             });
           }
           else
           {
        	  Edit_SharedPref_Values(Arr_Grp_Data[0]);
			   Chk_Required_Permissions();//Check Permission Required
           }
        }
        else
        {
			Chk_Required_Permissions();//Check Permission Required
        }
    }


	@Override
	public void onPermissionsGranted(final int requestCode)
	{
		//Toast.makeText(this, "Permissions Received.", Toast.LENGTH_LONG).show();
		GoToLogin("Single");
	}


	////Check Required Permissions for Android Verison>23/////
	public void Chk_Required_Permissions(){

		if(android.os.Build.VERSION.SDK_INT>=23) {
			String[] permissionStr={
					groupmanager.samadhan.Manifest.permission.READ_PHONE_STATE,
					groupmanager.samadhan.Manifest.permission.CALL_PHONE,
					groupmanager.samadhan.Manifest.permission.READ_EXTERNAL_STORAGE,
					groupmanager.samadhan.Manifest.permission.WRITE_EXTERNAL_STORAGE,
					groupmanager.samadhan.Manifest.permission.GET_ACCOUNTS,
					groupmanager.samadhan.Manifest.permission.WAKE_LOCK,
					groupmanager.samadhan.Manifest.permission.VIBRATE,
					groupmanager.samadhan.Manifest.permission.C2D_MESSAGE};
			MainActivity.super.requestAppPermissions(
					permissionStr,
					R.string.runtime_permissions_txt,
					REQUEST_PERMISSIONS);
		}
		else{
			GoToLogin("Single");//Move to Next activity
		}
	}



    private void Edit_SharedPref_Values(String Grp_Values) {
		editor = sharedpreferences.edit();
		String Arr1[]=Grp_Values.split("~");
		editor.putString(cLid, Arr1[0].trim());
		editor.putString(uid, Arr1[1].trim());
		editor.putString(pass, Arr1[2].trim());
		editor.putString(datetm, Arr1[3].trim());
		////////////////////////////
		editor.putString(value2, Arr1[4].trim());
		editor.putString(value3, Arr1[5].trim());
		editor.putString(value4, Arr1[6].trim());
		editor.putString(ValueMenuList, Arr1[7].trim()); 
		Login=Arr1[8].trim();
		editor.putString("Login", Login);
		editor.putString("AppSettings", Arr1[9].trim());
		editor.putString("TCount_Tab2", Arr1[10].trim());
		editor.putBoolean("CallScreen", Boolean.parseBoolean(Arr1[11].trim()));
		editor.putString("CallScreen_Position", Arr1[12].trim());
		editor.putString("Birday_Noti_Time", Arr1[13].trim());
		editor.putString("MemDir", Arr1[14].trim());
		editor.putString("UserType", Arr1[15].trim());
		if(Arr1[16].trim().length()>0)
		{
		  editor.putString("DOB_Disp", Arr1[16].trim());
		}
		else
		{
		  editor.putString("DOB_Disp", "0");
		}
		if(Arr1[18].trim().length()>0)
		{
		  editor.putString("AdminMenu", Arr1[18].trim());
		}
		else
		{
		  editor.putString("AdminMenu", "");
		}
		editor.putString("ChkSync",Arr1[19].trim());//Check Sync Required Val
		editor.putString("ChkSync_OneTime","1");//Check Sync OneTime Display
		editor.commit();
	 }
    
    
    private void GoToLogin(String User)
    {
    	Intent intent=null;
    	if(Login.equals("Guest"))
    		intent= new Intent(this,Guest_Registration.class);
    	else
    		intent= new Intent(this,Login.class);
    	
        intent.putExtra("AddGrp", "NO");
        intent.putExtra("User", User);
		startActivity(intent);
	    finish();
    }
    
    

    
    // Get All Group Details from LoginMain Table
    private void Get_LoginMain_Data()
    {
    	SQLiteDatabase db = openOrCreateDatabase("MDA_Club", SQLiteDatabase.CREATE_IF_NECESSARY, null);

		db.execSQL("CREATE TABLE IF NOT EXISTS LoginMain(M_ID INTEGER PRIMARY KEY,ClientID Text,UID Text,PWD Text,DDateTime Text,LogName Text,LogId Text,ClubName Text,MenuList Text,Login Text,Appsettings Text,Tab2Count INTEGER,setting_Callscrn Text,setting_Callscrn_posi Text,setting_Birday_noti_time Text,setting_MemDir Text,setting_extra_1 Text,setting_extra_2 Text,setting_extra_3 Text,setting_extra_4 Text,setting_extra_5 Text,setting_extra_6 Text,setting_extra_7 Text,setting_extra_8 Text,setting_extra_9 Text,setting_extra_10 Text)");

    	String SqlQry="Select * from LoginMain";
    	Cursor cursorT = db.rawQuery(SqlQry, null);
    	int RCount=cursorT.getCount();
    	if(RCount>0)
    	{
    	   Arr_Grp_Data=new String[RCount];
    	   int i=0;	
		   while(cursorT.moveToNext())
		   {
			 String Group_Data=ChkVal(cursorT.getString(1))+"~"+ChkVal(cursorT.getString(2))+"~"+ChkVal(cursorT.getString(3))+"~"+
					 ChkVal(cursorT.getString(4))+"~"+ChkVal(cursorT.getString(5))+"~"+ChkVal(cursorT.getString(6))+"~"+
					 ChkVal(cursorT.getString(7))+"~"+ChkVal(cursorT.getString(8))+"~"+ChkVal(cursorT.getString(9))+"~"+
					 ChkVal(cursorT.getString(10))+"~"+ChkVal(cursorT.getString(11))+"~"+ChkVal(cursorT.getString(12))+"~"+
					 ChkVal(cursorT.getString(13))+"~"+ChkVal(cursorT.getString(14))+"~"+ChkVal(cursorT.getString(15))+"~"+
					 ChkVal(cursorT.getString(16))+"~"+ChkVal(cursorT.getString(17))+"~"+ChkVal(cursorT.getString(18))+"~"+
					 ChkVal(cursorT.getString(19))+"~"+ChkVal(cursorT.getString(20))+" ";
			 Arr_Grp_Data[i] =Group_Data;
			 i++;
		  }
    	}
    	else
    	{
    		Arr_Grp_Data=null;
    	}
		cursorT.close();
		db.close();
    }
    
    
    private String ChkVal(String DVal)
	{
		if((DVal==null)||(DVal.equalsIgnoreCase("null")))
		{
			DVal="";
		}
		return DVal;
	}
  
}
