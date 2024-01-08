package com.jamesfchen.ibc

import com.jamesfchen.P
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter
class IbcInjectorClassVisitor extends ClassVisitor {
    List<RouterInfo> addRouters
    List<RouterInfo> deleteRouters
    List<ApiInfo> addApis
    List<ApiInfo> deleteApis
    private hasRegisterMethod = false


    IbcInjectorClassVisitor(ClassVisitor classVisitor, List<RouterInfo> addRouters, List<ApiInfo> addApis, List<RouterInfo> deleteRouters, List<ApiInfo> deleteApis) {
        super(Opcodes.ASM6, classVisitor)
        this.addRouters = addRouters
        this.deleteRouters = deleteRouters
        this.addApis = addApis
        this.deleteApis = deleteApis
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        if ("register" != name) return methodVisitor
        hasRegisterMethod = true
        return new MyMethodVisitor(methodVisitor, access, name, descriptor)
    }

    @Override
    void visitEnd() {
        super.visitEnd()
        if (!hasRegisterMethod) {
            P.error("没有找到register方法")
        }
    }

    class MyMethodVisitor extends AdviceAdapter {
        boolean jummp = false

        MyMethodVisitor(MethodVisitor methodVisitor, int access, String name, String descriptor) {
            super(Opcodes.ASM6, methodVisitor, access, name, descriptor)
        }

        @Override
        void visitLdcInsn(Object value) {
            def deleteRoutersIterator = deleteRouters.iterator()
            while (deleteRoutersIterator.hasNext()) {
                RouterInfo routerInfo = deleteRoutersIterator.next()
                if (Type.getType(routerInfo.descriptor) == value || routerInfo.bindingBundleName == value) {
                    jummp = true
                    return
                }
            }

            def deleteApisIterator = deleteApis.iterator()
            while (deleteApisIterator.hasNext()) {
                def apiInfo = deleteApisIterator.next()
                if (Type.getType(apiInfo.descriptor) == value) {
                    jummp = true
                    return
                }
            }
            jummp = false
            super.visitLdcInsn(value)

        }

        @Override
        void visitMethodInsn(int opcode, String owner, String methodName, String desc, boolean itf) {
            if (jummp) return
            super.visitMethodInsn(opcode, owner, methodName, desc, itf);
        }

        @Override
        void visitCode() {
            super.visitCode();
            P.info("router or api 将要注入代码到${name}")
            def addRoutersIterator = addRouters.iterator()
            while (addRoutersIterator.hasNext()) {
                def routerInfo = addRoutersIterator.next()
                mv.visitLdcInsn(routerInfo.bindingBundleName)
                mv.visitLdcInsn(Type.getType(routerInfo.descriptor))
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, Constants.REGISTRY_CLASS_PATH, "registerRouter", "(Ljava/lang/String;Ljava/lang/Class;)V", false)
            }
            def addApisIterator = addApis.iterator()
            while (addApisIterator.hasNext()) {
                def apiInfo = addApisIterator.next()
                mv.visitLdcInsn(Type.getType(apiInfo.descriptor))
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, Constants.REGISTRY_CLASS_PATH, "registerApi", "(Ljava/lang/Class;)V", false)
            }
        }
    }
}