package com.electrolytej.manager

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.CollectionComboBoxModel
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.dialog
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.ui.dsl.gridLayout.VerticalAlign
import org.jetbrains.annotations.Nullable
import java.awt.*
import java.awt.event.ItemEvent
import javax.swing.*


class SampleDialogWrapper(val viewModel: ViewModel) : DialogWrapper(true) {
    private var okl: OkListener? = null
    private var cancell: CancelListener? = null

    init {
        title = "module manager"
        init()
        setSize(1000, 697)
        isModal = true
    }

    @Nullable
    override fun createCenterPanel(): DialogPanel {
        return panel {
            //            bagConstraints.fill = GridBagConstraints.BOTH
//            bagConstraints.insets = JBUI.insets(10)
            group("modules") {
                for (entry in viewModel.excludeModuleMap.entries) {
                    row(entry.key) {
                        comboBox(items(entry)).bindItem(viewModel::defaultGroupName1)
                    }
                }
                for (entry in viewModel.sourceModuleMap.entries) {
//                val fieldText = createFieldText(entry, JBColor.RED, "exclude")
                    row(entry.key) {
//                    bagConstraints.weightx = 1.0
//                    comboBox.isEditable = true
//                    comboBox.addItemListener { e: ItemEvent?
//                        if ("source" == comboBox.selectedItem) {
//                            jLabel.foreground = JBColor.GREEN
//                        } else if ("exclude" == comboBox.selectedItem) {
//                            jLabel.foreground = JBColor.RED
//                        } else if ("binary" == comboBox.selectedItem) {
//                            jLabel.foreground = JBColor.YELLOW
//                        }
//                    }
//                        createFieldText(entry, JBColor.RED, "exclude")
                        comboBox(items(entry)).bindItem(viewModel::defaultGroupName2)
                    }
                }
                for (entry in viewModel.binaryModuleMap.entries) {
                    row(entry.key) {
                        comboBox(items(entry)).bindItem(viewModel::defaultGroupName3)
                    }
                }
            }


            panel {
                row("Panel.panel row:") {
                    textField()
                }.rowComment("Panel.panel method creates sub-panel that occupies whole width and uses own grid inside")
            }

            rowsRange {
                row("Panel.rowsRange row:") {
                    textField()
                }.rowComment("Panel.rowsRange is similar to Panel.panel but uses the same grid as parent. " +
                        "Useful when grouped content should be managed together with RowsRange.enabledIf for example")
            }

            group("Panel.group") {
                row("Panel.group row:") {
                    textField()
                }.rowComment("Panel.group adds panel with independent grid, title and some vertical space before/after the group")
            }

            groupRowsRange("Panel.groupRowsRange") {
                row("Panel.groupRowsRange row:") {
                    textField()
                }.rowComment("Panel.groupRowsRange is similar to Panel.group but uses the same grid as parent. " +
                        "See how aligned Panel.rowsRange row")
            }

            collapsibleGroup("Panel.collapsible&Group") {
                row("Panel.collapsibleGroup row:") {
                    textField()
                }.rowComment("Panel.collapsibleGroup adds collapsible panel with independent grid, title and some vertical " +
                        "space before/after the group. The group title is focusable via the Tab key and supports mnemonics")
            }
            separator()
                .rowComment("Use separator() for horizontal separator")
            panel {
                row("PARENT_GRID is set, cell[0,0]:") {

                    label("Label 2 in parent grid, cell[2,0]")
                }.layout(RowLayout.PARENT_GRID)

                row("PARENT_GRID is set:") {
                    (1..60).forEach {
                        label("Label 1 in parent grid, cell[1,0]")
                    }
                }.layout(RowLayout.PARENT_GRID)

                row("Row label provided, LABEL_ALIGNED is used:") {
                    textField().text("textField1")
                    textField().text("textField2")
                }.layout(RowLayout.LABEL_ALIGNED)

                row {
                    label("Row label is not provided, INDEPENDENT is used:")
                }
            }
        }
    }

    fun items(entry: Map.Entry<String, Module>): List<String> {
        return if ("fwk".equals(entry.value.group, ignoreCase = true)
            || "home".equals(entry.value.group, ignoreCase = true)
            || "main".equals(entry.value.group, ignoreCase = true)
        ) {
            listOf("source", "binary")
        } else if ("plugin".equals(entry.value.format, ignoreCase = true)) {
            listOf("source", "exclude")
        } else {
            listOf("source", "binary", "exclude")
        }
    }

    interface onEach {
        fun call(moduleNameJLabel: JLabel?, moduleStateComboBox: ComboBox<String>?)
    }

//    fun foreachModules(o: onEach) {
//        val componentCount: Int = allModulePanel.getComponentCount()
//        for (i in 0 until componentCount) {
//            val fieldText = allModulePanel.getComponent(i) as JPanel
//            val moduleNameJLabel = fieldText.getComponent(0) as JLabel
//            val comboBox = fieldText.getComponent(1) as ComboBox<String>
//            o.call(moduleNameJLabel, comboBox)
//        }
//    }

//    fun bindExcludePanel(moduleMap: Map<String, Module>) {
//        excludeModuleMap = moduleMap
//
//        bindPanel(moduleMap, object : Callback {
//            override fun call(entry: Map.Entry<String, Module>): JComponent {
//                val fieldText: JPanel = createFieldText(entry, JBColor.RED, "exclude")
//                return fieldText
//            }
//        })
//    }
//
//    fun bindSourcePanel(moduleMap: Map<String, Module>) {
//        sourceModuleMap = moduleMap
//        bindPanel(moduleMap, object : Callback {
//            override fun call(entry: Map.Entry<String, Module>): JComponent {
//                return createFieldText(
//                    entry,
//                    JBColor.GREEN,
//                    "source"
//                )
//            }
//        })
//    }
//
//    fun bindBinaryPanel(moduleMap: Map<String, Module>) {
//        binaryModuleMap = moduleMap
//        bindPanel(moduleMap, object : Callback {
//            override fun call(entry: Map.Entry<String, Module>): JComponent {
//                return createFieldText(
//                    entry,
//                    JBColor.YELLOW,
//                    "binary"
//                )
//            }
//        })
//    }

//    fun bindBuildVariants(activeBuildVariant: String, variants: List<String>) {
//        this.activeBuildVariant = activeBuildVariant
//        val bagConstraints = GridBagConstraints()
//        bagConstraints.fill = GridBagConstraints.HORIZONTAL
//        //add jlabel
//        bagConstraints.weightx = 1.0
//        val jLabel = JLabel()
//        jLabel.text = activeBuildVariant
//        jLabel.horizontalAlignment = SwingConstants.CENTER
//        jLabel.font = Font("黑体", 1, 12)
//        jLabel.foreground = JBColor.GREEN
//        buildVariantsPanel.add(jLabel, bagConstraints)
//        val comboBoxModel: ComboBoxModel<String> = CollectionComboBoxModel(variants)
//        val comboBox = ComboBox(comboBoxModel)
//        bagConstraints.weightx = 1.0
//        comboBox.isEditable = true
//        comboBox.selectedItem = activeBuildVariant
//        comboBox.addItemListener { e: ItemEvent ->
//            jLabel.text = e.item.toString()
//            this.activeBuildVariant = e.item.toString()
//        }
//        buildVariantsPanel.add(comboBox, bagConstraints)
//    }

//    interface Callback {
//        fun call(entry: Map.Entry<String, Module>): JComponent
//    }

//    var i: Int = 0
//    var j: Int = 0
//
//    fun bindPanel(moduleMap: Map<String, Module>, cb: Callback) {
//        if (moduleMap.size == 0) return
//        val bagConstraints = GridBagConstraints()
//        bagConstraints.fill = GridBagConstraints.BOTH
//        bagConstraints.insets = JBUI.insets(10)
//        for (entry in moduleMap.entries) {
//            val fieldText = cb.call(entry)
//            bagConstraints.weightx = 1.0
//            bagConstraints.gridx = i % 4
//            bagConstraints.gridy = j / 4
//            allModulePanel.add(fieldText, bagConstraints)
//            i++
//            j++
//        }
//    }

    private fun createFieldText(entry: Map.Entry<String, Module>, color: Color, defaultGroupName: String): JPanel {
        val jPanel = JPanel(GridBagLayout())
        val bagConstraints = GridBagConstraints()
        bagConstraints.fill = GridBagConstraints.BOTH
        //add jlabel
        bagConstraints.weightx = 1.0
        val jLabel = JLabel()
        jLabel.text = entry.key
        jLabel.horizontalAlignment = SwingConstants.CENTER
        jLabel.font = Font("黑体", 1, 12)
        jLabel.foreground = color
        jPanel.add(jLabel, bagConstraints)
        val comboBoxModel: ComboBoxModel<String> =
            if ("fwk".equals(entry.value.group, ignoreCase = true) || "home".equals(
                    entry.value.group, ignoreCase = true
                ) || "main".equals(entry.value.group, ignoreCase = true)
            ) {
                CollectionComboBoxModel(mutableListOf("source", "binary"))
            } else if ("plugin".equals(entry.value.format, ignoreCase = true)) {
                CollectionComboBoxModel(mutableListOf("source", "exclude"))
            } else {
                CollectionComboBoxModel(mutableListOf("source", "binary", "exclude"))
            }
        val comboBox = ComboBox(comboBoxModel)
        comboBox.selectedItem = defaultGroupName
        bagConstraints.weightx = 1.0
        comboBox.isEditable = true
        comboBox.addItemListener { e: ItemEvent? ->
            if ("source" == comboBox.selectedItem) {
                jLabel.foreground = JBColor.GREEN
            } else if ("exclude" == comboBox.selectedItem) {
                jLabel.foreground = JBColor.RED
            } else if ("binary" == comboBox.selectedItem) {
                jLabel.foreground = JBColor.YELLOW
            }
        }
        jPanel.add(comboBox, bagConstraints)
        return jPanel
    }

    fun setOKListener(l: OkListener) {
        this.okl = l
    }

    fun setCancelListener(l: CancelListener?) {
        this.cancell = l
    }

    interface OkListener {
        fun call(result: Result?): Boolean
    }

    interface CancelListener {
        fun call()
    }
}

