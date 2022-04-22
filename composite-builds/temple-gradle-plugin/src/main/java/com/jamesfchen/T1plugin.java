package com.jamesfchen;

import com.android.build.api.transform.Transform;
import com.android.build.gradle.BaseExtension;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.kronos.plugin.base.BaseTransform;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: Apr/19/2022  Tue
 */
public class T1plugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getExtensions().findByType(BaseExtension.class)
                .registerTransform(new LTransform());
    }

    static class LTransform extends Transform {
        @Override
        public boolean isIncremental() {
            return true;
        }

        @Override
        public String getName() {
            return "IbcTransform";
        }

        @Override
        public java.util.Set<com.android.build.api.transform.QualifiedContent.ContentType> getInputTypes() {
            return TransformManager.CONTENT_CLASS;
        }

        @Override
        public java.util.Set<? super com.android.build.api.transform.QualifiedContent.Scope> getScopes() {
            return TransformManager.SCOPE_FULL_PROJECT;
        }

        @Override
        public void transform(com.android.build.api.transform.TransformInvocation invocation) {
            BaseTransform baseTransform = new BaseTransform(invocation, new com.kronos.plugin.base.TransformCallBack() {
                @Nullable
                @Override
                public byte[] process(@NotNull String className, @Nullable byte[] classBytes) {
                    return null;
                }
            }, false);
            baseTransform.setDeleteCallBack(new com.kronos.plugin.base.DeleteCallBack() {
                @Override
                public void delete(String className, byte[] bytes) {
                }
            });
            baseTransform.openSimpleScan();
            baseTransform.startTransform();
        }
    }
}