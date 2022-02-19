package com.jamesfchen.manager;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.sun.istack.NotNull;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.jamesfchen.manager.NotificationUtil.showNotification;

public class FileIOUtil {
    public static Properties getLocalProperties(@NotNull Project project) {
        VirtualFile local = project.getBaseDir().findFileByRelativePath("./local.properties");
        if (local == null) {
            showNotification("localid", "不存在local.properties文件");
            return null;
        }
        Properties localProperties = new Properties();
        try {
            localProperties.load(local.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            showNotification("local_id2", "local.properties解析失败");
        }
        return localProperties;
    }

    public static void storeLocalProperties(Properties properties, Project project) {
        try {
            VirtualFile local = project.getBaseDir().findFileByRelativePath("./local.properties");
            if (local == null) {
                showNotification("local_id3", "不存在local.properties文件");
                return;
            }
            OutputStream outputstream = new FileOutputStream(local.getCanonicalPath());
            properties.store(outputstream, "update modules");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            showNotification("local_id3", "不存在local.properties文件");
        } catch (IOException e) {
            e.printStackTrace();
            showNotification("local_id4", "存入local.properties解析失败");
        }
    }

    public static ModuleConfig parseModuleConfig(@NotNull Project project) {
        try {
            VirtualFile moduleConfig = project.getBaseDir().findFileByRelativePath("./module_config.json");
            if (moduleConfig == null) {
                showNotification("moduleconfig_id", "不存在module_config.json文件");
            }
            JsonReader reader = new JsonReader(new InputStreamReader(moduleConfig.getInputStream()));
            return new Gson().fromJson(reader, ModuleConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
            showNotification("moduleconfig_id2", "module_config.json解析失败");
            return null;
        }
    }

}
