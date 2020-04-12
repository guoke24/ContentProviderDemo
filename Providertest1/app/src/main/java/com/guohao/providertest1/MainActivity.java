package com.guohao.providertest1;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText etUserDesc,etUserPhoneNumber,etUserCompanyId;
    private EditText etCompanyId,etCompanyBussiness,etCompanyAddress ;
    private Button btnSubmitUserInfo,btnSubmitCompany;

    private ContentObserverSubClass mContentObserverSubClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidget();

        mContentObserverSubClass = new ContentObserverSubClass(null,this);
        getContentResolver().registerContentObserver(SimpleContentProvider.COMPANY_CONTENT_URI,true,mContentObserverSubClass);
    }

    // 初始化组件
    private void initWidget(){
        etUserDesc = (EditText) findViewById(R.id.et_user_desc);
        etUserPhoneNumber = (EditText) findViewById(R.id.et_user_phone_number);
        etUserCompanyId = (EditText) findViewById(R.id.et_user_company_id);

        etCompanyId = (EditText) findViewById(R.id.et_company_id);
        etCompanyBussiness  = (EditText) findViewById(R.id.et_company_bussiness);
        etCompanyAddress = (EditText) findViewById(R.id.et_company_address);

        btnSubmitUserInfo = (Button) findViewById(R.id.btn_save_userinfo);
        btnSubmitCompany = (Button) findViewById(R.id.btn_save_company);

        btnSubmitUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //存数用户信息数据
                saveUserInfoRecord();

                btnSubmitUserInfo.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        queryUserInfo();
                    }
                },1000);
            }
        });


        btnSubmitCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCompanyInfo();

                btnSubmitCompany.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        queryCompanyInfo();
                    }
                },3000) ;
            }
        });

    }


    /**
     * 存储用户信息到 ContentProvider
     */
    private void saveUserInfoRecord() {
        ContentValues newRecord = new ContentValues();

        newRecord.put(UserInfoDbHelper.DESC_COLUMN, etUserDesc.getText().toString());
        newRecord.put(UserInfoDbHelper.COMP_ID_COLUMN, etUserCompanyId.getText().toString());
        newRecord.put(UserInfoDbHelper.TEL_COLUMN, etUserPhoneNumber.getText().toString());

        // "content://com.guohao.providertest1/userinfo"
        getContentResolver().insert(SimpleContentProvider.USERINFO_CONTENT_URI,newRecord) ;

        // 先去 AndroidManifest.xml 中找到 authorities="com.guohao.providertest1" 的 provider
        // 再调用上述的 provider 的 insert 函数
    }

    /**
     * 存储公司信息到 ContentProvider
     */
    private void saveCompanyInfo() {
        ContentValues newRecord = new ContentValues();
        newRecord.put(UserInfoDbHelper.ID_COLUMN,etCompanyId.getText().toString());
        newRecord.put(UserInfoDbHelper.BUSINESS_COLUMN,etCompanyBussiness.getText().toString());
        newRecord.put(UserInfoDbHelper.ADDR_COLUMN,etCompanyAddress.getText().toString());
        getContentResolver().insert(SimpleContentProvider.COMPANY_CONTENT_URI,newRecord);
    }

    /**
     * 通过电话号码查询相关信息
     */
    private void queryUserInfo() {
        Uri queryUri = Uri.parse("content://com.guohao.providertest1/userinfo/123456") ;

        Cursor cursor = getContentResolver().query(
                queryUri,
                new String[]{
                        UserInfoDbHelper.DESC_COLUMN,
                        UserInfoDbHelper.COMP_ID_COLUMN,
                        UserInfoDbHelper.TEL_COLUMN},
                null,
                null,
                null);

        if(cursor.moveToFirst()){
            Toast.makeText(this,
                    " 描述信息"+cursor.getString(0)
                            +" 公司id"+cursor.getString(1)
                            +" 电话来自"+cursor.getString(2),Toast.LENGTH_SHORT).show();
        }

        cursor.close();
    }

    private void queryCompanyInfo(){
        Cursor cursor = getContentResolver().query(
                // "content://com.guohao.providertest1/company"
                SimpleContentProvider.COMPANY_CONTENT_URI,
                new String[]{
                        UserInfoDbHelper.ID_COLUMN,
                        UserInfoDbHelper.BUSINESS_COLUMN,
                        UserInfoDbHelper.ADDR_COLUMN},
                null,
                null,
                null);

        StringBuffer sb = new StringBuffer();

        while (cursor.moveToNext()){
            sb.append("id: "+cursor.getString(0)+" business: "+cursor.getString(1)+" address: "+cursor.getString(2));
            sb.append("\n");
        }

        Toast.makeText(this,sb.toString(),Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mContentObserverSubClass!=null) {
            this.getContentResolver().unregisterContentObserver(mContentObserverSubClass);
        }
    }

}
