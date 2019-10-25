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
 * Created by intel on 27-12-2017.
 */

public class Adapter_Educational_Content extends ArrayAdapter<RowEnvt> {

    Context context;
    int Type;

    public Adapter_Educational_Content(Context context, int textViewResourceId, List<RowEnvt> items,int Type) {
        super(context, textViewResourceId, items);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.Type = Type;
    }

    private class ViewHolder {
        TextView txt1;
        ImageView img1;
        LinearLayout LL1;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Adapter_Educational_Content.ViewHolder holder = null;
        RowEnvt rowItem = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.educational_content_cell, null);
            holder = new Adapter_Educational_Content.ViewHolder();
            holder.LL1 = (LinearLayout) convertView.findViewById(R.id.LL1);
            holder.txt1 = (TextView) convertView.findViewById(R.id.txt1);
            holder.img1 = (ImageView) convertView.findViewById(R.id.img1);
            convertView.setTag(holder);
        } else {
            holder = (Adapter_Educational_Content.ViewHolder) convertView.getTag();
        }

        String txt1=rowItem.getEvtName();
        holder.txt1.setText(txt1);

        holder.img1.setVisibility(View.GONE);
        if(Type==2) {
            holder.img1.setVisibility(View.VISIBLE);

            if(txt1.contains("Video"))
                holder.img1.setImageResource(R.drawable.video_icon);
            else if(txt1.contains("Document"))
                holder.img1.setImageResource(R.drawable.view_document_icon);
            else if(txt1.contains("PPS"))
                holder.img1.setImageResource(R.drawable.pps);
        }

        int p=position%2;

        if(Type==3){
            if(p==0)
                holder.LL1.setBackgroundResource(R.drawable.hobbi_list);
            else
                holder.LL1.setBackgroundResource(R.drawable.hobbi_list1);
        }
        else {
            if (p == 0)
                holder.LL1.setBackgroundResource(R.drawable.i_list);
            else
                holder.LL1.setBackgroundResource(R.drawable.i_list1);
        }

        return convertView;
    }

}
