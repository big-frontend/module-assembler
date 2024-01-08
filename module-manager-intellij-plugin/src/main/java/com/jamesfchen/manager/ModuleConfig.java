package com.jamesfchen.manager;

import org.jetbrains.annotations.NotNull;
import java.util.List;

public class ModuleConfig {
//    String author;
//    String description;
    @NotNull
    List<String> buildVariants;
    @NotNull
    List<Module> allModules;
}
