package com.androidyuan.generator;

import static com.androidyuan.generator.ParcelableGenerator.PARAM_PARCELABLE_DEST;
import static com.androidyuan.generator.ParcelableGenerator.PARAM_PARCELABLE_FLAGS;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;

/**
 * Created by wei on 17-1-6.
 */
public class ParcelableCodeBlockGenerator {

    static final TypeName TEXTUTILS = ClassName.get("android.text", "TextUtils");
    static final TypeName STRING = ClassName.get("java.lang", "String");
    static final TypeName MAP = ClassName.get("java.util", "Map");
    static final TypeName LIST = ClassName.get("java.util", "List");
    static final TypeName BOOLEANARRAY = ArrayTypeName.of(boolean.class);
    static final TypeName BYTEARRAY = ArrayTypeName.of(byte.class);
    static final TypeName CHARARRAY = ArrayTypeName.of(char.class);
    static final TypeName INTARRAY = ArrayTypeName.of(int.class);
    static final TypeName LONGARRAY = ArrayTypeName.of(long.class);
    static final TypeName STRINGARRAY = ArrayTypeName.of(String.class);
    static final TypeName SPARSEARRAY = ClassName.get("android.util", "SparseArray");
    static final TypeName SPARSEBOOLEANARRAY = ClassName.get("android.util", "SparseBooleanArray");
    static final TypeName BUNDLE = ClassName.get("android.os", "Bundle");
    static final TypeName PARCEL = ClassName.get("android.os", "Parcel");
    static final TypeName PARCELABLE = ClassName.get("android.os", "Parcelable");
    static final TypeName PARCELABLEARRAY = ArrayTypeName.of(PARCELABLE);
    static final TypeName CHARSEQUENCE = ClassName.get("java.lang", "CharSequence");
    static final TypeName IBINDER = ClassName.get("android.os", "IBinder");
    static final TypeName OBJECTARRAY = ArrayTypeName.of(TypeName.OBJECT);
    static final TypeName SERIALIZABLE = ClassName.get("java.io", "Serializable");
    static final TypeName PERSISTABLEBUNDLE = ClassName.get("android.os", "PersistableBundle");
    static final TypeName SIZE = ClassName.get("android.util", "Size");
    static final TypeName SIZEF = ClassName.get("android.util", "SizeF");
    static final TypeName ENUM = ClassName.get(Enum.class);

    AnnotatedClass anno;

    public ParcelableCodeBlockGenerator(AnnotatedClass anno) {

        this.anno = anno;
    }


    public void generatorWriteCodeBlock(MethodSpec.Builder builder) {

        for (String name : anno.variableNames) {
            generatorwriteElementCodeBlocks(name, builder);
        }
    }


    public void generatorwriteElementCodeBlocks(String name, MethodSpec.Builder builder) {

        StringBuilder blockWrite = new StringBuilder();
        String out = PARAM_PARCELABLE_DEST;
        String flags = PARAM_PARCELABLE_FLAGS;

        TypeName type = TypeName.get(anno.getFiledType(name));


        if (type.equals(STRING)) {
            blockWrite.append(String.format("%s.writeString(%s)", out, name));
        } else if (type.equals(TypeName.BYTE) || type.equals(TypeName.BYTE.box())
                || type.equals(TypeName.INT) || type.equals(TypeName.INT.box())
                || type.equals(TypeName.CHAR) || type.equals(TypeName.CHAR.box())
                || type.equals(TypeName.SHORT)) {
            blockWrite.append(String.format("%s.writeInt(%s)", out, name));
            builder.addStatement(blockWrite.toString());
        } else if (type.equals(TypeName.SHORT.box())) {
            blockWrite.append(String.format("%s.writeInt(%s.intValue())", out, name));
            builder.addStatement(blockWrite.toString());
        } else if (type.equals(TypeName.LONG) || type.equals(TypeName.LONG.box())) {
            blockWrite.append(String.format("%s.writeLong(%s)", out, name));
            builder.addStatement(blockWrite.toString());
        } else if (type.equals(TypeName.FLOAT) || type.equals(TypeName.FLOAT.box())) {
            blockWrite.append(String.format("%s.writeFloat(%s)", out, name));
            builder.addStatement(blockWrite.toString());
        } else if (type.equals(TypeName.DOUBLE) || type.equals(TypeName.DOUBLE.box())) {
            blockWrite.append(String.format("%s.writeDouble(%s)", out, name));
            builder.addStatement(blockWrite.toString());
        } else if (type.equals(TypeName.BOOLEAN) || type.equals(TypeName.BOOLEAN.box())) {
            blockWrite.append(String.format("%s.writeInt(%s ? 1 : 0)", out, name));
            builder.addStatement(blockWrite.toString());
        } else if (type.equals(PARCELABLE)) {
            blockWrite.append(String.format("%s.writeParcelable(%s, %s)", out, name, flags));
            builder.addStatement(blockWrite.toString());
        } else if (type.equals(CHARSEQUENCE)) {
            blockWrite.append(String.format("$T.writeToParcel(%s, %s, %s)", name, out, flags));
            builder.addStatement(blockWrite.toString(), TEXTUTILS);
        } else if (type.equals(MAP)) {
            blockWrite.append(String.format("%s.writeMap(%s)", out, name));
            builder.addStatement(blockWrite.toString());
        } else if (type.equals(LIST)) {
            blockWrite.append(String.format("%s.writeList(%s)", out, name));
            builder.addStatement(blockWrite.toString());
        } else if (type.equals(BOOLEANARRAY)) {
            blockWrite.append(String.format("%s.writeBooleanArray(%s)", out, name));
            builder.addStatement(blockWrite.toString());
        } else if (type.equals(BYTEARRAY)) {
            blockWrite.append(String.format("%s.writeByteArray(%s)", out, name));
            builder.addStatement(blockWrite.toString());
        } else if (type.equals(CHARARRAY)) {
            blockWrite.append(String.format("%s.writeCharArray(%s)", out, name));
            builder.addStatement(blockWrite.toString());
        } else if (type.equals(STRINGARRAY)) {
            blockWrite.append(String.format("%s.writeStringArray(%s)", out, name));
            builder.addStatement(blockWrite.toString());
        } else if (type.equals(IBINDER)) {
            blockWrite.append(String.format("%s.writeStrongBinder(%s)", out, name));
            builder.addStatement(blockWrite.toString());
        } else if (type.equals(OBJECTARRAY)) {
            blockWrite.append(String.format("%s.writeArray(%s)", out, name));
            builder.addStatement(blockWrite.toString());
        } else if (type.equals(INTARRAY)) {
            blockWrite.append(String.format("%s.writeIntArray(%s)", out, name));
            builder.addStatement(blockWrite.toString());
        } else if (type.equals(LONGARRAY)) {
            blockWrite.append(String.format("%s.writeLongArray(%s)", out, name));
            builder.addStatement(blockWrite.toString());
        } else if (type.equals(SERIALIZABLE)) {
            blockWrite.append(String.format("%s.writeSerializable(%s)", out, name));
            builder.addStatement(blockWrite.toString());
        } else if (type.equals(PARCELABLEARRAY)) {
            blockWrite.append(String.format("%s.writeParcelableArray(%s)", out, name));
            builder.addStatement(blockWrite.toString());
        } else if (type.equals(SPARSEARRAY)) {
            blockWrite.append(String.format("%s.writeSparseArray(%s)", out, name));
            builder.addStatement(blockWrite.toString());
        } else if (type.equals(SPARSEBOOLEANARRAY)) {
            blockWrite.append(String.format("%s.writeSparseBooleanArray(%s)", out, name));
            builder.addStatement(blockWrite.toString());
        } else if (type.equals(BUNDLE)) {
            blockWrite.append(String.format("%s.writeBundle(%s)", out, name));
            builder.addStatement(blockWrite.toString());
        } else if (type.equals(PERSISTABLEBUNDLE)) {
            blockWrite.append(String.format("%s.writePersistableBundle(%s)", out, name));
            builder.addStatement(blockWrite.toString());
        } else if (type.equals(SIZE)) {
            blockWrite.append(String.format("%s.writeSize(%s)", out, name));
            builder.addStatement(blockWrite.toString());
        } else if (type.equals(SIZEF)) {
            blockWrite.append(String.format("%s.writeSizeF(%s)", out, name));
            builder.addStatement(blockWrite.toString());
        } else if (type.equals(ENUM)) {
            blockWrite.append(String.format("%s.writeSerializable(%s)", out, name));
            builder.addStatement(blockWrite.toString());
        } else {
            blockWrite.append(String.format("%s.writeValue(%s)", out, name));
            builder.addStatement(blockWrite.toString());
        }
    }


    public void generatorReadCodeBlock(MethodSpec.Builder builder) {

        for (String name : anno.variableNames) {
            generatorReadCodeBlocks(name, builder);
        }
    }

    public void generatorReadCodeBlocks(String name, MethodSpec.Builder builder) {

        StringBuilder blockWrite = new StringBuilder();
        blockWrite.append("this." + name + " = ");

        TypeName parcelableType = TypeName.get(anno.getFiledType(name));

        if (parcelableType.equals(STRING)) {
            blockWrite.append("in.readString()");
            builder.addStatement(blockWrite.toString());
        } else if (parcelableType.equals(TypeName.BYTE) || parcelableType.equals(
                TypeName.BYTE.box())) {
            blockWrite.append("in.readByte()");
            builder.addStatement(blockWrite.toString());
        } else if (parcelableType.equals(TypeName.INT) || parcelableType.equals(
                TypeName.INT.box())) {
            blockWrite.append("in.readInt()");
            builder.addStatement(blockWrite.toString());
        } else if (parcelableType.equals(TypeName.SHORT) || parcelableType.equals(
                TypeName.SHORT.box())) {
            blockWrite.append("(short) in.readInt()");
            builder.addStatement(blockWrite.toString());
        } else if (parcelableType.equals(TypeName.CHAR) || parcelableType.equals(
                TypeName.CHAR.box())) {
            blockWrite.append("(char) in.readInt()");
            builder.addStatement(blockWrite.toString());
        } else if (parcelableType.equals(TypeName.LONG) || parcelableType.equals(
                TypeName.LONG.box())) {
            blockWrite.append("in.readLong()");
            builder.addStatement(blockWrite.toString());
        } else if (parcelableType.equals(TypeName.FLOAT) || parcelableType.equals(
                TypeName.FLOAT.box())) {
            blockWrite.append("in.readFloat()");
            builder.addStatement(blockWrite.toString());
        } else if (parcelableType.equals(TypeName.DOUBLE) || parcelableType.equals(
                TypeName.DOUBLE.box())) {
            blockWrite.append("in.readDouble()");
            builder.addStatement(blockWrite.toString());
        } else if (parcelableType.equals(TypeName.BOOLEAN) || parcelableType.equals(
                TypeName.BOOLEAN.box())) {
            blockWrite.append("in.readInt() == 1");
        } else if (parcelableType.equals(PARCELABLE)) {
            blockWrite.append("($T) in.readParcelable($T.class.getClassLoader())");
            builder.addStatement(blockWrite.toString(), anno.getFiledType(name),
                    anno.getFiledType(name));
        } else if (parcelableType.equals(CHARSEQUENCE)) {
            blockWrite.append("$T.CHAR_SEQUENCE_CREATOR.createFromParcel(in)");
            builder.addStatement(blockWrite.toString(), TEXTUTILS);
        } else if (parcelableType.equals(MAP)) {
            blockWrite.append("($T) in.readHashMap($T.class.getClassLoader())");
            builder.addStatement(blockWrite.toString(), anno.getFiledType(name));
        } else if (parcelableType.equals(LIST)) {
            blockWrite.append("($T) in.readArrayList($T.class.getClassLoader())");

        } else if (parcelableType.equals(BOOLEANARRAY)) {
            blockWrite.append("in.createBooleanArray()");
            builder.addStatement(blockWrite.toString());
        } else if (parcelableType.equals(BYTEARRAY)) {
            blockWrite.append("in.createByteArray()");
            builder.addStatement(blockWrite.toString());
        } else if (parcelableType.equals(CHARARRAY)) {
            blockWrite.append("in.createCharArray()");
            builder.addStatement(blockWrite.toString());
        } else if (parcelableType.equals(STRINGARRAY)) {
            blockWrite.append("in.readStringArray()");
            builder.addStatement(blockWrite.toString());
        } else if (parcelableType.equals(IBINDER)) {
            blockWrite.append("in.readStrongBinder()");
            builder.addStatement(blockWrite.toString());
        } else if (parcelableType.equals(OBJECTARRAY)) {
            blockWrite.append("($T)in.readArray($T.class.getClassLoader())");
            builder.addStatement(blockWrite.toString(), anno.getFiledType(name),
                    anno.getFiledType(name));

        } else if (parcelableType.equals(INTARRAY)) {
            blockWrite.append("in.createIntArray()");
            builder.addStatement(blockWrite.toString());
        } else if (parcelableType.equals(LONGARRAY)) {
            blockWrite.append("in.createLongArray()");
            builder.addStatement(blockWrite.toString());
        } else if (parcelableType.equals(SERIALIZABLE)) {
            blockWrite.append("($T) in.readSerializable()");
            builder.addStatement(blockWrite.toString(), anno.getFiledType(name),
                    anno.getFiledType(name));

        } else if (parcelableType.equals(PARCELABLEARRAY)) {

            blockWrite.append("(List<$T>)in.readParcelableArray($T.class.getClassLoader())");
            builder.addStatement(blockWrite.toString(), PARCELABLE);

        } else if (parcelableType.equals(SPARSEARRAY)) {
            blockWrite.append("in.readSparseArray($T.class.getClassLoader())");
            builder.addStatement(blockWrite.toString(), anno.getFiledType(name));
        } else if (parcelableType.equals(SPARSEBOOLEANARRAY)) {
            blockWrite.append("in.readSparseBooleanArray()");
            builder.addStatement(blockWrite.toString());
        } else if (parcelableType.equals(BUNDLE)) {
            builder.addStatement(blockWrite.toString(), anno.getFiledType(name));
        } else if (parcelableType.equals(PERSISTABLEBUNDLE)) {
            blockWrite.append("in.readPersistableBundle($T.class.getClassLoader())");
        } else if (parcelableType.equals(SIZE)) {
            blockWrite.append("in.readSize()");
            builder.addStatement(blockWrite.toString());
        } else if (parcelableType.equals(SIZEF)) {
            blockWrite.append("in.readSizeF()");
            builder.addStatement(blockWrite.toString());
        } else if (parcelableType.equals(ENUM)) {
            blockWrite.append("$T.valueOf($T.class, in.readString())");
            builder.addStatement(blockWrite.toString(), Enum.class, anno.getFiledType(name));
        } else {
            blockWrite.append("($T)in.readValue($T.class.getClassLoader())");
            builder.addStatement(blockWrite.toString(), anno.getFiledType(name),
                    anno.getFiledType(name));
        }

    }


    //禁止使用,编译会报错
    private String getElementName(Element element) {

        return element.getSimpleName().toString();
    }

    //禁止使用,编译会报错
    private TypeName getElementTypeName(Element element) {

        return TypeName.get(element.asType());
    }

}
