package com.jamesfchen.manager;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.sun.istack.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class SampleDialogWrapper extends DialogWrapper {
    public SampleDialogWrapper() {
        super(true); // use current window as parent
        setTitle("module manager");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.setPreferredSize(new Dimension(1000, 700));

        JPanel contentPanel = new JPanel();
        GridBagLayout gridBagLayout = new GridBagLayout();
        contentPanel.setLayout(gridBagLayout);
        GridBagConstraints bagConstraints = new GridBagConstraints();
        bagConstraints.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.setBorder(BorderFactory.createTitledBorder("9"));
        for (int i = 0; i < 10; ++i) {
            JLabel label = new JLabel("testing");
            label.setPreferredSize(new Dimension(1000 / 4, 50));
            contentPanel.add(label, bagConstraints);
        }

        dialogPanel.add(contentPanel, BorderLayout.CENTER);
        return dialogPanel;
    }
}