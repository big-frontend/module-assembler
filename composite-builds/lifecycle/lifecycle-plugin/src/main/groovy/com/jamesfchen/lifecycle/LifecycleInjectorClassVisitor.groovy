package com.jamesfchen.lifecycle

import com.jamesfchen.P
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

class LifecycleInjectorClassVisitor extends ClassVisitor {
    List<String> appLifecycles
    private hasRegisterMethod = false

    LifecycleInjectorClassVisitor(ClassVisitor classVisitor, List<String> appLifecycles) {
        super(Opcodes.ASM6, classVisitor)
        this.appLifecycles = appLifecycles
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        P.info("lifecycle 将要注入代码到${name}")
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        if ("register" != name) return methodVisitor
        if (appLifecycles == null || appLifecycles.isEmpty()) {
            P.info("lifecycles数据为空")
            return methodVisitor
        }
        hasRegisterMethod = true
        methodVisitor = new AdviceAdapter(Opcodes.ASM6, methodVisitor, access, name, descriptor) {

            @Override
            protected void onMethodExit(int opcode) {
                super.onMethodExit(opcode)
                appLifecycles.each {
//                    def clz = it.replace('/','.')
//                    methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
//                methodVisitor.visitFieldInsn(Opcodes.GETFIELD,'android/app/Application', appObjectName, appObjectDesc);
                    methodVisitor.visitFieldInsn(Opcodes.GETSTATIC,'com/jamesfchen/lifecycle/LifecycleInitializer', 'sApplication', 'Landroid/app/Application;');
//                    methodVisitor.visitLocalVariable()
                    methodVisitor.visitLdcInsn(Type.getType(it));
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "com/jamesfchen/lifecycle/LifecycleInitializer", "registerInternal", "(Landroid/app/Application;Ljava/lang/Class;)V", false);
                }

            }
        }
        return methodVisitor
    }

    @Override
    void visitEnd() {
        super.visitEnd()
        if (!hasRegisterMethod) {
            P.info("没有找到register方法，app类需要重写register")
        }
    }
}