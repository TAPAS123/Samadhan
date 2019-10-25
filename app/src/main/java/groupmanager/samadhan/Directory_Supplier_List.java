package groupmanager.samadhan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import java.util.ArrayList;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by intel on 05-01-2018.
 */

public class Directory_Supplier_List extends AppCompatActivity {
    ArrayList<RowEnvt> ListObj;
    ListView LV1;
    TextView txthead;
    String Tab4Name,Tab2Name, Str_club,Logname,Logclubid,LogclubName,DbName;
    final Context context=this;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.educational_content1);
        LV1 = (ListView) findViewById(R.id.Lv1);
        txthead = (TextView) findViewById(R.id.txthead);

        Intent menuIntent = getIntent();
        Logname = menuIntent.getStringExtra("Clt_Log");
        Logclubid = menuIntent.getStringExtra("Clt_LogID");
        LogclubName = menuIntent.getStringExtra("Clt_ClubName");
        Str_club = menuIntent.getStringExtra("UserClubName");
        DbName=menuIntent.getStringExtra("DbName");//Connected Database Name

        txthead.setText("SUPPLIERS");
        Typeface face = Typeface.createFromAsset(getAssets(), "calibri.ttf");
        txthead.setTypeface(face);

        Tab2Name = "C_" + Str_club + "_2";
        Tab4Name = "C_" + Str_club + "_4";

        FillList();//get data from database and show in listview.


        LV1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String FilterType = ListObj.get(position).getEvtName();

                String DirName = "DIR";

                ////Special Directory Condition with DirName
                DbHandler dbhandlerObj = new DbHandler(context, Tab2Name);
                //dbhandlerObj.Chk_Filter_SavedPermanent(Tab4Name);// Saved Filter Permanent Or Temporary
                String CCBYear = "";//GetCCBYear();//Get CCB Cuurent Year
                String Special_Dir_Condition = dbhandlerObj.Special_Dir_Condition(DirName, Tab4Name, CCBYear);
                dbhandlerObj.close();//Close DbHandler
                ///////////////////////////////////////////

                Intent menuIntent = new Intent(context, Directory_New.class); // Directory Activity
                menuIntent.putExtra("Special_Dir_Condition", Special_Dir_Condition);
                menuIntent.putExtra("CFrom", "MENU");//Comes From MenuPage
                menuIntent.putExtra("FilterType", FilterType);//Set Filter
                menuIntent.putExtra("DbName", DbName);//Set DbName use to connect this database name
                menuIntent.putExtra("Clt_Log", Logname);
                menuIntent.putExtra("Clt_LogID", Logclubid);
                menuIntent.putExtra("UserClubName", Str_club);
                startActivity(menuIntent);
            }
        });

    }


    private void FillList() {
        SQLiteDatabase db;
        Cursor cursorT;
        RowEnvt item;
        ListObj = new ArrayList<RowEnvt>();
        db = openOrCreateDatabase(DbName, SQLiteDatabase.CREATE_IF_NECESSARY, null);
        String StrQuery = "Select distinct(Oth) from " + Tab2Name + " Order by Oth";
        cursorT = db.rawQuery(StrQuery, null);
        if (cursorT.moveToFirst()) {
            do {
                String s1 = ChkVal(cursorT.getString(0));//Category

                item = new RowEnvt(s1, "");
                ListObj.add(item);// Adding contact to list
            } while (cursorT.moveToNext());

            cursorT.close();
            db.close();

            Adapter_Educational_Content adp = new Adapter_Educational_Content(this, R.layout.educational_content_cell, ListObj,3);
            LV1.setAdapter(adp);

        } else {
            cursorT.close();
            db.close();
            AlertDialog ad = new AlertDialog.Builder(this).create();
            ad.setTitle(Html.fromHtml("<font color='#FF7F27'>Result</font>"));
            ad.setMessage("No record found.");
            ad.setCancelable(false);
            ad.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    backs();
                }
            });
            ad.show();
        }

    }


    private String ChkVal(String Val)
    {
        if(Val==null)
            Val="";
        return Val.trim();
    }



    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backs();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void backs(){
        finish();
    }



}