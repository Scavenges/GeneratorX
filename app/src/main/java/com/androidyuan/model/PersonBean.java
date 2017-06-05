package com.androidyuan.model;

import android.os.Parcelable;
import com.androidyuan.SimpleGenerator;


@SimpleGenerator
public abstract class PersonBean implements Parcelable {

    public Enum s;
    public String name;
    public int age = 0;

    public CharSequence str;

}