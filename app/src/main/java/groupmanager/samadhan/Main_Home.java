package groupmanager.samadhan;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.android.gcm.GCMRegistrar;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Main_Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String ClientID,UID,Logname,Logclubid,LogclubName,MenuList,Str_IEMI,Tab2Name,Tab4Name,TabMiscName,SyncImpData,TabFamilyName,ChkSync_OneTime,TabOpinion1,TabOpinion2,TableNameEvent,ChkSync="";
    TelephonyManager tm;
    AlertDialog ad;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    WebServiceCall webcall;
    String[] temp;
    Thread networkThread;
    boolean InternetPresent = false;
    Chkconnection chkconn;
    SQLiteDatabase dbObj;
    int s_count;
    Context context=this;
    SQLiteDatabase db;
    ProgressDialog Progsdial;
    String Full_AdditionalData_UpdatePro="",Webevent="",bPic,packageName="";
    String Tab6Name,t2name,t2mob,t2add1,t2add2,t2add3,t2city,t2email,t2bg,t2day,t2mon,t2year,UserType,Ann_D,Ann_M,Ann_Y,Temp_M_S;
    byte[] imgP,t2pic=null;
    int T2mid;

    ///PUSH NOTIFICATION////////////////////////
    String SharePre_GCMRegId="";//For Shared Preference
    Controller aController;
    AsyncTask<Void, Void, Void> mRegisterTask;// Asyntask
    //////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Get_SharedPref_Values();// Get Stored Shared Pref Values of Login

        ad=new AlertDialog.Builder(this).create();
        webcall=new WebServiceCall();//Intialise WebserviceCall Object
        chkconn=new Chkconnection();
        tm =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        Str_IEMI = tm.getDeviceId();

        Tab2Name="C_"+ClientID+"_2";
        Tab4Name="C_"+ClientID+"_4";
        TabMiscName="C_"+ClientID+"_MISC";
        TabFamilyName="C_"+ClientID+"_Family";
        Tab6Name="C_"+ClientID+"_6";
        TableNameEvent="C_"+ClientID+"_Event";
        TabOpinion1="C_"+ClientID+"_OP1";//Table Opinion Poll 1
        TabOpinion2="C_"+ClientID+"_OP2";//Table Opinion Poll 2

        ///// PUSH NOTIFICATION REGISTRATION/////
        if(SharePre_GCMRegId.length()==0){
            RegisteredPushNotificationGCM(); // Registered Push Gcm
            //UN_RegisteredPushNotificationGCM(); // UnRegistered Push Gcm
        }
        /////////////////////////////////////////

        Chk_Sync_AppUpdate();//Check Sync And App Update

        /// SYNC MOBILE TO SERVER(M-S)
        Sync_M_S ObjsyncMS=new Sync_M_S(context);
        //ObjsyncMS.Sync_Add_News();// Sync M-S Add_News
        //ObjsyncMS.Sync_Add_Events();// Sync M-S Add_Events
        //ObjsyncMS.Sync_ReadNewsEvent("Both");// Sync M-S ReadNewsEvent
        //ObjsyncMS.Sync_EventAttend_Or_Not_Confirmation();// Sync (M-S) Event Attended or Not
        ObjsyncMS.Sync_OpPoll_Data();//Sync (M-S) Opinion Poll added on (13-04-2017)
        ///////////////////////////////

        /////Copy PreStored DataBase(MDA_Club_Saved) in local database directory from assests folder

        int RCount=ChkHasData_InNewPreStoredDatabase();
        if(RCount==0){
            ImportDatabase_from_Assests();///Import database from assests to local directory
        }
        Create_All_Tables_DatabaseSaved();///Create some extras Tables in MDA_Club_Saved

        ///////////////////////////////////////////////////////////////////////////////////////////

        Bundle bundle = new Bundle();
        bundle.putString("Clt_Log",Logname);
        bundle.putString("Clt_LogID",Logclubid);
        bundle.putString("Clt_ClubName",LogclubName);
        bundle.putString("UserClubName",ClientID);
        Fragment fragmentobj = new FragmentTabs();
        fragmentobj.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame,fragmentobj).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
//        View navHeaderView= navigationView.inflateHeaderView(R.layout.nav_header_main);
        View headerView = navigationView.getHeaderView(0);
//        tvappname= (TextView) headerView.findViewById(R.id.tvappname);
//        tvappname.setText("ASI new");

    }

//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }
private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;


@Override
public void onBackPressed() {
    FragmentManager manager = getSupportFragmentManager();
    if (manager.getBackStackEntryCount() > 0 ) {
        manager.popBackStack();
    }
    else if(mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
    {
            super.onBackPressed();
            return;
    }
    else
    {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
            mBackPressed = System.currentTimeMillis();
    }

}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_global_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_globalSearch)
        {
            Bundle bundle = new Bundle();
            bundle.putString("Clt_Log",Logname);
            bundle.putString("Clt_LogID",Logclubid);
            bundle.putString("UserClubName",ClientID);
            Fragment fragmentobj = new Global_Search();
            fragmentobj.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame,fragmentobj).addToBackStack(null).commit();
//            Intent menuIntent= new Intent(getBaseContext(),Global_Search.class); // Global Search Activity
//            menuIntent.putExtra("Clt_Log",Logname);
//            menuIntent.putExtra("Clt_LogID",Logclubid);
//            menuIntent.putExtra("UserClubName",ClientID);
//            startActivity(menuIntent);
            //finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void Get_SharedPref_Values()
    {
        sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        if (sharedpreferences.contains("clientid"))
        {
            ClientID=sharedpreferences.getString("clientid", "");
        }
        if (sharedpreferences.contains("userid"))
        {
            UID=sharedpreferences.getString("userid", "");
        }
        if (sharedpreferences.contains("name"))
        {
            Logname=sharedpreferences.getString("name", "");
        }
        if (sharedpreferences.contains("cltid"))
        {
            Logclubid=sharedpreferences.getString("cltid", "");
        }
        if (sharedpreferences.contains("clubname"))
        {
            LogclubName=sharedpreferences.getString("clubname", "");
        }
        if (sharedpreferences.contains("MenuList"))
        {
            MenuList=sharedpreferences.getString("MenuList", "");
        }
        /*if (sharedpreferences.contains("TCount_Tab2"))
        {
            String Tab2Count=sharedpreferences.getString("TCount_Tab2", "");// Tab2Count is the Total Records of Table 2
            TCount_Tab2=Integer.parseInt(Tab2Count); // Here Tab2Count Takes Only Integer Value
        }*/
        if (sharedpreferences.contains("UserType"))
        {
            UserType=sharedpreferences.getString("UserType", "");
            System.out.println("member: "+UserType);
        }
        if (sharedpreferences.contains("SharePre_GCMRegId"))
        {
            SharePre_GCMRegId=sharedpreferences.getString("SharePre_GCMRegId", "");
        }
        if (sharedpreferences.contains("ChkSync"))
        {
            ChkSync=sharedpreferences.getString("ChkSync", "");
        }
        if (sharedpreferences.contains("ChkSync_OneTime"))
        {
            ChkSync_OneTime=sharedpreferences.getString("ChkSync_OneTime", "");
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home)
        {
            Bundle bundle = new Bundle();
            bundle.putString("Clt_Log",Logname);
            bundle.putString("Clt_LogID",Logclubid);
            bundle.putString("Clt_ClubName",LogclubName);
            bundle.putString("UserClubName",ClientID);

            Fragment fragmentobj = new FragmentTabs();
            fragmentobj.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame,fragmentobj).commit();
        }
        else if(id==R.id.nav_About)
        {
            Bundle bundle = new Bundle();
            bundle.putString("Clt_Log",Logname);
            bundle.putString("Clt_LogID",Logclubid);
            bundle.putString("UserClubName",ClientID);
            Fragment fragmentobj = new Fragment_Aboutus();
            fragmentobj.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame,fragmentobj).addToBackStack(null).commit();
        }
        else if(id==R.id.nav_dir_executive || id==R.id.nav_dir_professional || id==R.id.nav_dir_corporate || id==R.id.nav_dir_experts )
        {
            String FilterType="";
            if(id==R.id.nav_dir_executive)
            {
                FilterType="Executive";
            }
            else if(id==R.id.nav_dir_professional )
            {
                FilterType="Professional";
            }
            else if(id==R.id.nav_dir_corporate )
            {
                FilterType="Corporate";
            }
            else if(id==R.id.nav_dir_experts)
            {
                FilterType="Experts";
            }

            String DirName = "DIR";
            String DbName="MDA_Club";///Connected Database Name

            ////Special Directory Condition with DirName
            DbHandler dbhandlerObj = new DbHandler(context, Tab2Name);
            //dbhandlerObj.Chk_Filter_SavedPermanent(Tab4Name);// Saved Filter Permanent Or Temporary
            String CCBYear = "";//GetCCBYear();//Get CCB Cuurent Year
            String Special_Dir_Condition = dbhandlerObj.Special_Dir_Condition(DirName, Tab4Name, CCBYear);
            dbhandlerObj.close();//Close DbHandler
            ///////////////////////////////////////////

            Intent menuIntent = new Intent(getBaseContext(), Directory_New.class); // Directory Activity
            menuIntent.putExtra("Special_Dir_Condition", Special_Dir_Condition);
            menuIntent.putExtra("CFrom", "MENU");//Comes From MenuPage
            menuIntent.putExtra("FilterType", FilterType);//Set Filter
            menuIntent.putExtra("DbName", DbName);//Set DbName use to connect this database name
            menuIntent.putExtra("Clt_Log", Logname);
            menuIntent.putExtra("Clt_LogID", Logclubid);
            menuIntent.putExtra("UserClubName", ClientID);
            startActivity(menuIntent);
        }
        else if(id==R.id.nav_dir_suppliers)
        {
            String DbName="MDA_Club_Saved";///Connected Database Name

            Intent Mintent=new Intent(getBaseContext(),Directory_Supplier_List.class);
            Mintent.putExtra("Clt_Log",Logname);
            Mintent.putExtra("Clt_LogID",Logclubid);
            Mintent.putExtra("Clt_ClubName",LogclubName);
            Mintent.putExtra("UserClubName",ClientID);
            Mintent.putExtra("DbName", DbName);//Set DbName use to connect this database name
            startActivity(Mintent);

        }
        else if (id == R.id.nav_news)
        {
            Bundle bundle = new Bundle();
            bundle.putInt("Count",999999);
            bundle.putInt("POstion",0);
            bundle.putString("Clt_Log",Logname);
            bundle.putString("Clt_LogID",Logclubid);
            bundle.putString("Clt_ClubName",LogclubName);
            bundle.putString("UserClubName",ClientID);
            Fragment fragmentobj = new FragmentWhatsnew();
            fragmentobj.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame,fragmentobj).addToBackStack(null).commit();
        }
        else if (id == R.id.nav_events)
        {
            Bundle bundle = new Bundle();
            bundle.putString("Eventschk","1");
            bundle.putString("Clt_Log",Logname);
            bundle.putString("Clt_LogID",Logclubid);
            bundle.putString("Clt_ClubName",LogclubName);
            bundle.putString("UserClubName",ClientID);
            Fragment fragmentobj = new FragmentICSIEvents();
            fragmentobj.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame,fragmentobj).addToBackStack(null).commit();
        }
        else if (id == R.id.nav_industrial || id == R.id.nav_professional || id == R.id.nav_technical || id == R.id.nav_personal
                || id == R.id.nav_startups || id == R.id.nav_small_busi || id == R.id.nav_dir_producer_comp || id == R.id.nav_ad_vac ||
                id == R.id.nav_SmallBusiMain)
        {
            String Type="";
            String MTitle=item.getTitle().toString();
            if(id == R.id.nav_industrial)
                Type="INDUSTRIAL";
            if(id == R.id.nav_professional)
                Type="PROFESSIONAL";
            if(id == R.id.nav_technical)
                Type="TECHNICAL";
            if(id == R.id.nav_personal)
                Type="PERSONAL";
            if(id == R.id.nav_startups)
                Type="STARTUP";
            if(id == R.id.nav_small_busi)
                Type="SMALLBUSINESS";
            if(id == R.id.nav_dir_producer_comp)
                Type="PRODUCER";
            if(id == R.id.nav_ad_vac)
                Type="VAC";
            if(id == R.id.nav_SmallBusiMain)
                Type="SMALLBUSINESSMAIN";

            Bundle bundle = new Bundle();
            bundle.putString("Type",Type);
            bundle.putString("MTitle",MTitle);
            bundle.putString("Clt_Log",Logname);
            bundle.putString("Clt_LogID",Logclubid);
            bundle.putString("Clt_ClubName",LogclubName);
            bundle.putString("UserClubName",ClientID);
            Fragment fragmentobj = new Educational_Content();
            fragmentobj.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame,fragmentobj).addToBackStack(null).commit();
        }

        else if(id==R.id.nav_opinionpoll)
        {
            Bundle bundle = new Bundle();
            bundle.putString("MTitle","Opinion Poll");
            bundle.putString("ComeFrom","1");
            bundle.putString("Clt_Log",Logname);
            bundle.putString("Clt_LogID",Logclubid);
            bundle.putString("UserClubName",ClientID);
            Fragment fragmentobj = new Fragment_OpinionPoll();
            fragmentobj.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame,fragmentobj).addToBackStack(null).commit();
        }

        else if(id==R.id.nav_Gallery)
        {
            Bundle bundle = new Bundle();
            bundle.putString("NEType","Event");
            bundle.putString("Clt_Log",Logname);
            bundle.putString("Clt_LogID",Logclubid);
            bundle.putString("UserClubName",ClientID);
            Fragment fragmentobj = new Fragment_Gallery();
            fragmentobj.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame,fragmentobj).addToBackStack(null).commit();
        }
        else if(id==R.id.nav_globalsearch)
        {
            Bundle bundle = new Bundle();
            bundle.putString("Clt_Log",Logname);
            bundle.putString("Clt_LogID",Logclubid);
            bundle.putString("UserClubName",ClientID);
            Fragment fragmentobj = new Global_Search();
            fragmentobj.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame,fragmentobj).addToBackStack(null).commit();
        }
        else if(id==R.id.nav_suggestion)
        {
            Bundle bundle = new Bundle();
            bundle.putString("Clt_Log",Logname);
            bundle.putString("Clt_LogID",Logclubid);
            bundle.putString("Clt_ClubName",LogclubName);
            bundle.putString("UserClubName",ClientID);
            bundle.putString("PType","SUGG");//Sugeestion
            Fragment fragmentobj = new FragmentSuggestion();
            fragmentobj.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame,fragmentobj).addToBackStack(null).commit();
        }
        else if(id==R.id.nav_live_stream)
        {
            Bundle bundle = new Bundle();
            bundle.putString("Clt_Log",Logname);
            bundle.putString("Clt_LogID",Logclubid);
            bundle.putString("Clt_ClubName",LogclubName);
            bundle.putString("UserClubName",ClientID);
            Fragment fragmentobj = new Fragment_live_stream();
            fragmentobj.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame,fragmentobj).addToBackStack(null).commit();
        }
        else if(id==R.id.nav_Helpline)
        {
            Bundle bundle = new Bundle();
            bundle.putString("Clt_Log",Logname);
            bundle.putString("Clt_LogID",Logclubid);
            bundle.putString("Clt_ClubName",LogclubName);
            bundle.putString("UserClubName",ClientID);
            Fragment fragmentobj = new FragmentHelpline();
            fragmentobj.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame,fragmentobj).addToBackStack(null).commit();
        }
        else if(id==R.id.nav_ContactUs)
        {
            Bundle bundle = new Bundle();
            bundle.putString("Clt_Log",Logname);
            bundle.putString("Clt_LogID",Logclubid);
            bundle.putString("UserClubName",ClientID);
            Fragment fragmentobj = new ContactUs();
            fragmentobj.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame,fragmentobj).addToBackStack(null).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    //Get Sync Prerequisite
    public void Get_Sync_RData()
    {
                try {
                    int Maxid2 = 0,Minsycid2 = 0,Maxid4 = 0,s_count4 = 0,Minsycid4 = 0,MinSyncDT2=0,MinSyncDT4=0;
                    int MaxidMisc=0,MinSyncidMisc=0,s_countMisc=0,MinSyncDTMisc=0;
                    int MaxidFamily=0,MinSyncidFamily=0,s_countFamily=0,MinSyncDTFamily=0;
                    int MaxidOpinion1=0,MinSyncidOpinion1=0,s_countOpinion1=0,MinSyncDTOpinion1=0;
                    int MaxidOpinion2=0,MinSyncidOpinion2=0,s_countOpinion2=0,MinSyncDTOpinion2=0;

                    SQLiteDatabase db = openOrCreateDatabase("MDA_Club", SQLiteDatabase.CREATE_IF_NECESSARY, null);

                    //Table2/////////////
                    String sqlSearch = "select max(m_id),count(m_id),min(Syncid),min(SyncDT) from "+Tab2Name;
                    Cursor cursorT = db.rawQuery(sqlSearch, null);
                    while(cursorT.moveToNext())
                    {
                        Maxid2 =cursorT.getInt(0);
                        s_count=cursorT.getInt(1);
                        Minsycid2=cursorT.getInt(2);
                        MinSyncDT2=cursorT.getInt(3);
                        break;
                    }
                    //System.out.println(s_count);
                    cursorT.close();

                    //Table4/////////////
                    sqlSearch = "select max(m_id),count(m_id),min(Syncid),min(SyncDT) from "+Tab4Name;
                    cursorT = db.rawQuery(sqlSearch, null);
                    while(cursorT.moveToNext())
                    {
                        Maxid4=cursorT.getInt(0);
                        s_count4 =cursorT.getInt(1);
                        Minsycid4=cursorT.getInt(2);
                        MinSyncDT4=cursorT.getInt(3);
                        break;
                    }
                    cursorT.close();

                    //Table MISC/////////////
                    sqlSearch = "select max(m_id),count(m_id),min(Syncid),min(SyncDT) from "+TabMiscName;
                    cursorT = db.rawQuery(sqlSearch, null);
                    while(cursorT.moveToNext())
                    {
                        MaxidMisc=cursorT.getInt(0);
                        s_countMisc =cursorT.getInt(1);
                        MinSyncidMisc=cursorT.getInt(2);
                        MinSyncDTMisc=cursorT.getInt(3);
                        break;
                    }
                    cursorT.close();

                    //Table Family/////////////
                    sqlSearch = "select max(m_id),count(m_id),min(Syncid),min(SyncDT) from "+TabFamilyName;
                    cursorT = db.rawQuery(sqlSearch, null);
                    while(cursorT.moveToNext())
                    {
                        MaxidFamily=cursorT.getInt(0);
                        s_countFamily =cursorT.getInt(1);
                        MinSyncidFamily=cursorT.getInt(2);
                        MinSyncDTFamily=cursorT.getInt(3);
                        break;
                    }
                    cursorT.close();

                    //Table Opinion Poll 1/////////////
                    sqlSearch = "select max(m_id),count(m_id),min(Syncid),min(SyncDT) from "+TabOpinion1;
                    cursorT = db.rawQuery(sqlSearch, null);
                    while(cursorT.moveToNext())
                    {
                        MaxidOpinion1=cursorT.getInt(0);
                        s_countOpinion1 =cursorT.getInt(1);
                        MinSyncidOpinion1=cursorT.getInt(2);
                        MinSyncDTOpinion1=cursorT.getInt(3);
                        break;
                    }
                    cursorT.close();

                    //Table Opinion Poll 2 /////////////
                    sqlSearch = "select max(m_id),count(m_id),min(Syncid),min(SyncDT) from "+TabOpinion2;
                    cursorT = db.rawQuery(sqlSearch, null);
                    while(cursorT.moveToNext())
                    {
                        MaxidOpinion2=cursorT.getInt(0);
                        s_countOpinion2 =cursorT.getInt(1);
                        MinSyncidOpinion2=cursorT.getInt(2);
                        MinSyncDTOpinion2=cursorT.getInt(3);
                        break;
                    }
                    cursorT.close();

                    db.close();//Close Db

                    SyncImpData=Maxid2+"#"+s_count+"#"+Minsycid2+"#"+MinSyncDT2+"#"+
                            Maxid4+"#"+s_count4+"#"+Minsycid4+"#"+MinSyncDT4+"#"+
                            MaxidMisc+"#"+s_countMisc+"#"+MinSyncidMisc+"#"+MinSyncDTMisc+"#"+
                            MaxidFamily+"#"+s_countFamily+"#"+MinSyncidFamily+"#"+MinSyncDTFamily+"#"+
                            MaxidOpinion1+"#"+s_countOpinion1+"#"+MinSyncidOpinion1+"#"+MinSyncDTOpinion1+"#"+
                            MaxidOpinion2+"#"+s_countOpinion2+"#"+MinSyncidOpinion2+"#"+MinSyncDTOpinion2;

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }



    private void Chk_Sync_AppUpdate()
    {
        /////Check Sync Is Required Or Not
        InternetPresent = chkconn.isConnectingToInternet(context);
        if(InternetPresent==true )
        {
            String StrChkSync=Chk_Sync_Required();
            if((ChkSync.length()!=0 && !ChkSync.equals(StrChkSync)) || StrChkSync.length()==0){
                Sync();//Go To Symc
            }

            ///Check App Updation is Required or Not
            Check_AppStore_Version();
            ///////////////////////////////////////
        }
        /////////////////////////////////
    }



    private void ImportDatabase_from_Assests() {

        try {
            copyDatabase();
        } catch (IOException e) {
            throw new RuntimeException("Error creating source database", e);
        }

    }

    private void copyDatabase() throws IOException {

        //File data = Environment.getDataDirectory();
        String pkg="groupmanager.samadhan";
        String DB_NAME="MDA_Club_Saved.db";
        String DB_PATH =Environment.getDataDirectory()+ "/data/"+pkg+"/databases/";

        //Open your local db as the input stream
        final InputStream myInput = getAssets().open(DB_NAME);

        // Path to the just created empty db
        final String outFileName = DB_PATH + "MDA_Club_Saved";

        //Open the empty db as the output stream
        final OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        final byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }


    //Create All Tables(Create DataBase and Tables Of Authorised ClientID)
    private void Create_All_Tables_DatabaseSaved()
    {
        ////New  Database for precreated sqllite database
        SQLiteDatabase db = openOrCreateDatabase("MDA_Club_Saved", SQLiteDatabase.CREATE_IF_NECESSARY, null);

        //Create Table 4
        db.execSQL("CREATE TABLE IF NOT EXISTS "+Tab4Name+" (M_ID INTEGER PRIMARY KEY,Rtype Text,Text1 Text,Text2 Text,Text3 Text,Date1 Text,Date2 Text,Date3 Text,Date1_1 INTEGER,Date2_1 INTEGER,Date3_1 INTEGER," +
                "Num1 INTEGER,Num2 INTEGER,Num3 INTEGER,Add1 TEXT,Photo1 BLOB,Text4 Text,Text5 Text,Text6 Text,Text7 Text,Text8 Text,Add2 Text,SYNCID Int,SyncDT INTEGER,COND1 Text,COND2 Text,COND3 Text,COND4 Text,COND5 Text,COND6 Text,COND7 Text)");

        //Create Table MISC
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TabMiscName+" (M_ID INTEGER PRIMARY KEY,Rtype Text,MemId INTEGER,"+
                "Text1 Text,Text2 Text,Text3 Text,Text4 Text,Text5 Text,Text6 Text,Text7 Text,Text8 Text,Text9 Text,"+
                "Text10 Text,Text11 Text,Text12 Text,Text13 Text,Text14 Text,Text15 Text,Text16 Text,Text17 Text,Text18 Text,Text19 Text,"+
                "Text20 Text,Text21 Text,Text22 Text,Text23 Text,Text24 Text,Text25 Text,Text26 Text,Text27 Text,Text28 Text,Text29 Text,"+
                "Text30 Text,Text31 Text,Text32 Text,Text33 Text,Text34 Text,Text35 Text,Text36 Text,Text37 Text,Text38 Text,Text39 Text,"+
                "Text40 Text,Text41 Text,Text42 Text,Text43 Text,Text44 Text,Text45 Text,Text46 Text,Text47 Text,Text48 Text,Text49 Text,Text50 Text,"+
                "Add1 Text,Add2 Text,Add3 Text,Add4 Text,Add5 Text,SYNCID INTEGER,SyncDT INTEGER)");

        ///////////////////////////////////////////////////

        db.close();//Database Close
    }


    /// Check Database(MDA_Club_Saved) Table 2 has some records
    public int ChkHasData_InNewPreStoredDatabase()
    {
        int s_count = 0;
        try {
            SQLiteDatabase db = openOrCreateDatabase("MDA_Club_Saved", SQLiteDatabase.CREATE_IF_NECESSARY, null);

            //Table2/////////////
            String sqlSearch = "select count(m_id) from "+Tab2Name;
            Cursor cursorT = db.rawQuery(sqlSearch, null);
            while(cursorT.moveToNext())
            {
                s_count=cursorT.getInt(0);
                break;
            }
            //System.out.println(s_count);
            cursorT.close();

            db.close();//Close Db

        } catch (Exception e) {
            e.printStackTrace();
        }

        return s_count;

    }


    protected void progressdial()
    {
        Progsdial = new ProgressDialog(this, R.style.MyTheme);
        Progsdial.setMessage("Please Wait....");
        Progsdial.setIndeterminate(true);
        Progsdial.setCancelable(false);
        Progsdial.getWindow().setGravity(Gravity.DISPLAY_CLIP_VERTICAL);
        Progsdial.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Progsdial.show();
    }

    //// Check Sync Is Required or Not
    private String Chk_Sync_Required()
    {
        String V1="";
        String sqlSearch="Select setting_extra_5 from LoginMain Where ClientID='"+ClientID.trim()+"' AND UID='"+ UID.trim()+"'";
        SQLiteDatabase db = openOrCreateDatabase("MDA_Club", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        Cursor cursorT = db.rawQuery(sqlSearch, null);
        if(cursorT.moveToFirst())
        {
            V1 =ChkVal(cursorT.getString(0));
        }
        cursorT.close();
        db.close();

        return V1.trim();
    }




    private void Sync()
    {
        Get_Sync_RData();//Get SyncImpData

        Intent Mintent=new Intent(this,ClubSync.class);
        Mintent.putExtra("StrTT",SyncImpData); // All Tables Count,MaxMId,MinSyncId
        Mintent.putExtra("Clt_Log",Logname);
        Mintent.putExtra("Clt_LogID",Logclubid);
        Mintent.putExtra("Clt_ClubName",LogclubName);
        Mintent.putExtra("UserClubName",ClientID);
        startActivity(Mintent);
    }



    //call function for initialise blank if null is there
    private String ChkVal(String DVal)
    {
        if((DVal==null)||(DVal.equalsIgnoreCase("null"))){
            DVal="";
        }
        return DVal.trim();
    }




    ////Check App Update Required Or Not
    private void Check_AppStore_Version()
    {
        networkThread = new Thread() {
            @Override
            public void run() {
                try {
                    UnCommonProperties objuncm=new UnCommonProperties();
                    packageName = objuncm.GET_PackageName();
                    Webevent=webcall.Get_PlayStore_version(packageName);

                    runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                            String currentVersion="";
                            try {
                                currentVersion = context.getPackageManager().getPackageInfo(packageName, 0).versionName;
                            } catch (PackageManager.NameNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            String LatestVersion=currentVersion;

                            if(!Webevent.contains("Error")){
                                LatestVersion=Webevent;//Get Latest Version from App Store
                            }

                            if(!currentVersion.equals(LatestVersion))
                            {
                                //Toast.makeText(getApplicationContext(),"New update available " + LatestVersion + " from playstore ",Toast.LENGTH_LONG).show();
                                AlertDialog.Builder AdBuilder = new AlertDialog.Builder(context);
                                AdBuilder.setMessage(Html.fromHtml("<font color='#E3256B'>New update available. <br/>Do you want to update the app ?</font>"));
                                AdBuilder
                                        .setPositiveButton("Update",new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                dialog.dismiss();
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                //Try Google play
                                                intent.setData(Uri.parse("market://details?id="+packageName));
                                                startActivity(intent);
                                            }
                                        })
                                        .setNegativeButton("Remind Later",new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                dialog.dismiss();
                                            }
                                        });

                                AdBuilder.show();
                            }
                        }
                    });
                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        networkThread.start();
    }



    //// Set Push Notification Registered IN GCM /////////////////
    public void RegisteredPushNotificationGCM()
    {
        //Get Global Controller Class object (see application tag in AndroidManifest.xml)
        aController = (Controller) getApplicationContext();

        // Check if Internet present
        if (!aController.isConnectingToInternet()) {

            // Internet Connection is not present
            //aController.showAlertDialog(this,"Internet Connection Error",
            //        "Please connect to Internet connection", false);
            // stop executing code by return
            return;
        }

        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);

        // Make sure the manifest permissions was properly set
        GCMRegistrar.checkManifest(this);

        // Register custom Broadcast receiver to show messages on activity
        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                Config.DISPLAY_MESSAGE_ACTION));


        // Get GCM registration id
        final String regId = GCMRegistrar.getRegistrationId(this);

        // Check if regid already presents
        if (regId.equals("")) {

            // Register with GCM
            try{
                GCMRegistrar.register(this, Config.GOOGLE_SENDER_ID);
            }
            catch(Exception ex){
                Log.e("GCMRegister Error", "> " + ex.getMessage());
            }

        } else {

            // Device is already registered on GCM Server
            if (GCMRegistrar.isRegisteredOnServer(this)) {

                // Skips registration.
                Log.e("GCMRegistered", "> Already registered with GCM Server");

            } else {

                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.

                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {

                        // Register on our server
                        // On server creates a new user
                        aController.Reg_UnReg_GCM(context,regId,"Y");
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }

                };

                // execute AsyncTask
                mRegisterTask.execute(null, null, null);
            }
        }
    }


    ////Set Push Notification UnRegistered IN GCM /////////////////
    public void UN_RegisteredPushNotificationGCM() {
        GCMRegistrar.unregister(this);
    }


    // Create a Custom broadcast receiver to show msg on screen for PUSH NOTIFICATION
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String newMessage = intent.getExtras().getString(Config.EXTRA_MESSAGE);

            // Waking up mobile if it is sleeping
            aController.acquireWakeLock(getApplicationContext());

            // Display message on the screen
            //lblMessage.append(newMessage + "");

            Toast.makeText(getApplicationContext(),
                    "Got Message: " + newMessage,Toast.LENGTH_LONG).show();

            // Releasing wake lock
            aController.releaseWakeLock();
        }
    };


    @Override
    protected void onDestroy() {
        // Cancel AsyncTask
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        try {
            // Unregister Custom Broadcast Receiver
            unregisterReceiver(mHandleMessageReceiver);

            //Clear internal resources.
            GCMRegistrar.onDestroy(this);

        } catch (Exception e) {
            //Log.e("UnRegister Receiver Error", "> ");
        }
        super.onDestroy();
    }

}

