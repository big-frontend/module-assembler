package com.electrolytej.assembler.util;

import com.electrolytej.assembler.model.ModuleConfig;
import com.electrolytej.assembler.model.Module;
public class ModuleConfigUtil {
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
