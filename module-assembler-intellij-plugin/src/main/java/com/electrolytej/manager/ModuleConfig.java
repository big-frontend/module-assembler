package com.electrolytej.manager;

import org.jetbrains.annotations.NotNull;
import java.util.List;

public class ModuleConfig {
//    String author;
//    String description;
    @NotNull
    public List<String> buildVariants;
    @NotNull
    public List<Module> allModules;
}
