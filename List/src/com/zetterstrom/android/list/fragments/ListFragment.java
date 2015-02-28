package com.zetterstrom.android.list.fragments;

import java.util.ArrayList;

import com.zetterstrom.android.dragsortlibrary.DragSortController;
import com.zetterstrom.android.dragsortlibrary.DragSortListView;
import com.zetterstrom.android.list.R;
import com.zetterstrom.android.list.adapter.ListRowAdapter;
import com.zetterstrom.android.list.db.ListContract.ListItemEntry;
import com.zetterstrom.android.list.db.ListItemDbHelper;
import com.zetterstrom.android.list.dto.ListItem;

import android.content.ContentValues;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ListFragment extends Fragment {
    private ListRowAdapter mAdapter;
    private ArrayList<ListItem> mItems = null;;
    private DragSortListView mDslv;
    private DragSortController mController;
    public int mDragStartMode = DragSortController.ON_DOWN;
    public int mRemoveMode = DragSortController.FLING_REMOVE;
    public boolean mDragEnabled = true;
    private ListItemDbHelper mDbHelper = null;
    private String mCurrentListId = "";
    private EditText mEditText;
    private ImageButton mEditButton;

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            if (from != to) {

                ListItem item = mAdapter.getItem(from);
                mAdapter.remove(item);
                mAdapter.insert(item, to);
                listModified(mItems);

            }
        }
    };

    private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
        @Override
        public void remove(int which) {

            ListItem item = mAdapter.getItem(which);
            mAdapter.remove(item);
            listItemDeleted(item);

        }
    };

    public DragSortController buildController(DragSortListView dslv) {
        DragSortController controller = new DragSortController(dslv);
        controller.setDragHandleId(R.id.drag_handle);
        controller.setRemoveEnabled(true);
        controller.setDragInitMode(mDragStartMode);
        controller.setRemoveMode(mRemoveMode);
        controller.setBackgroundColor(Color.TRANSPARENT);
        return controller;
    }

    public void updateItems(ArrayList<ListItem> items) {
        mItems.clear();
        mItems.addAll(items);

        mAdapter.notifyDataSetChanged();
    }

    public void setListAdapter() {
        if (mItems == null) {
            mItems = new ArrayList<ListItem>();
        }
        mItems.clear();
        getListItems();

        mAdapter = new ListRowAdapter(getActivity().getApplicationContext(),
                R.id.list_row_title, mItems, true);
        mDslv.setAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mCurrentListId = getArguments().getString("LIST_ID");
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        mDslv = (DragSortListView) view.findViewById(R.id.my_list);

        mEditText = (EditText) view.findViewById(R.id.list_new_item);
        mEditText.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                    KeyEvent event) {
                if (actionId == EditorInfo.IME_NULL
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    addListItem();
                    return true;
                }
                return false;
            }
        });

        mEditButton = (ImageButton) view
                .findViewById(R.id.list_new_item_button);
        mEditButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                addListItem();

            }
        });

        mController = buildController(mDslv);
        mDslv.setFloatViewManager(mController);
        mDslv.setOnTouchListener(mController);
        mDslv.setDragEnabled(mDragEnabled);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDslv.setDropListener(onDrop);
        mDslv.setRemoveListener(onRemove);
        mDbHelper = new ListItemDbHelper(getActivity().getApplicationContext());

        setListAdapter();
    }

    public ArrayList<ListItem> currentItems() {
        if (mItems == null) {
            mItems = new ArrayList<ListItem>();
        }
        return mItems;
    }

    private ArrayList<ListItem> getListItems() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = { ListItemEntry._ID,
                ListItemEntry.COLUMN_NAME_LIST_ID,
                ListItemEntry.COLUMN_NAME_LIST_ITEM_ID,
                ListItemEntry.COLUMN_NAME_LIST_ITEM,
                ListItemEntry.COLUMN_NAME_LIST_ITEM_COMPLETED,
                ListItemEntry.COLUMN_NAME_SORT };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = ListItemEntry.COLUMN_NAME_SORT + " ASC";

        Cursor cursor = db.query(ListItemEntry.TABLE_NAME, // The table
                                                           // to
                // query
                projection, // The columns to return
                ListItemEntry.COLUMN_NAME_LIST_ID + "=?", // The columns for the
                                                          // WHERE clause
                new String[] { mCurrentListId }, // The values for the WHERE
                                                 // clause
                null, // don't group the rows
                null, // don't filter by row groups
                sortOrder // The sort order
                );
        try {
            if (mItems == null)
                mItems = new ArrayList<ListItem>();
            mItems.clear();
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                ListItem item = new ListItem();
                item.setTitle(cursor.getString(cursor
                        .getColumnIndexOrThrow(ListItemEntry.COLUMN_NAME_LIST_ITEM)));
                // item.setCompleted(cursor.getString(cursor
                // .getColumnIndexOrThrow(ListItemEntry.COLUMN_NAME_LIST_ITEM_COMPLETED)));
                item.setParentListId(cursor.getString(cursor
                        .getColumnIndexOrThrow(ListItemEntry.COLUMN_NAME_LIST_ID)));
                item.setItemId(cursor.getString(cursor
                        .getColumnIndexOrThrow(ListItemEntry.COLUMN_NAME_LIST_ITEM_ID)));

                mItems.add(item);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mItems;
    }

    private void listItemAdded(ListItem item) {

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ListItemEntry.COLUMN_NAME_LIST_ID, item.getParentListId());
        values.put(ListItemEntry.COLUMN_NAME_LIST_ITEM_ID, item.getItemId());
        values.put(ListItemEntry.COLUMN_NAME_LIST_ITEM, item.getTitle());
        values.put(ListItemEntry.COLUMN_NAME_LIST_ITEM_COMPLETED,
                item.getCompleted());

        values.put(ListItemEntry.COLUMN_NAME_SORT, mItems.size());
        // Insert the new row, returning the primary key value of the new row
        db.insert(ListItemEntry.TABLE_NAME, null, values);
        mItems.add(item);
        mAdapter.notifyDataSetChanged();
    }

    private void listModified(ArrayList<ListItem> items) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // New value for one column
        for (int i = 0; i < items.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(ListItemEntry.COLUMN_NAME_SORT, i);

            // Which row to update, based on the ID
            String selection = ListItemEntry.COLUMN_NAME_LIST_ITEM_ID
                    + " LIKE ?";
            String[] selectionArgs = { items.get(i).getItemId() };

            db.update(ListItemEntry.TABLE_NAME, values, selection,
                    selectionArgs);
        }

    }

    private void listItemDeleted(ListItem item) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Define 'where' part of query.
        String selection = ListItemEntry.COLUMN_NAME_LIST_ITEM_ID + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { item.getItemId() };
        // Issue SQL statement.
        db.delete(ListItemEntry.TABLE_NAME, selection, selectionArgs);

        mItems.clear();
        getListItems();
        mAdapter.notifyDataSetChanged();
    }

    public void removeAllItems() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Define 'where' part of query.
        String selection = ListItemEntry.COLUMN_NAME_LIST_ID + " =?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { mCurrentListId };
        // Issue SQL statement.
        db.delete(ListItemEntry.TABLE_NAME, selection, selectionArgs);

        mItems.clear();
        getListItems();
        mAdapter.notifyDataSetChanged();
    }

    private void addListItem() {
        if (mEditText.getEditableText().toString().trim().length() <= 0) {
            return;
        }

        ListItem item = new ListItem();
        item.setTitle(mEditText.getEditableText().toString());
        item.setItemId(String.valueOf(System.currentTimeMillis()));
        item.setParentListId(mCurrentListId);

        listItemAdded(item);
        mEditText.clearComposingText();
        mEditText.getText().clear();

    }

    public String getListId() {
        return mCurrentListId;
    }
}
