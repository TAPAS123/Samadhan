package groupmanager.samadhan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class YouTube_Video extends AppCompatActivity {

    String VCode;
    private Context context=this;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_gallery);

        WebView webview = (WebView) findViewById(R.id.webView1);

        Intent menuIntent = getIntent();
        VCode = menuIntent.getStringExtra("VCode");

        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        webview.setInitialScale(1);
        webview.getSettings().setUseWideViewPort(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        //settings.setDomStorageEnabled(true);
        settings.setPluginState(WebSettings.PluginState.ON);

        Chkconnection chkconn = new Chkconnection();
        boolean InternetPresent = chkconn.isConnectingToInternet(context); // Chk Internet

        if (InternetPresent == true) {

            webview.loadUrl("http://www.mybackup.co.in/ytube_video.aspx?lnk="+VCode);

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

    }


    @SuppressWarnings("deprecation")
    private void DispAlert(String Title, String Msg, final boolean chk) {
        AlertDialog ad = new AlertDialog.Builder(context).create();
        ad.setTitle(Html.fromHtml("<font color='#1C1CF0'>" + Title + "</font>"));
        ad.setMessage(Msg);
        ad.setCancelable(false);
        ad.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (chk) {
                    Goback();
                } else
                    dialog.dismiss();
            }
        });
        ad.show();
    }



    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Goback();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void Goback(){
        finish();
    }

}
