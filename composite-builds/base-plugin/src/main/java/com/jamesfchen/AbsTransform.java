package com.jamesfchen;

import static com.jamesfchen.Constants.DIR;
import static com.jamesfchen.Constants.JAR;

import com.android.build.api.transform.Format;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.jar.JarEntry;

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 *
 * AbsTransform
 * - 提供并发能力
 * - 增编编译
 */
public abstract class AbsTransform extends Transform {
    ExecutorService executor = ForkJoinPool.commonPool();
    List<Callable<Void>> tasks = new ArrayList<>();


    protected abstract void onTransformBegin(TransformInvocation transformInvocation) throws Exception;

    /**
     * 增加 文件/目录
     */
    protected abstract void onFileAdded(File srcRootDir, File destRootDir, File srcFile, File destFile) throws Exception;

    /**
     * 增加 或者 删除 文件/目录
     */
    protected abstract void onFileChanged(File srcRootDir, File destRootDir, File srcFile, File destFile) throws Exception;

    /**
     * 删除 文件/目录
     */
    protected abstract void onFileRemoved(File srcRootDir, File destRootDir, File srcFile, File destFile) throws Exception;

    /**
     * 删除 文件/目录
     */
    protected abstract void onFileNotChanged(File srcRootDir, File destRootDir, File srcFile, File destFile) throws Exception;

    /**
     * 增加 jar
     */
    protected abstract void onJarAdded(File srcJar, File destJar, List<JarEntry> addedList) throws Exception;

    /**
     * 增加或者删除 jar entry
     */
    protected abstract void onJarChanged(File srcJar, File destJar, List<JarEntry> addedList, List<JarEntry> removedList) throws Exception;

    /**
     * 删除 jar
     */
    protected abstract void onJarRemoved(File srcJar, File destJar, List<JarEntry> removedList) throws Exception;

    /**
     * 没有变化 jar
     */
    protected abstract void onJarNotChanged(File srcJar, File destJar, List<JarEntry> unchangedList) throws Exception;

    /**
     * 全量编译
     */
    protected abstract void onFull(int where, File src, File dest) throws Exception;

    protected abstract void onTransformEnd() throws Exception;

    @Override
    public boolean isIncremental() {
        return true;
    }

    @Override
    public synchronized final void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        try {
            long start = System.currentTimeMillis();
            boolean isIncremental = transformInvocation.isIncremental();
            if (!isIncremental) {
                transformInvocation.getOutputProvider().deleteAll();
            }
            TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
            onTransformBegin(transformInvocation);
            transformInvocation.getInputs().forEach(input -> {
                //源代码编译之后的class目录
                input.getDirectoryInputs().forEach(directoryInput -> {
                    File srcDir = directoryInput.getFile();
                    File destDir = outputProvider.getContentLocation(
                            directoryInput.getName(), directoryInput.getContentTypes(),
                            directoryInput.getScopes(), Format.DIRECTORY
                    );
                    if (isIncremental) {
                        directoryInput.getChangedFiles().forEach((srcFile, status) -> {
                            File destFile = new File(srcFile.getAbsolutePath().replace(srcDir.getAbsolutePath(), destDir.getAbsolutePath()));
                            tasks.add((Callable) () -> {
                                try {
                                    switch (status) {
                                        case ADDED: {
                                            onFileAdded(srcDir, destDir, srcFile, destFile);
                                            break;
                                        }
                                        case CHANGED: {
                                            onFileChanged(srcDir, destDir, srcFile, destFile);
                                            break;
                                        }
                                        case REMOVED: {
                                            onFileRemoved(srcDir, destDir, srcFile, destFile);
                                            break;
                                        }
                                        default: {
                                            onFileNotChanged(srcDir, destDir, srcFile, destFile);
                                            break;
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return null;
                            });
                        });
                    } else {
                        tasks.add((Callable) () -> {
                            try {
                                onFull(DIR, srcDir, destDir);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        });
                    }

                });
                //jar包，jar包或者aar包
                input.getJarInputs().forEach(jarInput -> {
                    File srcJar = jarInput.getFile();
                    String destName = srcJar.getName();
                    /* 重名名输出文件,因为可能同名,会覆盖*/
                    String hexName = DigestUtils.md5Hex(srcJar.getAbsolutePath()).substring(0, 8);
                    if (destName.endsWith(".jar")) {
                        destName = destName.substring(0, destName.length() - 4);
                    }
                    File destJar = outputProvider.getContentLocation(
                            destName + "_" + hexName, jarInput.getContentTypes(),
                            jarInput.getScopes(), Format.JAR
                    );
                    if (isIncremental) {
                        tasks.add((Callable) () -> {
                            try {
                                switch (jarInput.getStatus()) {
                                    case ADDED: {
                                        onJarAdded(srcJar, destJar, Jarer.getClassListOnJar(srcJar));
                                        break;
                                    }
                                    case CHANGED: {
                                        JarDiffer differ = new JarDiffer(srcJar, destJar).computeDiff();
                                        onJarChanged(srcJar, destJar, differ.getAddedList(), differ.getRemovedList());
                                        break;
                                    }
                                    case REMOVED: {
                                        onJarRemoved(srcJar, destJar, Jarer.getClassListOnJar(srcJar));
                                        break;
                                    }
                                    default: {
                                        onJarNotChanged(srcJar, destJar, Jarer.getClassListOnJar(srcJar));
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        });
                    } else {
                        tasks.add((Callable) () -> {
                            try {
                                onFull(JAR, srcJar, destJar);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        });
                    }
                });
            });
            executor.invokeAll(tasks);
            onTransformEnd();
            long cost = System.currentTimeMillis() - start;
            P.info("Transform cost : " + cost + " ms  isIncremental:" + isIncremental);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            executor.shutdownNow();
        }
    }


}
