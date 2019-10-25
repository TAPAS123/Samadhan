package groupmanager.samadhan;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHome extends Fragment implements View.OnClickListener {
    Button btn_event,btn_news,btn_latestupdate;
    String Str_club,Logname,Logclubid,LogclubName;

    public FragmentHome()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        Logname = this.getArguments().getString("Clt_Log");
        Logclubid = this.getArguments().getString("Clt_LogID");
        LogclubName = this.getArguments().getString("Clt_ClubName");
        Str_club = this.getArguments().getString("UserClubName");

        btn_news = (Button)rootView.findViewById(R.id.btn_news);
        btn_event = (Button)rootView.findViewById(R.id.btn_event);
        btn_latestupdate = (Button)rootView.findViewById(R.id.btn_latestupdate);

        btn_news.setOnClickListener(this);
        btn_event.setOnClickListener(this);
        btn_latestupdate.setOnClickListener(this);

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if(v == btn_latestupdate)
        {
            Intent Mintent=new Intent(getContext(),Activity_Education_Content.class);
            Mintent.putExtra("Type","LATEST");
            Mintent.putExtra("MTitle","LATEST UPDATE");
            Mintent.putExtra("Clt_Log",Logname);
            Mintent.putExtra("Clt_LogID",Logclubid);
            Mintent.putExtra("Clt_ClubName",LogclubName);
            Mintent.putExtra("UserClubName",Str_club);
            startActivity(Mintent);
        }
        else if(v == btn_event)
        {
            Bundle bundle = new Bundle();
            bundle.putString("Eventschk","1");
            bundle.putString("Clt_Log",Logname);
            bundle.putString("Clt_LogID",Logclubid);
            bundle.putString("Clt_ClubName",LogclubName);
            bundle.putString("UserClubName",Str_club);
            Fragment fragmentobj = new FragmentICSIEvents();
            fragmentobj.setArguments(bundle);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame,fragmentobj).addToBackStack(null).commit();
        }
        else if(v == btn_news)
        {
            Bundle bundle = new Bundle();
            bundle.putInt("Count",999999);
            bundle.putInt("POstion",0);
            bundle.putString("Clt_Log",Logname);
            bundle.putString("Clt_LogID",Logclubid);
            bundle.putString("Clt_ClubName",LogclubName);
            bundle.putString("UserClubName",Str_club);
            Fragment fragmentobj = new FragmentWhatsnew();
            fragmentobj.setArguments(bundle);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame,fragmentobj).addToBackStack(null).commit();
        }
    }


    private String Chk_YearSettingCCB()
    {
        String flag="N";
        SQLiteDatabase db = getActivity().openOrCreateDatabase("MDA_Club", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        String SqlQry="Select M_ID from C_"+Str_club+"_4 Where Rtype='YearWiseCCB'";
        Cursor cursorT = db.rawQuery(SqlQry, null);
        if(cursorT.getCount()>0){
            flag="Y";
        }
        cursorT.close();
        db.close();
        return flag;
    }

    ///Get Year for Commitee,Council,Branch
    private String GetCCBYear()
    {
        String CCBYear="";

        String HasCCB= Chk_YearSettingCCB();//Check Commitee,council,branch year wise setting enabled or not

        if(HasCCB.equals("Y"))
        {
            SharedPreferences sharedpreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE); // Initialise Shared Pref
            if (sharedpreferences.contains(Str_club+"_CCBYear"))
            {
                CCBYear=sharedpreferences.getString(Str_club+"_CCBYear", "");
            }

            if(CCBYear.length()==0)
            {
                SQLiteDatabase db = getActivity().openOrCreateDatabase("MDA_Club", SQLiteDatabase.CREATE_IF_NECESSARY, null);
                String SqlQry="Select Num1 from C_"+Str_club+"_4 Where Rtype='Year' order by num1 Desc";
                Cursor cursorT = db.rawQuery(SqlQry, null);
                if (cursorT.moveToFirst()) {
                    CCBYear=cursorT.getString(0);
                }
                cursorT.close();
                db.close();
            }
        }
        return CCBYear;
    }
}
