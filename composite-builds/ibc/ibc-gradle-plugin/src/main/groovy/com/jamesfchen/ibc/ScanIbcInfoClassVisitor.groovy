package com.jamesfchen.ibc


import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

class ScanIbcInfoClassVisitor extends ClassVisitor {

    boolean hasRouterAnnotation = false
    boolean hasApiAnnotation = false
    String classDescriptor
    String bindingBundleName
    List<RouterInfo> routers
    List<ApiInfo> api

    ScanIbcInfoClassVisitor(ClassWriter classVisitor, List<RouterInfo> routers,List<ApiInfo> api) {
        super(Opcodes.ASM6, classVisitor)
        this.routers = routers
        this.api = api
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        classDescriptor = "L" + name + ";"
    }


    @Override
    AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if (Constants.ROUTER_ANNOTATION_DESC == descriptor) {
            hasRouterAnnotation = true
        }
        if (Constants.API_ANNOTATION_DESC == descriptor) {
            hasApiAnnotation = true
        }
        AnnotationVisitor av = super.visitAnnotation(descriptor, visible)
        av = new AnnotationVisitor(Opcodes.ASM5,av) {
            @Override
            void visit(String name, Object value) {
                super.visit(name, value)
                if (hasRouterAnnotation){
//                    P.info("cjf "+name + " = " + value)
                    bindingBundleName = value
                }
            }

        }
        return av
    }

    @Override
    void visitEnd() {
        super.visitEnd()
        if (hasRouterAnnotation) {
            if (routers != null) {
                routers.add(new RouterInfo(bindingBundleName,classDescriptor))
            }
        }
        if (hasApiAnnotation) {
            if (api != null) {
                api.add(new ApiInfo(classDescriptor))
            }
        }
    }
}