package com.androidyuan.model;

import android.os.Parcelable;

import android.support.annotation.UiThread;
import com.androidyuan.SimpleGenerator;

import java.util.Map;


@SimpleGenerator
public abstract class PersonBean implements Parcelable {


    public Enum s;


    public String name;

    public int age = 0;

    @SuppressWarnings("unchecked")
    @Deprecated
    public CharSequence str;
}