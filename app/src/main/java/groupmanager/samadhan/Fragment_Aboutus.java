package groupmanager.samadhan;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by intel on 20-07-2017.
 */

public class
Fragment_Aboutus extends Fragment {

    String ClientId,Table4Name;
    WebView WebVw;

    public Fragment_Aboutus() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View rootView = inflater.inflate(R.layout.aboutus, container, false);

        WebVw=(WebView)rootView.findViewById(R.id.WebVw1);

        setHasOptionsMenu(true);///To Show Action Bar Menu

        ClientId =  this.getArguments().getString("UserClubName");



            Bitmap theImage = BitmapFactory.decodeResource(getResources(), R.drawable.about_page);
            //Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.logo, null);
            //Bitmap myLogo = ((BitmapDrawable) myDrawable).getBitmap();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            theImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String imgageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            String image = "data:image/png;base64," + imgageBase64;
            String html="<html><body><img src='{IMAGE_URL}' width='330px' height='490px' /></body></html>";

            //String html="<html><body><img src='{IMAGE_URL}' width='100%' height='100%' /></body></html>";
            // Use image for the img src parameter in your html and load to webview
            html = html.replace("{IMAGE_URL}", image);
            WebVw.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", "");
            WebVw.getSettings().setSupportZoom(true);
            WebVw.getSettings().setBuiltInZoomControls(true);

            //WebVw.setBackgroundColor(Color.DKGRAY);

        return rootView;
    }



    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_globalSearch);
        item.setVisible(false);///To hide Global Search Menu button
    }
}
