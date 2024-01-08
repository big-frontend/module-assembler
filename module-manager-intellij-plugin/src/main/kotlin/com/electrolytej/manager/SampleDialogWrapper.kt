package com.electrolytej.manager

import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.bind
import com.intellij.ui.dsl.builder.panel
import org.jetbrains.annotations.Nullable


class SampleDialogWrapper : DialogWrapper(true) {
    init {
        title = "Test DialogWrapper"
        init()
    }

    @Nullable
    override fun createCenterPanel(): DialogPanel {
        return panel {
            var value = true
            buttonsGroup("Panel.buttonsGroup:") {
                row {
                    radioButton("true", true)
                }
                row {
                    radioButton("false", false)
                }
            }.bind({ value }, { value = it })
        }
    }
}