package groupmanager.samadhan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by intel on 28-12-2017.
 */

public class Directory_New extends AppCompatActivity implements SimpleGestureFilter.SimpleGestureListener,View.OnClickListener {
    TextView txtName,txtMAX;
    String s,Str_user,Log,logid,sqlSearch,finalresult,Table2Name,Table4Name,TableMiscName;
    String [] temp,Arrschcri;
    ImageView BtnNxt,BtnPrev,ImgSrchCrit;
    Intent menuIntent;
    AlertDialog ad;
    private SimpleGestureFilter detect;
    SQLiteDatabase dbObj;
    Cursor cursorT;
    int s_count,Cnt,i=0;
    Integer tempsize;
    int[] CodeArr;
    Dialog dialog;
    EditText ET_Search_Name, ET_Search_Mob,ET_Search_MemNo,ET_Search_Addr;
    LinearLayout llaydialog,llaysetting,llaySrchCrit,LLaySearchCriteriashow;
    private static final int MY_BUTTON = 9000;
    CheckBox cb;
    ImageView ImgVw_Ad;
    String Additional_Data,Additional_Data2;
    final Context context=this;
    ArrayAdapter<String> listAdp_NewOpTitle=null;
    String MemDir="Member",TableFamilyName;
    int DOB_Disp=0,CHKsrhcri=0;
    List<Spinner> allSps;
    String strSrchcrival="",StrCaptionvale="",StrTable="";
    Button BtnSrchcrit;
    LinearLayout LLMain;
    List<EditText> allEds;
    ArrayList<String> listarr,TextVal;
    String Special_Dir_Condition;
    String Dir_Filter_Condition,CFrom,DbName;
    String New_Op_Title="";

    ListView LV1;
    ArrayList<RowEnvt> ListObj;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.directory_new);
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
        Special_Dir_Condition=menuIntent.getStringExtra("Special_Dir_Condition");//Special Directory Condition with DirName
        CFrom=menuIntent.getStringExtra("CFrom");//Comes From
        DbName=menuIntent.getStringExtra("DbName");//Connected Database Name
        String FilterType=menuIntent.getStringExtra("FilterType");//FilterType

        Table2Name="C_"+Str_user+"_2";
        Table4Name="C_"+Str_user+"_4";
        TableMiscName="C_"+Str_user+"_MISC";
        TableFamilyName="C_"+Str_user+"_Family";

        allSps = new ArrayList<Spinner>();

        Get_SharedPref_Values(); // Get Shared Pref Values of MemDir(Display Member/Spouse)

        Open_Database();//Open Database
        Display_Image_Ad();// Display Ad (Advertisement)

        Dir_Filter_Condition=" AND Oth='"+FilterType+"'";//DirFilter_Condition();//Directory Filter Condional Query

        CHKsrhcri=getSearchCriteria();


        if(Special_Dir_Condition.contains("A.M_Id"))
        {
            sqlSearch=Special_Dir_Condition.replace("SELECT A.M_Id","SELECT Count(A.M_Id)");
        }
        else
        {
            //Get Total records Count
            if(MemDir.equals("Member"))
                sqlSearch="select Count(M_id) from "+Table2Name +" Where M_Name Is Not NULL "+Dir_Filter_Condition+Special_Dir_Condition;
            else
                sqlSearch="select Count(M_id) from "+Table2Name +" Where S_Name Is Not NULL AND LENGTH(S_Name)<>0 "+Dir_Filter_Condition+Special_Dir_Condition;
        }

        //System.out.println(sqlSearch);
        try{
            cursorT = dbObj.rawQuery(sqlSearch, null);
        }catch(Exception ex){
            System.out.println(ex.getMessage());
            BBack();
        }

        if (cursorT.moveToFirst()) {
            do {
                s_count=cursorT.getInt(0);
            } while (cursorT.moveToNext());
        }
        cursorT.close();
        txtMAX.setText(Cnt+1+" of "+s_count);
        if(s_count!=0){
            Cnt=0;

            if(Special_Dir_Condition.contains("A.M_Id"))
            {
                sqlSearch=Special_Dir_Condition;
            }
            else
            {
                // Get Mid from Table2
                if(MemDir.equals("Member"))
                    sqlSearch="select M_id from "+Table2Name +" Where M_Name Is Not NULL "+Dir_Filter_Condition+Special_Dir_Condition+" Order by C4_LName,M_Name";//member
                else
                    sqlSearch="select M_id from "+Table2Name +" Where S_Name Is Not NULL AND LENGTH(S_Name)<>0 "+Dir_Filter_Condition+Special_Dir_Condition+" Order by S_Name";//Spouse
            }
            cursorT = dbObj.rawQuery(sqlSearch, null);
            tempsize=cursorT.getCount();
            CodeArr=new int[tempsize];
            if (cursorT.moveToFirst()) {
                do {
                    CodeArr[i]=cursorT.getInt(0);
                    i++;
                } while (cursorT.moveToNext());
            }
            cursorT.close();


            Additional_Data="NODATA";
            Additional_Data2="NODATA";

            //Get Additional Data for MainScreen
            String Qry="Select Add1 from "+TableMiscName+" Where Rtype='MASTER'";
            cursorT = dbObj.rawQuery(Qry, null);
            while(cursorT.moveToNext())
            {
                Additional_Data=cursorT.getString(0);
                if(Additional_Data==null || Additional_Data=="")
                {
                    Additional_Data="NODATA";
                }
                break;
            }
            cursorT.close();
            //////////////////////////////

            //Get Additional Data for PopupScreen
            Qry="Select Add2 from "+TableMiscName+" Where Rtype='MASTER'";
            cursorT = dbObj.rawQuery(Qry, null);
            while(cursorT.moveToNext())
            {
                Additional_Data2=cursorT.getString(0);
                if(Additional_Data2==null || Additional_Data2=="")
                {
                    Additional_Data2="NODATA";
                }
                break;
            }
            cursorT.close();
            //////////////////////////////


            ///////Get New Option Caption or Title which is used to display some list in popup screen
            Qry="Select Text1 from "+Table4Name+" Where Rtype='ADDL'";
            cursorT = dbObj.rawQuery(Qry, null);
            if(cursorT.moveToFirst())
            {
                New_Op_Title=cursorT.getString(0);
            }
            cursorT.close();
            ////////////////////////////////////////////////

            Close_Database();//Close DataBase
            FillMainData();    // Fill Main Data Display
        }
        else
        {
            DisplayAlert("Result!","No Record found");
        }

        ActionCall();


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



        BtnPrev.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View arg0){
                Prev();
            }
        });

        BtnNxt.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View arg0){
                Next();
            }
        });

        ImgVw_Ad.setOnClickListener(new View.OnClickListener(){
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
		    		/*if(ImgAdg!=null){
	    			  menuIntent= new Intent(getBaseContext(),FullAdvertisement.class);
		    		  menuIntent.putExtra("Type","8");
		    		  menuIntent.putExtra("Clt_Log",Log);
		    		  menuIntent.putExtra("Clt_LogID",logid);
		    		  menuIntent.putExtra("Clt_ClubName",ClubName);
		    		  menuIntent.putExtra("UserClubName",Str_user);
		    		  menuIntent.putExtra("AppLogo", AppLogo);
		    		  menuIntent.putExtra("Photo1", ImgAdg);
		    	      startActivity(menuIntent);
		    	      //finish();
		    		}*/
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
        //System.out.println(MemDir);
    }




    // Display Ad in the ImageView
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent m1){
        // Call onTouchEvent of SimpleGestureFilter class
        this.detect.onTouchEvent(m1);
        return super.dispatchTouchEvent(m1);
    }


    public void Next(){
        if(Cnt+1==tempsize){
            Toast.makeText(getBaseContext(), "No Further Record", Toast.LENGTH_SHORT).show();
        }else{
            Cnt=Cnt+1;
            FillMainData();
            txtMAX.setText(Cnt+1+" of "+s_count);
        }
    }

    public void Prev(){
        if(Cnt==0){
            Toast.makeText(getBaseContext(), "No Previous Record", Toast.LENGTH_SHORT).show();
        }else{
            Cnt=Cnt-1;
            FillMainData();
            txtMAX.setText(Cnt+1+" of "+s_count);
        }
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
            //System.out.println("Sms failed");
        }
    }


    private void DisplayAlert(String title,String msg){
        AlertDialog ad=new AlertDialog.Builder(this).create();
        ad.setTitle( Html.fromHtml("<font color='#E3256B'>"+title+"</font>"));
        ad.setMessage(Html.fromHtml("<font color='#1C1CF0'>"+msg+"</font>"));
        ad.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                BBack();
            }
        });
        ad.show();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            BBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //Back
    private void BBack()
    {
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

    @Override
    public void onDoubleTap() {
        // TODO Auto-generated method stub
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
                sql="select M_id,S_Name,M_Add1,M_Add2,M_Add3,(M_City || \" \" || M_Pin),S_Email,S_Mob,C4_Gender,S_BG,S_Pic,C3_BG,S_DOB_D,S_DOB_M,S_DOB_Y,M_MrgAnn_D,M_MrgAnn_M,M_MrgAnn_Y,C4_DOB_D,M_BussNm,M_BussCate,M_MemSince_D,C3_FName,C3_MName from "+Table2Name+" where M_id="+CodeArr[Cnt];

            System.out.println("Sql:  "+sql);
            try{
                cursorT = dbObj.rawQuery(sql, null);
            }catch(Exception ex){
                System.out.println(ex.getMessage());
                BBack();
            }

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

            //Get Records Additional Data 1 (for Main Screen) Only For Member
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

            //Get Records Additional Data 2 (for PopUp Screen) Only For Member
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
                    listAdapter = new ArrayAdapter<String>(this, R.layout.listitem_additionaldata, arrList);
                }
                else
                {
                    listAdapter=null;
                }
                cursorT.close();
            }
            else
            {
                listAdapter=null;
            }*/

            Close_Database();//Close DataBase
            Fill_Main(finalresult,AddDATA); // Set Display Data of Directory
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }



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

    public void ActionCall(){
        ActionBar actionBar = getSupportActionBar();
        //setTitle("Clubmanager");
        // add the custom view to the action bar
        actionBar.setCustomView(R.layout.search_layout);

        ImageView ImgVwSearch = (ImageView) actionBar.getCustomView().findViewById(R.id.imageSerchMenu);
        ImgSrchCrit = (ImageView) actionBar.getCustomView().findViewById(R.id.imagesearchcritri);
        ImageView ImgVwFilter= (ImageView) actionBar.getCustomView().findViewById(R.id.imageSetting);

        ImgVwSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0){

                ActionBarDisp_Alert(1);
            	 /*if(CHKsrhcri==1){
                 	ImgSrchCrit.setVisibility(View.VISIBLE);
                 }else{
                 	ImgSrchCrit.setVisibility(View.GONE);
                 }*/
            }
        });

        ImgVwFilter.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0){
                ActionBarDisp_Alert(2);
            }
        });

        ImgSrchCrit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0){
                ActionBarDisp_Alert(3);
            }
        });


        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM |ActionBar.DISPLAY_SHOW_HOME);
    }

    public void ActionBarDisp_Alert(final int serchck) {
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            final FrameLayout frameView = new FrameLayout(context);
            builder.setView(frameView);
            final AlertDialog alertDialog = builder.create();
            LayoutInflater inflater = alertDialog.getLayoutInflater();

            View dialoglayout = inflater.inflate(R.layout.view_dialog_box, frameView);
            llaydialog= (LinearLayout) dialoglayout.findViewById(R.id.llViewbox);
            LinearLayout ll = new LinearLayout(this);
            llaydialog.addView(ll, new LinearLayout.LayoutParams(100, WindowManager.LayoutParams.WRAP_CONTENT));

            Button btnSearch= (Button) dialoglayout.findViewById(R.id.btnSearch);
            llaySrchCrit=(LinearLayout) dialoglayout.findViewById(R.id.linearLayoutSCrhcri);
            RelativeLayout RlSearch = (RelativeLayout) dialoglayout.findViewById(R.id.llBottom1);
            LinearLayout LLSettingMain = (LinearLayout) dialoglayout.findViewById(R.id.linearLayoutMM);

            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();
            wmlp.gravity = Gravity.TOP | Gravity.RIGHT;
            wmlp.x = 50;   //x position
            wmlp.y = 50;   //y position
            alertDialog.show();


            if(serchck==1){

                llaySrchCrit.setVisibility(View.GONE);
                RlSearch.setVisibility(View.VISIBLE);

                String val="",Title="",fielddata="";
                allEds = new ArrayList<EditText>();
                TextVal= new ArrayList<String>();

                LinearLayout LL_Binocular = (LinearLayout) dialoglayout.findViewById(R.id.llaybinosrch);
                LinearLayout LL_add = (LinearLayout) dialoglayout.findViewById(R.id.addresssrch);
                LinearLayout LL_categ = (LinearLayout) dialoglayout.findViewById(R.id.catecontact);

                LL_Binocular.setVisibility(View.VISIBLE);
                LL_add.setVisibility(View.VISIBLE);
                LL_categ.setVisibility(View.GONE);

                ET_Search_Name = (EditText) dialoglayout.findViewById(R.id.etName);
                ET_Search_Mob = (EditText) dialoglayout.findViewById(R.id.etMobileBino);
                ET_Search_MemNo = (EditText) dialoglayout.findViewById(R.id.etMemberBino);
                ET_Search_Addr = (EditText) dialoglayout.findViewById(R.id.etAddBino);
                LLMain=(LinearLayout)dialoglayout.findViewById(R.id.LLMain);

                try{
                    System.out.println("in try");
                    Open_Database();//Open Database
                    String sql="Select Add1 from "+TableMiscName+" Where Rtype='Search_Add'";//"+Title.trim()+"
                    cursorT = dbObj.rawQuery(sql, null);
                    if(cursorT.moveToFirst()){
                        val=cursorT.getString(0);
                        if(val==null)
                            val="";
                    }
                    cursorT.close();
                    Close_Database();//Close DataBase

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

            }else if(serchck==2){
                llaySrchCrit.setVisibility(View.GONE);
                LLSettingMain.setVisibility(View.VISIBLE);
                llaysetting = (LinearLayout) dialoglayout.findViewById(R.id.llayMenucheck);
                Button btnSaveFilter = (Button) dialoglayout.findViewById(R.id.btnSaveFilter);
                btnSaveFilter.setId(MY_BUTTON);
                btnSaveFilter.setOnClickListener(this);

                try{
                    String sql="";
                    if(Special_Dir_Condition.contains("A.M_Id"))
                    {
                        Special_Dir_Condition="";//Set Blank if Special_Dir_Condition contains a Query
                    }
                    sql ="select distinct(Upper(oth)) from "+ Table2Name+" where oth is not null and  length(oth) <> 0 "+Special_Dir_Condition+" Order By Upper(oth)";
                    Open_Database();//Open Database
                    cursorT = dbObj.rawQuery(sql, null);
                    i=0;
                    if(cursorT.getCount()==0){
                        alertDialog.cancel();
                        Toast.makeText(getBaseContext(), "No Setting Available", Toast.LENGTH_SHORT).show();
                    }else{
                        if (cursorT.moveToFirst()) {
                            do {
                                cb = new CheckBox(this);
                                sql="";
                                sql ="select Text1 from "+ Table4Name +" Where rtype = 'RecShow' and text1 = '"+cursorT.getString(0)+"'";
                                Cursor cursorT2 = dbObj.rawQuery(sql, null);
                                if(cursorT2.getCount()==0){
                                    cb.setText(cursorT.getString(0));
                                }else{
                                    cb.setText(cursorT.getString(0));
                                    cb.setChecked(true);
                                }
                                cursorT2.close();
                                cb.setId(i+10);
                                llaysetting.addView(cb);
                                i++;
                            } while (cursorT.moveToNext());
                        }
                    }
                    cursorT.close();
                    Close_Database();//Close DataBase
                }catch(Exception ex){
                    System.out.println(ex.toString());
                }
            }
            else if(serchck==3){
                String []sp=null;
                String caption="",value="",Tablename="";
                llaySrchCrit.setVisibility(View.VISIBLE);
                LLaySearchCriteriashow = (LinearLayout) dialoglayout.findViewById(R.id.llaySrchcriteria);
                BtnSrchcrit=(Button)dialoglayout.findViewById(R.id.butnSaveSechCrt);
                String [] tempsrch = strSrchcrival.split("#");
                for(int i=0;i<tempsrch.length; i++){
                    String s= tempsrch[i].toString().replace("^", "#")+" ";
                    sp=s.trim().split("#");
                    caption=sp[0].toString().trim();
                    value=sp[1].toString().trim();
                    Tablename=sp[2].toString().trim();
                    try{
                        String sql="";
                        int j=1;
                        sql ="select distinct("+value+") from "+"C_"+Str_user+"_"+Tablename+" Where "+value+" Is Not NULL AND LENGTH("+value+")<>0  order by "+value;
                        Open_Database();//Open Database
                        cursorT = dbObj.rawQuery(sql, null);
                        int temp_size=cursorT.getCount();
                        Arrschcri=new String[temp_size+1];
                        Arrschcri[0]="";
                        if (cursorT.moveToFirst()) {
                            do {
                                Arrschcri[j]=cursorT.getString(0);
                                j++;
                            } while (cursorT.moveToNext());
                        }
                        cursorT.close();
                        Close_Database();//Close DataBase
                    }catch(Exception ex){
                        System.out.println(ex.getMessage());
                    }

                    if(Arrschcri.length==0){
                        Arrschcri[0]="";
                    }
                    StrCaptionvale=StrCaptionvale+value+"#";
                    StrTable=StrTable+Tablename+"#";
                    LLaySearchCriteriashow.addView(NewView(i+1000,caption,Arrschcri));
                }
                //System.out.println(allSps.size());

                BtnSrchcrit.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View arg0){
		                	/*String StrF=" and M_id in (",StrM="",StrL=")",StrTotal="",StrQuery="";
		                	String []ttemp=StrCaptionvale.split("#");
		                	String []tbtemp=StrTable.split("#");
		                	for(int i=0; i < allSps.size(); i++){
		     	            	 String FieldData = allSps.get(i).getSelectedItem().toString().trim();
		     	            	 if(FieldData.length()!=0){
		     	            		StrQuery="select memid from c_"+Str_user+"_"+tbtemp[i]+" where "+ttemp[i]+" = '"+FieldData+"'";
		     	            		StrM=StrF+StrQuery+StrL;
		     	            		StrTotal=StrTotal+StrM;
		     	            		System.out.println("v "+StrTotal+" "+StrTotal.length());
		     	            	 }
		                	}
		                	if(StrTotal.length()!=0){
		                		//StrTotal = StrTotal.substring(0, StrTotal.length()-1).trim();
		                		System.out.println("fi:: "+StrTotal+" "+StrTotal.length());
		                		 editorCrit = shrdcriteria.edit(); // Edit Shared Pref critria Records
		                		 editorCrit.putString("Critval", StrTotal);
		                		 editorCrit.commit();
		                		 callitself();
	     	            	 }else{
	     	            		Toast.makeText(context, "Slelect any field", 1).show();
	     	            	 }*/
                    }
                });
            }

            btnSearch.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View arg0){

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
                        Toast.makeText(getBaseContext(), "input atleast one field!", Toast.LENGTH_SHORT).show();
                    }else{
                        alertDialog.dismiss();
                        waiting();

                        String query=StrName+"#"+StrMob+"#"+StrMemNo+"#"+StrAddr;
                        menuIntent= new Intent(getBaseContext(),SearchDisplay.class);
                        menuIntent.putExtra("Clt_LogID",logid);
                        menuIntent.putExtra("Clt_Log",Log);
                        menuIntent.putExtra("UserClubName",Str_user);
                        menuIntent.putExtra("STRslct","2");
                        //menuIntent.putExtra("StrCriteria",StrShrdCrival);
                        menuIntent.putExtra("Qury",query);
                        menuIntent.putExtra("AddData1",Additional_Data);
                        menuIntent.putExtra("AddData2",Additional_Data2);
                        menuIntent.putExtra("Dir_Filter_Condition",Dir_Filter_Condition);
                        menuIntent.putExtra("Special_Dir_Condition",Special_Dir_Condition);
                        menuIntent.putExtra("New_Op_Title",New_Op_Title);//Added in 10-02-2017
                        menuIntent.putExtra("DbName",DbName);//Connected Database Name

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
        }catch(Exception ex){
            System.out.println("msg:: "+ex.getMessage());
        }
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        //boolean bl=false;
        String sql= null;
        String aa = null;
        switch (v.getId()) {
            case MY_BUTTON:
                //if(aa==)
                Open_Database();//Open Database
                sql ="delete from "+Table4Name+" where Rtype = 'RecShow'";
                dbObj.execSQL(sql);

                for(int i=0; i<llaysetting.getChildCount(); i++) {
                    View nextChild = llaysetting.getChildAt(i);
                    if(nextChild instanceof CheckBox) {
                        CheckBox check = (CheckBox) nextChild;
                        if (check.isChecked()) {
                            aa=  check.getText().toString();
                            sql = "insert into "+Table4Name+"(Rtype,Text1) values('RecShow','"+aa+"')";
                            dbObj.execSQL(sql);
                        }
                    }
                }
                Close_Database();//Close DataBase
                Toast.makeText(getBaseContext(), "Filter Applied", Toast.LENGTH_SHORT).show();
                //callitself();
        }
    }

   /* public void callitself() {
        menuIntent= new Intent(getBaseContext(),SwipeScreen.class);
        menuIntent.putExtra("Clt_LogID",logid);
        menuIntent.putExtra("Clt_Log",Log);
        menuIntent.putExtra("UserClubName",Str_user);
        menuIntent.putExtra("Special_Dir_Condition",Special_Dir_Condition);
        menuIntent.putExtra("CFrom",CFrom);//Comes From
        startActivity(menuIntent);
        finish();
    }*/


    private LinearLayout NewView(int id,String TvTitle,String DValue)
    {
        LinearLayout L1=new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(15,5,5,5);
        L1.setLayoutParams(params);

        L1.setId(id);
        L1.setOrientation(LinearLayout.HORIZONTAL);


        L1.addView(editText(id,TvTitle,DValue));

        return L1;
    }

    private EditText editText(int id,String hint,final String DValue) {
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        final EditText ET = new EditText(this);
        allEds.add(ET);// Add EditText in Edittext List
        TextVal.add(DValue);
        ET.setLayoutParams(lparams);
        ET.setId(id);
        ET.setHint(hint);
        ET.setText("");

        return ET;
    }


    // This function is used to get Directory Filter Condition
    public String DirFilter_Condition() {
        String Result="";
        sqlSearch = "SELECT Text1 FROM "+Table4Name+" Where Rtype = 'RecShow'";
        cursorT = dbObj.rawQuery(sqlSearch, null);
        int Rcount=cursorT.getCount();
        if(Rcount!=0){
            if (cursorT.moveToFirst()) {
                do {
                    Result=Result+"'"+cursorT.getString(0)+"'"+",";
                } while (cursorT.moveToNext());
            }
            if (Result.endsWith(",")) {
                Result = Result.substring(0, Result.length() - 1);
            }
            Result=" AND Oth in ("+Result+")";
        }
        cursorT.close();
        return Result;
    }


    public int getSearchCriteria() {
        int returnval=0;
        sqlSearch = "SELECT Text2 FROM "+Table4Name+" Where Rtype='Sea_Cri'";
        cursorT = dbObj.rawQuery(sqlSearch, null);
        int Sc_count=cursorT.getCount();
        if(Sc_count>0){
            returnval=1;
            if (cursorT.moveToFirst()) {
                do {
                    strSrchcrival=cursorT.getString(0);
                } while (cursorT.moveToNext());
            }
        }else{
            returnval=0;
        }
        cursorT.close();
        return returnval;
    }



    private LinearLayout NewView(int id,String TvTitle,String[] DValue)
    {
        LinearLayout L1=new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(5,5,5,5);
        L1.setLayoutParams(params);
        L1.setId(id);
        L1.setOrientation(LinearLayout.HORIZONTAL);

        //Add Textview
        TextView tv=new TextView(this);
        tv.setTextSize(18);
        tv.setText(TvTitle);
        tv.setTextColor(Color.BLUE);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        L1.addView(tv);
        L1.addView(spinneradd(id,TvTitle,DValue));
        return L1;
    }

    private Spinner spinneradd(int id,String hint,String[] DValue) {
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        Spinner SP = new Spinner(this);
        allSps.add(SP);// Add EditText in Edittext List
        SP.setLayoutParams(lparams);
        SP.setId(id);
        SP.setLeft(5);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_dropdown_item, DValue);
        SP.setAdapter(spinnerArrayAdapter);
        return SP;
    }

    public void waiting(){
        final ProgressDialog progressDialog = ProgressDialog.show(context, "",  "Loading......");
        progressDialog.setCancelable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Do some stuff that take some time...
                    Thread.sleep(3000); // Let's wait for some time
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                progressDialog.dismiss();
            }
        }).start();
    }
}