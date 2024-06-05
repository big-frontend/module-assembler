package com.jamesfchen.manager;

import com.electrolytej.manager.Module;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.JBColor;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.lang.String;
public class SampleDialogWrapper extends DialogWrapper {
    Dashboard dashboard = new Dashboard();

    protected SampleDialogWrapper() {
        super(true);
        setTitle("Test DialogWrapper");
        init();
        setSize(1000, 697);
        setModal(true);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new GridLayout());

        JLabel label = new JLabel("Testing");
        label.setPreferredSize(new Dimension(100, 100));
//        dialogPanel.add(label);
        JLabel label2 = new JLabel("Testing");
        label2.setPreferredSize(new Dimension(100, 100));
//        dialogPanel.add(label2);
//        dialogPanel.add(dashboard, BorderLayout.CENTER);
        MyForm myForm = new MyForm();
//        return myForm;
        return dialogPanel;
    }

    public void setOKListener(Dashboard.OkListener l) {
        dashboard.setOKListener(l);
    }

    public void setCancelListener(Dashboard.CancelListener l) {
        dashboard.setCancelListener(l);
    }

    public void bindExcludePanel(Map<String, Module> moduleMap) {
        dashboard.bindExcludePanel(moduleMap);
    }

    public void bindSourcePanel(Map<String, Module> moduleMap) {
        dashboard.bindSourcePanel(moduleMap);
    }

    public void bindBinaryPanel(Map<String, Module> moduleMap) {
        dashboard.bindBinaryPanel(moduleMap);
    }

    public void bindBuildVariants(String activeBuildVariant, List<String> variants) {
        dashboard.bindBuildVariants(activeBuildVariant, variants);

    }
}
