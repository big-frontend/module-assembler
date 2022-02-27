package com.jamesfchen

import com.android.build.api.transform.*
import groovy.io.FileType
import org.apache.commons.io.FileUtils

import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

abstract class AbsInsertCodeTransform extends Transform implements IInsertCode {

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        long start = System.currentTimeMillis();
        if (!isIncremental()) {
            try {
                transformInvocation.outputProvider.deleteAll()
            } catch (IOException e) {
                P.error(e.getLocalizedMessage())
            }
        }
        TransformOutputProvider outputProvider = transformInvocation.outputProvider
        onInsertCodeBegin()
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
                            || it.name.startsWith("R\$") || (it.name == ("BuildConfig.class"))
                    ) {
                        return
                    }
                    def classPath = it.absolutePath.replace(rootPath, '')
                    def canonicalName = classPath.split('\\.')[0].replace(File.separator, '.')
                    it.withInputStream { fis ->
                        byte[] codes = onInsertCode(destDir, fis, canonicalName)

                        if (codes != null && codes.length > 0) {
                            it.withOutputStream { os ->
                                os.write(codes)
                            }
                        }
                    }
                }

            }
            //jar包，jar包或者aar包
            for (int k = 0; k < input.jarInputs.size(); ++k) {
                JarInput jarInput = input.jarInputs[k]
                File destJar = outputProvider.getContentLocation(
                        jarInput.getName(), jarInput.getContentTypes(),
                        jarInput.getScopes(), Format.JAR
                )
                FileUtils.copyFile(jarInput.file, destJar)

                def optZipFile = new File(destJar.getParent(), destJar.name + ".opt")
                if (optZipFile.exists()) {
                    optZipFile.delete()
                }
                new ZipOutputStream(new FileOutputStream(optZipFile)).withCloseable { optZipOutputStream ->
                    new ZipFile(destJar).withCloseable { theZip ->
                        theZip.entries().each { zipEntry ->
                            String zipName = zipEntry.name
                            ZipEntry optZipEntry = new ZipEntry(zipName)
                            InputStream inputStream = theZip.getInputStream(zipEntry)
                            optZipOutputStream.putNextEntry(optZipEntry)
                            //zipName 有时候是文件夹
                            String canonicalName = zipName.split('\\.')[0].replace('/', '.')
                            String fileName = zipName.substring(zipName.lastIndexOf('/') + 1)
                            if (!fileName.endsWith(".class") || (fileName == ("R.class"))
                                    || fileName.startsWith("R\$")
                                    || (fileName == ("BuildConfig.class"))
//                                    || canonicalName.startsWith("androidx")
//                                    || canonicalName.startsWith("android")
//                                    || canonicalName.startsWith("kotlin")
                                    || canonicalName.startsWith("org.bouncycastle")
                            ) {
                                optZipOutputStream.write(inputStream.bytes)
                                inputStream.close()
                                optZipOutputStream.closeEntry()
                                return
                            }
                            byte[] codes = onInsertCode(destJar, inputStream, canonicalName)
                            if (codes != null && codes.size() > 0) {
                                optZipOutputStream.write(codes)
                            } else {
                                optZipOutputStream.write(inputStream.bytes)
                            }
                            inputStream.close()
                            optZipOutputStream.closeEntry()

                        }
                    }
                }
                if (destJar.exists()) {
                    destJar.delete()
                }
                optZipFile.renameTo(destJar)

            }
        }

        onInsertCodeEnd()
        def cost = System.currentTimeMillis() - start
        P.info("Transform cost : $cost ms")
    }


}