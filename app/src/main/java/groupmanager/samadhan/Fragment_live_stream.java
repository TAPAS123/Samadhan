package groupmanager.samadhan;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by intel on 15-02-2018.
 */

public class Fragment_live_stream  extends Fragment {
    String ClientId,Mob,passwordKey,LogMid;
    ProgressDialog Progsdial;


    public Fragment_live_stream() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        WebView webview = (WebView) rootView.findViewById(R.id.webView1);

        setHasOptionsMenu(true);///To Show Action Bar Menu

        ClientId = this.getArguments().getString("UserClubName");
        LogMid = this.getArguments().getString("Clt_LogID");
//        Intent menuIntent = getIntent();
//        ClientId =  menuIntent.getStringExtra("UserClubName");
//        NEType=menuIntent.getStringExtra("NEType");
        //setContentView(webview);

        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        webview.setInitialScale(1);
        webview.getSettings().setUseWideViewPort(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        //settings.setDomStorageEnabled(true);
        settings.setPluginState(WebSettings.PluginState.ON);

        Chkconnection chkconn = new Chkconnection();
        boolean InternetPresent = chkconn.isConnectingToInternet(getContext()); // Chk Internet

        Get_SharedPref_Values();

        if (InternetPresent == true) {
            String Name = "",Email="";

            if(passwordKey.contains("@_@"))
            {
                String s[]=passwordKey.split("@_@");
                Name=s[0].trim();
                Email=s[1].trim();
            }
            else{
                SQLiteDatabase db = getActivity().openOrCreateDatabase("MDA_Club", SQLiteDatabase.CREATE_IF_NECESSARY, null);
                String sql="Select M_Name,M_Email from C_"+ClientId+"_2 where M_id="+LogMid;
                Cursor cursorT = db.rawQuery(sql, null);
                if(cursorT.moveToFirst()){
                    Name=ChkVal(cursorT.getString(0));
                    Email=ChkVal(cursorT.getString(1));
                }
                cursorT.close();
                db.close();
            }

            //livevideolist.aspx?Tname=Tapas Panndey&Email=tmda007@gmail.com&Mob=9198025419
            //progressdial();//Start Progress Dialog
            webview.loadUrl("http://www.mybackup.co.in/livevideolist.aspx?Tname="+Name+"&Email="+Email+"&Mob="+Mob);

            webview.setBackgroundColor(android.R.color.transparent);

        } else {
            DispAlert("Connection Problem !", "No Internet Connection ", true);
        }


        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView viewx, String urlx) {
                viewx.loadUrl(urlx);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //if(Progsdial.isShowing())
                //Progsdial.dismiss();//Progress dialog dismiss
                // do something
            }

        });

        return rootView;
    }


    private void Get_SharedPref_Values()
    {
        SharedPreferences sharedpreferences = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        if (sharedpreferences.contains("userid"))
        {
            Mob=sharedpreferences.getString("userid", "");
        }
        if (sharedpreferences.contains("passwordKey"))
        {
            passwordKey=sharedpreferences.getString("passwordKey", "");
        }
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_globalSearch);
        item.setVisible(false);///To hide Global Search Menu button
    }


    @SuppressWarnings("deprecation")
    private void DispAlert(String Title, String Msg, final boolean chk) {
        AlertDialog ad = new AlertDialog.Builder(getContext()).create();
        ad.setTitle(Html.fromHtml("<font color='#1C1CF0'>" + Title + "</font>"));
        ad.setMessage(Msg);
        ad.setCancelable(false);
        ad.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (chk) {
                    //   Goback();
                } else
                    dialog.dismiss();
            }
        });
        ad.show();
    }


    private String ChkVal(String Val)
    {
        if(Val==null)
            Val="";

        return Val.trim();
    }



    protected void progressdial() {
        Progsdial = new ProgressDialog(getContext(), R.style.MyTheme);
        //Progsdial.setTitle("App Loading");
        Progsdial.setMessage("Please Wait....");
        Progsdial.setIndeterminate(true);
        Progsdial.setCancelable(false);
        Progsdial.getWindow().setGravity(Gravity.DISPLAY_CLIP_VERTICAL);
        Progsdial.show();
    }
}
