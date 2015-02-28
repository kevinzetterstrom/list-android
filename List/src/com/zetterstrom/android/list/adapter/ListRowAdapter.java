package com.zetterstrom.android.list.adapter;

import java.util.ArrayList;

import com.zetterstrom.android.list.R;
import com.zetterstrom.android.list.dto.ListItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListRowAdapter extends ArrayAdapter<ListItem> {
    private ArrayList<ListItem> mItems;
    private Context mContext;
    private boolean mShowDragHandle = false;

    public ListRowAdapter(final Context context, final int textViewResourceId,
            ArrayList<ListItem> items) {
        super(context, textViewResourceId, items);
        this.mItems = items;
        this.mContext = context;
    }

    public ListRowAdapter(final Context context, final int textViewResourceId,
            ArrayList<ListItem> items, boolean showDragHandle) {
        super(context, textViewResourceId, items);
        this.mItems = items;
        this.mContext = context;
        this.mShowDragHandle = showDragHandle;
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView,
            final ViewGroup parent) {
        if (convertView == null) {
            final LayoutInflater vi = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.list_item_row, null);
        }

        ImageView dragHandle = (ImageView) convertView
                .findViewById(R.id.drag_handle);
        if ((dragHandle != null) && !mShowDragHandle)
            dragHandle.setVisibility(View.GONE);

        ListItem currentItem = mItems.get(position);
        /*CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.list_row_checkbox);
        checkbox.setChecked(currentItem.getCompleted());*/

        TextView title = (TextView) convertView
                .findViewById(R.id.list_row_title);
        title.setText(currentItem.getTitle());
        return convertView;
    }

    public void updateEntries(ArrayList<ListItem> events) {
        mItems.addAll(events);
        notifyDataSetChanged();
    }
}
