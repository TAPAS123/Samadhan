package groupmanager.samadhan;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_PastPresi extends Fragment {
    ArrayList<RowEnvt> contact_list;
    ListView listView;
    TextView TVPastpresident,TvPastsecretray;
    String Chkpast="0",StrQuery="",Tab4name,Str_user,getp_s;//Log,logid;
    String MTitle;

    public Fragment_PastPresi() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_past_presi, container, false);
        listView = (ListView)rootView.findViewById(R.id.listviewpast);
        TVPastpresident= (TextView)rootView.findViewById(R.id.Txtpastpresident);
        TvPastsecretray= (TextView)rootView.findViewById(R.id.Txtpastsceretray);
        if(Chkpast.equals("0")){
            TvPastsecretray.setVisibility(View.VISIBLE);
        }else{
            TvPastsecretray.setVisibility(View.GONE);
        }
        getp_s =  this.getArguments().getString("selectP_S");
        Str_user =  this.getArguments().getString("UserClubName");
        MTitle =  this.getArguments().getString("MTitle");
        TVPastpresident.setText(MTitle);
        Typeface face=Typeface.createFromAsset(getContext().getAssets(), "calibri.ttf");
        TVPastpresident.setTypeface(face);
        TvPastsecretray.setTypeface(face);

        Tab4name="C_"+Str_user+"_4";

        setHasOptionsMenu(true);///To Show Action Bar Menu

        if(getp_s.equals("2")){
            StrQuery="Select Text1,Text2,Text3,Text4 from "+Tab4name+" where Rtype='PASTS' Order by Num2 desc";
        }else{
            StrQuery="Select Text1,Text2,Text3,Text4 from "+Tab4name+" where Rtype='PASTP' Order by Num2 desc";
        }
        getandshowdata(StrQuery,getp_s);//get data from database and show in listview.

        TVPastpresident.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0){
                TVPastpresident.setBackgroundResource(R.drawable.boxred);
                TvPastsecretray.setBackgroundResource(R.drawable.lightorangetab);
                StrQuery="Select Text1,Text2,Text3,Text4 from "+Tab4name+" where Rtype='PASTP' Order by Num2 desc";
                getandshowdata(StrQuery,"1");//get data from database and show in listview.
            }
        });

        TvPastsecretray.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0){
                TvPastsecretray.setBackgroundResource(R.drawable.boxred);
                TVPastpresident.setBackgroundResource(R.drawable.lightorangetab);
                StrQuery="Select Text1,Text2,Text3,Text4 from "+Tab4name+" where Rtype='PASTS' Order by Num2 desc";
                getandshowdata(StrQuery,"2");//get data from database and show in listview.
            }
        });
        return  rootView;
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_globalSearch);
        item.setVisible(false);///To hide Global Search Menu button
    }


    private void getandshowdata(String sqlquery,final String chk){
        SQLiteDatabase db;
        Cursor cursorT;
        RowEnvt item;
        CustomAffil adapter;
        String name,year,mobile,email;
        contact_list = new ArrayList<RowEnvt>();
        db = getActivity().openOrCreateDatabase("MDA_Club", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        cursorT = db.rawQuery(sqlquery, null);
        if (cursorT.moveToFirst()) {
            do {
                name=cursorT.getString(0);
                year=cursorT.getString(1);
                mobile=cursorT.getString(2);
                email=cursorT.getString(3);
                if(name==null){
                    name="";
                }if(year==null){
                    year="";
                }if(mobile==null){
                    mobile="";
                }if(email==null){
                    email="";
                }

                item = new RowEnvt(year,name,mobile,email,"","");
                // Adding contact to list
                contact_list.add(item);
            } while (cursorT.moveToNext());
            cursorT.close();
            db.close();
            adapter = new CustomAffil(getContext(),R.layout.affiliationlist,contact_list);
            listView.setAdapter(adapter);
            // listView.getSelectedView().setBackgroundColor(getResources().getColor(Color.RED));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    String year=contact_list.get(position).getEvtName();
                    String name=contact_list.get(position).getEvtDesc();
                    String mob=contact_list.get(position).getEvtdate();
                    String email=contact_list.get(position).getEvtVenue();
                    Intent menuIntent= new Intent(getContext(),ShowPastPresSec.class);
                    menuIntent.putExtra("UserClubName",Str_user);
                    menuIntent.putExtra("Pyear",year);
                    menuIntent.putExtra("Pname",name);
                    menuIntent.putExtra("Pmob",mob);
                    menuIntent.putExtra("Pemail",email);
                    menuIntent.putExtra("StrChk",chk);
                    menuIntent.putExtra("MTitle",MTitle);
                    startActivity(menuIntent);
                }
            });
        }else{
            cursorT.close();
            db.close();
            AlertDialog ad=new AlertDialog.Builder(getContext()).create();
            ad.setTitle( Html.fromHtml("<font color='#FF7F27'>Result</font>"));
            ad.setMessage("No record found.");
            ad.setCancelable(false);
            ad.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
               //     Goback();
                }
            });
            ad.show();
        }

    }

}
