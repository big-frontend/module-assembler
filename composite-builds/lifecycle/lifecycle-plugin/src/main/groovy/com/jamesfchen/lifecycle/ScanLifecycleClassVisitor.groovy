package com.jamesfchen.lifecycle


import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

class ScanLifecycleClassVisitor extends ClassVisitor {
    private static final String APP_LIFECYCLE_ANNOTATION_DESCRIPTOR = "Lcom/jamesfchen/lifecycle/AppLifecycle;"
    boolean hasAppLifecycleAnnotation = false
    String classDescriptor
    List<String> appLifecycles

    ScanLifecycleClassVisitor(ClassWriter classVisitor, List<String> appLifecycles) {
        super(Opcodes.ASM6, classVisitor)
        this.appLifecycles = appLifecycles
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        classDescriptor = "L" + name + ";"
    }

    @Override
    AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if (APP_LIFECYCLE_ANNOTATION_DESCRIPTOR == descriptor) {
            hasAppLifecycleAnnotation = true
        }
        return super.visitAnnotation(descriptor, visible)
    }

    @Override
    void visitEnd() {
        super.visitEnd()
        if (hasAppLifecycleAnnotation) {
            appLifecycles.add(classDescriptor)
        }
        hasAppLifecycleAnnotation = false

    }
}