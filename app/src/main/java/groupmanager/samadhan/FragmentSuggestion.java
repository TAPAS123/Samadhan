package groupmanager.samadhan;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSuggestion extends Fragment {

    String Str_club,Logname,Logclubid,LogclubName;
    Button btnsug_submit;
    EditText edtsugtitle,edtsugdesc;
    String title="",desc="",Str_IEMI="",webResponse="";
    int flag =0;
    boolean InternetPresent = false;
    Chkconnection chkconn;
    TelephonyManager tm;
    WebServiceCall webcall;
    ProgressDialog Progsdial;
    Thread networkThread;


    public FragmentSuggestion() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_suggestion, container, false);

        ImageView imgLogo = (ImageView) rootView.findViewById(R.id.imageView);
        btnsug_submit = (Button)rootView.findViewById(R.id.btnsug_submit);
        edtsugtitle = (EditText) rootView.findViewById(R.id.edtsugtitle);
        TextView txtDesc = (TextView) rootView.findViewById(R.id.textView2);
        edtsugdesc = (EditText) rootView.findViewById(R.id.edtsugdesc);

        Logname = this.getArguments().getString("Clt_Log");
        Logclubid = this.getArguments().getString("Clt_LogID");
        LogclubName = this.getArguments().getString("Clt_ClubName");
        Str_club = this.getArguments().getString("UserClubName");
        String PType = this.getArguments().getString("PType");

        if(!PType.equals("SUGG")){
            imgLogo.setImageResource(R.drawable.raise_query);
            txtDesc.setText("Query");
        }

        chkconn=new Chkconnection();
        webcall=new WebServiceCall();
        tm =(TelephonyManager)getContext().getSystemService(Context.TELEPHONY_SERVICE);
        Str_IEMI = tm.getDeviceId();

        setHasOptionsMenu(true);///To Show Action Bar Menu

        btnsug_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getdata()) {
                    InternetPresent = chkconn.isConnectingToInternet(getContext());
                    if(InternetPresent==true){
                        progressdial();
                        networkThread = new Thread()
                        {
                            public void run()
                            {
                                try
                                {
                                    webResponse=webcall.clubSugg(Str_club,Str_IEMI,Logclubid,title,desc,"");
                                    getActivity().runOnUiThread(new Runnable()
                                    {
                                        public void run()
                                        {
                                            callAlert(webResponse);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("Clt_Log",Logname);
                                            bundle.putString("Clt_LogID",Logclubid);
                                            bundle.putString("Clt_ClubName",LogclubName);
                                            bundle.putString("UserClubName",Str_club);
                                            Fragment fragmentobj = new FragmentTabs();
                                            fragmentobj.setArguments(bundle);
                                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                            fragmentManager.beginTransaction().replace(R.id.content_frame,fragmentobj).commit();

                                        }
                                    });
                                    Progsdial.dismiss();
                                    return;
                                }
                                catch (Exception localException)
                                {
                                    System.out.println(localException.getMessage());
                                }
                            }
                        };
                        networkThread.start();
                    }else{
                        callAlert("Internet Connection Required!");
                    }
                }
                }
            });

        return rootView;
    }

    private boolean getdata()
    {
       title =  edtsugtitle.getText().toString();
       desc =  edtsugdesc.getText().toString();
        if(title.length()==0)
        {
            edtsugtitle.setError("input title");
            flag = 1;
        }
        if(desc.length()==0)
        {
            edtsugdesc.setError("input description");
            flag = 1;
        }
        if(flag == 1)
        {
            return false;
        }
        else
        {
            return  true;
        }
    }

    protected void progressdial()
    {
        Progsdial = new ProgressDialog(getContext(), R.style.MyTheme);
        Progsdial.setMessage("Please Wait....");
        Progsdial.setIndeterminate(true);
        Progsdial.setCancelable(false);
        Progsdial.getWindow().setGravity(Gravity.DISPLAY_CLIP_VERTICAL);
        Progsdial.show();
    }

    public void callAlert(String msg)
    {
        AlertDialog ad = new AlertDialog.Builder(getContext()).create();
       // ad.setTitle(Html.fromHtml("<font color='#204604'>Result</font>"));
        ad.setMessage(msg);
        ad.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which)
            {
                  //   Goback();
            }
        });
        ad.show();
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        MenuItem item = menu.findItem(R.id.action_globalSearch);
        item.setVisible(false);///To hide Global Search Menu button
    }
}
