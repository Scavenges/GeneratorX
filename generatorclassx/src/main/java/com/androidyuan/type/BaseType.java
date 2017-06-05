package com.androidyuan.type;


import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by wei on 17-1-6.
 */
public class BaseType {

    static final TypeName STRING = ClassName.get("java.lang", "String");
    static final TypeName MAP = ClassName.get("java.util", "Map");
    static final TypeName BOOLEAN = TypeName.get(boolean.class);
    static final TypeName BYTE = TypeName.get(byte.class);
    static final TypeName CHAR = TypeName.get(char.class);
    static final TypeName INT = TypeName.get(int.class);
    static final TypeName LONG = TypeName.get(long.class);
    static final TypeName ENUM = ClassName.get(Enum.class);

    private static final Set<TypeName> VALID_TYPES = new HashSet<>();

    static {
        VALID_TYPES.add(STRING);
        VALID_TYPES.add(MAP);
        VALID_TYPES.add(BOOLEAN);
        VALID_TYPES.add(BYTE);
        VALID_TYPES.add(CHAR);
        VALID_TYPES.add(INT);
        VALID_TYPES.add(LONG);
        VALID_TYPES.add(ENUM);
    }

    public static boolean isBaseType(TypeName tm) {

        if (tm == null) {
            return false;
        }
        return VALID_TYPES.contains(tm);
    }

}
