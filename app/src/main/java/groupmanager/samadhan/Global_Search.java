package groupmanager.samadhan;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Global_Search extends Fragment{


	String Log,logid,Str_user,TableMiscName,Table4Name,Additional_Data,Additional_Data2;
	EditText ET_Search_Name,ET_Search_Mob,ET_Search_MemNo,ET_Search_Addr;
	List<EditText> allEds;
	ArrayList<String> listarr,TextVal;
	String New_Op_Title="";
	TextView textView1;

	public Global_Search() {
		// Required empty public constructor
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.view_dialog_box, container, false);

		LinearLayout LL_Binocular = (LinearLayout) rootView.findViewById(R.id.llaybinosrch);
		LinearLayout LL_add = (LinearLayout) rootView.findViewById(R.id.addresssrch);
		LinearLayout LL_categ = (LinearLayout) rootView.findViewById(R.id.catecontact);
		textView1=(TextView)rootView.findViewById(R.id.textView1);
		ET_Search_Name = (EditText) rootView.findViewById(R.id.etName);
		ET_Search_Mob = (EditText) rootView.findViewById(R.id.etMobileBino);
	    ET_Search_MemNo = (EditText) rootView.findViewById(R.id.etMemberBino);
		ET_Search_Addr = (EditText) rootView.findViewById(R.id.etAddBino);
		LinearLayout LLMain=(LinearLayout)rootView.findViewById(R.id.LLMain);
		Button btnSearch= (Button) rootView.findViewById(R.id.btnSearch);
		RelativeLayout RlSearch = (RelativeLayout) rootView.findViewById(R.id.llBottom1);
		Typeface face=Typeface.createFromAsset(getContext().getAssets(), "calibri.ttf");
		btnSearch.setTypeface(face);
		textView1.setTypeface(face);
		ET_Search_Name.setTypeface(face);
		ET_Search_Mob.setTypeface(face);
		ET_Search_MemNo.setTypeface(face);
		ET_Search_Addr.setTypeface(face);
		
		LL_Binocular.setVisibility(View.VISIBLE);
		LL_add.setVisibility(View.VISIBLE);
		LL_categ.setVisibility(View.GONE);
		RlSearch.setVisibility(View.VISIBLE);
		

		Log =  this.getArguments().getString("Clt_Log");
		logid =  this.getArguments().getString("Clt_LogID");
		Str_user = this.getArguments().getString("UserClubName");
		
		TableMiscName="C_"+Str_user+"_MISC";
		Table4Name="C_"+Str_user+"_4";

		String val="",Title="",fielddata="";
		allEds = new ArrayList<EditText>();
		TextVal= new ArrayList<String>();
		
		try{
			SQLiteDatabase db = getActivity().openOrCreateDatabase("MDA_Club", SQLiteDatabase.CREATE_IF_NECESSARY, null);//Conn Open
			
			GetAdditionalData(db);//Get Additional Data
			
			Get_New_Op(db);////Added in 10-02-2017
			
			String sql="Select Add1 from "+TableMiscName+" Where Rtype='Search_Add'";//"+Title.trim()+"
			Cursor cursorT = db.rawQuery(sql, null);
			if(cursorT.moveToFirst()){
				val=cursorT.getString(0);
				if(val==null)
					val="";
			}
			cursorT.close();
			db.close();///Connection Close
			
			if(val.contains("^")){
				 String [] temp=val.split("#");
				 for(int i=0;i<temp.length;i++){
					String s= temp[i].replace("^", "#")+" ";
					String []arr=s.split("#");
					Title=arr[0].toString();
					fielddata=arr[1].toString();
					LLMain.addView(NewView(i+10,Title,fielddata));
				 }
			}	
		}catch(Exception ex){
			System.out.println(ex.getMessage());
		}
		
		
		//// BtnGlobal Search
		btnSearch.setOnClickListener(new OnClickListener()
        { 
			@Override
			public void onClick(View arg0) {
				String StrName=ET_Search_Name.getText().toString();
            	String StrMob=ET_Search_Mob.getText().toString();
            	String StrMemNo=ET_Search_MemNo.getText().toString();
            	String StrAddr=ET_Search_Addr.getText().toString();
            	
            	listarr = new ArrayList<String>();
            	for(int j=0;j<allEds.size();j++){
            	   String FieldData = allEds.get(j).getText().toString();
            	   String DValue = TextVal.get(j);
            	   if((FieldData!=null)&&(FieldData.length()!=0)){
            		   listarr.add(FieldData+"#@"+DValue); 
					}
            	}
            	int count=listarr.size();
            	
            	if((StrName.length()==0)&&(StrMob.length()==0)&&(StrMemNo.length()==0)&&(StrAddr.length()==0)&&(count==0)){
            		Toast.makeText(getContext(), "input atleast one field!", Toast.LENGTH_SHORT).show();
            	}else{
            		//waiting();
            		
            		String query=StrName+"#"+StrMob+"#"+StrMemNo+"#"+StrAddr;
            		Intent menuIntent= new Intent(getContext(),SearchDisplay.class);
            		menuIntent.putExtra("Clt_LogID",logid);
            		menuIntent.putExtra("Clt_Log",Log);
            		menuIntent.putExtra("UserClubName",Str_user);
            		menuIntent.putExtra("STRslct","2");
            		//menuIntent.putExtra("StrCriteria",StrShrdCrival);
            		menuIntent.putExtra("Qury",query);
            		menuIntent.putExtra("AddData1",Additional_Data);
	        		menuIntent.putExtra("AddData2",Additional_Data2);
	        		menuIntent.putExtra("Dir_Filter_Condition","");
	        		menuIntent.putExtra("Special_Dir_Condition","");
	        		menuIntent.putExtra("New_Op_Title",New_Op_Title);//Added in 10-02-2017
					menuIntent.putExtra("DbName","MDA_Club");//Connected Database Name
	        		
	        		if(count==0){
	        			menuIntent.putStringArrayListExtra("stock_list", null);
	        		}else{
	        			menuIntent.putStringArrayListExtra("stock_list", listarr);
	        		}
                	startActivity(menuIntent);
                	//finish();
            		//Toast.makeText(getBaseContext(),Str_etmobsrch+"  "+Str_etmemNosrch+"  "+Str_etAddsrch, 1).show();
            	}
			}
        });

		setHasOptionsMenu(true);///To Show Action Bar Menu
		return rootView;
	}

	
	
	private void GetAdditionalData(SQLiteDatabase db)
	{
		Additional_Data="NODATA"; 
		Additional_Data2="NODATA";
		    
		//Get Additional Data for MainScreen and PopupScreen
		String Qry="Select Add1,Add2 from "+TableMiscName+" Where Rtype='MASTER'";
		Cursor cursorT = db.rawQuery(Qry, null);
		while(cursorT.moveToNext())
		{
		      Additional_Data=cursorT.getString(0);
		      Additional_Data2=cursorT.getString(0);
		      if(Additional_Data==null || Additional_Data=="")
		      {
		    	Additional_Data="NODATA";
		      }
		      if(Additional_Data2==null || Additional_Data2=="")
		      {
		    	Additional_Data2="NODATA";
		      }
		      break;
		}
		cursorT.close();
	}
	
	 
	
   ///////Get New Option Caption or Title which is used to display some list in popup screen
    private void Get_New_Op(SQLiteDatabase db)
    { 
      ///////Get New Option Caption or Title which is used to display some list in popup screen
		String Qry="Select Text1 from "+Table4Name+" Where Rtype='ADDL'";
		Cursor cursorT = db.rawQuery(Qry, null);
		if(cursorT.moveToFirst())
		{
		   New_Op_Title=cursorT.getString(0);
		}
		cursorT.close();
		////////////////////////////////////////////////
		db.close(); 
  }
	
	
	
	private LinearLayout NewView(int id,String TvTitle,String DValue)
	{
		LinearLayout L1=new LinearLayout(getContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(15,5,5,5);
		L1.setLayoutParams(params);
		
		L1.setId(id);
		L1.setOrientation(LinearLayout.HORIZONTAL);
		
		//Add Textview
		/*TextView tv=new TextView(this);
		tv.setTextSize(19);
		tv.setText(TvTitle);
		L1.addView(tv);*/
		
		L1.addView(editText(id,TvTitle,DValue));
		
		return L1;
	}
	
   private EditText editText(int id,String hint,final String DValue) 
   {
	  final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
	  final EditText ET = new EditText(getContext());
	  allEds.add(ET);// Add EditText in Edittext List
	  TextVal.add(DValue);
	  ET.setLayoutParams(lparams);
      ET.setId(id);
      ET.setHint(hint);
      ET.setText("");
    
      return ET;
   }
	
	
//	public boolean onKeyDown(int keyCode, KeyEvent event)
//	{
//	   	if (keyCode == KeyEvent.KEYCODE_BACK) {
//	   		backs();
//	   	    return true;
//	   	}
//	   	return super.onKeyDown(keyCode, event);
//	}
	
	 private void DisplayMsg(String head,String body,final int ckk){
		 AlertDialog ad=new AlertDialog.Builder(getContext()).create();
		  ad.setTitle(Html.fromHtml("<font color='#E3256B'>"+head+"</font>"));
	      ad.setMessage(Html.fromHtml("<font color='#1C1CF0'>"+body+"</font>"));
		  ad.setButton("OK", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	              if(ckk==1)
	            	  backs();
	              else
	            	  dialog.dismiss();
	            }
	        });
	      ad.show();	
	}
	 
	 // To go Back
	 public void backs(){
	    getActivity().finish();
	 }

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		MenuItem item = menu.findItem(R.id.action_globalSearch);
		item.setVisible(false);///To hide Global Search Menu button
	}


}
