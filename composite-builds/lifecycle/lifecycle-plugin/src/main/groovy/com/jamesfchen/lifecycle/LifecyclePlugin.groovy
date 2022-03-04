package com.jamesfchen.lifecycle

import com.jamesfchen.ClassInfo
import com.jamesfchen.Injector
import com.jamesfchen.P
import com.jamesfchen.ScanClassPlugin
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

class LifecyclePlugin extends ScanClassPlugin {
    List<String> appLifecycles
    static final String LIFECYCLE_CANONICAL_NAME = "com.jamesfchen.lifecycle.LifecycleInitializer";
    ClassInfo lifecycleClassInfo
    @Override
    String pluginName() {
        return "Lifecycle"
    }

    @Override
    void apply(Project project) {
        super.apply(project)
        P.info("project[${project}] apply ${this.getClass().getSimpleName()}")
    }

    @Override
    void onScanBegin() {
        appLifecycles = new ArrayList<>()
    }

    @Override
    void onScanClassInDir(ClassInfo classInfo) {
        if (classInfo.canonicalName == LIFECYCLE_CANONICAL_NAME) {
            lifecycleClassInfo = classInfo
            return
        }
        ClassReader reader = new ClassReader(classInfo.classFile.bytes)
//      ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS)
        ClassWriter writer = new ClassWriter(reader, 0)
        ClassVisitor visitor = new ScanLifecycleClassVisitor(writer, appLifecycles)
        reader.accept(visitor, ClassReader.SKIP_FRAMES)
    }

    @Override
    void onScanClassInJar(ClassInfo classInfo) {
        if (classInfo.canonicalName == LIFECYCLE_CANONICAL_NAME) {
            lifecycleClassInfo = classInfo
            return
        }
        ClassReader reader = new ClassReader(classInfo.classStream)
//      ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS)
        ClassWriter writer = new ClassWriter(reader, 0)
        ClassVisitor visitor = new ScanLifecycleClassVisitor(writer, appLifecycles)
        reader.accept(visitor, ClassReader.SKIP_FRAMES)
    }

    @Override
    void onScanEnd() {
        P.info("app observer:${appLifecycles.toListString()}")
        if (appLifecycles.isEmpty()) return
        Injector.injectCode(lifecycleClassInfo) { type, classStream ->
            ClassReader reader = new ClassReader(classStream)
            ClassWriter writer = new ClassWriter(reader, 0)
            ClassVisitor visitor = new LifecycleInjectorClassVisitor(writer, appLifecycles)
            reader.accept(visitor, ClassReader.SKIP_FRAMES)
            return writer.toByteArray()
        }
    }
}