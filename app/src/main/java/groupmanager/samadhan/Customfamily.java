package groupmanager.samadhan;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class Customfamily extends ArrayAdapter<RowEnvt>{
	Context context;
	public Customfamily(Context context, int textViewResourceId,List<RowEnvt> items) {
		super(context, textViewResourceId, items);
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	
	 private class ViewHolder {
	        TextView txtDate,txttilte,txtmob; //txtEvtDesc
	   }
	 
	  public View getView(int position, View convertView, ViewGroup parent) {
	        ViewHolder holder = null;
	        RowEnvt  rowItem = getItem(position);
	        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	        if (convertView == null) {
	            convertView = mInflater.inflate(R.layout.familylist, null);
	            holder = new ViewHolder();
	            holder.txtDate = (TextView) convertView.findViewById(R.id.txtDatenews);
	            holder.txttilte = (TextView) convertView.findViewById(R.id.txtnewsTitle);
	            holder.txtmob = (TextView) convertView.findViewById(R.id.textViewmob);
	            convertView.setTag(holder);
	        } else{
	            holder = (ViewHolder) convertView.getTag();
	        }
	        
	        holder.txtDate.setText(rowItem.getEvtName());
	        if(rowItem.getEvtDesc().equals("0"))
	        {
	        	 holder.txttilte.setVisibility(View.GONE);
	        }
	        holder.txttilte.setText(rowItem.getEvtDesc());
	        
	        String Mob=rowItem.getEvtdate();
	        if(Mob.length()>0)
	        {
	        	holder.txtmob.setVisibility(View.VISIBLE);
	        	holder.txtmob.setText(Mob);
	        }
	        else{
	        	holder.txtmob.setVisibility(View.GONE);
	        }
	        
	       
	        return convertView;
	    }
	       
}
