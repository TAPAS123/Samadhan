package groupmanager.samadhan;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentWhatsnew extends Fragment {

    TextView txtHead;
    ListView LV1;
    String sqlSearch,Table2Name,Table4Name,Log,ClubName,logid,Str_user,StrClubName,UserType="";
    int StrCount,post;
    byte[] AppLogo;
    Intent menuIntent;
    List<RowEnvt> rowItems;


    public FragmentWhatsnew() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_whatsnew, container, false);
        // Inflate the layout for this fragment
        txtHead=(TextView)rootView.findViewById(R.id.txthead);
        LV1 = (ListView) rootView.findViewById(R.id.Lv1);

        Typeface face=Typeface.createFromAsset(getContext().getAssets(), "calibri.ttf");
        txtHead.setTypeface(face);

        setHasOptionsMenu(true);///To Show Action Bar Menu


        Log = this.getArguments().getString("Clt_Log");
        logid = this.getArguments().getString("Clt_LogID");
        
        ClubName = this.getArguments().getString("Clt_ClubName");
        Str_user = this.getArguments().getString("UserClubName");
        StrCount = this.getArguments().getInt("Count", StrCount);
        post = this.getArguments().getInt("POstion", post);

//        menuIntent = getIntent();
//        Log =  menuIntent.getStringExtra("Clt_Log");
//        logid =  menuIntent.getStringExtra("Clt_LogID");
//        ClubName =  menuIntent.getStringExtra("Clt_ClubName");
//        Str_user =  menuIntent.getStringExtra("UserClubName");
//        StrCount =  menuIntent.getIntExtra("Count", StrCount);
//        post =  menuIntent.getIntExtra("POstion", post);

        Table2Name="C_"+Str_user+"_2";
        Table4Name="C_"+Str_user+"_4";

        Get_SharedPref_Values();// Get Stored Shared Pref Values of Login

        if(StrCount==999999 || StrCount==111 || StrCount==222)
        {
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

        FillListData(sqlSearch);

        //ListView Click Event
        LV1.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                String Pwd=rowItems.get(position).getEvtdate();
                String mid=rowItems.get(position).getEvtVenue();

                if(StrCount==111 || StrCount==222)
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
                    menuIntent= new Intent(getContext(),NewsMain2.class);
                    //menuIntent.putExtra("CHKg","@@@");
                    menuIntent.putExtra("MID",Pwd);
                    menuIntent.putExtra("Count", StrCount);
                    menuIntent.putExtra("Positn", position);
                }
                menuIntent.putExtra("Clt_Log",Log);
                menuIntent.putExtra("Clt_LogID",logid);
                menuIntent.putExtra("Clt_ClubName",ClubName);
                menuIntent.putExtra("UserClubName",Str_user);
                startActivity(menuIntent);

                //finish();
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
        SharedPreferences ShPref = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        if (ShPref.contains("UserType")){
            UserType=ShPref.getString("UserType", "");
        }
    }


    ////Fill List///
    private void FillListData(String sql)
    {
        SQLiteDatabase db = getActivity().openOrCreateDatabase("MDA_Club", SQLiteDatabase.CREATE_IF_NECESSARY, null);

        Cursor cursorT = db.rawQuery(sql, null);
        int RCount=cursorT.getCount();

        if(RCount==0){
            if(StrCount==999999 || StrCount==111){
                txtHead.setText("News");
            }else{
                txtHead.setText("News");
            }
        }else{
            rowItems = new ArrayList<RowEnvt>();
            RowEnvt item;
            if (cursorT.moveToFirst()) {
                do {
                    String NewsTitle=ChkVal(cursorT.getString(0));
                    String NewsDate =ChkVal(cursorT.getString(1));
                    String mid =ChkVal(cursorT.getString(2));
                    item = new RowEnvt(NewsTitle,NewsDate,mid,"");
                    rowItems.add(item);
                } while (cursorT.moveToNext());
            }
            Adapter_NewsMain Adp1 = new Adapter_NewsMain(getContext(),R.layout.newsmainlist, rowItems);
            LV1.setAdapter(Adp1);
            LV1.setSelection(post);
        }
        cursorT.close();
        db.close();
    }


    //call function for initialise blank if null is there
    private String ChkVal(String DVal)
    {
        if((DVal==null)||(DVal.equalsIgnoreCase("null"))){
            DVal="";
        }
        return DVal.trim();
    }


    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            getActivity().finish();

            return true;
        }
        return super.getActivity().onKeyDown(keyCode, event);
    }


}
