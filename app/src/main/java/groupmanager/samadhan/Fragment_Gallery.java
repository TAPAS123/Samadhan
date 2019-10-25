package groupmanager.samadhan;


import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import static android.content.Context.DOWNLOAD_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Gallery extends Fragment {
    String ClientId,NEType="";
    ProgressDialog Progsdial;


    public Fragment_Gallery() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        WebView webview=(WebView)rootView.findViewById(R.id.webView1);

        setHasOptionsMenu(true);///To Show Action Bar Menu

        ClientId = this.getArguments().getString("UserClubName");
        NEType = this.getArguments().getString("NEType");
//        Intent menuIntent = getIntent();
//        ClientId =  menuIntent.getStringExtra("UserClubName");
//        NEType=menuIntent.getStringExtra("NEType");
        //setContentView(webview);

        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        webview.setInitialScale(1);
        webview.getSettings().setUseWideViewPort(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setDomStorageEnabled(true);

        Chkconnection chkconn=new Chkconnection();
        boolean InternetPresent = chkconn.isConnectingToInternet(getContext()); // Chk Internet

        if(InternetPresent==true){
            String Rtype="";

            if(NEType.equals("Event"))
                Rtype="Event_Img";
            else if(NEType.equals("Event"))
                Rtype="News_Img";

            //progressdial();//Start Progress Dialog
            webview.loadUrl("http://www.mybackup.co.in/EventNewsGallary.aspx?CClub="+ClientId+"&Rtype="+Rtype);
        }else{
            DispAlert("Connection Problem !","No Internet Connection ",true);
        }


        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView viewx, String urlx) {
                viewx.loadUrl(urlx);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                super.onPageFinished(view, url);
                //if(Progsdial.isShowing())
                //Progsdial.dismiss();//Progress dialog dismiss
                // do something
            }

        });


        webview.setDownloadListener(new DownloadListener() {

            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.allowScanningByMediaScanner();

                request.setNotificationVisibility(
                        DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                String DownfileName=url.replace("http://mybackup.co.in/Club_Images", "");

                request.setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,    //Download folder
                        DownfileName);                        //Name of file

                DownloadManager dm = (DownloadManager) getContext().getSystemService(DOWNLOAD_SERVICE);

                dm.enqueue(request);
            }
        });
        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_globalSearch);
        item.setVisible(false);///To hide Global Search Menu button
    }


    @SuppressWarnings("deprecation")
    private void DispAlert(String Title,String Msg,final boolean chk)
    {
        AlertDialog ad=new AlertDialog.Builder(getContext()).create();
        ad.setTitle( Html.fromHtml("<font color='#1C1CF0'>"+Title+"</font>"));
        ad.setMessage(Msg);
        ad.setCancelable(false);
        ad.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(chk) {
                    //   Goback();
                }
                else
                    dialog.dismiss();
            }
        });
        ad.show();
    }


    protected void progressdial()
    {
        Progsdial = new ProgressDialog(getContext(), R.style.MyTheme);
        //Progsdial.setTitle("App Loading");
        Progsdial.setMessage("Please Wait....");
        Progsdial.setIndeterminate(true);
        Progsdial.setCancelable(false);
        Progsdial.getWindow().setGravity(Gravity.DISPLAY_CLIP_VERTICAL);
        Progsdial.show();
    }


//    public boolean onKeyDown(int keyCode, KeyEvent event)
//    {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            Goback();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    //Back
//    private void Goback()
//    {
//        finish();
//    }


}
