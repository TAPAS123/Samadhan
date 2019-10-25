package groupmanager.samadhan;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by intel on 27-01-2018.
 */

public class Fragment_Education_Content  extends Fragment implements View.OnClickListener{
    Button btn_industrial,btn_technical,btn_professional,btn_personal,btn_startups,btn_GovernmentScheme,btn_producer_comp,btn_SmallBusi;
    String Str_club,Logname,Logclubid,LogclubName;

    public Fragment_Education_Content() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_educational_content, container, false);

        Logname = this.getArguments().getString("Clt_Log");
        Logclubid = this.getArguments().getString("Clt_LogID");
        LogclubName = this.getArguments().getString("Clt_ClubName");
        Str_club = this.getArguments().getString("UserClubName");

        btn_industrial = (Button)rootView.findViewById(R.id.btn_industrial);
        btn_technical = (Button)rootView.findViewById(R.id.btn_technical);
        btn_professional = (Button)rootView.findViewById(R.id.btn_professional);
        btn_personal = (Button)rootView.findViewById(R.id.btn_personal);
        btn_startups = (Button)rootView.findViewById(R.id.btn_startups);
        btn_GovernmentScheme = (Button)rootView.findViewById(R.id.btn_GovernmentScheme);
        btn_producer_comp = (Button)rootView.findViewById(R.id.btn_producer_comp);
        btn_SmallBusi = (Button)rootView.findViewById(R.id.btn_SmallBusi);

        btn_industrial.setOnClickListener(this);
        btn_technical.setOnClickListener(this);
        btn_professional.setOnClickListener(this);
        btn_personal.setOnClickListener(this);
        btn_startups.setOnClickListener(this);
        btn_GovernmentScheme.setOnClickListener(this);
        btn_producer_comp.setOnClickListener(this);
        btn_SmallBusi.setOnClickListener(this);

        Typeface face=Typeface.createFromAsset(getContext().getAssets(), "calibri.ttf");
        btn_industrial.setTypeface(face);
        btn_technical.setTypeface(face);
        btn_professional.setTypeface(face);
        btn_personal.setTypeface(face);
        btn_startups.setTypeface(face);
        btn_GovernmentScheme.setTypeface(face);
        btn_producer_comp.setTypeface(face);
        btn_SmallBusi.setTypeface(face);

        setHasOptionsMenu(true);///To Show Action Bar Menu
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onClick(View v) {

        String Type="";
        if(v ==btn_industrial )
        {
            Type="INDUSTRIAL";
        }
        else if(v == btn_technical)
        {
            Type="TECHNICAL";
        }
        else if(v == btn_professional)
        {
            Type="PROFESSIONAL";
        }
        else if(v == btn_personal)
        {
            Type="PERSONAL";
        }
        else if(v == btn_startups)
        {
            Type="STARTUP";
        }
        else if(v == btn_GovernmentScheme)
        {
            Type="SMALLBUSINESS";
        }
        else if(v == btn_producer_comp)
        {
            Type="PRODUCER";
        }
        else if(v == btn_SmallBusi)
        {
            Type="SMALLBUSINESSMAIN";
        }

        Button b=(Button)v;
        String MTitle=b.getText().toString().replace("\n","").toUpperCase();

        Intent Mintent=new Intent(getContext(),Activity_Education_Content.class);
        Mintent.putExtra("Type",Type);
        Mintent.putExtra("MTitle",MTitle);
        Mintent.putExtra("Clt_Log",Logname);
        Mintent.putExtra("Clt_LogID",Logclubid);
        Mintent.putExtra("Clt_ClubName",LogclubName);
        Mintent.putExtra("UserClubName",Str_club);
        startActivity(Mintent);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_globalSearch);
        item.setVisible(false);///To hide Global Search Menu button
    }
}
