package com.a2bsystemapp.a2bsystemapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class PrepaListRowDataAdapter extends ArrayAdapter<PrepaListRowData> {

    private ViewHolder viewHolder;

    private static class ViewHolder {
        private LinearLayout itemView;
    }

    public PrepaListRowDataAdapter(Context context, int textViewResourceId, ArrayList<PrepaListRowData> items) {
        super(context, textViewResourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.prepa_line, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.itemView = (LinearLayout) convertView.findViewById(R.id.line);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PrepaListRowData item = getItem(position);
        if (item!= null) {
            if(!item.q_pal_code.equalsIgnoreCase( "0" )) {
                viewHolder.itemView.setBackgroundResource(R.color.green);
            }

            TextView tvArticle = viewHolder.itemView.findViewById(R.id.article);
            tvArticle.setText(item.Article);

            TextView tvQuantite = viewHolder.itemView.findViewById(R.id.quantite);
            tvQuantite.setText(item.Quantite);
        }

        return convertView;
    }
}
