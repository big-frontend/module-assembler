package com.electrolytej.assembler;

import com.electrolytej.assembler.model.BuildVariant;
import com.electrolytej.assembler.model.Module;
import com.electrolytej.assembler.model.ModuleConfig;
import com.electrolytej.assembler.util.StringUtil;

import java.io.*;
import java.util.*;

public class ModuleParser {
    private Properties mLocalProperties;
    private String mLocalPropertiesFilePath;
    private Module mAppModule;
    private ModuleConfig mModuleConfig;
    Map<String, Module> allModuleMap = new LinkedHashMap<>();
    Map<String, Module> excludeModuleMap = new LinkedHashMap<>();
    Map<String, Module> sourceModuleMap = new LinkedHashMap<>();
    Map<String, Module> binaryModuleMap = new LinkedHashMap<>();
    Map<String, String> sourcePath2SimpleNameMap = new LinkedHashMap<>();
    private final List<String> mDynamicModules = new ArrayList<>();

    public void parser(File localPropertiesFile, ModuleConfig config) throws IOException {
        parser(localPropertiesFile.getCanonicalPath(), config);
    }

    public void parser(String localPropertiesFilePath, ModuleConfig config) throws IOException {
        mLocalProperties = new Properties();
        mLocalPropertiesFilePath = localPropertiesFilePath;
        mLocalProperties.load(new FileInputStream(mLocalPropertiesFilePath));
        mModuleConfig = config;

        String excludeModulesStr = mLocalProperties.getProperty("excludeModules");
        String sourceModulesStr = mLocalProperties.getProperty("sourceModules");
        Iterator<Module> iterator = mModuleConfig.allModules.iterator();
        while (iterator.hasNext()) {
            Module module = iterator.next();
            if ("app".equals(module.simpleName) || "host".equals(module.group)) {
                mAppModule = module;
                iterator.remove();
                continue;
            }
            allModuleMap.put(module.simpleName, module);
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
            mLocalProperties.setProperty("sourceModules", sourceModulesStr);
            excludeModulesStr = excludesb.toString();
            mLocalProperties.setProperty("excludeModules", excludeModulesStr);

            storeLocalProperties(mLocalProperties);
            System.out.println(" run once ");
        } else {
            for (String s : excludeModulesStr.split(",")) {
                for (Map.Entry<String, Module> entry : allModuleMap.entrySet()) {
                    if (s.equals(entry.getKey())) {
                        excludeModuleMap.put(entry.getKey(), entry.getValue());
                        entry.getValue().type = Module.Type.EXCLUDE;
                    }
                }
            }
            for (String s : sourceModulesStr.split(",")) {
                for (Map.Entry<String, Module> entry : allModuleMap.entrySet()) {
                    if (s.equals(entry.getKey())) {
                        sourceModuleMap.put(entry.getKey(), entry.getValue());
                        entry.getValue().type = Module.Type.SOURCE;
                        Module module = entry.getValue();
                        sourcePath2SimpleNameMap.put(module.sourcePath, module.simpleName);
                        if ("ndbundle".equals(module.format)) {
                            mDynamicModules.add(module.sourcePath);
                        }
                    }
                }

            }
            for (Map.Entry<String, Module> entry : allModuleMap.entrySet()) {
                if (!sourceModuleMap.containsKey(entry.getKey()) && !excludeModuleMap.containsKey(entry.getKey())) {
                    binaryModuleMap.put(entry.getKey(), entry.getValue());
                    entry.getValue().type = Module.Type.BINARY;
                }
            }
            System.out.println(" run again " + excludeModulesStr + "   " + sourceModulesStr);
        }

        Iterator<String> dynamicModuleIterator = mDynamicModules.iterator();
        while (dynamicModuleIterator.hasNext()) {
            String next = dynamicModuleIterator.next();
            boolean hasExit = false;
            for (Map.Entry<String, Module> entry : allModuleMap.entrySet()) {
                if (next.equals(entry.getValue().sourcePath)) {
                    hasExit = true;
                    break;
                }
            }
            if (!hasExit) dynamicModuleIterator.remove();
        }

    }

    public void storeLocalProperties(String excludeModules, String sourceModules, String activeBuildVariant) {
        mLocalProperties.setProperty("excludeModules", excludeModules);
        mLocalProperties.setProperty("sourceModules", sourceModules);
        mLocalProperties.setProperty("activeBuildVariant", activeBuildVariant);
        storeLocalProperties(mLocalProperties);
    }

    public void storeLocalProperties(Properties properties) {
        if (StringUtil.isEmpty(mLocalPropertiesFilePath)) {
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(mLocalPropertiesFilePath)) {
            properties.store(fos, "update modules");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Module> getExcludeModuleMap() {
        return excludeModuleMap;
    }

    public Map<String, Module> getBinaryModuleMap() {
        return binaryModuleMap;
    }

    public Map<String, String> getSourcePath2SimpleNameMap() {
        return sourcePath2SimpleNameMap;
    }

    public Map<String, Module> getSourceModuleMap() {
        return sourceModuleMap;
    }

    public List<String> getDynamicModules() {
        return mDynamicModules;
    }

    public String getGroupId() {
        return mModuleConfig.groupId;
    }

    public Module getAppModule() {
        return mAppModule;
    }

    public List<Module> getAllModules() {
        return mModuleConfig.allModules;
    }

    public List<BuildVariant> getBuildVariants() {
        String activeBuildVariant = getActiveBuildVariant();
        if (activeBuildVariant == null) {
            return null;
        }
        List<BuildVariant> buildVariants = new ArrayList<>();
        for (String variant : mModuleConfig.buildVariants) {
            BuildVariant buildVariant = new BuildVariant();
            buildVariant.name = variant;
            buildVariant.selected = activeBuildVariant.equals(variant);
            buildVariants.add(buildVariant);
        }
        return buildVariants;
    }

    public String getActiveBuildVariant() {
        String activeBuildVariant = mLocalProperties.getProperty("activeBuildVariant");
        if (mModuleConfig.buildVariants.isEmpty()) {
            return null;
        }
        //第一次初始化项目时,local.properties文件没有activeBuildVariant
        if (activeBuildVariant == null || activeBuildVariant.isEmpty()) {
            activeBuildVariant = mModuleConfig.buildVariants.get(0);
            mLocalProperties.setProperty("activeBuildVariant", activeBuildVariant);
            storeLocalProperties(mLocalProperties);
        }
        return activeBuildVariant;
    }
}
