package groupmanager.samadhan;

import java.io.ByteArrayInputStream;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.LinkAddress;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Adapter_Committee extends ArrayAdapter<RowEnvt>  {
	 Context context;
	TextView txthead;
		
	 public Adapter_Committee(Context context, int textViewResourceId, List<RowEnvt> items,TextView txthead) {
		super(context, textViewResourceId, items);
		// TODO Auto-generated constructor stub
		this.context = context;
		 this.txthead=txthead;
	 }
	 
	 private class ViewHolder {
	    TextView txt1; 
	    ImageView Img1;
		 LinearLayout linearlayout;
	 }
	 
	 public View getView(int position, View convertView, ViewGroup parent) {
	    ViewHolder holder = null;
	    RowEnvt  rowItem = getItem(position);
	    LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	    if (convertView == null) {
	      convertView = mInflater.inflate(R.layout.committee_list_items, null);
	      holder = new ViewHolder();
	      holder.Img1=(ImageView) convertView.findViewById(R.id.img1);
	      holder.txt1 = (TextView) convertView.findViewById(R.id.txt1);
		  holder.linearlayout = (LinearLayout) convertView.findViewById(R.id.linearlayout);
	      convertView.setTag(holder);
	    } else{
	      holder = (ViewHolder) convertView.getTag();
	    }
	    
	    byte[] imgP=rowItem.getImgP();
	    if(imgP!=null)
	    {
	    	ByteArrayInputStream imageStream = new ByteArrayInputStream(imgP);
	    	holder.Img1.setVisibility(View.VISIBLE);
	    	holder.Img1.setImageBitmap(BitmapFactory.decodeStream(imageStream));
	    }
	    else{
	    	holder.Img1.setVisibility(View.GONE);
	    }
	    
	    holder.txt1.setText(rowItem.getEvtName());
		Typeface face=Typeface.createFromAsset(getContext().getAssets(), "calibri.ttf");
		holder.txt1.setTypeface(face);
		 if(txthead.getText().toString().contains("Committee"))
		 {
			 holder.linearlayout.setBackgroundResource(R.drawable.boxblue);
		 }
		 else if(txthead.getText().toString().contains("Editorial"))
		 {
			 holder.linearlayout.setBackgroundResource(R.drawable.boxyellow);
		 }
		 else if(txthead.getText().toString().contains("Sections"))
		 {
			 holder.linearlayout.setBackgroundResource(R.drawable.boxviolet);
		 }
		 else if(txthead.getText().toString().contains("Chapters"))
		 {
			 holder.linearlayout.setBackgroundResource(R.drawable.boxpurple);
		 }
		 else if(txthead.getText().toString().contains("Governing Council Members"))
		 {
			 holder.linearlayout.setBackgroundResource(R.drawable.boxdarkblue);
		 }



		 return convertView;
	}
}
