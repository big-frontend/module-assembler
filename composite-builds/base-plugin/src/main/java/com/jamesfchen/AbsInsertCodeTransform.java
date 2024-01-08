package com.jamesfchen;

import static com.jamesfchen.Constants.DIR;
import static com.jamesfchen.Constants.JAR;

import com.android.build.api.transform.TransformInvocation;

import org.apache.commons.io.FileUtils;
import org.gradle.internal.impldep.com.google.common.io.Files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Copyright ® $ 2017
 * All right reserved.
 */
public abstract class AbsInsertCodeTransform extends AbsTransform {
    @Override
    public boolean isIncremental() {
        return true;
    }

    @Override
    protected void onTransformBegin(TransformInvocation transformInvocation) throws Exception {
        onInsertCodeBegin(transformInvocation);
    }

    @Override
    protected void onTransformEnd() throws Exception {
        onInsertCodeEnd();
    }

    protected abstract void onInsertCodeBegin(TransformInvocation transformInvocation) throws Exception;

    protected abstract byte[] onInsertCode(ClassInfo info) throws Exception;

    protected abstract void onInsertCodeEnd() throws Exception;

    @Override
    protected void onFileNotChanged(File srcRootDir, File destRootDir, File srcFile, File destFile) throws Exception {
    }

    @Override
    protected void onJarNotChanged(File srcJar, File destJar, List<JarEntry> unchangedList) throws Exception {
    }

    @Override
    protected void onFileAdded(File srcRootDir, File destRootDir, File srcFile, File destFile) throws Exception {
        P.debug("onFileAdded >>> " + srcFile.getName());
        fileAddedAndChanged(srcRootDir, destRootDir, srcFile, destFile);
    }

    @Override
    protected void onFileChanged(File srcRootDir, File destRootDir, File srcFile, File destFile) throws Exception {
        P.debug("onFileChanged >>> " + srcFile.getName());
        fileAddedAndChanged(srcRootDir, destRootDir, srcFile, destFile);
    }

    protected void fileAddedAndChanged(File srcRootDir, File destRootDir, File srcFile, File destFile) throws Exception {
        try {
            FileUtils.touch(destFile);
        } catch (Exception ignored) {
            Files.createParentDirs(destFile);
        }
        //新增加文件夹不需要copy
        if (srcFile.isDirectory()) {
            return;
        }
        String canonicalName = F.canonicalName(srcRootDir, srcFile);
        if (canonicalName != null && !canonicalName.isEmpty()) {
//            onScanClass(new ClassInfo(ClassInfo.BIRTH_DIR, destRootDir, srcFile, canonicalName));
            FileUtils.copyFile(srcFile, destFile);
        } else {
            FileUtils.copyFile(srcFile, destFile);
        }

    }

    @Override
    protected void onFileRemoved(File srcRootDir, File destRootDir, File srcFile, File destFile) throws Exception {
        P.debug("onFileRemoved >>> " + srcFile.getName() + "   srcFile exits:" + srcFile.exists() + "  destFile exits:" + destFile.exists());
        if (destFile.isDirectory()) {
            java.nio.file.Files.walkFileTree(Paths.get(destFile.getAbsolutePath()), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String canonicalName = F.canonicalName(srcRootDir, file.toFile());
                    if (canonicalName != null && !canonicalName.isEmpty()) {
//                    onScanClass(new ClassInfo(ClassInfo.DEATH_DIR, destRootDir, file, canonicalName));
                    }
                    return super.visitFile(file, attrs);
                }
            });
        } else {
            String canonicalName = F.canonicalName(destRootDir, destFile);
            if (canonicalName != null && !canonicalName.isEmpty()) {
//                onScanClass(new ClassInfo(ClassInfo.DEATH_DIR, destRootDir, destFile, canonicalName));
            }
        }

        if (destFile.exists()) {
            FileUtils.forceDelete(destFile);
        }

    }

    @Override
    protected void onJarAdded(File srcJar, File destJar, List<JarEntry> addedList) throws Exception {
        P.debug("onJarAdded >>> addList:" + Arrays.toString(addedList.toArray()));
//        scanJar(srcJar, destJar, ClassInfo.BIRTH_JAR, addedList);
        FileUtils.copyFile(srcJar, destJar);
    }

    @Override
    protected void onJarChanged(File srcJar, File destJar, List<JarEntry> addedList, List<JarEntry> removedList) throws Exception {
        P.debug("onJarChanged >>> addedList:" + Arrays.toString(addedList.toArray()) + " removedList:" + Arrays.toString(removedList.toArray()));

//        scanJar(srcJar, destJar, ClassInfo.BIRTH_JAR, addedList);
//        scanJar(destJar, destJar, ClassInfo.DEATH_JAR, removedList);
        FileUtils.copyFile(srcJar, destJar);
    }

    @Override
    protected void onJarRemoved(File srcJar, File destJar, List<JarEntry> removedList) throws Exception {
        P.debug("onJarAdded >>> removedList:" + Arrays.toString(removedList.toArray()));
//        scanJar(srcJar, destJar, ClassInfo.DEATH_JAR, removedList);
        if (destJar.exists()) {
            FileUtils.forceDelete(destJar);
        }
    }

    @Override
    protected void onFull(int where, File src, File dest) throws Exception {
        switch (where) {
            case DIR: {
                insertDir(src, dest);
                FileUtils.copyDirectory(src, dest);
                break;
            }
            case JAR: {
                insertJar(src, dest, ClassInfo.BIRTH_JAR, null);
                FileUtils.copyFile(src, dest);
                break;
            }
            default:
                throw new UnsupportedOperationException("不存在 " + where);
        }
    }

    void insertJar(File srcJar, File destJar, int classStatus, List<ZipEntry> jarEntries) {
        File optZipFile = new File(srcJar.getParent(), srcJar.getName() + ".opt");
        if (optZipFile.exists()) {
            optZipFile.delete();
        }
        try (ZipOutputStream optZipFos = new ZipOutputStream(new FileOutputStream(optZipFile)); ZipFile theZip = new ZipFile(srcJar)) {
            List<? extends ZipEntry> entries;
            if (jarEntries != null) {
                entries = jarEntries;
            } else {
                entries = Collections.list(theZip.entries());
            }
            for (ZipEntry zipEntry : entries) {
                try (InputStream inputStream = theZip.getInputStream(zipEntry)) {
                    String canonicalName = F.canonicalName(zipEntry);
                    String fileName = zipEntry.getName().substring(zipEntry.getName().lastIndexOf("/") + 1);
                    //创建一个新的zip entry
                    optZipFos.putNextEntry(new ZipEntry(zipEntry.getName()));
                    if (canonicalName == null || canonicalName.isEmpty()
                            || "R.class".equals(fileName)
                            || "BuildConfig.class".equals(fileName)
                            || fileName.startsWith("R$")
                            //                            || canonicalName.startsWith("androidx")
                            //                            || canonicalName.startsWith("android")
                            //                            || canonicalName.startsWith("kotlin")
                            || canonicalName.startsWith("org.bouncycastle")
                    ) {
                        F.copyIs2Os(inputStream, optZipFos);
                    } else {
                        byte[] codes = onInsertCode(new ClassInfo(classStatus, srcJar, destJar, inputStream, canonicalName));
                        if (codes.length != 0) {
                            optZipFos.write(codes);
                        } else {
                            F.copyIs2Os(inputStream, optZipFos);
                        }
                    }
                    optZipFos.closeEntry();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (srcJar.exists()) {
            srcJar.delete();
        }
        optZipFile.renameTo(srcJar);

    }

    void insertDir(File srcRootDir, File destRootDir) throws IOException {
        java.nio.file.Files.walkFileTree(Paths.get(srcRootDir.getAbsolutePath()), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String canonicalName = F.canonicalName(srcRootDir, file.toFile());
                if (canonicalName == null || canonicalName.isEmpty()
                        || "R.class".equals(file.getFileName().toString())
                        || "BuildConfig.class".equals(file.getFileName().toString())
                        || file.getFileName().toString().startsWith("R$")
                ) {
                    return super.visitFile(file, attrs);
                }
                FileOutputStream fos = null;
                try {
                    byte[] codes = new byte[0];
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(file.toFile());
                        codes = onInsertCode(new ClassInfo(ClassInfo.BIRTH_DIR, srcRootDir, destRootDir, fis, canonicalName));
                    } catch (Exception e) {
                        P.error(e.getLocalizedMessage());
                    } finally {
                        if (fis != null) fis.close();
                    }
                    if (codes.length != 0) {
                        fos = new FileOutputStream(file.toFile());
                        fos.write(codes);
                    }
                } catch (Exception e) {
                    P.error(e.getLocalizedMessage());
                } finally {
                    if (fos != null) {
                        fos.close();
                    }
                }
                return super.visitFile(file, attrs);
            }
        });
    }
}
