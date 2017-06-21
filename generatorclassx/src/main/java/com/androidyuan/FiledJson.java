package com.androidyuan;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * user：yangtao.
 * date：2017/6/20
 * describe：测试注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface FiledJson {
    String value();

    int num();

    boolean falg();
}
