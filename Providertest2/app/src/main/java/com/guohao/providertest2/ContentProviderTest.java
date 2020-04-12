package com.guohao.providertest2;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Demo描述:
 * 自定义ContentProvider的实现
 * ContentProvider主要用于在不同的应用程序之间共享数据,这也是官方推荐的方式.
 * <p>
 * 注意事项:
 * 1 在AndroidManifest.xml中注册ContentProvider时的属性
 * android:exported=true表示允许其他应用访问.
 * 2 注意*和#这两个符号在Uri中的作用
 * 其中*表示匹配任意长度的字符
 * 其中#表示匹配任意长度的数据
 * 所以：
 * 一个能匹配所有表的Uri可以写成:
 * content://cn.bs.testcontentprovider/*
 * 一个能匹配person表中任意一行的Uri可以写成:
 * content://cn.bs.testcontentprovider/person/#
 */
public class ContentProviderTest extends ContentProvider {

    static final String TAG = "providertest2";

    private SQLiteDatabaseOpenHelper mSQLiteDatabaseOpenHelper;
    private final static String AUTHORITY = "cn.bs.testcontentprovider";
    private static UriMatcher mUriMatcher;
    private static final int PERSON_DIR = 0;
    private static final int PERSON = 1;

    /**
     * 利用静态代码块初始化UriMatcher
     * 在UriMatcher中包含了多个Uri,每个Uri代表一种操作
     * 当调用UriMatcher.match(Uri uri)方法时就会返回该uri对应的code;
     * 比如此处的PERSONS和PERSON
     */
    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // 该URI表示返回所有的person,其中 PERSON_DIR 为该特定 Uri 的标识码
        mUriMatcher.addURI(AUTHORITY, "person", PERSON_DIR);

        // 该URI表示返回某一个person,其中 PERSON 为该特定 Uri 的标识码
        mUriMatcher.addURI(AUTHORITY, "person/#", PERSON);
    }


    /**
     * 在自定义ContentProvider中必须覆写getType(Uri uri)方法.
     * 该方法用于获取Uri对象所对应的MIME类型.
     * <p>
     * 一个Uri对应的MIME字符串遵守以下三点:
     * 1  必须以vnd开头
     * 2  如果该Uri对应的数据可能包含多条记录,那么返回字符串应该以vnd.android.cursor.dir/开头
     * 3  如果该Uri对应的数据只包含一条记录,那么返回字符串应该以vnd.android.cursor.item/开头
     */
    @Override
    public String getType(Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case PERSON_DIR:
                return "vnd.android.cursor.dir/" + AUTHORITY + ".persons";
            case PERSON:
                return "vnd.android.cursor.item/" + AUTHORITY + ".person";
            default:
                throw new IllegalArgumentException("unknown uri " + uri.toString());
        }
    }


    @Override
    public boolean onCreate() {
        Log.e(TAG,"onCreate");
        mSQLiteDatabaseOpenHelper = new SQLiteDatabaseOpenHelper(getContext());
        return true;
    }


    /**
     * 插入操作:
     * 插入操作只有一种可能:向一张表中插入
     * 返回结果为新增记录对应的Uri
     * 方法db.insert()返回结果为新增记录对应的主键值
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mSQLiteDatabaseOpenHelper.getWritableDatabase();
        switch (mUriMatcher.match(uri)) {
            case PERSON_DIR:
                //long newId = db.insert("person", name,phone,salary, values);
                long newId = db.insert("person", null, values);
                //向外界通知该ContentProvider里的数据发生了变化 ,以便ContentObserver作出相应
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, newId);
            default:
                throw new IllegalArgumentException("unknown uri" + uri.toString());
        }
    }

    /**
     * 更新操作:
     * 更新操作有两种可能:更新一张表或者更新某条数据
     * 在更新某条数据时原理类似于查询某条数据,见下.
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mSQLiteDatabaseOpenHelper.getWritableDatabase();
        int updatedNum = 0;
        switch (mUriMatcher.match(uri)) {
            // 更新表
            case PERSON_DIR:
                updatedNum = db.update("person", values, selection, selectionArgs);
                break;
            // 按照id更新某条数据
            case PERSON:
                long id = ContentUris.parseId(uri);
                String where = "personid= " + id;
                if (selection != null && !"".equals(selection.trim())) {
                    where = selection + " and " + where;
                }
                updatedNum = db.update("person", values, where, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("unknown uri " + uri.toString());
        }
        //向外界通知该ContentProvider里的数据发生了变化 ,以便ContentObserver作出相应
        getContext().getContentResolver().notifyChange(uri, null);
        return updatedNum;
    }

    /**
     * 删除操作:
     * 删除操作有两种可能:删除一张表或者删除某条数据
     * 在删除某条数据时原理类似于查询某条数据,见下.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mSQLiteDatabaseOpenHelper.getWritableDatabase();
        int deletedNum = 0;
        switch (mUriMatcher.match(uri)) {
            // 删除表
            case PERSON_DIR:
                deletedNum = db.delete("person", selection, selectionArgs);
                break;
            // 按照id删除某条数据
            case PERSON:
                long id = ContentUris.parseId(uri);
                String where = "personid= " + id;
                if (selection != null && !"".equals(selection.trim())) {
                    where = selection + "and" + where;
                }
                deletedNum = db.delete("person", where, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("unknown uri " + uri.toString());
        }
        //向外界通知该ContentProvider里的数据发生了变化 ,以便ContentObserver作出相应
        getContext().getContentResolver().notifyChange(uri, null);
        return deletedNum;
    }

    /**
     * 查询操作:
     * 查询操作有两种可能:查询一张表或者查询某条数据
     * <p>
     * 注意事项:
     * 在查询某条数据时要注意--因为此处是按照personid来查询
     * 某条数据,但是同时可能还有其他限制.例如:
     * 要求personid为2且name为xiaoming1
     * 所以在查询时分为两步:
     * 第一步:
     * 解析出personid放入where查询条件
     * 第二步:
     * 判断是否有其他限制(如name),若有则将其组拼到where查询条件.
     * <p>
     * 详细代码见下.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mSQLiteDatabaseOpenHelper.getWritableDatabase();
        Cursor cursor = null;
        switch (mUriMatcher.match(uri)) {
            // 查询表
            case PERSON_DIR:
                cursor = db.query("person", projection, selection, selectionArgs, null, null, sortOrder);
                break;
            // 按照id查询某条数据
            case PERSON:
                // 第一步:
                long id = ContentUris.parseId(uri);
                String where = "personid = " + id;
                // 第二步:
                if (selection != null && !"".equals(selection.trim())) {
                    where = selection + " and " + where;
                }
                cursor = db.query("person", projection, where, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("unknown uri " + uri.toString());
        }
        return cursor;
    }


}
