package com.jamesfchen

import androidx.annotation.CallSuper
import com.android.build.api.transform.*
import groovy.io.FileType
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils

import java.util.jar.JarFile

abstract class AbsScanClassTransform extends Transform implements IScanClass {

    @Override
    boolean isIncremental() {
        return false
    }

    @CallSuper
    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        long start = System.currentTimeMillis()
        if (!isIncremental()){
            try {
                transformInvocation.outputProvider.deleteAll()
            } catch (IOException e) {
               P.error(e.getLocalizedMessage())
            }
        }
        def outputProvider = transformInvocation.outputProvider
        onScanBegin()
        transformInvocation.inputs.each { TransformInput input ->
            //源代码编译之后的class目录
            for (int j = 0; j < input.directoryInputs.size(); ++j) {
                DirectoryInput directoryInput = input.directoryInputs[j]
                File srcDir = directoryInput.file

                File destDir = outputProvider.getContentLocation(
                        directoryInput.getName(), directoryInput.getContentTypes(),
                        directoryInput.getScopes(), Format.DIRECTORY
                )
                String rootPath = destDir.absolutePath
                if (!rootPath.endsWith(File.separator)) {
                    rootPath += File.separator
                }
                FileUtils.copyDirectory(srcDir, destDir)

                destDir.eachFileRecurse(FileType.FILES) {
                    if (!it.name.endsWith(".class") || (it.name == ("R.class"))
                            || it.name.startsWith("R\$") || (it.name == ("BuildConfig.class"))) {
                        return
                    }
                    def classPath = it.absolutePath.replace(rootPath, '')
                    def canonicalName = classPath.split('\\.')[0].replace(File.separator, '.')
                    onScanClassInDir(new ClassInfo(destDir, it, canonicalName))
                }

            }
            //jar包，jar包或者aar包
            for (int k = 0; k < input.jarInputs.size(); ++k) {
                JarInput jarInput = input.jarInputs[k]
                def destName = jarInput.file.name
                /* 重名名输出文件,因为可能同名,会覆盖*/
                def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath).substring(0, 8)
                if (destName.endsWith(".jar")) {
                    destName = destName.substring(0, destName.length() - 4)
                }
                File destJar = outputProvider.getContentLocation(
                        destName + "_" + hexName, jarInput.getContentTypes(),
                        jarInput.getScopes(), Format.JAR
                )
                FileUtils.copyFile(jarInput.file, destJar)

                JarFile jarFile = new JarFile(destJar)
                jarFile.entries().each {
                    InputStream inputStream = jarFile.getInputStream(it)
                    def canonicalName = it.name.split('\\.')[0].replace('/', '.')
                    def fileName = it.name.substring(it.name.lastIndexOf('/') + 1)
                    if (!fileName.endsWith(".class") || (fileName == ("R.class"))
                            || fileName.startsWith("R\$")
                            || fileName == "BuildConfig.class"
//                            || canonicalName.startsWith("androidx")
//                            || canonicalName.startsWith("android")
//                            || canonicalName.startsWith("kotlin")
                            || canonicalName.startsWith("org.bouncycastle")
                    ) {
                        return
                    }
                    onScanClassInJar(new ClassInfo(destJar, inputStream, canonicalName))
                    inputStream.close()
                }
                jarFile.close()

            }
        }

        onScanEnd()
        def cost = System.currentTimeMillis() - start
        P.info("Transform cost : $cost ms")
    }
}