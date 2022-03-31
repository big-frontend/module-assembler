package com.jamesfchen.manager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Result {
    @NotNull
    String excludeModules;
    @NotNull
    String sourceModules;
    @NotNull
    String activeBuildVariant;
    @Nullable
    String activeBuildArtifact;
}
