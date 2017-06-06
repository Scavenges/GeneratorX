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
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
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
            if (annotatedElement instanceof TypeElement) {
                // Our annotation is defined with @Target(value=TYPE)
                TypeElement element = (TypeElement) annotatedElement;
                if (!isValidClass(element)) {

                    return true;
                }
                try {

                    AnnotatedClass annotatedClass = buildAnnotatedClass(element);
                    annotatedClasses.add(annotatedClass);
                } catch (NoPackageNameException | IOException e) {
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
    private AnnotatedClass buildAnnotatedClass(TypeElement typeElement) throws
            NoPackageNameException, IOException {

        HashMap<String, TypeMirror> variableMap = new HashMap<>();
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
        }

        //打印 用于测试 是否为 parcelable
        if (isParcelable(typeElement)) {
            String message = String.format("Classes %s is parceleble.", ANNOTATION);
            //messager.printMessage(Diagnostic.Kind.OTHER, message, typeElement);
        }
        return new AnnotatedClass(typeElement, variableNames, variableMap,
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
