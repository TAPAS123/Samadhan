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
 * Created by intel on 27-12-2017.
 */

public class Educational_Content extends Fragment {
    ArrayList<RowEnvt> ListObj;
    ListView LV1;
    TextView txthead;
    String Tab4name, Str_user;
    String MTitle,Type;

    public Educational_Content() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.educational_content1, container, false);
        LV1 = (ListView) rootView.findViewById(R.id.Lv1);
        txthead = (TextView) rootView.findViewById(R.id.txthead);

        Type = this.getArguments().getString("Type");
        Str_user = this.getArguments().getString("UserClubName");
        MTitle = this.getArguments().getString("MTitle");

        txthead.setText(MTitle);
        Typeface face = Typeface.createFromAsset(getContext().getAssets(), "calibri.ttf");
        txthead.setTypeface(face);

        Tab4name = "C_" + Str_user + "_4";

        setHasOptionsMenu(true);///To Show Action Bar Menu

        FillList();//get data from database and show in listview.


        LV1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String txt1 = ListObj.get(position).getEvtDesc();
                Intent menuIntent = new Intent(getContext(), Educational_Content2.class);
                menuIntent.putExtra("UserClubName", Str_user);
                menuIntent.putExtra("Data", txt1);
                menuIntent.putExtra("MTitle", MTitle);
                startActivity(menuIntent);
            }
        });

        return rootView;
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_globalSearch);
        item.setVisible(false);///To hide Global Search Menu button
    }


    private void FillList() {
        SQLiteDatabase db;
        Cursor cursorT;
        RowEnvt item;
        ListObj = new ArrayList<RowEnvt>();
        db = getActivity().openOrCreateDatabase("MDA_Club", SQLiteDatabase.CREATE_IF_NECESSARY, null);

        String Qry = "";
        if (Type.equals("VAC")) {
            Qry = "Select Text2,Text4,Text5,Text6,Text3 from " + Tab4name + " where Rtype='VAC' Order by Num3 Desc,Text2";
        } else {
            Qry = "Select Text2,Text4,Text5,Text6,Text3 from " + Tab4name + " where Rtype='EDU' AND Text1='" + Type + "' Order by Num3 Desc,Text2";
        }

        cursorT = db.rawQuery(Qry, null);
        if (cursorT.moveToFirst()) {
            do {
                String s1 = ChkVal(cursorT.getString(0));//Title
                String s2 = ChkVal(cursorT.getString(1));//Video File Name(Main)
                String s3 = ChkVal(cursorT.getString(2));//Pdf File Name
                String s4 = ChkVal(cursorT.getString(3));//PPS(Powerpoint) File Name
                String s5 = ChkVal(cursorT.getString(4));//Video File Name(Demo) for GuestUser Only

                s2=s2+"##"+s3+"##"+s4+"##"+s5+"##";

                item = new RowEnvt(s1, s2);
                ListObj.add(item);// Adding contact to list
            } while (cursorT.moveToNext());

            cursorT.close();
            db.close();

            Adapter_Educational_Content adp = new Adapter_Educational_Content(getContext(), R.layout.educational_content_cell, ListObj,1);
            LV1.setAdapter(adp);

        } else {
            cursorT.close();
            db.close();
            AlertDialog ad = new AlertDialog.Builder(getContext()).create();
            ad.setTitle(Html.fromHtml("<font color='#FF7F27'>Result</font>"));
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


    private String ChkVal(String Val)
    {
        if(Val==null)
            Val="";
        return Val.trim();
    }

}