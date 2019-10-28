# 基于JSR-269的对象转换器

### 实现进度
1. [x] 生成内部类并在内部类生成Getter, Setter，外部对象只暴露业务方法  
2. [x] 两个对象通过注解确认映射关系  
    * 目前只能映射属性值完全相同的对象，自定义映射后续实现
    * 编译时不明原因报错暂时不支持非public类，尚未排查到原因，Javac的报错实在难以排查，错误堆栈见[附录](#附录)
3. [ ] 为存在映射关系的对象生成转换方法

### 使用方法

1. 准备需要转换的对象

#### Person.java
```java
package vip.wuweijie.test.demo;

import vip.wuweijie.wheel.objectmapper.InnerData;
import vip.wuweijie.wheel.objectmapper.MapTo;

import java.util.StringJoiner;
@InnerData
@MapTo({"vip.wuweijie.test.demo.PersonPo"})
public class Person {

    private Long id;
    private String name;
    private int age;
    private String demo;

    public Person() {
    }

    public Person(Long id, String name, int age, String demo) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.demo = demo;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Person.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("age=" + age)
                .add("demo='" + demo + "'")
                .toString();
    }
}
```

#### PersonPo.java
```java
package vip.wuweijie.test.demo;

import vip.wuweijie.wheel.objectmapper.InnerData;
import java.util.StringJoiner;

@InnerData
public class PersonPo {

    private Long id;
    private String name;
    private int age;
    private String demo;

    @Override
    public String toString() {
        return new StringJoiner(", ", PersonPo.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("age=" + age)
                .add("demo='" + demo + "'")
                .toString();
    }
}
```

2. 定义转换器方法签名，方法体随意定义，编译时方法体内的代码会被覆盖

#### PersonMapper.java
```java
package vip.wuweijie.test.demo;

import vip.wuweijie.wheel.objectmapper.InnerData;
import vip.wuweijie.wheel.objectmapper.Mapper;

public class PersonMapper {

    @Mapper
    public static PersonPo toPo(Person person) {
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) {
        Person person = new Person(23333L, "TESLA_CN", 22, "hello, demo");
        System.out.println(person);
        System.out.println(toPo(person));
    }
}
```

3. 编译时加入本项目中的注解处理器，运行代码
输出结果
```
Person[id=23333, name='TESLA_CN', age=22, demo='hello, demo']
PersonPo[id=23333, name='TESLA_CN', age=22, demo='hello, demo']
```

### 附录

#### 当前未解决问题

* 编译时不明原因报错，错误出现在注解处理器已完成注解后。Debug排查语法树对应的代码，没有语法问题，代码直接放入一个文件也可以使用。
```
[语法分析开始时间 SimpleFileObject[C:\Users\sia\IdeaProjects\object-mapper\object-mapper-processor\src\main\java\vip\wuweijie\test\demo\Person.java]]
[语法分析已完成, 用时 21 毫秒]
[语法分析开始时间 SimpleFileObject[C:\Users\sia\IdeaProjects\object-mapper\object-mapper-processor\src\main\java\vip\wuweijie\test\demo\PersonPo.java]]
[语法分析已完成, 用时 0 毫秒]
[语法分析开始时间 SimpleFileObject[C:\Users\sia\IdeaProjects\object-mapper\object-mapper-processor\src\main\java\vip\wuweijie\test\demo\PersonMapper.java]]
[语法分析已完成, 用时 1 毫秒]
[语法分析开始时间 SimpleFileObject[C:\Users\sia\IdeaProjects\object-mapper\object-mapper-processor\src\main\java\vip\wuweijie\test\demo\DefaultMain.java]]
[语法分析已完成, 用时 1 毫秒]
[正在加载/modules/jdk.jdeps/module-info.class]
[正在加载/modules/jdk.naming.rmi/module-info.class]
[正在加载/modules/jdk.unsupported.desktop/module-info.class]
[正在加载/modules/jdk.net/module-info.class]
[正在加载/modules/java.logging/module-info.class]
[正在加载/modules/jdk.naming.dns/module-info.class]
[正在加载/modules/java.sql.rowset/module-info.class]
[正在加载/modules/java.desktop/module-info.class]
[正在加载/modules/java.xml/module-info.class]
[正在加载/modules/jdk.hotspot.agent/module-info.class]
[正在加载/modules/java.scripting/module-info.class]
[正在加载/modules/jdk.editpad/module-info.class]
[正在加载/modules/jdk.accessibility/module-info.class]
[正在加载/modules/jdk.security.jgss/module-info.class]
[正在加载/modules/java.instrument/module-info.class]
[正在加载/modules/jdk.management.agent/module-info.class]
[正在加载/modules/java.datatransfer/module-info.class]
[正在加载/modules/jdk.internal.ed/module-info.class]
[正在加载/modules/java.prefs/module-info.class]
[正在加载/modules/jdk.zipfs/module-info.class]
[正在加载/modules/jdk.internal.opt/module-info.class]
[正在加载/modules/jdk.jsobject/module-info.class]
[正在加载/modules/jdk.security.auth/module-info.class]
[正在加载/modules/jdk.jconsole/module-info.class]
[正在加载/modules/jdk.jshell/module-info.class]
[正在加载/modules/jdk.jartool/module-info.class]
[正在加载/modules/jdk.jlink/module-info.class]
[正在加载/modules/jdk.scripting.nashorn.shell/module-info.class]
[正在加载/modules/java.rmi/module-info.class]
[正在加载/modules/java.net.http/module-info.class]
[正在加载/modules/jdk.jstatd/module-info.class]
[正在加载/modules/jdk.crypto.mscapi/module-info.class]
[正在加载/modules/jdk.rmic/module-info.class]
[正在加载/modules/jdk.internal.le/module-info.class]
[正在加载/modules/jdk.management.jfr/module-info.class]
[正在加载/modules/jdk.xml.dom/module-info.class]
[正在加载/modules/java.sql/module-info.class]
[正在加载/modules/jdk.attach/module-info.class]
[正在加载/modules/java.security.jgss/module-info.class]
[正在加载/modules/jdk.charsets/module-info.class]
[正在加载/modules/jdk.jdwp.agent/module-info.class]
[正在加载/modules/jdk.management/module-info.class]
[正在加载/modules/jdk.jfr/module-info.class]
[正在加载/modules/jdk.internal.vm.compiler.management/module-info.class]
[正在加载/modules/jdk.scripting.nashorn/module-info.class]
[正在加载/modules/jdk.javadoc/module-info.class]
[正在加载/modules/jdk.crypto.cryptoki/module-info.class]
[正在加载/modules/java.management/module-info.class]
[正在加载/modules/jdk.crypto.ec/module-info.class]
[正在加载/modules/jdk.dynalink/module-info.class]
[正在加载/modules/java.security.sasl/module-info.class]
[正在加载/modules/java.naming/module-info.class]
[正在加载/modules/jdk.compiler/module-info.class]
[正在加载/modules/jdk.pack/module-info.class]
[正在加载/modules/java.compiler/module-info.class]
[正在加载/modules/jdk.httpserver/module-info.class]
[正在加载/modules/jdk.aot/module-info.class]
[正在加载/modules/jdk.internal.jvmstat/module-info.class]
[正在加载/modules/jdk.internal.vm.ci/module-info.class]
[正在加载/modules/jdk.localedata/module-info.class]
[正在加载/modules/java.transaction.xa/module-info.class]
[正在加载/modules/jdk.sctp/module-info.class]
[正在加载/modules/java.se/module-info.class]
[正在加载/modules/jdk.unsupported/module-info.class]
[正在加载/modules/java.smartcardio/module-info.class]
[正在加载/modules/java.xml.crypto/module-info.class]
[正在加载/modules/java.base/module-info.class]
[正在加载/modules/java.management.rmi/module-info.class]
[正在加载/modules/jdk.jdi/module-info.class]
[正在加载/modules/jdk.internal.vm.compiler/module-info.class]
[正在加载/modules/jdk.jcmd/module-info.class]
[源文件的搜索路径: C:\Users\sia\IdeaProjects\object-mapper\object-mapper-processor\build\classes\java\main,C:\Users\sia\IdeaProjects\object-mapper\object-mapper-annotation\build\classes\java\main]
[类文件的搜索路径: C:\Program Files\Java\jdk-11.0.2\lib\modules,C:\Users\sia\IdeaProjects\object-mapper\object-mapper-processor\build\classes\java\main,C:\Users\sia\IdeaProjects\object-mapper\object-mapper-annotation\build\classes\java\main]
[正在加载C:\Users\sia\IdeaProjects\object-mapper\object-mapper-annotation\build\classes\java\main\vip\wuweijie\wheel\objectmapper\InnerData.class]
[正在加载C:\Users\sia\IdeaProjects\object-mapper\object-mapper-annotation\build\classes\java\main\vip\wuweijie\wheel\objectmapper\MapTo.class]
[正在加载/modules/java.base/java/util/StringJoiner.class]
[正在加载/modules/java.base/java/lang/Object.class]
[正在加载/modules/java.base/java/lang/Long.class]
[正在加载/modules/java.base/java/lang/String.class]
[正在加载C:\Users\sia\IdeaProjects\object-mapper\object-mapper-annotation\build\classes\java\main\vip\wuweijie\wheel\objectmapper\Mapper.class]
[正在加载/modules/java.base/java/lang/Deprecated.class]
[正在加载/modules/java.base/java/lang/annotation/Target.class]
[正在加载/modules/java.base/java/lang/annotation/ElementType.class]
[正在加载/modules/java.base/java/lang/annotation/Retention.class]
[正在加载/modules/java.base/java/lang/annotation/RetentionPolicy.class]
[正在加载/modules/java.base/java/lang/annotation/Annotation.class]
[正在加载/modules/java.base/java/lang/Override.class]
循环 1:
	输入文件: {vip.wuweijie.test.demo.Person, vip.wuweijie.test.demo.PersonPo, vip.wuweijie.test.demo.PersonMapper, vip.wuweijie.test.demo.DefaultMain, vip.wuweijie.test.demo.DefaultPerson, vip.wuweijie.test.demo.DefaultPersonPo}
	注释: [vip.wuweijie.wheel.objectmapper.InnerData, vip.wuweijie.wheel.objectmapper.MapTo, java.lang.Override, vip.wuweijie.wheel.objectmapper.Mapper]
	最后一个循环: false
处理程序vip.wuweijie.wheel.objectmapper.InnerDataProcessor与[/vip.wuweijie.wheel.objectmapper.InnerData]匹配并返回true。


处理程序vip.wuweijie.wheel.objectmapper.ObjectMappingProcessor与[/vip.wuweijie.wheel.objectmapper.MapTo, /vip.wuweijie.wheel.objectmapper.Mapper]匹配并返回true。
注: Inner Class Generated for => vip.wuweijie.test.demo.Person
注: Inner Class Generated for => vip.wuweijie.test.demo.PersonPo
注: Inner Class Generated for => vip.wuweijie.test.demo.PersonMapper
注: Inner Class Generated for => vip.wuweijie.test.demo.DefaultPerson
注: Inner Class Generated for => vip.wuweijie.test.demo.DefaultPersonPo
循环 2:
	输入文件: {}
	注释: []
	最后一个循环: true
[正在检查vip.wuweijie.test.demo.Person]
[正在加载/modules/java.base/java/io/Serializable.class]
[正在加载/modules/java.base/java/lang/AutoCloseable.class]
[正在加载/modules/java.base/java/lang/System.class]
[正在加载/modules/java.base/java/io/PrintStream.class]
[正在加载/modules/java.base/java/lang/Appendable.class]
[正在加载/modules/java.base/java/io/Closeable.class]
[正在加载/modules/java.base/java/io/FilterOutputStream.class]
[正在加载/modules/java.base/java/io/OutputStream.class]
[正在加载/modules/java.base/java/io/Flushable.class]
[正在加载/modules/java.base/java/lang/Comparable.class]
[正在加载/modules/java.base/java/lang/CharSequence.class]
[正在加载/modules/java.base/java/lang/Byte.class]
[正在加载/modules/java.base/java/lang/Character.class]
[正在加载/modules/java.base/java/lang/Short.class]
[正在加载/modules/java.base/java/lang/Float.class]
[正在加载/modules/java.base/java/lang/Integer.class]
[正在加载/modules/java.base/java/lang/Double.class]
[正在加载/modules/java.base/java/lang/Boolean.class]
[正在加载/modules/java.base/java/lang/Void.class]
[正在加载/modules/java.base/java/lang/Class.class]
[正在加载/modules/java.base/java/lang/reflect/GenericDeclaration.class]
[正在加载/modules/java.base/java/lang/reflect/AnnotatedElement.class]
[正在加载/modules/java.base/java/lang/reflect/Type.class]
[已写入C:\Users\sia\IdeaProjects\object-mapper\object-mapper-processor\out\production\classes\vip\wuweijie\test\demo\Person$__Person__Inner.class]
[正在加载/modules/java.base/java/lang/invoke/StringConcatFactory.class]
[正在加载/modules/java.base/java/lang/invoke/MethodHandles.class]
[正在加载/modules/java.base/java/lang/invoke/MethodHandles$Lookup.class]
[正在加载/modules/java.base/java/lang/invoke/MethodType.class]
[正在加载/modules/java.base/java/lang/invoke/CallSite.class]
[已写入C:\Users\sia\IdeaProjects\object-mapper\object-mapper-processor\out\production\classes\vip\wuweijie\test\demo\Person.class]
[正在检查vip.wuweijie.test.demo.PersonPo]
[已写入C:\Users\sia\IdeaProjects\object-mapper\object-mapper-processor\out\production\classes\vip\wuweijie\test\demo\PersonPo$__PersonPo__Inner.class]
[已写入C:\Users\sia\IdeaProjects\object-mapper\object-mapper-processor\out\production\classes\vip\wuweijie\test\demo\PersonPo.class]
[正在检查vip.wuweijie.test.demo.PersonMapper]
[正在加载/modules/java.base/java/lang/Number.class]
[已写入C:\Users\sia\IdeaProjects\object-mapper\object-mapper-processor\out\production\classes\vip\wuweijie\test\demo\PersonMapper$__PersonMapper__Inner.class]
[正在加载/modules/java.base/java/util/Objects.class]
[正在加载/modules/java.base/java/util/function/Supplier.class]
[已写入C:\Users\sia\IdeaProjects\object-mapper\object-mapper-processor\out\production\classes\vip\wuweijie\test\demo\PersonMapper.class]
[正在检查vip.wuweijie.test.demo.DefaultMain]
[已写入C:\Users\sia\IdeaProjects\object-mapper\object-mapper-processor\out\production\classes\vip\wuweijie\test\demo\DefaultMain.class]
[正在检查vip.wuweijie.test.demo.DefaultPerson]
[共 458 毫秒]
编译器 (11.0.2) 中出现异常错误。如果在 Bug Database (http://bugs.java.com) 中没有找到该错误, 请通过 Java Bug 报告页 (http://bugreport.java.com) 建立该 Java 编译器 Bug。请在报告中附上您的程序和以下诊断信息。谢谢。
java.lang.AssertionError
	at jdk.compiler/com.sun.tools.javac.util.Assert.error(Assert.java:155)
	at jdk.compiler/com.sun.tools.javac.util.Assert.check(Assert.java:46)
	at jdk.compiler/com.sun.tools.javac.util.Bits.incl(Bits.java:186)
	at jdk.compiler/com.sun.tools.javac.comp.Flow$AssignAnalyzer.initParam(Flow.java:1933)
	at jdk.compiler/com.sun.tools.javac.comp.Flow$AssignAnalyzer.visitMethodDef(Flow.java:1882)
	at jdk.compiler/com.sun.tools.javac.tree.JCTree$JCMethodDecl.accept(JCTree.java:866)
	at jdk.compiler/com.sun.tools.javac.tree.TreeScanner.scan(TreeScanner.java:49)
	at jdk.compiler/com.sun.tools.javac.comp.Flow$BaseAnalyzer.scan(Flow.java:398)
	at jdk.compiler/com.sun.tools.javac.comp.Flow$AssignAnalyzer.scan(Flow.java:1453)
	at jdk.compiler/com.sun.tools.javac.comp.Flow$AssignAnalyzer.visitClassDef(Flow.java:1824)
	at jdk.compiler/com.sun.tools.javac.tree.JCTree$JCClassDecl.accept(JCTree.java:774)
	at jdk.compiler/com.sun.tools.javac.tree.TreeScanner.scan(TreeScanner.java:49)
	at jdk.compiler/com.sun.tools.javac.comp.Flow$BaseAnalyzer.scan(Flow.java:398)
	at jdk.compiler/com.sun.tools.javac.comp.Flow$AssignAnalyzer.scan(Flow.java:1453)
	at jdk.compiler/com.sun.tools.javac.comp.Flow$AssignAnalyzer.visitClassDef(Flow.java:1817)
	at jdk.compiler/com.sun.tools.javac.tree.JCTree$JCClassDecl.accept(JCTree.java:774)
	at jdk.compiler/com.sun.tools.javac.tree.TreeScanner.scan(TreeScanner.java:49)
	at jdk.compiler/com.sun.tools.javac.comp.Flow$BaseAnalyzer.scan(Flow.java:398)
	at jdk.compiler/com.sun.tools.javac.comp.Flow$AssignAnalyzer.scan(Flow.java:1453)
	at jdk.compiler/com.sun.tools.javac.comp.Flow$AssignAnalyzer.analyzeTree(Flow.java:2510)
	at jdk.compiler/com.sun.tools.javac.comp.Flow$AssignAnalyzer.analyzeTree(Flow.java:2493)
	at jdk.compiler/com.sun.tools.javac.comp.Flow.analyzeTree(Flow.java:217)
	at jdk.compiler/com.sun.tools.javac.main.JavaCompiler.flow(JavaCompiler.java:1401)
	at jdk.compiler/com.sun.tools.javac.main.JavaCompiler.flow(JavaCompiler.java:1375)
	at jdk.compiler/com.sun.tools.javac.main.JavaCompiler.compile(JavaCompiler.java:973)
	at jdk.compiler/com.sun.tools.javac.main.Main.compile(Main.java:311)
	at jdk.compiler/com.sun.tools.javac.main.Main.compile(Main.java:170)
	at jdk.compiler/com.sun.tools.javac.Main.compile(Main.java:57)
	at jdk.compiler/com.sun.tools.javac.Main.main(Main.java:43)

```
