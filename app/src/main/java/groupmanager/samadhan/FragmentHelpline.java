package groupmanager.samadhan;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHelpline extends Fragment implements View.OnClickListener {
    TextView tvcall1,tvcall2;
    String Str_club,Logname,Logclubid,LogclubName;
    Button btnQuery;

    public FragmentHelpline() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_helpline, container, false);
        // Inflate the layout for this fragment
        tvcall1 = (TextView) rootView.findViewById(R.id.tvcallno1);
        tvcall2 = (TextView) rootView.findViewById(R.id.tvcallno2);
        btnQuery = (Button) rootView.findViewById(R.id.btnQuery);

        Logname = this.getArguments().getString("Clt_Log");
        Logclubid = this.getArguments().getString("Clt_LogID");
        LogclubName = this.getArguments().getString("Clt_ClubName");
        Str_club = this.getArguments().getString("UserClubName");

        setHasOptionsMenu(true);///To Show Action Bar Menu

        tvcall1.setOnClickListener(this);
        btnQuery.setOnClickListener(this);

      //  tvcall2.setOnClickListener(this);
        tvcall1.setText(Html.fromHtml("<U><font color=#e7624e>7607655555</font></U>"));
        return rootView;
    }



    @Override
    public void onClick(View v) {
        if (v == tvcall1)
        {
            String no1 = tvcall1.getText().toString().trim();
            call(no1);
        }
        else if(v == btnQuery)
        {
            Bundle bundle = new Bundle();
            bundle.putString("Clt_Log",Logname);
            bundle.putString("Clt_LogID",Logclubid);
            bundle.putString("Clt_ClubName",LogclubName);
            bundle.putString("UserClubName",Str_club);
            bundle.putString("PType","QUERY");//QUERY
            Fragment fragmentobj = new FragmentSuggestion();
            fragmentobj.setArguments(bundle);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame,fragmentobj).commit();
        }
    }

    private void call(String no1) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + no1));
            startActivity(callIntent);
        }
        catch (ActivityNotFoundException activityException)
        {
            Log.e("Dial", "Call failed", activityException);
        }
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        MenuItem item = menu.findItem(R.id.action_globalSearch);
        item.setVisible(false);///To hide Global Search Menu button
    }
}
