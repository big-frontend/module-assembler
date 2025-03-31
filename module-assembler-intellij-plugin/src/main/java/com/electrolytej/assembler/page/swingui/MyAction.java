package com.electrolytej.assembler.page.swingui;

import com.electrolytej.assembler.ModuleParser;
import com.electrolytej.assembler.model.BuildVariant;
import com.electrolytej.assembler.model.ModuleConfig;
import com.electrolytej.assembler.util.NotificationUtil;
import com.electrolytej.assembler.util.StringUtil;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import com.electrolytej.assembler.model.Module;
import com.intellij.openapi.vfs.VirtualFile;

import static com.electrolytej.assembler.util.NotificationUtil.showErrorNotification;

public class MyAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        if (project == null || project.getBaseDir() == null) return;
        VirtualFile localPropertiesFile = project.getBaseDir().findFileByRelativePath("./local.properties");
        if (localPropertiesFile == null || StringUtil.isEmpty(localPropertiesFile.getCanonicalPath())) {
            showErrorNotification("localid", "不存在local.properties文件");
            return;
        }

        VirtualFile moduleConfigFile = project.getBaseDir().findFileByRelativePath("./module_config.json");
        if (moduleConfigFile == null || StringUtil.isEmpty(moduleConfigFile.getCanonicalPath())) {
            showErrorNotification("moduleconfig_id", "不存在module_config.json文件");
            return;
        }
        ModuleConfig config = null;
        try (JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(moduleConfigFile.getCanonicalPath())))) {
            config = new Gson().fromJson(reader, ModuleConfig.class);
        } catch (IOException e) {
            return;
        }
        if (config == null) {
            showErrorNotification("moduleconfig_id", "不存在config文件");
            return;
        }
        ModuleParser parser = new ModuleParser();
        try {
            parser.parser(localPropertiesFile.getCanonicalPath(), config);
        } catch (IOException e) {
            return;
        }

        List<Module> modules = parser.getAllModules();
        if (modules.isEmpty()) return;
        List<BuildVariant> buildVariants = parser.getBuildVariants();
        if (buildVariants == null || buildVariants.isEmpty()) return;

        Dashboard d2 = new Dashboard();
        d2.setOKListener(result -> {
            System.out.println("result:" + result);
            if ("all".equals(result.activeBuildVariant) && !result.binaryModules.isEmpty()) {
                NotificationUtil.showErrorNotification("notsupport", "all 模式下，不支持组件化,请将所有binary模块转换成source 或者 exclude");
                return false;
            }
            parser.storeLocalProperties(result.excludeModules,result.sourceModules,result.activeBuildVariant);
            AnAction syncProjectAction = event.getActionManager().getAction("Android.SyncProject");
            if (syncProjectAction != null) {
                syncProjectAction.actionPerformed(event);
            }
            return true;
        });
        d2.setCancelListener(() -> d2.dispose());
        if (!d2.isShowing()) {
            //modules
            d2.bindPanel(modules);
            d2.bindBuildVariants(parser.getActiveBuildVariant(),buildVariants.stream().map(buildVariant -> buildVariant.name).toList());
            d2.pack();
            d2.setVisible(true);
        }
    }

}
