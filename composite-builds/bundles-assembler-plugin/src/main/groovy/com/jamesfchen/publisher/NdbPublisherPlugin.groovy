package com.jamesfchen.publisher

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class NdbPublisherPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        if (!Checker.isApk(project)) {
            throw new GradleException("必须apply plugin在application工程中")
        }
        project.extensions.create('publish', PublishExtension)

        project.afterEvaluate {
            def ext = project['publish'] as PublishExtension
            project.group = ext.groupId
            project.version = ext.version
            loadUserInfo(project, ext)
            //创建上传的任务 publishDebugApkPublicationToXRepository snapshot
            //创建上传的任务 publishReleaseApkPublicationToXRepository
            project.components.each { component ->
                if (ext.buildVariant == null) {
                    project.tasks.create(name: "publish${component.name}ApkPublicationToXRepository", group: 'publishing') {
                        doLast {
                            project.android.applicationVariants.all { variant ->
                                File apkFile = getApkFile(project, variant)
                                com.jamesfchen.b.Api.postFile("debug" in component.name && ext.version.endsWith('SNAPSHOT') ? ext.snapshotsRepoUrl : ext.releasesRepoUrl, apkFile)
                            }
                        }
                    }
                } else if (component.name.equalsIgnoreCase(ext.buildVariant)) {
                    project.tasks.create(name: "publish${component.name}ApkPublicationToXRepository", group: 'publishing') {
                        doLast {
                            File apkFile = getApkFile(project, variant)
                            com.jamesfchen.b.Api.postFile("debug" in component.name && ext.version.endsWith('SNAPSHOT') ? ext.snapshotsRepoUrl : ext.releasesRepoUrl, apkFile)
                        }
                    }
                    return
                }
            }
        }


    }

    public getApkFile(Project project, def variant) {
        File apkDir = new File(project.getBuildDir(), "outputs" + File.separator + "apk")
        def buildType = variant.buildType.name
        def apkName = ''
        variant.outputs.each {
            apkName = it.outputFileName
        }
        def flavor = variant.getFlavorName()
        return new File("${apkDir}/$flavor/$buildType", apkName)

    }

    private void loadUserInfo(Project project, PublishExtension ext) {
        Properties properties = new Properties()
        File localPropertiesFile = project.rootProject.file("local.properties");
        if (!localPropertiesFile.exists()) {
            throw new GradleException("local.properties文件不存在")
        }
        properties.load(localPropertiesFile.newDataInputStream())
        if (project.properties.containsKey("useJamesfChenSnapshots") && project["useJamesfChenSnapshots"] != null && project["useJamesfChenSnapshots"] == "true") {
            ext.releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            ext.snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
        } else {
            ext.releasesRepoUrl = properties.getProperty("releasesRepoUrl")
            ext.snapshotsRepoUrl = properties.getProperty("snapshotsRepoUrl")
        }
    }

}

