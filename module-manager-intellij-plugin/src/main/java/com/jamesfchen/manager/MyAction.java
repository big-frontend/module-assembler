package com.jamesfchen.manager;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.jamesfchen.manager.NotificationUtil.showNotification;

public class MyAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;
        FileUtil.init(project);
        ModuleConfig config = FileUtil.parseModuleConfig();
        if (config == null) {
            return;
        }
        Properties localProperties = FileUtil.getLocalProperties();
        if (localProperties == null) return;
        String activeBuildVariant = getActiveBuildVariant(localProperties,config.buildVariants);
        if (activeBuildVariant ==null){
            showNotification("bindBuildVariants", "请配置buildVariants");
            return;
        }
        String activeBuildArtifact = getActiveBuildArtifact(localProperties,config.buildArtifacts);
        String excludeModulesStr = localProperties.getProperty("excludeModules");
        String sourceModulesStr = localProperties.getProperty("sourceModules");
        Map<String, Module> allModuleMap = new HashMap<String, Module>();
        Map<String, Module> excludeModuleMap = new HashMap<String, Module>();
        Map<String, Module> sourceModuleMap = new HashMap<String, Module>();
        Map<String, Module> binaryModuleMap = new HashMap<String, Module>();
        for (Module m : config.allModules) {
            allModuleMap.put(m.simpleName, m);
        }
        //第一次初始化项目时，local.properties文件没有excludeModules、sourceModules、apps这三个，默认所有模块都为binary
        if (excludeModulesStr == null || sourceModulesStr == null) {
            localProperties.setProperty("excludeModules", "");
            StringBuffer sb = new StringBuffer();
            for (Map.Entry<String, Module> entry : allModuleMap.entrySet()) {
                String key = entry.getKey();
                sb.append(key);
                sb.append(",");
                sourceModuleMap.put(key, entry.getValue());
            }
            localProperties.setProperty("sourceModules", sb.toString());
            FileUtil.storeLocalProperties(localProperties);
            excludeModulesStr = "";
            sourceModulesStr = sb.toString();
            System.out.println(" run once ");
        } else {
            for (String s : excludeModulesStr.split(",")) {
                for (Map.Entry<String, Module> entry : allModuleMap.entrySet()) {
                    if (s.equals(entry.getKey())) {
                        excludeModuleMap.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            for (String s : sourceModulesStr.split(",")) {
                for (Map.Entry<String, Module> entry : allModuleMap.entrySet()) {
                    if (s.equals(entry.getKey())) {
                        sourceModuleMap.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            for (Map.Entry<String, Module> entry : allModuleMap.entrySet()) {
                if (!sourceModuleMap.containsKey(entry.getKey()) && !excludeModuleMap.containsKey(entry.getKey())) {
                    binaryModuleMap.put(entry.getKey(), entry.getValue());
                }
            }
            System.out.println(" run again " + excludeModulesStr + "   " + sourceModulesStr);
        }

        System.out.println(" sourceModuleMap:" + sourceModuleMap);
        System.out.println(" excludeModuleMap:" + excludeModuleMap);
        System.out.println(" binaryModuleMap:" + binaryModuleMap);
        System.out.println(" activeBuildVariant:" + activeBuildVariant + " buildVariants:" + config.buildVariants);
        System.out.println(" activeBuildArtifact:" + activeBuildArtifact + " buildArtifacts:" + config.buildArtifacts);
        Dashboard d2 = new Dashboard();
        d2.setOKListener(new Dashboard.OkListener() {
            @Override
            public void call(Result result) {
                localProperties.setProperty("excludeModules",result.excludeModules);
                localProperties.setProperty("sourceModules", result.sourceModules);
                localProperties.setProperty("activeBuildVariant", result.activeBuildVariant);
                if (result.activeBuildArtifact !=null){
                    localProperties.setProperty("activeBuildArtifact", result.activeBuildArtifact);
                }
                FileUtil.storeLocalProperties(localProperties);
                AnAction syncProjectAction = e.getActionManager().getAction("Android.SyncProject");
                if (syncProjectAction != null) {
                    syncProjectAction.actionPerformed(e);
                }
            }
        });
        d2.setCancelListener(new Dashboard.CancelListener() {
            @Override
            public void call() {
                d2.dispose();
            }
        });
        if (!d2.isShowing()) {
            d2.bindSourcePanel(sourceModuleMap);
            d2.bindExcludePanel(excludeModuleMap);
            d2.bindBinaryPanel(binaryModuleMap);
            d2.bindBuildVariants(activeBuildVariant, config.buildVariants);
            if (activeBuildArtifact != null) {
                d2.bindBuildArtifacts(activeBuildArtifact, config.buildArtifacts);
            }
            d2.pack();
            d2.setVisible(true);
        }
    }

    @Nullable
    String getActiveBuildVariant(Properties localProperties, List<String> buildVariants) {
        String activeBuildVariant = localProperties.getProperty("activeBuildVariant");
        if (buildVariants == null || buildVariants.isEmpty()) {
            return null;
        }
        //第一次初始化项目时,local.properties文件没有activeBuildVariant
        if (activeBuildVariant == null || activeBuildVariant.isEmpty()) {
            activeBuildVariant = buildVariants.get(0);
            localProperties.setProperty("activeBuildVariant", activeBuildVariant);
            FileUtil.storeLocalProperties(localProperties);
        }
        return activeBuildVariant;
    }
    @Nullable
    String getActiveBuildArtifact(Properties localProperties,List<String> buildArtifacts) {
        String activeBuildArtifact = localProperties.getProperty("activeBuildArtifact");
        if (buildArtifacts == null || buildArtifacts.isEmpty()) {
            return null;
        }
        //第一次初始化项目时,local.properties文件没有activeBuildArtifact
        if (activeBuildArtifact == null || activeBuildArtifact.isEmpty()) {
            activeBuildArtifact = buildArtifacts.get(0);
            localProperties.setProperty("activeBuildArtifact", activeBuildArtifact);
            FileUtil.storeLocalProperties(localProperties);
        }
        return activeBuildArtifact;
    }


}
