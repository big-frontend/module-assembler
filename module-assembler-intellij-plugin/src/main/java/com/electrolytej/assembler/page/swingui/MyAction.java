package com.electrolytej.assembler.page.swingui;

import com.electrolytej.assembler.model.BuildVariant;
import com.electrolytej.assembler.util.FileUtil;
import com.electrolytej.assembler.util.NotificationUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import java.util.*;
import com.electrolytej.assembler.model.Module;

public class MyAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;
        boolean ret = FileUtil.init(project);
        if (!ret) return;

        Map<String, Module> modules = FileUtil.getModules();
        if (modules.isEmpty()) return;
        List<BuildVariant> buildVariants = FileUtil.getBuildVariants();
        if (buildVariants == null || buildVariants.isEmpty()) return;

        Dashboard d2 = new Dashboard();
        d2.setOKListener(result -> {
            System.out.println("result:" + result);
            if ("all".equals(result.activeBuildVariant) && !result.binaryModules.isEmpty()) {
                NotificationUtil.showErrorNotification("notsupport", "all 模式下，不支持组件化,请将所有binary模块转换成source 或者 exclude");
                return false;
            }
            FileUtil.storeLocalProperties(result.excludeModules,result.sourceModules,result.activeBuildVariant);
            AnAction syncProjectAction = e.getActionManager().getAction("Android.SyncProject");
            if (syncProjectAction != null) {
                syncProjectAction.actionPerformed(e);
            }
            return true;
        });
        d2.setCancelListener(() -> d2.dispose());
        if (!d2.isShowing()) {
            //modules
            d2.bindPanel(modules);
            d2.bindBuildVariants(FileUtil.getActiveBuildVariant(),buildVariants.stream().map(buildVariant -> buildVariant.name).toList());
            d2.pack();
            d2.setVisible(true);
        }
    }

}
