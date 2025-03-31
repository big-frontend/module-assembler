package com.electrolytej.assembler.util;

import com.electrolytej.assembler.model.ModuleConfig;
import org.gradle.api.Project;
import org.gradle.api.UnknownProjectException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.electrolytej.assembler.model.Module;

public class ProjectUtil {

    public static String newVersion(Project project, Module m) {
        String branch = gitBranch(project);
//        String versionName = project.getVersion().toString();
        //        def ts = System.currentTimeMillis();
        String buildId = getCommitId(project);
        String activeBuildVariant = (String) project.getGradle().getExtensions().getExtraProperties().get("activeBuildVariant");
        var ver = branch + "-" + activeBuildVariant + "-" + buildId + "-SNAPSHOT";
        if (m.versionName != null && !m.versionName.isEmpty()) {
            if (StringUtil.isEmpty(m.versionCode)) {
                throw new IllegalArgumentException("参数错误 versionName:${m.versionName} versionCode:${m.versionCode}");
            }
            String[] a = m.versionName.split("-");
            if (a.length == 1) {
                ver = a[0] + "-" + activeBuildVariant;
            } else if (a.length == 2) {
                ver = a[0] + "-" + activeBuildVariant + "-SNAPSHOT";
            } else {
                throw new IllegalArgumentException("参数错误 versionName:${m.versionName} versionCode:${m.versionCode}");
            }
        }
        return ver;
    }


    public static String readText(Process proc) {
        try (BufferedReader reader = new BufferedReader(proc.inputReader())) {
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            return output.toString().trim();
        } catch (IOException e) {
            return null;
        }
    }

    public static String runCommand(String cmd, File workingDir) {
        Process proc = null;
        try {
            String[] parts = cmd.split("\\s");
            proc = new ProcessBuilder(parts).directory(workingDir).redirectOutput(ProcessBuilder.Redirect.PIPE).redirectError(ProcessBuilder.Redirect.PIPE).start();
            proc.waitFor(60, TimeUnit.MINUTES);
            return readText(proc);
        } catch (InterruptedException | IOException e) {
            return null;
        } finally {
            if (proc != null) {
                proc.destroy();
            }
        }
    }
//    public static String releaseTime() {
//        return new Date().format("yyMMddHHmm", TimeZone.getTimeZone("GMT+08:00"));
//    }

    public static String getUserName(Project project) {
        return runCommand("git config user.name", project.getRootDir());
    }

    public static String getCommitId(Project project) {
        return runCommand("git rev-parse --short HEAD", project.getRootDir());
    }

    public static String gitBranch(Project project) {
        return runCommand("git rev-parse --abbrev-ref HEAD", project.getRootDir());
    }

    public static boolean isSourcePath(String path) {
        return !path.contains(".");
    }

    public static void depsConfig(Project project, String configuration, String simpleName) {
        try {
            Object m = moduleify(project, simpleName);
            project.getDependencies().add(configuration, m);
        } catch (Exception e) {
        }
    }

    public static Object moduleify(Project project, String simpleName) {
        String path = findModulePath(project, simpleName);
        if (StringUtil.isEmpty(path)) return new UnknownProjectException("不支持" + simpleName);
        if (isSourcePath(path)) {
            return project.getRootProject().project(path);
        }
        return path;
    }

    public static String findModulePath(Project project, String simpleName) {
        Map<String, Module> binaryModuleMap = (Map<String, Module>) project.getGradle().getExtensions().getExtraProperties().get("binaryModuleMap");
        Map<String, Module> sourceModuleMap = (Map<String, Module>) project.getGradle().getExtensions().getExtraProperties().get("sourceModuleMap");
        if (binaryModuleMap.containsKey(simpleName)) {
            System.out.println("binaryModuleMap:" + binaryModuleMap.get(simpleName));
            Module module = binaryModuleMap.get(simpleName);
            if (module == null || StringUtil.isEmpty(module.binaryPath)) {
                throw new IllegalArgumentException("binary module 的binaryPath不能为空");
            }
            return module.binaryPath; //'com.jamesfchen:box-tool:1.0.0'
        } else if (sourceModuleMap.containsKey(simpleName)) {
            Module module = sourceModuleMap.get(simpleName);
            if (module == null || StringUtil.isEmpty(module.sourcePath)) {
                throw new IllegalArgumentException("source module 的sourcePath不能为空");
            }
            if ("ndbundle".equals(module.format)) {//动态库不能被app 模块引用
                return "";
            }
            return module.sourcePath; //':framework:common'
        }
//    //该模块为exclude，不会进行编译不需要依赖
        return null;
    }

    //fun Project.findModule(simpleName: String) {
//    if (project.gradle.ext.binaryModuleMap.containsKey(simpleName)) {
//        return project.gradle.ext.binaryModuleMap[simpleName]
//    } else if (project.gradle.ext.sourceModuleMap.containsKey(simpleName)) {
//        return project.gradle.ext.sourceModuleMap[simpleName]
//    }
//    return null
//}

    public static Module findModule(ModuleConfig moduleConfig, String name) {
        if (StringUtil.isEmpty(name)) return null;
        for (Module m : moduleConfig.allModules) {
            if (name.equals(m.simpleName)) {
                return m;
            }
        }
        return null;
    }
}
