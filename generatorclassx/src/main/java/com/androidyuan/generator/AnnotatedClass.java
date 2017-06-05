package com.androidyuan.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * Created by wei on 17-1-4.
 */
// 被 @SimpleGenerator 注解的类
public class AnnotatedClass {
    // 整个类元素
    public final TypeElement typeElement;
    // 类名
    public final String annotatedClassName;

    // 生成类名
    public final String generatorClassName;
    // 成员变量
    public final List<String> variableNames;

    //成员变量 以及对应 type
    HashMap<String, TypeMirror> variableMap = new HashMap<>();

    private boolean isParcelable = false;

    public AnnotatedClass(TypeElement typeElement, List<String> variableNames,
            HashMap<String, TypeMirror> map, boolean isParce) {

        this.annotatedClassName = typeElement.getSimpleName().toString();
        generatorClassName = annotatedClassName + "X";
        this.variableNames = variableNames;
        this.typeElement = typeElement;
        this.variableMap = map;
        this.isParcelable = isParce;
    }

    public TypeMirror getType() {

        return typeElement.asType();
    }


    public String getClassPackage() {

        String fullname = typeElement.getQualifiedName().toString();
        String packagename = fullname.replace(typeElement.getSimpleName(), "");
        packagename = packagename.substring(0, packagename.length() - 1);
        return packagename;
    }

    public TypeName getGeneratorClsName() {

        return ClassName.get(getClassPackage(), generatorClassName);
    }



    public TypeMirror getFiledType(String name) {

        if (variableMap.containsKey(name)) {
            return variableMap.get(name);
        } else {
            return null;
        }
    }


    //TODO 保持原来的修饰符
    public Set<Modifier> getFieldModifier(String name) {

        Element selElement = null;
        for (Element element : typeElement.getEnclosedElements()) {

            VariableElement variableElement = (VariableElement) element;
            String varname = variableElement.getSimpleName().toString();
            if (varname.equals(name)) {
                selElement = element;
                break;
            }
        }
        return selElement.getModifiers();
    }

    public boolean isParcelable() {

        return isParcelable;
    }
}
