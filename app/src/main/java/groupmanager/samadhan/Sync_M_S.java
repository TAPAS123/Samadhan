package groupmanager.samadhan;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Sync_M_S 
{
	private String ClientID,LogId,UserType,Tab2Name,Tab4Name,TableNameEvent;
	private Chkconnection chkconn;
	private boolean InternetPresent;
	private Context context;
	private Cursor cursorT;
	private WebServiceCall webcall;
	
	Sync_M_S(Context context)
	{
		this.context=context;
		Get_SharedPref_Values();//Get Values from Saved Shared Preference
		
		Tab2Name="C_"+ClientID+"_2";//Table 2
		Tab4Name="C_"+ClientID+"_4";//Table 4
		TableNameEvent="C_"+ClientID+"_Event";// Table Event where we save Event Attend or not Confirmation
		
		webcall=new WebServiceCall();//Intialise WebserviceCall Object
		chkconn=new Chkconnection();//Intialise Chkconnection Object
	}
	
	
	
	//Get Data from Saved Shared Preference
	private void Get_SharedPref_Values()
	{
		SharedPreferences ShPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
		 
		  if (ShPref.contains("clientid"))
	      {
			  ClientID=ShPref.getString("clientid", "");
	      }
	      if (ShPref.contains("cltid"))
	      {
	    	  LogId=ShPref.getString("cltid", "");
          } 
	      if (ShPref.contains("UserType"))
		  {
			  UserType=ShPref.getString("UserType", "");
		  }
	}
	

	//Sync Opinion Poll Data M-S ////
	 public void Sync_OpPoll_Data()
	 {
	  	Thread T2 = new Thread() {
	  	@Override
	  	public void run() {
	  	 try {
	  		   InternetPresent =chkconn.isConnectingToInternet(context);
	  		   if(InternetPresent==true)
	  		   {
	  			  SQLiteDatabase db = context.openOrCreateDatabase("MDA_Club", SQLiteDatabase.CREATE_IF_NECESSARY, null); 
	  				  
	  			  String SqlQry="Select Distinct OP1_ID from C_"+ClientID+"_OP3 Where Submit=1 AND SyncID=1 Order by OP1_ID";
	  			  Cursor cursorT = db.rawQuery(SqlQry, null);
	  			  String Op1Ids="";
	  			  while(cursorT.moveToNext())
	  			  {
	  				Op1Ids=Op1Ids+cursorT.getInt(0)+"#";
	  			  }
	  			  cursorT.close();
	  				  
	  			  String[] ArrOp1Id=null;
	  			  if(Op1Ids.length()>1)
	  			  {
	  				Op1Ids=Op1Ids.substring(0,Op1Ids.length()-1);
	  				ArrOp1Id=Op1Ids.split("#");
	  			  }
	  				  
	  			  if(ArrOp1Id!=null)
	  			  {
	  				 for(int i=0;i<ArrOp1Id.length;i++) 
	  				 {
	  				     String Op1_Id=ArrOp1Id[i].trim();
	  					 
	  				     SqlQry="Select OP2_ID,User_Ans,Remark from C_"+ClientID+"_OP3 Where OP1_ID="+Op1_Id+" AND Submit=1 AND SyncID=1 Order by OP2_ID";
	  				     cursorT = db.rawQuery(SqlQry, null);
	  				     String UAns,Remark,SData="",WebResult;
	  				     int Op2Id;
	  				     while(cursorT.moveToNext())
	  				     {
	  					   Op2Id=cursorT.getInt(0);
	  					   UAns=cursorT.getString(1);
	  					   Remark=cursorT.getString(2);
	  					   SData=SData+Op2Id+"^"+UAns+"^"+Remark+"@@";
	  				     }
	  				     cursorT.close();
	  				 
	  				     if(SData.length()>2)
	  				     {
	  					   SData=SData.substring(0,SData.length()-2);
	  					
	  					   String TempMS="M";
	  					   if(UserType.equals("SPOUSE")){
	  					     TempMS="S";
	  					   }
	  					
	  					   SData=LogId+"#"+TempMS+"#"+Op1_Id+"#"+SData;
	  					
	  					   WebServiceCall webcall=new WebServiceCall();
	  				       WebResult=webcall.Sync_OpinionPoll_MS(ClientID, SData);
						   if(WebResult.contains("Saved"))
						   {
							  SqlQry="Update C_"+ClientID+"_OP3 Set SyncID=0 Where OP1_ID="+Op1_Id;
	  				          db.execSQL(SqlQry);
						   }
	  				     }
	  				}
	  			  }
				  db.close();
	  		  }
	  			  
	  		}
	  		catch (Exception e) {
	  		  //System.out.println(e.getMessage()); 
	  		  e.printStackTrace();
	  		}
	  	  }
	  	};
	  	T2.start();
	 }

}
