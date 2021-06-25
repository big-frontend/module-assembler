package com.jamesfchen.manager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;

public class Dashboard_bkp extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel exclude_module_panel;
    private JPanel binary_module_panel;
    private JPanel source_module_panel;

    public Dashboard_bkp(Map<String,Module> excludeModuleMap, Map<String,Module> sourceModuleMap, Map<String,Module> binaryModuleMap) {
        bindSourcePanel(sourceModuleMap);
        bindExcludePanel(excludeModuleMap);
        bindBinaryPanel(binaryModuleMap);
        setSize(new Dimension(1000,697));
        setVisible(true);
        setLocationRelativeTo(null);
        setContentPane(contentPane);
//        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        exclude_module_panel.setAutoscrolls(true);
        binary_module_panel.setAutoscrolls(true);
        source_module_panel.setAutoscrolls(true);
    }
    public Dashboard_bkp() {
        setSize(new Dimension(1000,697));
        setVisible(true);
        setLocationRelativeTo(null);


        setContentPane(contentPane);

//        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        exclude_module_panel.setAutoscrolls(true);
        binary_module_panel.setAutoscrolls(true);
        source_module_panel.setAutoscrolls(true);
    }

    public void bindExcludePanel(Map<String,Module> moduleMap){
        if (moduleMap ==null ||moduleMap.size() ==0) return;
        if (exclude_module_panel==null) return;
        for (Map.Entry<String,Module> entry: moduleMap.entrySet()){
            JLabel jLabel = new JLabel();
            jLabel.setText(entry.getKey());
            jLabel.setHorizontalAlignment(SwingConstants.CENTER);
            jLabel.setFont(new java.awt.Font("黑体",1,12));
            jLabel.setForeground(Color.darkGray);
            exclude_module_panel.add(jLabel);
        }
//        exclude_module_panel.revalidate();
//        exclude_module_panel.repaint();
        exclude_module_panel.updateUI();
    }

    public void bindSourcePanel(Map<String,Module> moduleMap){
        if (moduleMap ==null ||moduleMap.size() ==0) return;
        if (source_module_panel==null) return;
        for (Map.Entry<String,Module> entry: moduleMap.entrySet()){
            JLabel jLabel = new JLabel();
            jLabel.setText(entry.getKey());
            jLabel.setHorizontalAlignment(SwingConstants.CENTER);
            jLabel.setFont(new java.awt.Font("黑体",1,12));
            jLabel.setForeground(Color.ORANGE);
            source_module_panel.add(jLabel);
        }
//        source_module_panel.revalidate();
//        source_module_panel.repaint();
        source_module_panel.updateUI();
    }

    public void bindBinaryPanel(Map<String,Module> moduleMap){
        if (moduleMap ==null ||moduleMap.size() ==0) return;
        if (binary_module_panel==null) return;
        for (Map.Entry<String,Module> entry: moduleMap.entrySet()){
            JLabel jLabel = new JLabel();
            jLabel.setText(entry.getKey());
            jLabel.setHorizontalAlignment(SwingConstants.CENTER);
            jLabel.setFont(new java.awt.Font("黑体",1,12));
            jLabel.setForeground(Color.pink);
            binary_module_panel.add(jLabel);
        }
//        binary_module_panel.revalidate();
//        binary_module_panel.repaint();
        binary_module_panel.updateUI();
    }

    @Override
    public void show() {
        super.show();
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        Dashboard_bkp dialog = new Dashboard_bkp();
        dialog.pack();
        dialog.setVisible(true);
//        System.exit(0);
    }
}
