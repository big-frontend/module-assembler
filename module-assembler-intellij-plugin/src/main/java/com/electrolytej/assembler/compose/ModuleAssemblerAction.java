package com.electrolytej.assembler.compose;

import com.electrolytej.assembler.BuildVariant;
import com.electrolytej.assembler.FileUtil;
import com.electrolytej.assembler.Module;
import com.electrolytej.assembler.NotificationUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

import java.util.List;
import java.util.Map;

public class ModuleAssemblerAction extends AnAction {
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

        ViewModel vm = new ViewModel();

        System.out.println(" ViewModel:" + vm);

        SampleDialogWrapper d2 = new SampleDialogWrapper(vm);
        d2.setOKListener(result -> {
            System.out.println("result:" + result);
            if ("all".equals(result.activeBuildVariant) && !result.binaryModules.isEmpty()) {
                NotificationUtil.showErrorNotification("notsupport", "all 模式下，不支持组件化,请将所有binary模块转换成source 或者 exclude");
                return false;
            }
            FileUtil.storeLocalProperties(result.excludeModules, result.sourceModules, result.activeBuildVariant);
            AnAction syncProjectAction = e.getActionManager().getAction("Android.SyncProject");
            if (syncProjectAction != null) {
                syncProjectAction.actionPerformed(e);
            }
            return true;
        });
        d2.setCancelListener(d2::disposeIfNeeded);
        if (!d2.isShowing()) {
//            d2.bindBuildVariants(activeBuildVariant, config.buildVariants);
            d2.showAndGet();
//            d2.setVisible(true);
        }
    }
}