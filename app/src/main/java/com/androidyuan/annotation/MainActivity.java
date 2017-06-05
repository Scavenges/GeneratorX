package com.androidyuan.annotation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.androidyuan.model.PersonBeanX;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        PersonBeanX personBeanX=new PersonBeanX();
        personBeanX.setAge(15);
        PersonBeanX personBeanX1=new PersonBeanX();
        personBeanX1.setAge(15);

        toast(personBeanX.equals(personBeanX1)+"");
    }


    private void toast(String str)
    {
        Toast.makeText(this,str,Toast.LENGTH_LONG).show();
    }




}
