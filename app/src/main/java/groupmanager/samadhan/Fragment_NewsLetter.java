package groupmanager.samadhan;


import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_NewsLetter extends Fragment {
    ExpandableListView expandablelistview;
    ExpandableListAdapter expandableListAdapter;
    List<RowEnvt> expandableListHeader;
    HashMap<RowEnvt, List<RowEnvt>> expandableListChild;
    ArrayList<RowEnvt>  obj1;
    SQLiteDatabase db;
    Adapter_NewsLetter exadapter;
    String qry,qry2;
    String ClientId,MTitle,Rtype;
    int countrow = 0;
    ImageView imgvw;


    public Fragment_NewsLetter() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_news_letter, container, false);
        expandablelistview = (ExpandableListView) rootView.findViewById(R.id.expandablelistview);
        TextView txtHead = (TextView)rootView.findViewById(R.id.txtHead);
     //   imgvw = (ImageView)rootView.findViewById(R.id.imgview);

        setHasOptionsMenu(true);///To Show Action Bar Menu

        MTitle = this.getArguments().getString("MTitle");
        Rtype = this.getArguments().getString("Rtype");
        ClientId = this.getArguments().getString("UserClubName");

        txtHead.setText(MTitle);
        Typeface face=Typeface.createFromAsset(getContext().getAssets(), "calibri.ttf");
        txtHead.setTypeface(face);

        expandableListHeader = new ArrayList<RowEnvt>();
        expandableListChild = new HashMap<RowEnvt, List<RowEnvt>>();
        obj1 = new ArrayList<RowEnvt>();
        countrow = 0;

        db = getActivity().openOrCreateDatabase("MDA_Club", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        qry = "select DISTINCT Text1 from C_"+ClientId+"_4 where Rtype = '"+Rtype+"' order by Num1 Desc" ;
        Cursor cursor1 = db.rawQuery(qry,null);
        if(cursor1.moveToFirst())
        {
            do {
                String year = cursor1.getString(0);
                qry2 = "select Text2,Text3 from C_"+ClientId+"_4 where Text1 = '"+year+"' AND Rtype = '"+Rtype+"'";
                 Cursor cursor2 = db.rawQuery(qry2,null);
                if(cursor2.moveToFirst())
                {
                    do {
                        String url = cursor2.getString(0);
                        String month = cursor2.getString(1);
                        obj1.add(new RowEnvt(month,url));
                    } while (cursor2.moveToNext());
                }

                if(Rtype.equals("Membership")) {
                    year="";
                }
                expandableListHeader.add(new RowEnvt(year, ""));
                expandableListChild.put(expandableListHeader.get(countrow), obj1);
                obj1 = new ArrayList<RowEnvt>();
                countrow++;
            }while (cursor1.moveToNext());
            exadapter = new Adapter_NewsLetter(getContext(),expandableListHeader,expandableListChild);
            expandableListAdapter = exadapter;
            expandablelistview.setAdapter(expandableListAdapter);
            expandablelistview.expandGroup(0);
        }
        else
        {
           // AlertDisplay("No Data","No Record found !");
        }

        expandablelistview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                /*if(chkpdfreader()) {
                	RowEnvt rowItem = (RowEnvt) exadapter.getChild(groupPosition, childPosition);
                    String pdf_url = rowItem.EvtDesc;
                    //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdf_url));
                    //startActivity(browserIntent);

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setDataAndType(Uri.parse(pdf_url), "application/pdf");
                    startActivity(i);
                }
                else
                {
                    AlertDisplay("PDF Reader issue","PDF Reader not available !");
                }*/

                try {

                    RowEnvt rowItem = (RowEnvt) exadapter.getChild(groupPosition, childPosition);
                    String pdf_url = rowItem.EvtDesc;
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_VIEW);
//                    Uri uri = Uri.fromFile(file);
//                    intent.setDataAndType(uri, "application/pdf");
//                    v.getContext().startActivity(intent);
                    Intent i;
                    i = new Intent(Intent.ACTION_VIEW);
                    i.setDataAndType(Uri.parse(pdf_url), "application/pdf");
                    getContext().startActivity(i);

                } catch (ActivityNotFoundException e) {
                    AlertDisplay("PDF Reader issue","No Application Available to view this file type !");
                }

                return true;
            }
        });
        return  rootView;
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_globalSearch);
        item.setVisible(false);///To hide Global Search Menu button
    }


    public boolean chkpdfreader() {
        PackageManager packageManager = getContext().getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        if (packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    private void AlertDisplay(String head,String body)
    {
        AlertDialog ad=new AlertDialog.Builder(getContext()).create();
        ad.setTitle( Html.fromHtml("<font color='#E3256B'>"+head+"</font>"));
        ad.setMessage(Html.fromHtml("<font color='#1C1CF0'>"+body+"</font>"));
        ad.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ad.show();
    }




}




