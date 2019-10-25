package groupmanager.samadhan;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_OpinionPoll extends Fragment implements View.OnClickListener,AdapterView.OnItemClickListener {
    Button livebtn,pastbtn;
    String ClientId,LogId,MTitle,ComeFrom;
    ListView lvmaintitle;
    ArrayList<RowItem_OpPoll_Main> arrayListTitle;
    TextView tvnoresultmsg;
    String StrCurdate="";


    public Fragment_OpinionPoll() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_opinion_poll, container, false);
        // Inflate the layout for this fragment
        TextView txtHead=(TextView) rootView.findViewById(R.id.txtHead);
        livebtn = (Button) rootView.findViewById(R.id.livebtn);
        pastbtn = (Button) rootView.findViewById(R.id.pastbtn);
        lvmaintitle = (ListView) rootView.findViewById(R.id.lvmaintitle);
        tvnoresultmsg = (TextView) rootView.findViewById(R.id.tvnoresultmsg);
        livebtn.setOnClickListener(this);
        pastbtn.setOnClickListener(this);
        lvmaintitle.setOnItemClickListener(this);
        tvnoresultmsg.setVisibility(View.GONE);

        setHasOptionsMenu(true);///To Show Action Bar Menu

        MTitle=this.getArguments().getString("MTitle");
        ComeFrom=this.getArguments().getString("ComeFrom");
        LogId =  this.getArguments().getString("Clt_LogID");
        ClientId =  this.getArguments().getString("UserClubName");

        txtHead.setText(MTitle);
        Typeface face=Typeface.createFromAsset(getContext().getAssets(), "calibri.ttf");
        txtHead.setTypeface(face);
        livebtn.setTypeface(face);
        pastbtn.setTypeface(face);
        tvnoresultmsg.setTypeface(face);

        ///Get Current date ///
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date CurDt = new Date();
        StrCurdate = df.format(CurDt);// Current Date in yyyy-MM-dd HH:mm:ss format

        //currdatediff = datedifference();////Get Date Difference from Currant date and 01/01/2001

        arrayListTitle = new ArrayList<RowItem_OpPoll_Main>();

        fillList_LivePast("LIVE");
        return rootView;
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_globalSearch);
        item.setVisible(false);///To hide Global Search Menu button
    }


    private void fillList_LivePast(String tabType) {

        arrayListTitle.clear();

        String Qry="";
        int[] Arr_OP1_Ids=null;

        SQLiteDatabase db = getActivity().openOrCreateDatabase("MDA_Club", SQLiteDatabase.CREATE_IF_NECESSARY, null);

        if(tabType.equals("LIVE"))
        {
            Qry = "Select Distinct OP1_ID From C_"+ClientId+"_OP3 Order By OP1_ID";
            Cursor cursor1 = db.rawQuery(Qry,null);
            Arr_OP1_Ids=new int[cursor1.getCount()];
            int i=0;
            while(cursor1.moveToNext())
            {
                Arr_OP1_Ids[i]= cursor1.getInt(0);
                i++;
            }
        }

        if(tabType.equals("LIVE"))
            Qry="Select M_ID,OP_Name,Co_Type,OP_From,OP_To,Time_Req,Time_Remains,U_Ans from C_"+ClientId+"_OP1 Where (OP_From <='"+StrCurdate+"' AND Op_To >='"+StrCurdate+"') AND Op_Publish=1";
        else
            Qry="Select M_ID,OP_Name,Co_Type,OP_From,OP_To,Time_Req,Time_Remains,U_Ans from C_"+ClientId+"_OP1 Where Op_To < '"+StrCurdate+"' AND Op_Publish=1";

        Cursor cursor = db.rawQuery(Qry,null);
        cursor.moveToFirst();
        if(cursor.getCount()>0)
        {
            tvnoresultmsg.setVisibility(View.GONE);
            lvmaintitle.setVisibility(View.VISIBLE);
            do
            {
                int M_ID = cursor.getInt(0);
                String OP_Name = chkVal(cursor.getString(1));
                String Co_Type = chkVal(cursor.getString(2));//OpinionPoll Or Quiz
                String Date_From=chkVal(cursor.getString(3));//Date From
                String Date_To=chkVal(cursor.getString(4));//Date To
                String DT_FromTo=DateTime_FromTo(Date_From,Date_To);
                int Time_Req=cursor.getInt(5);
                int Time_Remains =cursor.getInt(6);
                int U_Ans =cursor.getInt(7);

                boolean ChkNewPoll=false;
                if(tabType.equals("LIVE"))
                {
                    if(Arr_OP1_Ids!=null)
                    {
                        for(int i=0;i<Arr_OP1_Ids.length;i++)
                        {
                            ChkNewPoll=true;
                            if(M_ID==Arr_OP1_Ids[i])
                            {
                                ChkNewPoll=false;
                                break;
                            }
                        }
                    }
                }

                arrayListTitle.add(new RowItem_OpPoll_Main(M_ID,OP_Name,tabType,Co_Type,DT_FromTo,Time_Req,Time_Remains,U_Ans,ChkNewPoll));
            }while (cursor.moveToNext());
            lvmaintitle.setAdapter(new Adapter_OpPoll_Main(getContext(),R.layout.opinionpoll_mainquestionlistrow,arrayListTitle));
        }
        else
        {
            tvnoresultmsg.setVisibility(View.VISIBLE);
            lvmaintitle.setVisibility(View.GONE);
            lvmaintitle.setAdapter(new Adapter_OpPoll_Main(getContext(),R.layout.opinionpoll_mainquestionlistrow,arrayListTitle));
            tvnoresultmsg.setText("No data available");
        }
    }


    private int datedifference() {

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        ///Get Current date ///
        Date CurDt = new Date();
        String currdate = df.format(CurDt);

        String fromdate = "01/01/2001";
        String todate = currdate;
        long diffms = 0;
        try {

            Date startDate = df.parse(fromdate);   //Convert to Date
            Calendar c1 = Calendar.getInstance();
            c1.setTime(startDate);  //Change to Calendar Date

            Date endDate = df.parse(todate); //Convert to Date
            Calendar c2 = Calendar.getInstance();
            c2.setTime(endDate);    //Change to Calendar Date

            long ms1 = c1.getTimeInMillis(); //get Time in milli seconds
            long ms2 = c2.getTimeInMillis();
            diffms = ms2 - ms1;   //get difference in milli seconds

        } catch (ParseException e) {
            e.printStackTrace();
        }
        int diffindays = (int) (diffms / (24 * 60 * 60 * 1000));         //Find number of days by dividing the mili seconds

        ////Include the End Day also
        diffindays=diffindays+1;

        return diffindays;
    }



    @Override
    public void onClick(View v) {
        if(v == livebtn)
        {
            livebtn.setBackgroundResource(R.drawable.tabblue);
            pastbtn.setBackgroundResource(R.drawable.btn_tab);
            fillList_LivePast("LIVE");
        }
        else if(v == pastbtn)
        {
            livebtn.setBackgroundResource(R.drawable.btn_tab);
            pastbtn.setBackgroundResource(R.drawable.tabblue);
            fillList_LivePast("PAST");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int Mid = arrayListTitle.get(position).Mid;
        String title = arrayListTitle.get(position).title;
        String TabType = arrayListTitle.get(position).Type;
        String RType = arrayListTitle.get(position).RType;
        int TimerTime=arrayListTitle.get(position).Time_Req;
        int Time_Remains=arrayListTitle.get(position).Time_Remains;
        int U_Ans=arrayListTitle.get(position).U_Ans;

        Intent intent=null;
        if(ComeFrom.equals("1")) {
            intent = new Intent(getContext(),OpinionPoll_QuestionAnswer.class);
        }

        intent.putExtra("Mid",Mid);
        intent.putExtra("Title",title);
        intent.putExtra("TabType",TabType);
        intent.putExtra("MTitle",MTitle);
        intent.putExtra("RType",RType);
        intent.putExtra("U_Ans",U_Ans);
        intent.putExtra("TimerTime",TimerTime);
        intent.putExtra("Time_Remains",Time_Remains);
        intent.putExtra("Clt_LogID",LogId);
        intent.putExtra("UserClubName",ClientId);
        startActivity(intent);

    }



    private String chkVal(String str) {
        if(str == null) {
            str = "";
        }
        return str;
    }





    ////Convert DateTime FromTo in Specific Format
    private String DateTime_FromTo(String DFrom,String DTo)
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy (hh:mm a)");

        Date startDT=null,EndDT=null;

        try {
            startDT = df.parse(DFrom);//Convert to Date
            EndDT = df.parse(DTo);   //Convert to Date
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }   //Convert to Date


        DFrom="Start: "+df1.format(startDT);//New formated Start DateTime
        DTo="End  : "+df1.format(EndDT);//New formated End DateTime

        return DFrom+"\n"+DTo;
    }

}
