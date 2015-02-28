package com.zetterstrom.android.list.db;

import android.provider.BaseColumns;

public final class ListContract {
 // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public ListContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class ListEntry implements BaseColumns {
        public static final String TABLE_NAME = "list";
        public static final String COLUMN_NAME_LIST_ID = "list_id";
        public static final String COLUMN_NAME_LIST_NAME = "list_name";
        public static final String COLUMN_NAME_SORT = "sort";

    }
    
    public static abstract class ListItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "list_item";
        public static final String COLUMN_NAME_LIST_ID = "list_parent_id";
        public static final String COLUMN_NAME_LIST_ITEM_ID = "list_item_id";
        public static final String COLUMN_NAME_LIST_ITEM = "list_item_title";
        public static final String COLUMN_NAME_LIST_ITEM_COMPLETED = "list_item_completed";
        public static final String COLUMN_NAME_SORT = "sort";

    }
    
}
