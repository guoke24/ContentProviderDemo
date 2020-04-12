package com.guohao.providertest2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "contentprovidertest.db"; /*数据库名*/
    private static final int DB_VERSION = 1;/*版本号*/

    public SQLiteDatabaseOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table person" + "(" +
                "personid integer primary key autoincrement," +
                "name varchar(20)," +
                "phone varchar(12)," +
                "salary  Integer(12)" + ")"
        );
    }

    //当数据库版本号发生变化时调用该方法
    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        //db.execSQL(ALTER TABLE person ADD phone varchar(12) NULL);
        //db.execSQL(ALTER TABLE person ADD salary  Integer NULL);
    }

}
