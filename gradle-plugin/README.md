## Gradle Plugin Collector

> base-plugin
```
//快速插入code到指定的类文件，无需先扫描文件在插入code到指定类文件，代码可以参考perf-plugin插件
FastInsertCodePlugin

//对于有些功能需要先扫描类文件，收集各个类的信息，然后在插入code到指定类文件中，代码可以参考lifecycle-plugin插件
ScanClassPlugin

implementation 'io.github.jamesfchen:base-plugin:1.0.2'

or implementation 'io.github.jamesfchen:base-plugin-ktx:1.0.0'
```

> lifecycle-plugin

```gradle
plugins {
    id 'io.github.jamesfchen.lifecycle-plugin' version '1.0.1'
}

api 'io.github.jamesfchen:lifecycle-api:1.0.3'
```

```java
@App  //在AppLicatioin子类添加@App注解
public class TApp extends Application {
    //必须继承onCreate，插桩代码会在这里插入代码
    @Override
    public void onCreate() {
        super.onCreate();
    }
    ....
}
@AppLifecycle // 在LifecycleObserver子类添加@AppLifecycle
public class AppLifecycleObserver implements LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void connectListener(LifecycleOwner lifecycleOwner) {
        Log.d("cjf", "AppLifecycleObserver connectListener");
    }
}
```

> ibc-plugin

```
implementation 'io.github.jamesfchen:ibc-api:1.+'

//页面路由
@Router(name = "bundle1router")
public class Bundle1Router implements IModuleRouter {
}
```
> module-publisher-plugin

```gradle
apply plugin: 'io.github.jamesfchen.module-publisher-plugin'
publish{
    name="lifecycle-api"
    groupId = rootProject.groupId
    artifactId = "lifecycle-api"
    version = "1.0.0"
    website = "https://github.com/JamesfChen/spacecraft-android-gradle-plugin"
}
```

> moduleify-plugin
```gradle
//app 模块
plugins{
      id 'io.github.jamesfchen.app-plugin'
 }
//bundle插件自带路由，该插件主要用于bundle模块
plugins{
    id 'io.github.jamesfchen.bundle-plugin'
}
//foundation插件用于基础功能模块
plugins{
    id 'io.github.jamesfchen.bundle-plugin'
}
//业务组件ibc中的cpbc 模块
plugins{
      id 'io.github.jamesfchen.api-plugin'
 }

//处理模块的厨师，放在rootProject的build.gradle
plugins{
    id 'io.github.jamesfchen.module-chef-plugin'
}
```

`To Be Continue ...`

## 插桩(instrumentation)分类:

- 源代码插桩 Source Code Instrumentation(SCI)：kapt or java注解处理器生成java代码

- 二进制插桩（Binary Instrumentation）
    - 静态二进制插桩[Static Binary Instrumentation(SBI)]:插入额外的字节码，然后利用重打包技术 或者 asm在编译期间插入字节码
    - 动态二进制插桩[Dynamic Binary Instrumentation(DBI)]：hook需要修改的函数，在运行期间将本应该执行的函数指向篡改之后的函数

## reference
[aspectj](https://www.eclipse.org/aspectj/)

[asm](https://asm.ow2.io/index.html)

[ReDex](https://github.com/facebook/redex)

[Android字节码插桩](https://www.daimajiaoliu.com/daima/4795c92d31003fc)

[Jvm系列3—字节码指令](http://gityuan.com/2015/10/24/jvm-bytecode-grammar/)

[Advanced Java Bytecode ](https://www.jrebel.com/blog/java-bytecode-tutorial)

[bytecode with asm](https://courses.cs.ut.ee/MTAT.05.085/2016_spring/uploads/Main/Generating_bytecode.pdf)

[不得不学之「 ASM 」④ 加密字符串原理](https://www.yuque.com/mr.s/hs39hv/yrzlp5?language=zh-cn)

[JVM基础知识和ASM修改字节码](https://blog.csdn.net/sweatOtt/article/details/88114002)

[Introduction to Java Bytecode](https://dzone.com/articles/introduction-to-java-bytecode)

[Java MethodVisitor.visitTypeInsn方法代码示例](https://vimsky.com/examples/detail/java-method-org.objectweb.asm.MethodVisitor.visitTypeInsn.html)

[配置编译版本](https://developer.android.com/studio/build)

[Transform详解](https://www.jianshu.com/p/37a5e058830a)

[10分钟了解Android项目构建流程](https://juejin.cn/post/6844903555795517453#heading-8) 
