package com.jamesfchen.manager;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;

import static com.jamesfchen.manager.NotificationUtil.showErrorNotification;

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
            showErrorNotification("bindBuildVariants", "请配置buildVariants");
            return;
        }
        String activeServerApiEnv = getActiveServerApiEnv(localProperties,config.serverApiEnvs);
        if (activeServerApiEnv ==null){
            showErrorNotification("serverApiEnvs", "请配置serverApiEnvs");
            return;
        }
        String excludeModulesStr = localProperties.getProperty("excludeModules");
        String sourceModulesStr = localProperties.getProperty("sourceModules");
        Map<String, Module> allModuleMap = new TreeMap<>();
        Map<String, Module> excludeModuleMap = new TreeMap<String, Module>();
        Map<String, Module> sourceModuleMap = new TreeMap<String, Module>();
        Map<String, Module> binaryModuleMap = new TreeMap<String, Module>();
        for (Module m : config.allModules) {
            allModuleMap.put(m.simpleName, m);
        }
        //第一次初始化项目时，local.properties文件没有excludeModules、sourceModules、apps这三个，默认所有模块都为binary
        if (excludeModulesStr == null || sourceModulesStr == null) {
            StringBuilder sourcesb = new StringBuilder();
            StringBuilder excludesb = new StringBuilder();

            for (Map.Entry<String, Module> entry : allModuleMap.entrySet()) {
                String simpleName = entry.getKey();
                Module m = entry.getValue();
                sourceModuleMap.put(simpleName, entry.getValue());
                if ("plugin".equals(m.format)){
                    excludesb.append(m.simpleName);
                    excludesb.append(",");
                }else {
                    sourcesb.append(m.simpleName);
                    sourcesb.append(",");
                }
            }
            sourceModulesStr = sourcesb.toString();
            localProperties.setProperty("sourceModules", sourceModulesStr);
            excludeModulesStr = excludesb.toString();
            localProperties.setProperty("excludeModules", excludeModulesStr);

            FileUtil.storeLocalProperties(localProperties);
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
        System.out.println(" activeServerApiEnv:" + activeServerApiEnv + " serverApiEnvs:" + config.serverApiEnvs);
        Dashboard d2 = new Dashboard();
        d2.setOKListener(new Dashboard.OkListener() {
            @Override
            public boolean call(Result result) {
                System.out.println("result:"+result);
                if ("all".equals(result.activeBuildVariant)  && result.binaryModules.length() >=1 ){
                    NotificationUtil.showErrorNotification("notsupport", "all 模式下，不支持组件化,请将所有binary模块转换成source 或者 exclude");
                    return false;
                }
                localProperties.setProperty("excludeModules",result.excludeModules);
                localProperties.setProperty("sourceModules", result.sourceModules);
                localProperties.setProperty("activeBuildVariant", result.activeBuildVariant);
                localProperties.setProperty("activeServerApiEnv", result.activeServerApiEnv);
                FileUtil.storeLocalProperties(localProperties);
                AnAction syncProjectAction = e.getActionManager().getAction("Android.SyncProject");
                if (syncProjectAction != null) {
                    syncProjectAction.actionPerformed(e);
                }
                return true;
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
            d2.bindServerApiEnvs(activeServerApiEnv, config.serverApiEnvs);
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
    String getActiveServerApiEnv(Properties localProperties, List<String> serverApiEnvs) {
        String activeServerApiEnv = localProperties.getProperty("activeServerApiEnv");
        if (serverApiEnvs == null || serverApiEnvs.isEmpty()) {
            return null;
        }
        //第一次初始化项目时,local.properties文件没有activeServerApiEnv
        if (activeServerApiEnv == null || activeServerApiEnv.isEmpty()) {
            activeServerApiEnv = serverApiEnvs.get(0);
            localProperties.setProperty("activeServerApiEnv", activeServerApiEnv);
            FileUtil.storeLocalProperties(localProperties);
        }
        return activeServerApiEnv;
    }
}
