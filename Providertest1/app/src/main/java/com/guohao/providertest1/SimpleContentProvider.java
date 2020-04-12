package com.guohao.providertest1;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * 默认要实现的六个函数：
 *
 * 增删改查：insert、delete、update、query
 *
 * 创建：onCreate
 *
 * 类型：getType
 *
 */
public class SimpleContentProvider extends ContentProvider {


    private static final String TAG = "SimpleContentProvider";

    public static final String AUTHORITY = "com.guohao.providertest1";

    /*该ContentProvider返回的数据类型定义，数据集合*/
    private static final String CONTENT_TYPE ="vnd.android.cursor.dir/vnd."+AUTHORITY;

    /*单项数据*/
    private static final String CONTNET_TYPE_ITEM ="vnd.android.cursor.item/vnd."+AUTHORITY ;

    public static final Uri USERINFO_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/"
            + UserInfoDbHelper.TABLE_USER_INFO);

    public static final Uri COMPANY_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/"
            + UserInfoDbHelper.TABLE_COMPANY);

    public static final int USERINFO_CODE = 0;
    public static final int USERINFO_ITEM_CODE = 1;
    public static final int COMPANY_CODE = 2;
    public static final int COMPANY_ITEM_CODE = 3;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        /**
         * 这里使用2种通配符 "*"表示匹配任意长度的任意字符，"#"表示匹配任意长度的数字
         * 因此，content://com.guohao.providertest1/company 表示查询company表中的所有数据
         * 而 conent://com.guohao.providertest1/company/# 表示根据一个数字id 查询一条信息
         */
        sUriMatcher.addURI(AUTHORITY, "userinfo", USERINFO_CODE);
        sUriMatcher.addURI(AUTHORITY, "userinfo/*", USERINFO_ITEM_CODE);
        sUriMatcher.addURI(AUTHORITY, "company", COMPANY_CODE);
        sUriMatcher.addURI(AUTHORITY, "company/#", COMPANY_ITEM_CODE);
    }

    private SQLiteDatabase mDatabase;

    /**
     * 夸进程时发生在主线程
     *
     * @return
     */
    @Override
    public boolean onCreate() {
        Log.i(TAG, "onCreate: current thread: " + Thread.currentThread().getName());
        /*返回一个可读写的数据库*/
        mDatabase = new UserInfoDbHelper(getContext()).getWritableDatabase();
        return true;
    }

    /**
     * 夸进程时发生在工作线程
     *
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Log.i(TAG, "query: current thread: " + Thread.currentThread().getName());

        Cursor cursor = null;

        switch (sUriMatcher.match(uri)) {

            case USERINFO_CODE:
                cursor = mDatabase.query(UserInfoDbHelper.TABLE_USER_INFO, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case USERINFO_ITEM_CODE:
                String tel = uri.getPathSegments().get(1);
                cursor = mDatabase.query(UserInfoDbHelper.TABLE_USER_INFO, projection, "tel_num = ?", new String[]{tel}, null, null, sortOrder);
                break;

            case COMPANY_CODE:
                cursor = mDatabase.query(UserInfoDbHelper.TABLE_COMPANY, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case COMPANY_ITEM_CODE:
                String cid = uri.getPathSegments().get(1);
                cursor = mDatabase.query(UserInfoDbHelper.TABLE_COMPANY, projection, "id = ?", new String[]{cid}, null, null, sortOrder);
                break;
        }
        return cursor;
    }

    /**
     * 夸进程时发生在工作线程
     *
     * @param uri
     * @param values
     * @return
     */
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.i(TAG, "insert: current thread: " + Thread.currentThread().getName());

        long newId = 0;

        Uri newUri = null;

        switch (sUriMatcher.match(uri)) {

            case USERINFO_CODE:
                newId = mDatabase.insert(UserInfoDbHelper.TABLE_USER_INFO, null, values);
                newUri = Uri.parse("content://" + AUTHORITY + "/" + UserInfoDbHelper.TABLE_USER_INFO + "/" + newId);
                break;

            case COMPANY_CODE:
                newId = mDatabase.insert(UserInfoDbHelper.TABLE_COMPANY, null, values);
                newUri = Uri.parse("content://" + AUTHORITY + "/" + UserInfoDbHelper.TABLE_COMPANY + "/" + newId);
                break;
        }

        if (newId > 0) {
            //向外界通知该ContentProvider里的数据发生了变化 ,以便ContentObserver作出相应
            getContext().getContentResolver().notifyChange(uri, null);
            return newUri;
        }
        throw new IllegalArgumentException("Failed to insert row info" + uri);
    }


    /**
     * 跨进程时发生在工作线程
     *
     * @param uri
     * @return
     */
    @Nullable
    @Override
    public String getType(Uri uri) {
        Log.i(TAG, "getType: current thread: " + Thread.currentThread().getName());

        switch (sUriMatcher.match(uri)){

            case USERINFO_CODE:
            case COMPANY_CODE:
                return CONTENT_TYPE;

            case USERINFO_ITEM_CODE:
            case COMPANY_ITEM_CODE:
                return CONTNET_TYPE_ITEM;

            default:
                throw new RuntimeException("错误的 uri");
        }
    }


    /**
     * 夸进程时发生在工作线程
     *
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.i(TAG, "update: current thread: " + Thread.currentThread().getName());
        return 0;

    }

    /**
     * 夸进程时发生在工作线程
     *
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.i(TAG, "delete: current thread: " + Thread.currentThread().getName());
        return 0;
    }

}
