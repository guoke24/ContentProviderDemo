package com.guohao.providertest1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// 原文链接：https://blog.csdn.net/a992036795/article/details/51610936

public class UserInfoDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "UserInfoDbHelper";

    private static final String DB_NAME = "userinfo.db"; /*数据库名*/
    private static final int DB_VERSION = 1;/*版本号*/

    public static final String TABLE_USER_INFO = "userinfo";/*用户信息表*/
    public static final String TABLE_COMPANY = "company";/*公司表*/

    public static final String TEL_COLUMN = "tel_num";/*电话号码*/
    public static final String DESC_COLUMN = "descr";/*描述*/
    public static final String COMP_ID_COLUMN = "comp_id";/*公司id*/

    public static final String ID_COLUMN = "id";/*公司的id*/
    public static final String BUSINESS_COLUMN = "business";/*公司的业务*/
    public static final String ADDR_COLUMN = "addr";/*公司位置*/

    //  表 userinfo
    //  | 字段名            | 类型          |意义             |
    //  | Tel_num           | TEXT         |电话号码          |
    //  | Desc              | TEXT         |描述              |
    //  | comp_id           | INTEGER      | 公司id           |
    //

    //  表 company
    //  |字段名              |类型           |意义             |
    //  |Id                 |INTEGER        |公司的id          |
    //  |Business           | TEXT          |公司的业务        |
    //  | Addr              | TEXT          | 公司位置         |


    private static final String POSTCODE_TABLE_SQL ="CREATE TABLE IF NOT EXISTS "+TABLE_USER_INFO +" ("
            +TEL_COLUMN+" text ,"
            +COMP_ID_COLUMN+" integer ,"
            +DESC_COLUMN+" text"
            +")" ;

    private static final String COMPANY_TABLE_SQL ="CREATE TABLE IF NOT EXISTS " + TABLE_COMPANY + " ("
            + ID_COLUMN +" integer primary key ,"
            + BUSINESS_COLUMN +" text ,"
            + ADDR_COLUMN + " text"
            +" )" ;


    public UserInfoDbHelper(Context context) {
        super(context, DB_NAME , null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(POSTCODE_TABLE_SQL);
        db.execSQL(COMPANY_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

