package com.jamesfchen.b

import com.qihoo360.replugin.util.VariantCompat
import org.gradle.api.GradleException
import org.gradle.api.Project

import java.nio.channels.FileChannel

class AppModulePlugin extends AndroidPlugin {
    @Override
    String mainPlugin() {
        return 'com.android.application'
    }

    @Override
    void addPlugins(Project project) {
        super.addPlugins(project)
        project.plugins.apply(routerPlugin)
        if (lifecycleVersion) {
            project.plugins.apply('io.github.jamesfchen.lifecycle-plugin')
        }
        if (navigationVersion) {
            project.plugins.apply('androidx.navigation.safeargs')
//        project.plugins.apply('androidx.navigation.safeargs.kotlin')
        }
    }

    @Override
    void onApply(Project project) {
        super.onApply(project)
        project.android {
            defaultConfig {
//        multiDexEnabled = true//support android 20 or lower
                applicationId project.rootProject.applicationId
            }
            signingConfigs {
                debugSigningConfig {
                    (keyAlias, keyPassword, storePassword) = [project.rootProject.keyAlias, project.rootProject.keyPassword, project.rootProject.storePassword]
                    storeFile project.file("$project.rootDir/$project.rootProject.storeFilePath")
                    v1SigningEnabled true
                    v2SigningEnabled true

                }
                releaseSigningConfig {
                    (keyAlias, keyPassword, storePassword) = [project.rootProject.keyAlias, project.rootProject.keyPassword, project.rootProject.storePassword]
                    storeFile project.file("$project.rootDir/$project.rootProject.storeFilePath")
                    v1SigningEnabled true
                    v2SigningEnabled true
                }

            }
            buildTypes {
                release {
                    signingConfig signingConfigs.releaseSigningConfig
                }
                debug {
                    signingConfig signingConfigs.debugSigningConfig
                }
            }
        }
        project.configurations.create("dynamicImplementation")
        project.android.applicationVariants.all { variant ->
            def mergeVariantAssets = project.tasks.getByName("merge${variant.name.capitalize()}Assets")
            mergeVariantAssets.doLast {
                //copy plugin to assets目录
                for (def d : project.configurations.dynamicImplementation.dependencies) {
                    println("dynamicImplementation:" + d.name)
                    def m = project.gradle.pluginBinaryModuleMap[d.name]
                    if (m) {
                        def c = project.configurations.getByName("dynamicImplementation")
                        println(d.name + " " + c.asPath + " " + c.singleFile.name)
                        def pluginsDir = new File(project.buildDir, "plugins")
                        if (!pluginsDir.exists()) {
                            pluginsDir.mkdirs()
                            File oneDir = new File(new File(project.buildDir, "plugins"), c.singleFile.name)
                            if (!oneDir.exists()) {
                                oneDir.mkdirs()
                            }
                            P.info("${oneDir.name} 下载....1\n")
                            updatePlugin(variant, c.asPath, oneDir)
                        } else {
                            File oneDir = new File(new File(project.buildDir, "plugins"), c.singleFile.name)
                            if (!oneDir.exists()) {
                                oneDir.mkdirs()
                                File[] allDir = pluginsDir.listFiles()
                                def simpleName = c.singleFile.name.split("\\d.\\d.\\d")[0].trim()
                                for (def dir : allDir) {
                                    P.info(simpleName + " " + dir.name)
                                    if (dir.name.contains(simpleName)) {
                                        P.info("删除 ${dir.name}\n")
                                        dir.deleteDir()
                                        break
                                    }
                                }
                                P.info("${oneDir.name} 下载....2\n")
                                updatePlugin(variant, c.asPath, oneDir)
                            }
                        }
                    }
                }
            }
        }
    }

    private void updatePlugin(def variant, String zipPath, File destDir) {
        def ant = new AntBuilder()
        ant.unzip(src: zipPath, dest: destDir, overwrite: "true")
        def buildType = variant.buildType.name
        def flavor = variant.getFlavorName()
        def o = VariantCompat.getMergeAssetsTask(variant).outputDir?.get()?.getAsFile()
        if (o == null) {
            throw new GradleException("有点问题:${VariantCompat.getMergeAssetsTask(variant).outputDir}")
        }
        def pluginsDirOnAssets = new File(o, 'plugins')
        File apkFile = null
        String fileName = null
        for (def f : destDir.listFiles()) {
            def split = f.name.split('\\.')
            def suffix = split[1]
            if (suffix == 'apk') {
                apkFile = f
                fileName = split[0]
                break
            }
        }
        if (apkFile == null) {
            throw new GradleException("$destDir 没有找到apk")
        }
        copyFileNIO(apkFile, new File(pluginsDirOnAssets, fileName + ".jar"))
    }


    public boolean copyFileNIO(File srcFile, File destFile) {
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            inChannel = new FileInputStream(srcFile).getChannel();
            outChannel = new FileOutputStream(destFile).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (inChannel != null) {
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outChannel != null) {
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}