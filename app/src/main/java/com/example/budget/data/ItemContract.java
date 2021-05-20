package com.example.budget.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ItemContract {

    public static final String CONTENT_AUTHORITY = "com.example.budget";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ITEMS = "items";

    private ItemContract() {

    }

    public static final class ItemEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        public final static String TABLE_NAME = "Items";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_DESCRIPTION ="Description";
        public final static String COLUMN_AMOUNT = "Amount";
        public final static String COLUMN_TYPE = "Type";
        public final static String COLUMN_PHOTO = "Image";
        public final static String COLUMN_PHONE = "Phone";
    }

}
