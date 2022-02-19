package com.jamesfchen.manager;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;

import javax.swing.*;
import java.util.*;

import static com.jamesfchen.manager.FileIOUtil.*;
import static com.jamesfchen.manager.NotificationUtil.showNotification;

public class MyAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;
        Properties localProperties = getLocalProperties(project);
        if (localProperties == null) return;
        String excludeModulesStr = localProperties.getProperty("excludeModules");
        String sourceModulesStr = localProperties.getProperty("sourceModules");
        String activeBuildVariant = localProperties.getProperty("activeBuildVariant");
        Map<String, Module> allModuleMap = new HashMap<String, Module>();
        Map<String, Module> excludeModuleMap = new HashMap<String, Module>();
        Map<String, Module> sourceModuleMap = new HashMap<String, Module>();
        Map<String, Module> binaryModuleMap = new HashMap<String, Module>();

        ModuleConfig config = parseModuleConfig(project);
        if (config == null){
            return;
        }
        for (Module m : config.allModules) {
            allModuleMap.put(m.simpleName, m);
        }
        if ((activeBuildVariant == null || activeBuildVariant.isEmpty())
                &&(config.buildVariants == null || config.buildVariants.isEmpty()) ){
            showNotification("bindBuildVariants", "请配置buildVariants");
            return;
        }
//        第一次初始化项目时,local.properties文件没有activeBuildVariant
        if (activeBuildVariant == null || activeBuildVariant.isEmpty()){
            activeBuildVariant = config.buildVariants.get(0);
            localProperties.setProperty("activeBuildVariant", activeBuildVariant);
            storeLocalProperties(localProperties, project);
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
            storeLocalProperties(localProperties, project);
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
        System.out.println(" activeBuildVariant:" + activeBuildVariant);
        System.out.println(" buildVariants:" + config.buildVariants);
        Dashboard d2 = new Dashboard();
        d2.setOKListener(new Dashboard.OkListener() {
            @Override
            public void call(JPanel allModulePanel,String activeBuildVariant) {
                int componentCount = allModulePanel.getComponentCount();
                StringBuffer excludesb = new StringBuffer();
                StringBuffer sourcesb = new StringBuffer();
                for (int i =0;i<componentCount;++i){
                    JPanel fieldText = (JPanel) allModulePanel.getComponent(i);
                    JLabel moduleName = (JLabel) fieldText.getComponent(0);
                    ComboBox<String> comboBox = (ComboBox<String>) fieldText.getComponent(1);
                    String selectedItem = (String) comboBox.getSelectedItem();
                    if ("source".equals(selectedItem)){
                        sourcesb.append(moduleName.getText());
                        sourcesb.append(",");
                    }else if ("exclude".equals(selectedItem)){
                        excludesb.append(moduleName.getText());
                        excludesb.append(",");
                    }
                }

                localProperties.setProperty("excludeModules", excludesb.toString());
                localProperties.setProperty("sourceModules", sourcesb.toString());
                localProperties.setProperty("activeBuildVariant", activeBuildVariant);
                storeLocalProperties(localProperties, project);
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
            d2.bindBuildVariants(activeBuildVariant,config.buildVariants);
            d2.pack();
            d2.setVisible(true);

        }
    }


}
