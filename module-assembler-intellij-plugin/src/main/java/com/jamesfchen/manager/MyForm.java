package com.jamesfchen.manager;

import com.electrolytej.manager.Module;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.lang.String;
public class MyForm extends DialogWrapper {
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JButton button1;
    private JTextField textField;

    public MyForm() {
        super(true);
        setTitle("MyForm");
        init();
        setSize(1000, 697);
        setModal(true);
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new GridLayout());
        return dialogPanel;
    }

    public void setOKListener(Dashboard.OkListener l) {
    }

    public void setCancelListener(Dashboard.CancelListener l) {
    }

    public void bindExcludePanel(Map<String, Module> moduleMap) {
    }

    public void bindSourcePanel(Map<String, Module> moduleMap) {
    }

    public void bindBinaryPanel(Map<String, Module> moduleMap) {
    }

    public void bindBuildVariants(String activeBuildVariant, List<String> variants) {

    }
}
