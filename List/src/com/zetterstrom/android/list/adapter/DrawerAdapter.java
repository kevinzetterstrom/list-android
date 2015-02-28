package com.zetterstrom.android.list.adapter;

import java.util.ArrayList;

import com.zetterstrom.android.list.R;
import com.zetterstrom.android.list.dto.ListDetails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DrawerAdapter extends ArrayAdapter<ListDetails> {
    private ArrayList<ListDetails> mItems;
    private Context mContext = null;

    public DrawerAdapter(Context context, int textViewResourceId,
            ArrayList<ListDetails> items) {
        super(context, textViewResourceId, items);
        this.mItems = items;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    public void updateEntries(ArrayList<ListDetails> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item_drawer, null);
        }
        String s = mItems.get(position).getTitle();
        if (s != null) {
            TextView description = (TextView) v
                    .findViewById(R.id.drawer_list_text);
            description.setText(s);
        }

        return v;

    }
}
