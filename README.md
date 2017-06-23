# GeneratorX
帮助项目开发时，自动生成一个标准的java类。

会自动生成如下代码
* 默认构造函数　和　非默认构造函数
* get set
* hascode
* toString
* equals
* implements Parcelable 需要的代码

-------------------
## 使用 
1.1已经发布。
```
dependencies {
compile 'com.scavenges:GeneratorX:1.1'
}
```

当我写出如下代码
```
@SimpleGenerator
public abstract class PersonBean implements Parcelable {//可选　implements Parcelable　如果你需要做序列化的话　 

    public Enum s;
    public String name;
    public int age = 0;

    public CharSequence str;

}
```

我build一下，自动帮我生成一个类
 
```
package com.androidyuan.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
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

  private Enum s;

  private String name;

  private int age;

  private CharSequence str;

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


如果发现缺陷，欢迎提[issues](https://github.com/Scavenges/GeneratorX/issues/new)。

