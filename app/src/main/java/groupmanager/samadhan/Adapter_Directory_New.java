package groupmanager.samadhan;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by intel on 28-12-2017.
 */

public class Adapter_Directory_New extends ArrayAdapter<RowEnvt> {

    Context context;

    public Adapter_Directory_New(Context context, int textViewResourceId, List<RowEnvt> items) {
        super(context, textViewResourceId, items);
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    private class ViewHolder {
        TextView txt1;
        ImageView img1;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        RowEnvt rowItem = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.directory_new_list_cell, null);
            holder = new ViewHolder();
            holder.txt1 = (TextView) convertView.findViewById(R.id.txt1);
            holder.img1 = (ImageView) convertView.findViewById(R.id.img1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String txt1=rowItem.getEvtName();
        holder.txt1.setText(txt1);
        holder.img1.setImageResource(rowItem.getMid());

        return convertView;
    }

}
