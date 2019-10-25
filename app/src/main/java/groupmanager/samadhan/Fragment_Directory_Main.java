package groupmanager.samadhan;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Directory_Main extends Fragment implements View.OnClickListener{
    Button btn_executive,btn_professional,btn_corporate,btn_experts,btn_suppliers;
    String Str_club,Logname,Logclubid,LogclubName;



    public Fragment_Directory_Main() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_directory_main, container, false);

        Logname = this.getArguments().getString("Clt_Log");
        Logclubid = this.getArguments().getString("Clt_LogID");
        LogclubName = this.getArguments().getString("Clt_ClubName");
        Str_club = this.getArguments().getString("UserClubName");

        btn_executive = (Button)rootView.findViewById(R.id.btn_executive);
        btn_professional = (Button)rootView.findViewById(R.id.btn_professional);
        btn_corporate = (Button)rootView.findViewById(R.id.btn_corporate);
        btn_experts = (Button)rootView.findViewById(R.id.btn_experts);
        btn_suppliers = (Button)rootView.findViewById(R.id.btn_suppliers);

        btn_executive.setOnClickListener(this);
        btn_professional.setOnClickListener(this);
        btn_corporate.setOnClickListener(this);
        btn_experts.setOnClickListener(this);
        btn_suppliers.setOnClickListener(this);

        Typeface face=Typeface.createFromAsset(getContext().getAssets(), "calibri.ttf");
        btn_executive.setTypeface(face);
        btn_professional.setTypeface(face);
        btn_corporate.setTypeface(face);
        btn_experts.setTypeface(face);
        btn_suppliers.setTypeface(face);

        setHasOptionsMenu(true);///To Show Action Bar Menu
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onClick(View v) {

        boolean flag=false;
        String FilterType="";
        if(v ==btn_executive )
        {
            FilterType="Executive";
        }
        else if(v == btn_professional)
        {
            FilterType="Professional";
        }
        else if(v == btn_corporate)
        {
            FilterType="Corporate";
        }
        else if(v == btn_experts)
        {
            FilterType="Experts";
        }
        else if(v == btn_suppliers)
        {
            flag=true;
        }

        if(!flag) {
            String Tab2Name = "C_" + Str_club + "_2";
            String Tab4Name = "C_" + Str_club + "_4";

            String DirName = "DIR";
            String DbName="MDA_Club";///Connected Database Name

            ////Special Directory Condition with DirName
            DbHandler dbhandlerObj = new DbHandler(getContext(), Tab2Name);
            //dbhandlerObj.Chk_Filter_SavedPermanent(Tab4Name);// Saved Filter Permanent Or Temporary
            String CCBYear = "";//GetCCBYear();//Get CCB Cuurent Year
            String Special_Dir_Condition = dbhandlerObj.Special_Dir_Condition(DirName, Tab4Name, CCBYear);
            dbhandlerObj.close();//Close DbHandler
            ///////////////////////////////////////////

            Intent menuIntent = new Intent(getContext(), Directory_New.class); // Directory Activity
            menuIntent.putExtra("Special_Dir_Condition", Special_Dir_Condition);
            menuIntent.putExtra("CFrom", "MENU");//Comes From MenuPage
            menuIntent.putExtra("FilterType", FilterType);//Set Filter
            menuIntent.putExtra("DbName", DbName);//Set DbName use to connect this database name
            menuIntent.putExtra("Clt_Log", Logname);
            menuIntent.putExtra("Clt_LogID", Logclubid);
            menuIntent.putExtra("UserClubName", Str_club);
            startActivity(menuIntent);
        }
        else
        {

            /////Sync Table 2  for  mda_club_saved database
            /*String SyncImpData=Get_Sync_RData();//Get SyncImpData

            Intent Mintent=new Intent(getContext(),sync_test.class);
            Mintent.putExtra("StrTT",SyncImpData); // All Tables Count,MaxMId,MinSyncId
            Mintent.putExtra("Clt_Log",Logname);
            Mintent.putExtra("Clt_LogID",Logclubid);
            Mintent.putExtra("Clt_ClubName",LogclubName);
            Mintent.putExtra("UserClubName",Str_club);
            startActivity(Mintent);*/
            /////////////////////////////////////////////


            ////Export/import Database///
            //exportDB();
            ///////////////////////////


            String DbName="MDA_Club_Saved";///Connected Database Name

            Intent Mintent=new Intent(getContext(),Directory_Supplier_List.class);
            Mintent.putExtra("Clt_Log",Logname);
            Mintent.putExtra("Clt_LogID",Logclubid);
            Mintent.putExtra("Clt_ClubName",LogclubName);
            Mintent.putExtra("UserClubName",Str_club);
            Mintent.putExtra("DbName", DbName);//Set DbName use to connect this database name
            startActivity(Mintent);

        }
    }



    //Get Sync Prerequisite
    public String Get_Sync_RData()
    {
        String Result="";
        try {
            int Maxid2 = 0,Minsycid2 = 0,s_count = 0,MinSyncDT2=0;

            SQLiteDatabase db = getActivity().openOrCreateDatabase("MDA_Club_Saved", SQLiteDatabase.CREATE_IF_NECESSARY, null);

            //Table2/////////////
            String Tab2Name = "C_" + Str_club + "_2";
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

            db.close();//Close Db

            Result=Maxid2+"#"+s_count+"#"+Minsycid2+"#"+MinSyncDT2;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Result;

    }




    //This Function is used to Export Database
    private void exportDB(){
        try {
            String pkg="groupmanager.samadhan";
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {

                /////EXPORT DATA////////////
                String currentDBPath = "/data/"+pkg+"/databases/MDA_Club_Saved";
                String backupDBPath = "/Download/MDA_Club_Saved.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);
                ///////////////////////////////////////////

                /////IMPORT DATA///////////////////////
		        	/*String currentDBPath = "/Download/MDA_Club_Saved.db";
		            String backupDBPath = "/data/"+pkg+"/databases/MDA_Club_Saved";
		            File currentDB = new File(sd, currentDBPath);
		            File backupDB = new File(data, backupDBPath);*/
                ///////////////////////////

                //System.out.println(currentDB.toString()+"     "+backupDB.toString());
                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    System.out.println(src.size());
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    System.out.println("DB Exported!");
                    Toast.makeText(getContext(), "DB Exported!", Toast.LENGTH_LONG).show();
                }else{
                    System.out.println(2);
                }
            }
            System.out.println(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_globalSearch);
        item.setVisible(false);///To hide Global Search Menu button
    }


}
