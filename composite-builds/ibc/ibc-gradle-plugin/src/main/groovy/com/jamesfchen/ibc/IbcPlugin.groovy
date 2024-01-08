package com.jamesfchen.ibc

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.google.common.collect.ImmutableSet
import com.jamesfchen.ClassInfo
import com.jamesfchen.Injector
import com.jamesfchen.P
import com.jamesfchen.ScanClassPlugin
import org.objectweb.asm.*
/**
 * 1. 如果没有扫描与插桩的处理会耗时983ms，扫描与插桩会消耗2685ms
 *
 * 2.Transform cost : 58 ms  isIncremental:true
 */
class IbcPlugin extends ScanClassPlugin {
    List<RouterInfo> addRouters
    List<RouterInfo> deleteRouters
    List<ApiInfo> addApis
    List<ApiInfo> deleteApis
    File dest;


    @Override
    String getName() {
        return "Ibc"
    }

    @Override
    void onScanBegin(TransformInvocation transformInvocation) {
        dest = transformInvocation.getOutputProvider().getContentLocation(
                "IbcTransform", TransformManager.CONTENT_CLASS,
                ImmutableSet.of(QualifiedContent.Scope.PROJECT), Format.DIRECTORY)
        addRouters = Collections.synchronizedList(new ArrayList<RouterInfo>())
        deleteRouters = Collections.synchronizedList(new ArrayList<RouterInfo>())
        addApis = Collections.synchronizedList(new ArrayList<ApiInfo>())
        deleteApis = Collections.synchronizedList(new ArrayList<ApiInfo>())

    }
    @Override
    void onScanClass(ClassInfo classInfo) {
        if (classInfo.canonicalName.endsWith("Router") || classInfo.canonicalName.endsWith("Api")) {

            P.debug(classInfo.status + "  " + classInfo.canonicalName + "  " + classInfo.mather2)

            ClassReader reader
            if (classInfo.status == ClassInfo.BIRTH_DIR || classInfo.status == ClassInfo.DEATH_DIR) {
                reader = new ClassReader(classInfo.classFile.bytes)
            } else if (classInfo.status == ClassInfo.BIRTH_JAR || classInfo.status == ClassInfo.DEATH_JAR) {
                reader = new ClassReader(classInfo.classStream)
            } else {
                throw new UnsupportedOperationException("不支持 " + classInfo.status)
            }
            ClassWriter writer = new ClassWriter(reader, 0)
            ClassVisitor visitor
            if (classInfo.status == ClassInfo.BIRTH_DIR || classInfo.status == ClassInfo.BIRTH_JAR) {
                visitor = new ScanIbcInfoClassVisitor(writer, addRouters, addApis)
            } else if (classInfo.status == ClassInfo.DEATH_DIR || classInfo.status == ClassInfo.DEATH_JAR) {
                visitor = new ScanIbcInfoClassVisitor(writer, deleteRouters, deleteApis)
            } else {
                throw new UnsupportedOperationException("不支持 " + classInfo.status)
            }
            reader.accept(visitor, ClassReader.SKIP_FRAMES)
        }
    }

    @Override
    void onScanEnd() {
        File registerProxyClassFile = new File(dest, Constants.REGISTERPROXY_CLASS_PATH + ".class")
        P.info("${addRouters.size()} addRouters:${addRouters.toListString()}")
        P.info("${deleteRouters.size()} deleteRouters:${deleteRouters.toListString()}")
        P.info("${addApis.size()} addApis:${addApis.toListString()}")
        P.info("${deleteApis.size()} deleteApis:${deleteApis.toListString()}")
        if (addRouters.isEmpty() && deleteRouters.isEmpty() && addApis.isEmpty() && deleteApis.isEmpty()) return
        if (registerProxyClassFile.exists()) {
            P.warn("RegistryProxy:"+registerProxyClassFile)
            Injector.injectCode(registerProxyClassFile) { where, classStream ->
                ClassReader reader = new ClassReader(classStream)
                ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS)
                ClassVisitor visitor = new IbcInjectorClassVisitor(writer, addRouters, addApis, deleteRouters, deleteApis)
                reader.accept(visitor, ClassReader.SKIP_DEBUG)
                return writer.toByteArray()
            }
        } else {
            P.warn("RegistryProxy not exists , rootDir:"+dest)
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            ClassVisitor cv = new ClassVisitor(Opcodes.ASM5, writer) {
            };
            cv.visit(50, Opcodes.ACC_PUBLIC, Constants.REGISTERPROXY_CLASS_PATH, null, "java/lang/Object", null);

            MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                    "register", "()V", null, null);

            mv.visitCode();
            addRouters.each { routerInfo ->
                mv.visitLdcInsn(routerInfo.bindingBundleName)
                mv.visitLdcInsn(Type.getType(routerInfo.descriptor))
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, Constants.REGISTRY_CLASS_PATH, "registerRouter", "(Ljava/lang/String;Ljava/lang/Class;)V", false)
            }
            addApis.each {
                mv.visitLdcInsn(Type.getType(it.descriptor))
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, Constants.REGISTRY_CLASS_PATH, "registerApi", "(Ljava/lang/Class;)V", false)
            }
            mv.visitMaxs(0, 0)
            mv.visitInsn(Opcodes.RETURN)
            mv.visitEnd();
            cv.visitEnd();

            registerProxyClassFile.getParentFile().mkdirs();
            new FileOutputStream(registerProxyClassFile).withCloseable {
                it.write(writer.toByteArray())
            }
        }


    }
}