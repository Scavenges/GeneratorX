# GeneratorX
帮助项目开发时，自动生成一个标准的java类。

[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)

会自动生成如下代码
* 默认构造函数　和　非默认构造函数
* get set
* hascode
* toString
* equals
* implements Parcelable 需要的代码
* 字段注解(兼容多个注解类型，多个注解值)

-------------------
## 使用 

实体类字段增加注解，一个字段可以是多个注解，多个注解值。

```

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

```

build完成之后会在项目build/generated/source/apt/debug下生成对应的类文件。会为对应的字段生成相应的注解

```
	
	package com.androidyuan.model;
	
	import android.os.Parcel;
	import android.os.Parcelable;
	import android.text.TextUtils;
	import com.androidyuan.AcodeJson;
	import com.androidyuan.FiledJson;
	import com.androidyuan.Helper.ClsHelper;
	import java.lang.CharSequence;
	import java.lang.Enum;
	import java.lang.Object;
	import java.lang.Override;
	import java.lang.String;
	
	public class PersonBeanX extends PersonBean {
	  public static final Parcelable.Creator<PersonBeanX> CREATOR = new Parcelable.Creator<PersonBeanX>() {
	    @Override
	    public PersonBeanX createFromParcel(Parcel in) {
	      return new PersonBeanX(in);
	    }
	
	    @Override
	    public PersonBeanX[] newArray(int size) {
	      return new PersonBeanX[size];
	    }
	  };
	
	  @AcodeJson(
	      version = "s"
	  )
	  public Enum s;
	
	  @FiledJson(
	      value = "name1",
	      num = "0",
	      falg = "true"
	  )
	  public String name;
	
	  @AcodeJson(
	      version = "age1"
	  )
	  @FiledJson(
	      value = "name1",
	      num = "0",
	      falg = "true"
	  )
	  public int age;
	
	  @AcodeJson(
	      version = "str"
	  )
	  @FiledJson(
	      value = "name1",
	      num = "0",
	      falg = "true"
	  )
	  public CharSequence str;
	
	  public PersonBeanX() {
	  }
	
	  public PersonBeanX(Enum s, String name, int age, CharSequence str) {
	    this.s = s;
	    this.name = name;
	    this.age = age;
	    this.str = str;
	  }
	
	  public PersonBeanX(Parcel in) {
	    this.s = Enum.valueOf(Enum.class, in.readString());
	    this.name = in.readString();
	    this.age = in.readInt();
	    this.str = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
	  }
	
	  @Override
	  public void writeToParcel(Parcel dest, int flags) {
	    dest.writeSerializable(s);
	    dest.writeInt(age);
	    TextUtils.writeToParcel(str, dest, flags);
	  }
	
	  public void setS(Enum s) {
	    this.s = s;
	  }
	
	  public Enum getS() {
	    return this.s;
	  }
	
	  public void setName(String name) {
	    this.name = name;
	  }
	
	  public String getName() {
	    return this.name;
	  }
	
	  public void setAge(int age) {
	    this.age = age;
	  }
	
	  public int getAge() {
	    return this.age;
	  }
	
	  public void setStr(CharSequence str) {
	    this.str = str;
	  }
	
	  public CharSequence getStr() {
	    return this.str;
	  }
	
	  @Override
	  public int describeContents() {
	    return 0;
	  }
	
	  @Override
	  public int hashCode() {
	    return ClsHelper.hashCode(s , name , age , str);
	  }
	
	  @Override
	  public boolean equals(Object o) {
	
	        if (o == null) return false;
	        if (o == this) return true;
	        if (o instanceof PersonBeanX) {
	        	if (o.hashCode() == this.hashCode()) return true;
	        }
	        return false;
	  }
	
	  @Override
	  public String toString() {
	    return "PersonBeanX{"+"s =  "+s+","+"name =  "+name+","+"age =  "+age+","+"str =  "+str.toString()+"}";
	  }
	}

```

###记录几个发现###

1：AbstractProcessor是本开源项目的核心类，在build过程中，会执行该类的重写方法。process()是该类的核心方法，这相当于每个处理器的主函数main()。你在这里写你的扫描、评估和处理注解的代码，以及生成Java文件。输入参数RoundEnviroment，可以让你查询出包含特定注解的被注解元素。

2：之前在检测类元素的时候，判断是这么写的。

	if (annotatedElement instanceof TypeElement) 

这么写并不合理，因为所有的元素类型都是Element,所有要避免使用instanceof。配合TypeMirror使用EmentKind或者TypeKind。

	if (annotatedElement.getKind() == ElementKind.CLASS)

3:我们在遍历获取类文件里边的元素时，首先获取的是构造函数。

4：以下是获取注解的代码，返回一个集合

	 List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();

5：以下是获取注解类型的代码

	DeclaredType declaredType = annotationMirrors.get(i).getAnnotationType();

6：以下是获取注解值的代码，返回一个map

	Map<? extends ExecutableElement, ? extends AnnotationValue> map = annotationMirrors.get(i).getElementValues();

7：创建一个新的注解

    //通过全类名返回一个类，此类用于生成注解
    Class clazz = Class.forName(simpleName);
    //创建一个新的注解
    AnnotationSpec.Builder annotationBuilder = AnnotationSpec.builder(clazz);
	//for循环遍历集合，用户有可能自定义注解，并且会有多个注解值，看来这块需要遍历保存
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

8：messager打印的时候遇到一个问题，最后打印的数据貌似会把之前的数据覆盖，导致在for循环的时候，我一直认为数据是不对的，很坑爹。

如果发现缺陷，欢迎提[issues](https://github.com/Scavenges/GeneratorX/issues/new)。

