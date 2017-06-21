package com.androidyuan;

import static com.androidyuan.Helper.ClsHelper.isClassOfType;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.tools.Diagnostic.Kind.ERROR;

import com.androidyuan.generator.AnnotatedClass;
import com.androidyuan.generator.CodeGenerator;
import com.androidyuan.generator.ParcelableGenerator;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;


public class GeneratorProcessor extends AbstractProcessor {

    private static final String ANNOTATION = "@" + SimpleGenerator.class.getSimpleName();

    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {

        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {

        return Collections.singleton(SimpleGenerator.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {

        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        List<AnnotatedClass> annotatedClasses = new ArrayList<>();
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(SimpleGenerator.class)) {
            //检测被注解的是不是一个类
            if (annotatedElement.getKind() == ElementKind.CLASS) {
                //不能像下边这么写，因为Element和TypeElement的类型是一样的
//            if (annotatedElement instanceof TypeElement) {
                // Our annotation is defined with @Target(value=TYPE)
                TypeElement element = (TypeElement) annotatedElement;
                if (!isValidClass(element)) {

                    return true;
                }
                try {

                    AnnotatedClass annotatedClass = buildAnnotatedClass(element, roundEnv);
                    annotatedClasses.add(annotatedClass);
                } catch (Exception e) {
                    String message = String.format(
                            "Couldn't process class %s: %s",
                            element,
                            e.getMessage()
                    );
                    messager.printMessage(ERROR, message, annotatedElement);
                }
            }
        }
        try {
            generate(annotatedClasses);

        } catch (NoPackageNameException | IOException e) {
            messager.printMessage(ERROR, "Couldn't generate class");
        }


        Messager messager = processingEnv.getMessager();
        for (TypeElement te : annotations) {
            for (Element e : roundEnv.getElementsAnnotatedWith(te)) {

                messager.printMessage(
                        Diagnostic.Kind.NOTE,
                        "Processor Printing: " + e.toString()
                );
            }
        }
        return true;
    }

    // 构建被 @SimpleGenerator 注解的类
    private AnnotatedClass buildAnnotatedClass(TypeElement typeElement, RoundEnvironment roundEnv) throws
            NoPackageNameException, IOException, ClassNotFoundException {
        //成员变量的数据类型
        HashMap<String, TypeMirror> variableMap = new HashMap<>();
        //成员变量的注解类型
        HashMap<String, ArrayList<AnnotationSpec>> variableAnooMap = new HashMap<>();
        ArrayList<String> variableNames = new ArrayList<>();
        for (Element element : typeElement.getEnclosedElements()) {
            //过滤 字段 的Element
            if (!(element instanceof VariableElement)) {
                continue;
            }
            //过滤 static final 字段
            if (element.getModifiers().contains(STATIC) || element.getModifiers().contains(FINAL)) {
                continue;
            }
            VariableElement variableElement = (VariableElement) element;
            variableNames.add(variableElement.getSimpleName().toString());
            variableMap.put(
                    variableElement.getSimpleName().toString(),
                    variableElement.asType()
            );
            /**
             *     messager.printMessage(
             *     Diagnostic.Kind.ERROR,
             *     String.format(i + element.getKind().toString(), this),
             *     element);
             *     使用message输出elment的类型，如下，第一个居然检测的事构造函数，妈蛋，把它忽略，从第一个变量开始
             *     Error:(10, 17) 错误: 0CONSTRUCTOR
             *     Error:(13, 17) 错误: 1FIELD
             *     Error:(15, 19) 错误: 2FIELD
             *     Error:(17, 16) 错误: 3FIELD
             *     Error:(19, 25) 错误: 4FIELD
             */
            List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
            if (element.getKind() != ElementKind.CONSTRUCTOR) {
                //保存多个注解
                ArrayList<AnnotationSpec> annotationSpecs = new ArrayList<>();
                for (int i = 0; i < annotationMirrors.size(); i++) {
                    //返回此注解的类型
                    DeclaredType declaredType = annotationMirrors.get(i).getAnnotationType();
                    //asElement()返回对应于此类型的元素。
                    //getEnclosingType() 返回封装实例的最里层类型；如果没有任何封装实例，则返回种类为 NONE 的 NoType。
                    //getTypeArguments()返回此类型的实际类型参数。
                    //检测元素类型，各种蒙
                    ElementKind elementKind = declaredType.asElement().getKind();
                    //记录注解的全类名
                    String simpleName = null;
                    if (elementKind == ElementKind.ANNOTATION_TYPE) {
                        simpleName = declaredType.asElement().toString();
                    }
                    //返回此注释元素的值，是一个键值对的形式返回,有可能是多个。
                    Map<? extends ExecutableElement, ? extends AnnotationValue> map = annotationMirrors.get(i).getElementValues();

//                    ==============单个注释==================
//                    // 注解key
//                    String key = map.keySet().iterator().next().getSimpleName().toString();
//                    //注解value()返回的是一个集合Collection，通过迭代出下一个的值
//                    String value = (String) map.values().iterator().next().getValue();
//                    //通过全类名返回一个类，此类用于生成注解
//                    Class clazz = Class.forName(simpleName);
//                    //创建一个新的注解并增加初始值
//                    AnnotationSpec annotationSpec = AnnotationSpec.builder(clazz).addMember(key, "$S", value).build();
//                    annotationSpecs.add(annotationSpec);
//                    //打印信息
//                    messager.printMessage(
//                            Diagnostic.Kind.ERROR,
//                            String.format("全类名：" + simpleName + "   注解key：" + key + "   注解值：" + value, this),
//                            element);


                    //=========================多个注释值=============================
                    //妈蛋，突然想到用户有可能自定义注解，并且会有多个注解值，看来这块需要遍历保存
//                    for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : map.entrySet()) {
//                        messager.printMessage(
//                                Diagnostic.Kind.ERROR,
//                                String.format("   map:"+map.entrySet()+"   key:" + entry.getKey() + "   value:" + entry.getValue(), this),
//                                element);
//
//
//                    }

                    //通过全类名返回一个类，此类用于生成注解
                    Class clazz = Class.forName(simpleName);
                    //创建一个新的注解
                    AnnotationSpec.Builder annotationBuilder = AnnotationSpec.builder(clazz);
                    for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : map.entrySet()) {
                        //注解key
                        String key = entry.getKey().getSimpleName().toString();
                        //注解value()返回的是一个集合Collection，通过迭代出下一个的值
                        Object value = entry.getValue().getValue();
                        //增加注解初始值
                        annotationBuilder.addMember(key, "$S", value);
                        //打印信息，在此处遇到一个问题，下边的打印方法貌似会覆盖之前的打印数据。
                        messager.printMessage(
                                Diagnostic.Kind.ERROR,
                                String.format("全类名：" + simpleName + "   注解key：" + key + "   注解值：" + value, this),
                                element);
                    }
                    //new一个新的注解空间
                    AnnotationSpec annotationSpec = annotationBuilder.build();
                    //加到注解的集合中
                    annotationSpecs.add(annotationSpec);
                }
                variableAnooMap.put(
                        variableElement.getSimpleName().toString(),
                        annotationSpecs
                );
            }
        }

        //打印 用于测试 是否为 parcelable
        if (isParcelable(typeElement)) {
            String message = String.format("Classes %s is parceleble.", ANNOTATION);
            //messager.printMessage(Diagnostic.Kind.OTHER, message, typeElement);
        }
        return new AnnotatedClass(typeElement, variableNames, variableMap, variableAnooMap,
                isParcelable(typeElement));
    }

    // 生成 源代码
    private void generate(List<AnnotatedClass> list) throws NoPackageNameException, IOException {

        if (list.size() == 0) {
            return;
        }
        for (AnnotatedClass annotatedClass : list) {
            // debug
            String message =
                    annotatedClass.annotatedClassName + " / " + annotatedClass.typeElement + " / "
                            + Arrays.toString(annotatedClass.variableNames.toArray());
            //messager.printMessage(Diagnostic.Kind.NOTE, message, annotatedClass.typeElement);//这里如果不注视掉会 在build的时候停止 但run可以直接run
        }

        // 生成源代码
        String packageName = getPackageName(processingEnv.getElementUtils(),
                list.get(0).typeElement);


        for (AnnotatedClass anno : list) {

            TypeSpec generatedClass;
            if (anno.isParcelable()) {
                generatedClass = new ParcelableGenerator(anno, processingEnv).generateClass();
            } else {
                generatedClass = new CodeGenerator(anno, processingEnv).generateClass();
            }
            JavaFile javaFile = JavaFile.builder(packageName, generatedClass)
                    .build();

            // 在 app module/build/generated/source/apt 生成一份源代码
            javaFile.writeTo(processingEnv.getFiler());
        }
    }

    private boolean isPublic(TypeElement element) {

        return element.getModifiers().contains(PUBLIC);
    }

    private boolean isAbstract(TypeElement element) {

        return element.getModifiers().contains(ABSTRACT);
    }

    private boolean isValidClass(TypeElement element) {

        if (!isPublic(element)) {
            String message = String.format("Classes annotated with %s must be public.", ANNOTATION);
            messager.printMessage(Diagnostic.Kind.ERROR, message, element);
            return false;
        }

        if (!isAbstract(element)) {
            String message = String.format("Classes annotated with %s must be abstract.",
                    ANNOTATION);
            messager.printMessage(Diagnostic.Kind.ERROR, message, element);
            return false;
        }

        return true;
    }

    private String getPackageName(Elements elements, TypeElement typeElement) throws
            NoPackageNameException {

        PackageElement pkg = elements.getPackageOf(typeElement);
        if (pkg.isUnnamed()) {
            throw new NoPackageNameException(typeElement);
        }
        return pkg.getQualifiedName().toString();
    }


    public boolean isParcelable(TypeElement typeElement) {

        TypeElement parcelable = processingEnv.getElementUtils().getTypeElement(
                "android.os.Parcelable");
        TypeMirror cls = typeElement.asType();
        return isClassOfType(
                processingEnv.getTypeUtils(),
                parcelable.asType(),
                cls);
    }


}
