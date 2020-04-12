package com.guohao.providertest2_client;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
/**
 * Demo描述:
 * 本应用(providertest2_client)调用另外一个应用(providertest2)中的自定义ContentProvider,即:
 * 1 自定义ContentProvider的使用
 * 2 其它应用调用该ContentProvider
 * 3 ContentObserver的使用
 *
 * 备注说明:
 * 1 融合了ContentObserver的使用
 *   利用ContentObserver随时监听ContentProvider的数据变化.
 *   为实现该功能需要在自定义的ContentProvider的insert(),update(),delete()
 *   方法中调用getContext().getContentResolver().notifyChange(uri, null);
 *   向外界通知该ContentProvider里的数据发生了变化 ,以便ContentObserver作出相应
 *
 * 测试方法:
 * 1 依次测试ContentProvider的增查删改(注意该顺序)!!
 * 2 其它应用查询该ContentProvider的数据
 *
 * 测试结果：
 * 已经调通 增删改查四个功能 和 testType 函数
 */
public class MainActivity extends Activity {
    private static final String TAG = "providertest2_client";
    private Button mAddButton;
    private Button mDeleteButton;
    private Button mUpdateButton;
    private Button mQueryButton;
    private Button mTypeButton;
    private long lastTime=0;
    private ContentResolver mContentResolver;
    private ContentObserverSubClass mContentObserverSubClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initContentObserver();
    }

    private void init() {
        mContentResolver=this.getContentResolver();

        mAddButton=(Button) findViewById(R.id.addButton);
        mAddButton.setOnClickListener(new ClickListenerImpl());

        mDeleteButton=(Button) findViewById(R.id.deleteButton);
        mDeleteButton.setOnClickListener(new ClickListenerImpl());

        mUpdateButton=(Button) findViewById(R.id.updateButton);
        mUpdateButton.setOnClickListener(new ClickListenerImpl());

        mQueryButton=(Button) findViewById(R.id.queryButton);
        mQueryButton.setOnClickListener(new ClickListenerImpl());

        mTypeButton=(Button) findViewById(R.id.typeButton);
        mTypeButton.setOnClickListener(new ClickListenerImpl());

    }

    // 注册一个针对ContentProvider的ContentObserver用来观察内容提供者的数据变化
    private void initContentObserver() {
        Uri uri = Uri.parse("content://cn.bs.testcontentprovider/person");
        mContentObserverSubClass=new ContentObserverSubClass(new Handler());
        this.getContentResolver().registerContentObserver(uri, true,mContentObserverSubClass);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mContentObserverSubClass!=null) {
            this.getContentResolver().unregisterContentObserver(mContentObserverSubClass);
        }
    }

    // 自定义一个内容观察者ContentObserver
    private class ContentObserverSubClass extends ContentObserver {

        public ContentObserverSubClass(Handler handler) {
            super(handler);
        }

        //采用时间戳避免多次调用onChange( )
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            System.out.println(" ContentObserver onChange() selfChange = " + selfChange);
            if (System.currentTimeMillis()-lastTime>2000) {
                ContentResolver resolver = getContentResolver();
                Uri uri = Uri.parse("content://cn.bs.testcontentprovider/person");
                // 获取最新的一条数据
                Cursor cursor = resolver.query(uri, new String[]{"personid","name"}, null, null,null);//"limit 1"
                while (cursor.moveToNext()) {
                    int personid = cursor.getInt(cursor.getColumnIndex("personid"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    Log.i(TAG," 内容提供者中的数据发生变化,现数据中第一条数据的 personid = " + personid + ",name = " + name);
                }
                cursor.close();
                lastTime=System.currentTimeMillis();
            }else{
                Log.e(TAG,"时间间隔过短,忽略此次更新");
            }


        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

    }

    private class ClickListenerImpl implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.addButton:
                    Person person = null;
                    for (int i = 0; i < 5; i++) {
                        person = new Person("xiaoming" + i, "" + (9527 + i), (8888 + i));
                        testInsert(person);
                    }
                    break;
                case R.id.deleteButton:
                    testDelete(14);
                    break;
                case R.id.updateButton:
                    testUpdate(6);
                    break;
                case R.id.queryButton:
                    // 查询表
                    testQuery(0);

                    // 查询personid=2的数据
                    //testQuery(5);
                    break;
                case R.id.typeButton:
                    testType();
                    break;
                default:
                    break;
            }

        }

    }

    private void testInsert(Person person) {
        ContentValues contentValues=new ContentValues();
        contentValues.put("name", person.getName());
        contentValues.put("phone", person.getPhone());
        contentValues.put("salary",person.getSalary());
        Uri insertUri=Uri.parse("content://cn.bs.testcontentprovider/person");
        Uri returnUri=mContentResolver.insert(insertUri, contentValues);
        Log.e(TAG,"新增数据:returnUri = " +returnUri);
    }

    private void testDelete(int index){
        Uri uri=Uri.parse("content://cn.bs.testcontentprovider/person/" + String.valueOf(index));
        int row = mContentResolver.delete(uri, null, null);
        Log.e(TAG,"删除了" + row + "行数据");

    }

    private void testUpdate(int index){
        Uri uri=Uri.parse("content://cn.bs.testcontentprovider/person/" + String.valueOf(index));
        ContentValues values=new ContentValues();
        values.put("name", "hanmeimei");
        values.put("phone", 1234);
        values.put("salary", 333);
        int row = mContentResolver.update(uri, values, null, null);
        Log.e(TAG,"更新了" + row + "行数据");
    }

    private void testQuery(int index) {
        Uri uri=null;
        if (index<=0) {
            //查询表
            uri=Uri.parse("content://cn.bs.testcontentprovider/person");
        } else {
            //按照id查询某条数据
            uri=Uri.parse("content://cn.bs.testcontentprovider/person/" + String.valueOf(index));
        }

        //对应上面的:查询表
        //Cursor cursor= mContentResolver.query(uri, null, null, null, null);

        //对应上面的:查询personid=2的数据
        //注意:因为name是varchar字段的,所以应该写作name='xiaoming1'
        //     若写成name=xiaoming1查询时会报错
        //Cursor cursor= mContentResolver.query(uri, null, "name='xiaoming1'", null, null);

        //查全部
        Cursor cursor= mContentResolver.query(uri, null, null, null, null);

        while(cursor.moveToNext()){
            int personid=cursor.getInt(cursor.getColumnIndex("personid"));
            String name=cursor.getString(cursor.getColumnIndex("name"));
            String phone=cursor.getString(cursor.getColumnIndex("phone"));
            int salary=cursor.getInt(cursor.getColumnIndex("salary"));
            Log.e(TAG,"查询得到:personid = " + personid + ",name=" + name + ",phone = " +
                    phone + ",salary = " + salary);
        }
        cursor.close();
    }

    private void testType(){
        Uri dirUri=Uri.parse("content://cn.bs.testcontentprovider/person");
        String dirType=mContentResolver.getType(dirUri);
        Log.e(TAG,"dirType: "+ dirType);

        Uri itemUri=Uri.parse("content://cn.bs.testcontentprovider/person/3");
        String itemType=mContentResolver.getType(itemUri);
        Log.e(TAG,"itemType:" + itemType);
    }

}
