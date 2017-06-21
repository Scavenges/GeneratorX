package com.androidyuan.annotation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


import java.io.File;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        String path = getClass().getName().substring(0,getClass().getName().lastIndexOf("."));
//        Log.d("post","路径:"+path);

//        PersonBeanX personBeanX=new PersonBeanX();
//        personBeanX.setAge(15);
//        PersonBeanX personBeanX1=new PersonBeanX();
//        personBeanX1.setAge(15);
//
//        toast(personBeanX.equals(personBeanX1)+"");
    }


    private void toast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }


}
