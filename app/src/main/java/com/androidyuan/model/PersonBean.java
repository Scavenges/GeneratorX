package com.androidyuan.model;

import android.os.Parcelable;

import com.androidyuan.AcodeJson;
import com.androidyuan.FiledJson;
import com.androidyuan.SimpleGenerator;
import com.squareup.javapoet.AnnotationSpec;

import java.util.Map;


@SimpleGenerator
public abstract class PersonBean implements Parcelable {

    @AcodeJson(version = "s")
    public Enum s;

    @FiledJson(value = "name1",num = 0,falg = true)
    public String name;

    @AcodeJson(version = "age1")
    @FiledJson(value = "name1",num = 0,falg = true)
    public int age = 0;

    @AcodeJson(version = "str")
    @FiledJson(value = "name1",num = 0,falg = true)
    public CharSequence str;
}