package com.jamesfchen;

import com.android.build.api.transform.TransformInvocation;

import org.apache.commons.io.FileUtils;
import org.gradle.internal.impldep.com.google.common.io.Files;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: Apr/14/2022  Thu
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
            for (File file : com.android.utils.FileUtils.getAllFiles(destFile)) {
                String canonicalName = F.canonicalName(srcRootDir, file);
                if (canonicalName != null && !canonicalName.isEmpty()) {
//                    onScanClass(new ClassInfo(ClassInfo.DEATH_DIR, destRootDir, file, canonicalName));
                }
            }
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
//                scanDir(src, dest);
                FileUtils.copyDirectory(src, dest);
                break;
            }
            case JAR: {
//                scanJar(src, dest, ClassInfo.BIRTH_JAR, null);
                FileUtils.copyFile(src, dest);
                break;
            }
            default:
                throw new UnsupportedOperationException("不存在 " + where);
        }
    }
}
