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
    String binaryModules;
    @NotNull
    String activeBuildVariant;
    int fwkSelected = -1;
    int sbSelected = -1;
    int dbSelected = -1;

    @Override
    public String toString() {
        return "Result{" +
                "excludeModules='" + excludeModules + '\'' +
                ", sourceModules='" + sourceModules + '\'' +
                ", binaryModules='" + binaryModules + '\'' +
                ", activeBuildVariant='" + activeBuildVariant + '\'' +
                ", fwkSelected=" + fwkSelected +
                ", sbSelected=" + sbSelected +
                ", dbSelected=" + dbSelected +
                '}';
    }
}
