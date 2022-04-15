package com.jamesfchen.lifecycle

import com.android.build.api.transform.TransformInvocation
import com.jamesfchen.ClassInfo
import com.jamesfchen.P
import com.jamesfchen.ScanClassPlugin

class LifecyclePlugin extends ScanClassPlugin {
    List<String> appLifecycles
    static final String LIFECYCLE_CANONICAL_NAME = "com.jamesfchen.lifecycle.LifecycleInitializer";
    ClassInfo lifecycleClassInfo
    @Override
    String pluginName() {
        return "Lifecycle"
    }

    @Override
    void onScanBegin(TransformInvocation transformInvocation) {
        appLifecycles = new ArrayList<>()
    }

    @Override
    void onScanClass(ClassInfo classInfo) {
//        if (classInfo.canonicalName == LIFECYCLE_CANONICAL_NAME) {
//            lifecycleClassInfo = classInfo
//            return
//        }
//        ClassReader reader = new ClassReader(
//                classInfo.birth == ClassInfo.BIRTH_DIR
//                        ? classInfo.classFile.bytes
//                        : classInfo.classStream
//        )
////      ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS)
//        ClassWriter writer = new ClassWriter(reader, 0)
//        ClassVisitor visitor = new ScanLifecycleClassVisitor(writer, appLifecycles)
//        reader.accept(visitor, ClassReader.SKIP_FRAMES)
    }

    @Override
    void onScanEnd() {
        P.info("app observer:${appLifecycles.toListString()}")
//        if (appLifecycles.isEmpty()) return
//        Injector.injectCode(lifecycleClassInfo) { type, classStream ->
//            ClassReader reader = new ClassReader(classStream)
//            ClassWriter writer = new ClassWriter(reader, 0)
//            ClassVisitor visitor = new LifecycleInjectorClassVisitor(writer, appLifecycles)
//            reader.accept(visitor, ClassReader.SKIP_FRAMES)
//            return writer.toByteArray()
//        }
    }
}