## 元编程
编写以程序作为数据的程序
- 编译时
- 运行时 python/js 运行时生成函数或者类

注解处理器|注解类型
| ---|---
apt |java
kapt | java/kotlin
ksp | java/kotlin

ksp是kapt编译速度的两倍
> Kotlin Symbol Processing (KSP), our new tool for building lightweight compiler plugins in Kotlin,
> is now stable! KSP offers similar functionality to the Kotlin Annotation Processing Tool (KAPT),
> however it’s up to 2x faster

- kaptGenerateStubsDebugKotlin(618ms)：通过读取kotlin编译产物的元数据，生成能被apt解析的java stubs
- kaptDebugKotlin(306ms):apt编译生成源码

- kspDebugKotlin(72ms): ksp能直接读取kotlinABT语法树




## reference

[Accelerated Kotlin build times with Kotlin Symbol Processing 1.0](https://android-developers.googleblog.com/2021/09/accelerated-kotlin-build-times-with.html）

[注解处理器教程](https://www.bilibili.com/video/BV1RW411m7Hk?spm_id_from=333.999.0.0)

[GDG 直播回放：Kotlin 元编程：从注解处理器 KAPT到符号处理器 KSP](https://www.bilibili.com/video/BV1JY411H7pb?spm_id_from=333.999.0.0)

[让 Annotation Processor 支持增量编译](https://jiyang.site/posts/2020-03-24-%E8%AE%A9annotation-processor-%E6%94%AF%E6%8C%81%E5%A2%9E%E9%87%8F%E7%BC%96%E8%AF%91/)