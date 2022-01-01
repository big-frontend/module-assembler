package com.jamesfchen.ibc

import com.jamesfchen.ClassInfo
import com.jamesfchen.ScanClassPlugin
import com.jamesfchen.P
import com.jamesfchen.Injector
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

class IbcPlugin extends ScanClassPlugin {
    List<RouterInfo> routers
    ClassInfo ibcRouterClassInfo
    static final String IBCROUTER_CANONICAL_NAME = "com.jamesfchen.ibc.router.IBCRouter";
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

    }

    @Override
    void onScanClassInDir(ClassInfo classInfo) {
        if (classInfo.canonicalName == IBCROUTER_CANONICAL_NAME) {
            ibcRouterClassInfo = classInfo
            return
        }
        ClassReader reader = new ClassReader(classInfo.classFile.bytes)
        ClassWriter writer = new ClassWriter(reader, 0)
        ClassVisitor visitor = new ScanRouterClassVisitor(writer, routers)
        reader.accept(visitor, ClassReader.SKIP_FRAMES)
    }

    @Override
    void onScanClassInJar(ClassInfo classInfo) {
        if (classInfo.canonicalName == IBCROUTER_CANONICAL_NAME) {
            ibcRouterClassInfo = classInfo
            return
        }
        ClassReader reader = new ClassReader(classInfo.classStream)
        ClassWriter writer = new ClassWriter(reader, 0)
        ClassVisitor visitor = new ScanRouterClassVisitor(writer, routers)
        reader.accept(visitor, ClassReader.SKIP_FRAMES)
    }

    @Override
    void onScanEnd() {
        P.info("routers:${routers.toListString()} IBCRouter:${ibcRouterClassInfo}")
        if (routers.isEmpty() ||ibcRouterClassInfo ==null) return
        Injector.injectCode(ibcRouterClassInfo) { type, classStream ->
            ClassReader reader = new ClassReader(classStream)
            ClassWriter writer = new ClassWriter(reader, 0)
            ClassVisitor visitor = new RouterInjectorClassVisitor(writer, routers)
            reader.accept(visitor, ClassReader.SKIP_FRAMES)
            return writer.toByteArray()
        }
    }
}