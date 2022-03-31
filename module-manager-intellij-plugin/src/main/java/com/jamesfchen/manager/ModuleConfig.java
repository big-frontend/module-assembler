package com.jamesfchen.manager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModuleConfig {
//    String author;
//    String description;
    @NotNull
    List<String> buildVariants;
    @Nullable
    List<String> buildArtifacts;
    @NotNull
    List<Module> allModules;
}
