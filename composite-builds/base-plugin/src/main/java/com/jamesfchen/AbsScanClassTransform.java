package com.jamesfchen;

import static com.jamesfchen.Constants.DIR;
import static com.jamesfchen.Constants.JAR;

import com.android.build.api.transform.TransformInvocation;

import org.apache.commons.io.FileUtils;
import org.gradle.internal.impldep.com.google.common.io.Files;

import java.io.File;
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
import java.util.jar.JarFile;

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 */
public abstract class AbsScanClassTransform extends AbsTransform {

    @Override
    protected void onTransformBegin(TransformInvocation transformInvocation) throws Exception {
        onScanBegin(transformInvocation);
    }

    @Override
    protected void onTransformEnd() throws Exception {
        onScanEnd();
    }

    protected abstract void onScanBegin(TransformInvocation transformInvocation) throws Exception;

    protected abstract void onScanClass(ClassInfo info) throws Exception;

    protected abstract void onScanEnd() throws Exception;

    @Override
    protected void onFileNotChanged(File srcRootDir, File destRootDir, File srcFile, File destFile) throws Exception {
    }

    @Override
    protected void onJarNotChanged(File srcJar, File destJar, List<JarEntry> unchangedList) throws Exception {
    }

    @Override
    protected void onFileAdded(File srcRootDir, File destRootDir, File srcFile, File destFile) throws Exception {
        P.debug(this.getClass().getSimpleName()+" onFileAdded >>> " + srcFile.getName());
        fileAddedAndChanged(srcRootDir, destRootDir, srcFile, destFile);
    }

    @Override
    protected void onFileChanged(File srcRootDir, File destRootDir, File srcFile, File destFile) throws Exception {
        P.debug(this.getClass().getSimpleName()+" onFileChanged >>> " + srcFile.getName());
        fileAddedAndChanged(srcRootDir, destRootDir, srcFile, destFile);
    }

    protected void fileAddedAndChanged(File srcRootDir, File destRootDir, File srcFile, File destFile) throws Exception {
        try {
            FileUtils.touch(destFile);
        } catch (Exception ignored) {
            Files.createParentDirs(destFile);
        }

        String canonicalName = F.canonicalName(srcRootDir, srcFile);
        if (canonicalName != null && !canonicalName.isEmpty()) {
            onScanClass(new ClassInfo(ClassInfo.BIRTH_DIR,srcRootDir, destRootDir, srcFile, canonicalName));
        }
        if (srcFile.isDirectory()) {
            FileUtils.copyDirectory(srcFile, destFile);
        }else {
            FileUtils.copyFile(srcFile, destFile);
        }

    }

    @Override
    protected void onFileRemoved(File srcRootDir, File destRootDir, File srcFile, File destFile) throws Exception {
        P.debug(this.getClass().getSimpleName()+" onFileRemoved >>> " + srcFile.getName() + "   srcFile:" + srcFile+ "  destFile" + destFile);
        if (destFile.isDirectory()) {
            java.nio.file.Files.walkFileTree(Paths.get(destFile.getAbsolutePath()), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String canonicalName = F.canonicalName(destRootDir, file.toFile());
                    if (canonicalName != null && !canonicalName.isEmpty()) {
                        try {
                            onScanClass(new ClassInfo(ClassInfo.DEATH_DIR, srcRootDir, destRootDir, file.toFile(), canonicalName));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return super.visitFile(file, attrs);
                }
            });
        } else {
            String canonicalName = F.canonicalName(destRootDir, destFile);
            if (canonicalName != null && !canonicalName.isEmpty()) {
                onScanClass(new ClassInfo(ClassInfo.DEATH_DIR, srcRootDir,destRootDir, destFile, canonicalName));
            }
        }

        if (destFile.exists()) {
            FileUtils.forceDelete(destFile);
        }

    }

    @Override
    protected void onJarAdded(File srcJar, File destJar, List<JarEntry> addedList) throws Exception {
        P.debug(this.getClass().getSimpleName()+" onJarAdded >>> addList:" + Arrays.toString(addedList.toArray()));
        scanJar(srcJar, destJar, ClassInfo.BIRTH_JAR, addedList);
        FileUtils.copyFile(srcJar, destJar);
    }

    @Override
    protected void onJarChanged(File srcJar, File destJar, List<JarEntry> addedList, List<JarEntry> removedList) throws Exception {
        P.debug(this.getClass().getSimpleName()+" onJarChanged >>> addedList:" + Arrays.toString(addedList.toArray()) + " removedList:" + Arrays.toString(removedList.toArray()));

        scanJar(srcJar, destJar, ClassInfo.BIRTH_JAR, addedList);
        scanJar(destJar, destJar, ClassInfo.DEATH_JAR, removedList);
        FileUtils.copyFile(srcJar, destJar);
    }

    @Override
    protected void onJarRemoved(File srcJar, File destJar, List<JarEntry> removedList) throws Exception {
        P.debug(this.getClass().getSimpleName()+" onJarAdded >>> removedList:" + Arrays.toString(removedList.toArray()));
        scanJar(srcJar, destJar, ClassInfo.DEATH_JAR, removedList);
        if (destJar.exists()) {
            FileUtils.forceDelete(destJar);
        }
    }

    @Override
    protected void onFull(int where, File src, File dest) throws Exception {
        switch (where) {
            case DIR: {
                scanDir(src, dest);
                FileUtils.copyDirectory(src, dest);
                break;
            }
            case JAR: {
                scanJar(src, dest, ClassInfo.BIRTH_JAR, null);
                FileUtils.copyFile(src, dest);
                break;
            }
            default:
                throw new UnsupportedOperationException("不存在 " + where);
        }
    }

    void scanDir(File srcRootDir, File destRootDir) throws IOException {
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
                try {
                    onScanClass(new ClassInfo(ClassInfo.BIRTH_DIR,srcRootDir, destRootDir, file.toFile(), canonicalName));
                } catch (Exception e) {
                    P.error(e.getLocalizedMessage());
                }
                return super.visitFile(file, attrs);
            }
        });
    }

    void scanJar(File srcJar, File destJar, int classStatus, List<JarEntry> jarEntries) {
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(srcJar);
            List<JarEntry> entries;
            if (jarEntries != null) {
                entries = jarEntries;
            } else {
                entries = Collections.list(jarFile.entries());
            }
            for (JarEntry jarEntry : entries) {
                InputStream inputStream = jarFile.getInputStream(jarEntry);
                String canonicalName = F.canonicalName(jarEntry);
                String fileName = jarEntry.getName().substring(jarEntry.getName().lastIndexOf("/") + 1);
                if (canonicalName == null || canonicalName.isEmpty()
                        || "R.class".equals(fileName)
                        || "BuildConfig.class".equals(fileName)
                        || fileName.startsWith("R$")
                        //                            || canonicalName.startsWith("androidx")
                        //                            || canonicalName.startsWith("android")
                        //                            || canonicalName.startsWith("kotlin")
                        || canonicalName.startsWith("org.bouncycastle")
                ) {
                    continue;
                }
                onScanClass(new ClassInfo(classStatus, srcJar,destJar, inputStream, canonicalName));
                inputStream.close();
            }
        } catch (Exception e) {
            P.error(e.getLocalizedMessage());
        } finally {
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
