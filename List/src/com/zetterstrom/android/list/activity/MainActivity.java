package com.zetterstrom.android.list.activity;

import java.util.ArrayList;

import com.zetterstrom.android.list.adapter.DrawerAdapter;
import com.zetterstrom.android.list.db.ListContract.ListEntry;
import com.zetterstrom.android.list.db.ListDbHelper;
import com.zetterstrom.android.list.dto.ListDetails;
import com.zetterstrom.android.list.fragments.ListFragment;
import com.zetterstrom.android.list.R;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle;
    private ArrayList<ListDetails> mListDetails;
    private DrawerAdapter mDrawerAdapter = null;
    private int mPosition = 0;
    public static final String PREFS_FILE = "ListPrefsFile";
    private ListDbHelper mDbHelper = null;
    private ListFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("List");

        mDbHelper = new ListDbHelper(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);

        mTitle = getTitle();
        mDrawerList = (ListView) findViewById(R.id.main_left_drawer);
        getListItems();
        mDrawerAdapter = new DrawerAdapter(this, R.layout.list_item_drawer,
                mListDetails);

        // set a custom shadow that overlays the main content when the drawer
        // opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(mDrawerAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
        mDrawerLayout, /* DrawerLayout object */
        R.drawable.ic_drawer, /* nav drawer icon to replace 'Up' caret */
        R.string.drawer_open, /* "open drawer" description */
        R.string.drawer_close /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                ActivityCompat.invalidateOptionsMenu(MainActivity.this); // creates
                                                                         // call
                                                                         // to
                // onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                ActivityCompat.invalidateOptionsMenu(MainActivity.this); // creates
                                                                         // call
                                                                         // to
                // onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt("listPosition");
        } else {
            SharedPreferences settings = getSharedPreferences(PREFS_FILE,
                    MODE_PRIVATE);
            mPosition = settings.getInt("listPosition", 0);
        }
        selectItem(mPosition);
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            selectItem(position);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on
        // position
        mPosition = position;
        if (mListDetails.size() < 1) {
            return;
        }
        mFragment = null;
        mFragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString("LIST_ID", mListDetails.get(mPosition).getId());
        mFragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_content_frame, mFragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(mPosition, true);
        if (mListDetails.size() > 0) {
            setTitle(mListDetails.get(mPosition).getTitle());
        }
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content
        // view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
        case R.id.action_settings:
            Intent intent = new Intent(getApplicationContext(),
                    SettingsActivity.class);
            startActivity(intent);
            break;
        case R.id.action_new:
            createNewList();
            break;
        case R.id.action_remove:
            removeList();
            break;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putInt("listPosition", mPosition);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Store values between instances here
        SharedPreferences preferences = getSharedPreferences(PREFS_FILE,
                MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit(); // Put the values
                                                              // from the UI
        editor.putInt("listPosition", mPosition);
        // Commit to storage
        editor.commit();
    }

    private ArrayList<ListDetails> getListItems() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = { ListEntry._ID, ListEntry.COLUMN_NAME_LIST_NAME,
                ListEntry.COLUMN_NAME_LIST_ID, ListEntry.COLUMN_NAME_SORT };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = ListEntry.COLUMN_NAME_SORT + " ASC";

        Cursor cursor = db.query(ListEntry.TABLE_NAME, // The table
                                                       // to
                // query
                projection, // The columns to return
                null, // The columns for the
                      // WHERE clause
                null, // The values for the WHERE
                      // clause
                null, // don't group the rows
                null, // don't filter by row groups
                sortOrder // The sort order
                );
        try {
            if (mListDetails == null)
                mListDetails = new ArrayList<ListDetails>();
            mListDetails.clear();
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                ListDetails item = new ListDetails();
                item.setTitle(cursor.getString(cursor
                        .getColumnIndexOrThrow(ListEntry.COLUMN_NAME_LIST_NAME)));
                item.setId(cursor.getString(cursor
                        .getColumnIndexOrThrow(ListEntry.COLUMN_NAME_LIST_ID)));

                mListDetails.add(item);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mListDetails;
    }

    private void createNewList() {
        final EditText input = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("New List Title:")
                .setCancelable(false)
                .setView(input)
                .setPositiveButton("Create",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String inputText = input.getText().toString();
                                if (inputText.trim().length() <= 0) {
                                    return;
                                }
                                addList(inputText);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void addList(String listTitle) {
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ListEntry.COLUMN_NAME_LIST_NAME, listTitle);
        String id = String.valueOf(System.currentTimeMillis()) + listTitle;
        values.put(ListEntry.COLUMN_NAME_LIST_ID, id);
        values.put(ListEntry.COLUMN_NAME_SORT, mListDetails.size());

        // Insert the new row, returning the primary key value of the new row
        db.insert(ListEntry.TABLE_NAME, null, values);
        ListDetails item = new ListDetails(listTitle);
        item.setId(id);
        mListDetails.add(item);
        mDrawerAdapter.notifyDataSetChanged();
        selectItem(mListDetails.size() - 1);

    }

    private void removeList() {
        try {
            mFragment.removeAllItems();
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            // Define 'where' part of query.
            String selection = ListEntry.COLUMN_NAME_LIST_ID + " LIKE ?";
            // Specify arguments in placeholder order.
            String[] selectionArgs = { mFragment.getListId() };
            // Issue SQL statement.
            db.delete(ListEntry.TABLE_NAME, selection, selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mListDetails.remove(mPosition);
        mDrawerAdapter.notifyDataSetChanged();
        if (mListDetails.size() > 0) {
            selectItem(mListDetails.size() - 1);
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().remove(mFragment).commit();
            getSupportActionBar().setTitle("List");
        }
    }
}
