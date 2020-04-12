package com.guohao.providertest1;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

// 自定义一个内容观察者ContentObserver
class ContentObserverSubClass extends ContentObserver {

    private static final String TAG = "ContentObserverSubClass";
    Context mContext;
    long lastTime;

    public ContentObserverSubClass(Handler handler, Context context) {
        super(handler);
        mContext = context;
        lastTime = System.currentTimeMillis();
    }

    //采用时间戳避免多次调用onChange( )
    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Log.e(TAG," ContentObserver onChange() selfChange = "+ selfChange);

//        if (System.currentTimeMillis() - lastTime > 2000) {
//            queryUserInfo();
//            lastTime=System.currentTimeMillis();
//        }else{
//            System.out.println("时间间隔过短,忽略此次更新");
//        }

        queryCompanyInfo();

    }

    @Override
    public boolean deliverSelfNotifications() {
        return true;
    }

    /**
     * 通过电话号码查询相关信息
     */
    private void queryUserInfo() {
        Uri queryUri = Uri.parse("content://com.guohao.providertest1/userinfo/123456") ;

        Cursor cursor = mContext.getContentResolver().query(
                queryUri,
                new String[]{
                        UserInfoDbHelper.DESC_COLUMN,
                        UserInfoDbHelper.COMP_ID_COLUMN,
                        UserInfoDbHelper.TEL_COLUMN},
                null,
                null,
                null);

//        if(cursor.moveToFirst()){
//            Toast.makeText(this,
//                    " 描述信息 " + cursor.getString(0)
//                            +" 公司id "+cursor.getString(1)
//                            +" 电话来自 "+cursor.getString(2),Toast.LENGTH_SHORT).show();
//        }

        while (cursor.moveToNext()) {

            Log.e(TAG," 描述信息 " + cursor.getString(0)
                            +" 公司id "+cursor.getString(1)
                            +" 电话来自 "+cursor.getString(2));
        }

        cursor.close();
    }

    private void queryCompanyInfo(){
        Cursor cursor = mContext.getContentResolver().query(
                // "content://com.guohao.providertest1/company"
                SimpleContentProvider.COMPANY_CONTENT_URI,
                new String[]{
                        UserInfoDbHelper.ID_COLUMN,
                        UserInfoDbHelper.BUSINESS_COLUMN,
                        UserInfoDbHelper.ADDR_COLUMN},
                null,
                null,
                null);

//        StringBuffer sb = new StringBuffer();
//
//        while (cursor.moveToNext()){
//            sb.append("id: "+cursor.getString(0)+" business: "+cursor.getString(1)+" address: "+cursor.getString(2));
//            sb.append("\n");
//        }

        while (cursor.moveToNext()) {

            Log.e(TAG," 描述信息 " + cursor.getString(0)
                    +" 公司id "+cursor.getString(1)
                    +" 电话来自 "+cursor.getString(2));
        }

        cursor.close();


    }
}
