package com.androidyuan.generator;

import static com.squareup.javapoet.MethodSpec.constructorBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;


public class ParcelableGenerator extends CodeGenerator {

    public static final String PARAM_PARCELABLE_DEST = "dest";
    public static final String PARAM_PARCELABLE_FLAGS = "flags";

    public ParcelableGenerator(AnnotatedClass anno, ProcessingEnvironment environment) {

        super(anno, environment);
    }


    public boolean applicable() {

        return true;
    }

    // 构建类
    public TypeSpec generateClass() {


        TypeSpec.Builder builder = classBuilder(anno.annotatedClassName + "X")//1. cls name
                .addModifiers(PUBLIC)//2.public
                .superclass(TypeName.get(anno.getType()));//3.Type


        //construct
        builder.addMethod(makeDefaultConstructMethod());
        builder.addMethod(makeConstructMethod());
        builder.addMethod(makeParcelConstructMethod());
        builder.addMethod(makeWritePercelMethod());

        //variable

        builder.addField(generateCreator());
        for (String variablename : anno.variableNames) {

            builder.addField(//private Type variablename
                    getFiledType(variablename),
                    variablename,
                    Modifier.PRIVATE
            );
            builder.addMethod(makeSetMethod(variablename));
            builder.addMethod(makeGetMethod(variablename));
        }

        //parcelable begin
        builder.addMethod(generateDescribeContents());//describeContents
        builder.addMethod(makeHascodeMethod());//hasCode
        builder.addMethod(makeEqualsMethod());
        builder.addMethod(makeToStringMethod());//toString


        return builder.build();
    }

    /**
     * 生成构造方法
     *
     * @return public PersonBean(int age,String name)
     */
    protected MethodSpec makeParcelConstructMethod() {


        //方法的 头 和 尾  public void setField
        MethodSpec.Builder builder = constructorBuilder().addModifiers(PUBLIC);//

        builder.addParameter(ClassName.bestGuess("android.os.Parcel"), "in");


        ParcelableCodeBlockGenerator blockGenerator = new ParcelableCodeBlockGenerator(anno);
        blockGenerator.generatorReadCodeBlock(builder);


        return builder.build();
    }


    //创建 static CREATOR 字段
    private FieldSpec generateCreator() {

        TypeName type = anno.getGeneratorClsName();

        ClassName creator = ClassName.bestGuess("android.os.Parcelable.Creator");
        TypeName creatorOfClass = ParameterizedTypeName.get(creator, type);

        Types typeUtils = environment.getTypeUtils();
        CodeBlock.Builder ctorCall = CodeBlock.builder();
        ctorCall.add("return new $T(", type);
        ctorCall.add("in");
        ctorCall.indent().indent();
        ctorCall.unindent().unindent();
        ctorCall.add(");\n");

        MethodSpec.Builder createFromParcel = MethodSpec.methodBuilder("createFromParcel")
                .addAnnotation(Override.class);

        createFromParcel
                .addModifiers(PUBLIC)
                .returns(type)
                .addParameter(ClassName.bestGuess("android.os.Parcel"), "in");
        createFromParcel.addCode(ctorCall.build());

        TypeSpec creatorImpl = TypeSpec.anonymousClassBuilder("")
                .superclass(creatorOfClass)
                .addMethod(createFromParcel
                        .build())
                .addMethod(MethodSpec.methodBuilder("newArray")
                        .addAnnotation(Override.class)
                        .addModifiers(PUBLIC)
                        .returns(ArrayTypeName.of(type))
                        .addParameter(int.class, "size")
                        .addStatement("return new $T[size]", type)
                        .build())
                .build();

        return FieldSpec
                .builder(creatorOfClass, "CREATOR", PUBLIC, FINAL, STATIC)
                .initializer("$L", creatorImpl)
                .build();
    }





    MethodSpec generateDescribeContents() {

        return MethodSpec.methodBuilder("describeContents")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(int.class)
                .addStatement("return 0")
                .build();
    }


    MethodSpec makeWritePercelMethod() {

        ParcelableCodeBlockGenerator blockGenerator = new ParcelableCodeBlockGenerator(anno);

        MethodSpec.Builder builder = MethodSpec.methodBuilder("writeToParcel")
                .addAnnotation(Override.class)
                .addParameter(ParcelableCodeBlockGenerator.PARCEL, "dest")
                .addParameter(int.class, "flags")
                .addModifiers(PUBLIC)
                .returns(void.class);

        blockGenerator.generatorWriteCodeBlock(builder);

        return builder.build();
    }


//  private ImmutableMap<TypeMirror, FieldSpec> getTypeAdapters(List<Property> properties) {
//    Map<TypeMirror, FieldSpec> typeAdapters = new LinkedHashMap<>();
//    NameAllocator nameAllocator = new NameAllocator();
//    nameAllocator.newName("CREATOR");
//    for (Property property : properties) {
//      if (property.typeAdapter != null && !typeAdapters.containsKey(property.typeAdapter)) {
//        ClassName typeName = (ClassName) TypeName.get(property.typeAdapter);
//        String name = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, typeName
// .simpleName());
//        name = nameAllocator.newName(name, typeName);
//
//        typeAdapters.put(property.typeAdapter, FieldSpec.builder(
//            typeName, NameAllocator.toJavaIdentifier(name), PRIVATE, STATIC, FINAL)
//            .initializer("new $T()", typeName).build());
//      }
//    }
//    return ImmutableMap.copyOf(typeAdapters);
//  }

    private TypeElement getClsTypeElement() {

        return anno.typeElement;
    }

    private ProcessingEnvironment processingEnvironment() {

        return environment;
    }

}
