package com.electrolytej.assembler.util;

import com.electrolytej.assembler.model.BuildVariant;
import com.electrolytej.assembler.model.Module.Type;
import com.electrolytej.assembler.model.Module;
import com.electrolytej.assembler.model.ModuleConfig;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;

import static com.electrolytej.assembler.util.NotificationUtil.showErrorNotification;

public class FileUtil {
    static Project project;
    static VirtualFile localPropertiesFile;
    static VirtualFile moduleConfigFile;
    private static Properties localProperties;
    private static ModuleConfig config;

    public static boolean init(Project p) {
        project = p;
        localPropertiesFile = project.getBaseDir().findFileByRelativePath("./local.properties");
        moduleConfigFile = project.getBaseDir().findFileByRelativePath("./module_config.json");
        if (moduleConfigFile == null) {
            showErrorNotification("moduleconfig_id", "不存在module_config.json文件");
            return false;
        }
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(moduleConfigFile.getInputStream()));
            config = new Gson().fromJson(reader, ModuleConfig.class);
        } catch (IOException e) {
            showErrorNotification("moduleconfig_id2", "module_config.json解析失败");
            return false;
        }
        if (localPropertiesFile == null) {
            showErrorNotification("localid", "不存在local.properties文件");
            return false;
        }
        try {
            localProperties = new Properties();
            localProperties.load(localPropertiesFile.getInputStream());
            return true;
        } catch (IOException e) {
            showErrorNotification("local_id2", "local.properties解析失败");
            return false;
        }
    }

    public static void storeLocalProperties(String excludeModules, String sourceModules, String activeBuildVariant) {
        localProperties.setProperty("excludeModules", excludeModules);
        localProperties.setProperty("sourceModules", sourceModules);
        localProperties.setProperty("activeBuildVariant", activeBuildVariant);
        FileUtil.storeLocalProperties(localProperties);
    }

    public static void storeLocalProperties(Properties properties) {
        try {
            if (localPropertiesFile == null) {
                showErrorNotification("local_id3", "不存在local.properties文件");
                return;
            }
            OutputStream outputstream = new FileOutputStream(localPropertiesFile.getCanonicalPath());
            properties.store(outputstream, "update modules");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            showErrorNotification("local_id3", "不存在local.properties文件");
        } catch (IOException e) {
            e.printStackTrace();
            showErrorNotification("local_id4", "存入local.properties解析失败");
        }
    }

    public static Map<String, Module> getModules() {
        String excludeModulesStr = localProperties.getProperty("excludeModules");
        String sourceModulesStr = localProperties.getProperty("sourceModules");
        Map<String, Module> allModuleMap = new TreeMap<>();
        Map<String, Module> excludeModuleMap = new TreeMap<String, Module>();
        Map<String, Module> sourceModuleMap = new TreeMap<String, Module>();
        Map<String, Module> binaryModuleMap = new TreeMap<String, Module>();
        for (Module m : config.allModules) {
            if ("app".equals(m.simpleName) || "host".equals(m.group)) continue;
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
                if ("plugin".equals(m.format)) {
                    excludesb.append(m.simpleName);
                    excludesb.append(",");
                } else {
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
                        entry.getValue().type = Type.EXCLUDE;
                    }
                }
            }
            for (String s : sourceModulesStr.split(",")) {
                for (Map.Entry<String, Module> entry : allModuleMap.entrySet()) {
                    if (s.equals(entry.getKey())) {
                        sourceModuleMap.put(entry.getKey(), entry.getValue());
                        entry.getValue().type = Type.SOURCE;
                    }
                }
            }
            for (Map.Entry<String, Module> entry : allModuleMap.entrySet()) {
                if (!sourceModuleMap.containsKey(entry.getKey()) && !excludeModuleMap.containsKey(entry.getKey())) {
                    binaryModuleMap.put(entry.getKey(), entry.getValue());
                    entry.getValue().type = Type.BINARY;
                }
            }
            System.out.println(" run again " + excludeModulesStr + "   " + sourceModulesStr);
        }

        return allModuleMap;

    }

    @Nullable
    public static List<BuildVariant> getBuildVariants() {
        String activeBuildVariant = getActiveBuildVariant();
        if (activeBuildVariant == null) {
            showErrorNotification("bindBuildVariants", "请配置buildVariants");
            return null;
        }
        List<BuildVariant> buildVariants = new ArrayList<>();
        for (String variant : config.buildVariants) {
            BuildVariant buildVariant = new BuildVariant();
            buildVariant.name = variant;
            buildVariant.selected = activeBuildVariant.equals(variant);
            buildVariants.add(buildVariant);
        }
        return buildVariants;
    }

    public static String getActiveBuildVariant() {
        String activeBuildVariant = localProperties.getProperty("activeBuildVariant");
        if (config.buildVariants.isEmpty()) {
            return null;
        }
        //第一次初始化项目时,local.properties文件没有activeBuildVariant
        if (activeBuildVariant == null || activeBuildVariant.isEmpty()) {
            activeBuildVariant = config.buildVariants.get(0);
            localProperties.setProperty("activeBuildVariant", activeBuildVariant);
            FileUtil.storeLocalProperties(localProperties);
        }
        return activeBuildVariant;
    }

}
