package com.jamesfchen.manager;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import java.io.*;
import java.util.Properties;

import static com.jamesfchen.manager.NotificationUtil.showErrorNotification;

public class FileUtil {
    static Project project;
    static VirtualFile localPropertiesFile;
    static VirtualFile moduleConfigFile;

    public static void init(Project p) {
        project = p;
        localPropertiesFile = project.getBaseDir().findFileByRelativePath("./local.properties");
        moduleConfigFile = project.getBaseDir().findFileByRelativePath("./module_config.json");
    }

    public static Properties getLocalProperties() {
        if (localPropertiesFile == null) {
            showErrorNotification("localid", "不存在local.properties文件");
            return null;
        }
        Properties localProperties = new Properties();
        try {
            localProperties.load(localPropertiesFile.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            showErrorNotification("local_id2", "local.properties解析失败");
        }
        return localProperties;
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

    public static ModuleConfig parseModuleConfig() {
        try {
            if (moduleConfigFile == null) {
                showErrorNotification("moduleconfig_id", "不存在module_config.json文件");
                return null;
            }
            JsonReader reader = new JsonReader(new InputStreamReader(moduleConfigFile.getInputStream()));
            return new Gson().fromJson(reader, ModuleConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
            showErrorNotification("moduleconfig_id2", "module_config.json解析失败");
            return null;
        }
    }

}
