package com.jamesfchen.ibc

import com.jamesfchen.ClassInfo
import com.jamesfchen.ScanClassPlugin
import com.jamesfchen.P
import com.jamesfchen.Injector
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

/**
 * 如果没有扫描与插桩的处理会耗时983ms，扫描与插桩会消耗2685ms
 */
class IbcPlugin extends ScanClassPlugin {
    List<RouterInfo> routers
    List<ApiInfo> apis
    ClassInfo ibcClassInfo
    static final String IBC_CANONICAL_NAME = "com.jamesfchen.ibc.IBCInitializer";
    @Override
    String pluginName() {
        return "Ibc"
    }
    @Override
    void apply(Project project) {
        super.apply(project)
        P.info("project[${project}] apply ${this.getClass().getSimpleName()}")
    }
    @Override
    void onScanBegin() {
        routers = new ArrayList<>()
        apis = new ArrayList<>()

    }

    @Override
    void onScanClassInDir(ClassInfo classInfo) {
        if (classInfo.canonicalName == IBC_CANONICAL_NAME) {
            ibcClassInfo = classInfo
            return
        }
        ClassReader reader = new ClassReader(classInfo.classFile.bytes)
        ClassWriter writer = new ClassWriter(reader, 0)
        ClassVisitor visitor = new ScanIbcInfoClassVisitor(writer, routers,apis)
        reader.accept(visitor, ClassReader.SKIP_FRAMES)
    }

    @Override
    void onScanClassInJar(ClassInfo classInfo) {
        if (classInfo.canonicalName == IBC_CANONICAL_NAME) {
            ibcClassInfo = classInfo
            return
        }
        ClassReader reader = new ClassReader(classInfo.classStream)
        ClassWriter writer = new ClassWriter(reader, 0)
        ClassVisitor visitor = new ScanIbcInfoClassVisitor(writer, routers,apis)
        reader.accept(visitor, ClassReader.SKIP_FRAMES)
    }

    @Override
    void onScanEnd() {
        P.info("ibc initializer ${ibcClassInfo}")
        P.info("routers:${routers.toListString()}")
        P.info("apis:${apis.toListString()}")
        if (routers.isEmpty() ||ibcClassInfo ==null) return
        Injector.injectCode(ibcClassInfo) { type, classStream ->
            ClassReader reader = new ClassReader(classStream)
            ClassWriter writer = new ClassWriter(reader, 0)
            ClassVisitor visitor = new IbcInjectorClassVisitor(writer, routers,apis)
            reader.accept(visitor, ClassReader.SKIP_FRAMES)
            return writer.toByteArray()
        }
    }
}