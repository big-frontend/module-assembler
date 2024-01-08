package com.electrolytej.manager

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
class ModuleManagerAction : AnAction() {
    override fun actionPerformed(p0: AnActionEvent) {
        SampleDialogWrapper().showAndGet()
    }
}