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
    List<ApiInfo> addApi
    List<ApiInfo> deleteApi
    private hasRegisterMethod = false
    static final String REGISTRY_CLASS_PATH = "com/jamesfchen/ibc/Registry";

    IbcInjectorClassVisitor(ClassVisitor classVisitor, List<RouterInfo> addRouters, List<ApiInfo> addApi, List<RouterInfo> deleteRouters, List<ApiInfo> deleteApi) {
        super(Opcodes.ASM6, classVisitor)
        this.addRouters = addRouters
        this.deleteRouters = deleteRouters
        this.addApi = addApi
        this.deleteApi = deleteApi
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)

    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        if ("register" != name) return methodVisitor
        hasRegisterMethod = true
//        if ((addRouters == null || addRouters.isEmpty())
//                && (addApi == null || addApi.isEmpty())
//        ) {
//            P.error("addRouters & api数据为空")
//            return methodVisitor
//        }

        methodVisitor = new AdviceAdapter(Opcodes.ASM6, methodVisitor, access, name, descriptor) {
            boolean jummp = false

            @Override
            protected void onMethodEnter() {
                super.onMethodEnter()
            }

            @Override
            void visitLdcInsn(Object value) {
                for (RouterInfo routerInfo : deleteRouters) {
                    if (Type.getType(routerInfo.descriptor) == value || routerInfo.bindingBundleName == value) {
                        jummp = true
                        return
                    }
                }
                for (ApiInfo apiInfo : deleteApi) {
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
                addRouters.each {
                    methodVisitor.visitMethodInsn(INVOKESTATIC, REGISTRY_CLASS_PATH, "getInstance", "()Lcom/jamesfchen/ibc/Registry;", false)
                    methodVisitor.visitLdcInsn(it.bindingBundleName)
                    methodVisitor.visitLdcInsn(Type.getType(it.descriptor))
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, REGISTRY_CLASS_PATH, "registerRouter", "(Ljava/lang/String;Ljava/lang/Class;)V", false)
                }
                addApi.each {
                    methodVisitor.visitMethodInsn(INVOKESTATIC, REGISTRY_CLASS_PATH, "getInstance", "()Lcom/jamesfchen/ibc/Registry;", false)
                    methodVisitor.visitLdcInsn(Type.getType(it.descriptor))
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, REGISTRY_CLASS_PATH, "registerApi", "(Ljava/lang/Class;)V", false)
                }
            }

            @Override
            protected void onMethodExit(int opcode) {
                super.onMethodExit(opcode)


            }
        }
        return methodVisitor
    }

    @Override
    void visitEnd() {
        super.visitEnd()
        if (!hasRegisterMethod) {
            P.error("没有找到register方法")
        }
    }
}