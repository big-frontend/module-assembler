package com.jamesfchen.publisher

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class NdbPublisherPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
//        if (Checker.isApk(project)) return

        project.extensions.create('publish', PublishExtension)

        project.afterEvaluate {
            def ext = project['publish'] as PublishExtension
            project.group = ext.groupId
            project.version = ext.version
            loadUserInfo(project, ext)
        }
    }

    private void loadUserInfo(Project project, PublishExtension ext) {
        Properties properties = new Properties()
        File localPropertiesFile = project.rootProject.file("local.properties");
        if (!localPropertiesFile.exists()) {
            throw new GradleException("local.properties文件不存在")
        }
        properties.load(localPropertiesFile.newDataInputStream())
        if (project.properties.containsKey("useJamesfChenSnapshots")
                && project["useJamesfChenSnapshots"] != null
                && project["useJamesfChenSnapshots"] == "true") {
//                println(">>> 使用jamesfchen的maven central发布，但是只能发布snapshot")
            ext.ossrhUsername = 'delta'
            ext.ossrhPassword = 'vCKe*5vHBh3xH2.'
            ext.releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            ext.snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
        } else {
            ext.ossrhUsername = properties.getProperty("ossrhUsername")
            ext.ossrhPassword = properties.getProperty("ossrhPassword")
            ext.releasesRepoUrl = properties.getProperty("releasesRepoUrl")
            ext.snapshotsRepoUrl = properties.getProperty("snapshotsRepoUrl")
        }
    }


}

