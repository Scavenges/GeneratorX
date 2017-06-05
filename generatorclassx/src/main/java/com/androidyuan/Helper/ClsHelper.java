package com.androidyuan.Helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

/**
 * Created by wei on 17-1-4.
 */
public class ClsHelper {

    //首字母大写
    public static String bigFirstString(String str) {

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    //取第一个字母
    public static String firstString(String str) {

        return str.substring(0, 1);
    }

    public static boolean isClassOfType(Types typeUtils, TypeMirror type, TypeMirror cls) {

        return type != null && typeUtils.isAssignable(cls, type);
    }


    public static int hashCode(Object... objects) {

        List<Object> list = new ArrayList<>();
        for (Object c : objects) {
            if (c != null) {
                list.add(c);
            }
        }

        Object[] notNullList = new Object[list.size()];

        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                notNullList[i] = list.get(i);
            }
        }
        return Arrays.hashCode(notNullList);
    }

}
