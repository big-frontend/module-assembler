package com.jamesfchen.ibc

import com.jamesfchen.P
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

class IbcInjectorClassVisitor extends ClassVisitor {
    List<RouterInfo> routers
    List<ApiInfo> apis
    private hasInitMethod = false
    static final String REGISTRY_CLASS_PATH = "com/jamesfchen/ibc/Registry";

    IbcInjectorClassVisitor(ClassVisitor classVisitor, List<RouterInfo> routers, List<ApiInfo> apis) {
        super(Opcodes.ASM6, classVisitor)
        this.routers = routers
        this.apis = apis
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        P.info("router or api 将要注入代码到${name}")
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        if ("init" != name) return methodVisitor
        if ((routers == null || routers.isEmpty())
                && (apis == null || apis.isEmpty())
        ) {
            P.error("routers & apit数据为空")
            return methodVisitor
        }
        hasInitMethod = true
        methodVisitor = new AdviceAdapter(Opcodes.ASM6, methodVisitor, access, name, descriptor) {
            @Override
            protected void onMethodEnter() {
                super.onMethodEnter()
            }

            @Override
            protected void onMethodExit(int opcode) {
                super.onMethodExit(opcode)
                routers.each {
                    methodVisitor.visitMethodInsn(INVOKESTATIC, REGISTRY_CLASS_PATH, "getInstance", "()Lcom/jamesfchen/ibc/Registry;", false)
                    methodVisitor.visitLdcInsn(it.name)
                    methodVisitor.visitLdcInsn(Type.getType(it.descriptor))
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, REGISTRY_CLASS_PATH, "registerRouter", "(Ljava/lang/String;Ljava/lang/Class;)V", false)
                }
                apis.each {
                    methodVisitor.visitMethodInsn(INVOKESTATIC, REGISTRY_CLASS_PATH, "getInstance", "()Lcom/jamesfchen/ibc/Registry;", false)
                    methodVisitor.visitLdcInsn(Type.getType(it.descriptor))
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, REGISTRY_CLASS_PATH, "registerApi", "(Ljava/lang/Class;)V", false)
                }

            }
        }
        return methodVisitor
    }

    @Override
    void visitEnd() {
        super.visitEnd()
        if (!hasInitMethod) {
            P.error("没有找到init方法")
        }
    }
}