package com.jamesfchen;

import com.android.build.api.transform.QualifiedContent;
import com.android.build.gradle.AppExtension;
import com.android.build.gradle.internal.pipeline.TransformManager;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.util.Set;

public abstract class FastInsertCodePlugin extends AbsInsertCodeTransform implements Plugin<Project> {

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;//app project
    }

    @Override
    public void apply(Project project) {
        P.debug("project[" + project + "] apply " + this.getClass().getSimpleName());
        //如果使用plugins{}使用插件， registerTransform在agp7中需要在afterEvaluate中被注册
//        project.afterEvaluate(theProject -> {
        if (project.getPlugins().hasPlugin("com.android.application")) {
            AppExtension android = (AppExtension) project.getExtensions().getByType(AppExtension.class);
//            //groovy中不能使用匿名内部类，否则会报错
//            //A problem occurred configuring project ':app'.
//            //> java.lang.NullPointerException (no error message)
            android.registerTransform(this);
        }
//        project.getExtensions().findByType(BaseExtension.class)
//                .registerTransform(this);
//        });
    }

}