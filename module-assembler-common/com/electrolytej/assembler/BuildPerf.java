package com.electrolytej.assembler;

import com.electrolytej.assembler.util.P;

import org.gradle.BuildListener;
import org.gradle.BuildResult;
import org.gradle.api.Project;
import org.gradle.api.ProjectEvaluationListener;
import org.gradle.api.ProjectState;
import org.gradle.api.initialization.Settings;
import org.gradle.api.invocation.Gradle;

public class BuildPerf implements ProjectEvaluationListener, BuildListener {
    long start = System.currentTimeMillis();
    long projStart = 0;

    @Override
    public void beforeSettings(Settings settings) {

    }

    @Override
    public void settingsEvaluated(Settings settings) {
        P.error(">>>> evaluate setting脚本耗时:" + (System.currentTimeMillis() - start) + "ms");
        start = System.currentTimeMillis();
    }

    @Override
    public void projectsLoaded(Gradle gradle) {
        P.error(">>>> include完所有project 耗时:" + (System.currentTimeMillis() - start) + "ms");
        start = System.currentTimeMillis();
    }

    @Override
    public void projectsEvaluated(Gradle gradle) {
        P.error(">>>> evaluate完所有project脚本 耗时:" + (System.currentTimeMillis() - start) + "ms");
        start = System.currentTimeMillis();
    }

    @Override
    public void buildFinished(BuildResult buildResult) {
        P.error(">>>> gradle 结束 buildFinished");
    }

    @Override
    public void beforeEvaluate(Project project) {
//        if (!project.subprojects.isEmpty()) return
        
        projStart = System.currentTimeMillis();
    }

    @Override
    public void afterEvaluate(Project project, ProjectState state) {
//        if (!project.subprojects.isEmpty()) return
        P.error(">>>>evaluate ${project.getDisplayName()}项目 耗时:" + (System.currentTimeMillis() - projStart) + "ms");
    }
}
