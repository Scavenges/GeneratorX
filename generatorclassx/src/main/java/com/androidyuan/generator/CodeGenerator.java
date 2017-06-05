package com.androidyuan.generator;

import static com.squareup.javapoet.MethodSpec.constructorBuilder;
import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;

import static javax.lang.model.element.Modifier.PUBLIC;

import com.androidyuan.Helper.ClsHelper;
import com.androidyuan.SimpleGenerator;
import com.androidyuan.type.BaseType;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.tools.Diagnostic;

/**
 * Created by wei on 17-1-4.
 * 代码生成器
 */
public class CodeGenerator {

    private static final String ANNOTATION = "@" + SimpleGenerator.class.getSimpleName();

    protected AnnotatedClass anno;
    protected Messager messager;
    protected ProcessingEnvironment environment;

    public CodeGenerator(AnnotatedClass anno, ProcessingEnvironment environment) {

        this.anno = anno;
        this.messager = environment.getMessager();
        this.environment = environment;
    }

    // 构建类
    public TypeSpec generateClass() {


        TypeSpec.Builder builder = classBuilder(anno.annotatedClassName + "X")//1. cls name
                .addModifiers(PUBLIC)//2.public
                .superclass(TypeName.get(anno.getType()));//3.Type


        builder.addMethod(makeDefaultConstructMethod());
        builder.addMethod(makeConstructMethod());

        for (String variablename : anno.variableNames) {

            builder.addField(//private Type variablename
                    getFiledType(variablename),
                    variablename,
                    Modifier.PRIVATE
            );
            builder.addMethod(makeSetMethod(variablename));
            builder.addMethod(makeGetMethod(variablename));
        }
        builder.addMethod(makeHascodeMethod());
        builder.addMethod(makeEqualsMethod());
        builder.addMethod(makeToStringMethod());
        return builder.build();
    }


    /**
     * 生成构造方法
     *
     * @return public PersonBean(int age,String name)
     */
    protected MethodSpec makeDefaultConstructMethod() {


        //方法的 头 和 尾
        MethodSpec.Builder builder = constructorBuilder().addModifiers(PUBLIC);//publicvoid setField

        return builder.build();
    }

    /**
     * 生成全部参数的构造构造方法
     *
     * @return public PersonBean(int age,String name)
     */
    protected MethodSpec makeConstructMethod() {


        //方法的 头 和 尾
        MethodSpec.Builder builder = constructorBuilder().addModifiers(PUBLIC);//publicvoid setField

        for (String variablename : anno.variableNames) {
            builder.addParameter(getFiledType(variablename), variablename);//(String variableNames)
        }

        for (String variablename : anno.variableNames) {
            builder.addStatement("this." + variablename + " = " + variablename);
            //方法内的几行code this.variable = var;
        }

        return builder.build();
    }

    /**
     * 生成get方法
     *
     * @return void getS(String s)
     */
    protected MethodSpec makeGetMethod(String variable) {

        //方法内的几行code this.variable = var;
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("return this.%s", variable));

        //方法的 头 和 尾
        return methodBuilder(
                "get" + com.androidyuan.Helper.ClsHelper.bigFirstString(variable)).addModifiers(
                PUBLIC).returns(
                getFiledType(variable))//public Type getField
                .addStatement(builder.toString())// 添加方法内的几行代码到这里
                .build();
    }

    /**
     * 生成hascode方法
     *
     * @return @Override public int toString()
     */
    protected MethodSpec makeToStringMethod() {

        //方法内的几行code this.variable = var;
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("return \"%s{\"+", anno.generatorClassName));

        String vari = "";
        for (int iName = 0; iName < anno.variableNames.size(); iName++) {
            vari = anno.variableNames.get(iName);
            if (iName == anno.variableNames.size() - 1) {
                break;
            }
            if (BaseType.isBaseType(TypeName.get(anno.getFiledType(vari)))) {
                builder.append("\"" + vari + " =  \"+" + vari + "+\",\"+");
            } else {
                builder.append("\"" + vari + " =  \"+" + vari + ".toString() + \",\" + ");
            }
        }
        if (BaseType.isBaseType(TypeName.get(anno.getFiledType(vari)))) {
            builder.append("\"" + vari + " =  \"+" + vari);
        } else {
            builder.append("\"" + vari + " =  \"+" + vari + ".toString()");
        }


        builder.append("+\"}\"");

        //方法的 头 和 尾
        return methodBuilder("toString")// toString()
                .addModifiers(PUBLIC)//public
                .returns(String.class)// String
                .addAnnotation(Override.class)//@Override
                .addStatement(builder.toString())// 添加方法内的几行代码到这里
                .build();
    }


    /**
     * 生成hascode方法
     *
     * @return @Override public int toString()
     */
    protected MethodSpec makeEqualsMethod() {

        //方法内的几行code this.variable = var;
        StringBuilder builder = new StringBuilder();
        builder.append("\nif (o == null) return false;");
        builder.append("\nif (o == this) return true;");

        builder.append("\nif (o instanceof "+anno.generatorClassName+") {");
//        builder.append("\n\t"+anno.generatorClassName+" that = ("+anno.generatorClassName+") o;");
        builder.append("\n\tif (o.hashCode() == this.hashCode()) return true;");
        builder.append("\n}");
        builder.append("\nreturn false");

        //方法的 头 和 尾
        return methodBuilder("equals")//
                .addModifiers(PUBLIC)//public
                .addParameter(Object.class,"o")
                .returns(boolean.class)// String
                .addAnnotation(Override.class)//@Override
                .addStatement(builder.toString())// 添加方法内的几行代码到这里
                .build();
    }


    /**
     * 生成hascode方法
     *
     * @return @Override public int hashCode()
     */
    protected MethodSpec makeHascodeMethod() {

        ClassName namedBoards = ClassName.get("com.google.common.base", "Objects");

        //方法内的几行code this.variable = var;
        StringBuilder builder = new StringBuilder();
        builder.append("return $T.hashCode(");

        String vari = "";
        for (int iName = 0; iName < anno.variableNames.size(); iName++) {
            vari = anno.variableNames.get(iName);
            if (iName == anno.variableNames.size() - 1) {
                break;
            }
            builder.append(vari + " , ");
        }
        builder.append(vari);


        builder.append(")");

        //方法的 头 和 尾
        return methodBuilder("hashCode")// hashCode()
                .addModifiers(PUBLIC)//public
                .returns(int.class)// int
                .addAnnotation(Override.class)//@Override
                .addStatement(builder.toString(), ClsHelper.class)// 添加方法内的几行代码到这里
                .build();
    }

    /**
     * 生成set方法
     * @param variable
     * @return String getS(String s)
     */
    protected MethodSpec makeSetMethod(String variable) {

        //方法内的几行code this.variable = var;
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("this.%s", variable));
        builder.append(" = " + variable);

        //方法的 头 和 尾
        return methodBuilder(
                "set" + com.androidyuan.Helper.ClsHelper.bigFirstString(variable)).addModifiers(
                PUBLIC).returns(
                TypeName.VOID)//public void setField
                .addParameter(getFiledType(variable), variable)//(String variableNames)
                .addStatement(builder.toString())// 添加方法内的几行代码到这里
                .build();
    }

    /**
     * 取得 制定 变量的 type
     * @param variable
     * @return type
     */
    protected TypeName getFiledType(String variable) {

        TypeName name = TypeName.get(anno.getFiledType(variable));
        if (name == null) {
            String message = String.format(
                    "Classes annotated with %s variable does not exist.",
                    ANNOTATION
            );
            messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    message,
                    anno.typeElement
            );
        }
        return name;
    }


}