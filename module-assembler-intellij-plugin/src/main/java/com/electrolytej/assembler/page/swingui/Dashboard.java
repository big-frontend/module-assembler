package com.electrolytej.assembler.page.swingui;

import com.electrolytej.assembler.Result;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.ui.JBColor;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Map;
import java.util.List;
import java.util.TreeMap;

import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.lang.String;

import com.electrolytej.assembler.model.Module;

public class Dashboard extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTabbedPane tabbedPane;
    private JPanel allModulePanel;
    private JPanel settingsPanel;
    private JPanel buildVariantsPanel;
    private String activeBuildVariant;
    private JPanel moduleSettings;
    private JPanel hotkeys;
    private ButtonGroup fwbg;
    private JRadioButton fwkrb0;
    private JRadioButton fwkrb1;
    private ButtonGroup sbbg;
    private JRadioButton sbrb0;
    private JRadioButton sbrb1;
    private JRadioButton sbrb2;
    private ButtonGroup dbbg;
    private JRadioButton dbrb0;
    private JRadioButton dbrb1;
    private JRadioButton dbrb2;
    Map<String, Module> excludeModuleMap = new TreeMap<String, Module>();
    Map<String, Module> sourceModuleMap = new TreeMap<String, Module>();
    Map<String, Module> binaryModuleMap = new TreeMap<String, Module>();

    public Dashboard() {
        setSize(new Dimension(1000, 697));
        setLocationRelativeTo(null);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        buttonOK.addActionListener(e -> {
            boolean close = true;
            if (okl != null) {
                Result result = new Result();
                result.activeBuildVariant = activeBuildVariant;
                int componentCount = allModulePanel.getComponentCount();
                StringBuilder excludesb = new StringBuilder();
                StringBuilder sourcesb = new StringBuilder();
                StringBuilder binarysb = new StringBuilder();
                for (int i = 0; i < componentCount; ++i) {
                    JPanel fieldText = (JPanel) allModulePanel.getComponent(i);
                    JLabel moduleName = (JLabel) fieldText.getComponent(0);
                    ComboBox<String> comboBox = (ComboBox<String>) fieldText.getComponent(1);
                    String selectedItem = (String) comboBox.getSelectedItem();
                    if ("source".equals(selectedItem)) {
                        sourcesb.append(moduleName.getText());
                        sourcesb.append(",");
                    } else if ("exclude".equals(selectedItem)) {
                        excludesb.append(moduleName.getText());
                        excludesb.append(",");
                    } else {
                        binarysb.append(moduleName.getText());
                        binarysb.append(",");
                    }
                }
                result.sourceModules = sourcesb.toString();
                result.excludeModules = excludesb.toString();
                result.binaryModules = binarysb.toString();
                close = okl.call(result);
            }
            if (close) {
                dispose();
            }
        });
        buttonCancel.addActionListener(e -> {
            if (cancell != null) cancell.call();
            dispose();
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (cancell != null) cancell.call();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (cancell != null) cancell.call();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        fwbg = new ButtonGroup();
        fwbg.add(fwkrb0);
        fwbg.add(fwkrb1);
        fwkrb0.addActionListener(e -> {
            foreachModules((moduleNameJLabel, moduleStateComboBox) -> {
                Module module = binaryModuleMap.get(moduleNameJLabel.getText());
                if (module != null && "fwk".equals(module.group)) {
                    binaryModuleMap.remove(module.simpleName);
                    sourceModuleMap.put(module.simpleName, module);
                    moduleNameJLabel.setForeground(JBColor.GREEN);
                    moduleStateComboBox.setSelectedIndex(0);
                }
            });

        });
        fwkrb1.addActionListener(e -> {
            foreachModules((moduleNameJLabel, moduleStateComboBox) -> {
                Module module = sourceModuleMap.get(moduleNameJLabel.getText());
                if (module != null && "fwk".equals(module.group)) {
                    sourceModuleMap.remove(module.simpleName);
                    binaryModuleMap.put(module.simpleName, module);
                    moduleNameJLabel.setForeground(JBColor.ORANGE);
                    moduleStateComboBox.setSelectedIndex(1);
                }
            });
        });
        sbbg = new ButtonGroup();
        sbbg.add(sbrb0);
        sbbg.add(sbrb1);
        sbbg.add(sbrb2);
        sbrb0.addActionListener(e -> {
            foreachModules((moduleNameJLabel, moduleStateComboBox) -> {
                Module module = binaryModuleMap.get(moduleNameJLabel.getText());
                if (module == null) module = excludeModuleMap.get(moduleNameJLabel.getText());
                if (module != null && !"fwk".equals(module.group) && module.dynamic == null) {
                    binaryModuleMap.remove(module.simpleName);
                    excludeModuleMap.remove(module.simpleName);
                    sourceModuleMap.put(module.simpleName, module);
                    moduleNameJLabel.setForeground(JBColor.GREEN);
                    moduleStateComboBox.setSelectedIndex(0);
                }
            });
        });
        sbrb1.addActionListener(e -> {
            foreachModules((moduleNameJLabel, moduleStateComboBox) -> {
                Module module = sourceModuleMap.get(moduleNameJLabel.getText());
                if (module == null) module = excludeModuleMap.get(moduleNameJLabel.getText());
                if (module != null && !"fwk".equals(module.group) && module.dynamic == null) {
                    sourceModuleMap.remove(module.simpleName);
                    excludeModuleMap.remove(module.simpleName);
                    binaryModuleMap.put(module.simpleName, module);
                    moduleNameJLabel.setForeground(JBColor.ORANGE);
                    moduleStateComboBox.setSelectedIndex(1);
                }
            });
        });
        sbrb2.addActionListener(e -> {
            foreachModules((moduleNameJLabel, moduleStateComboBox) -> {
                Module module = sourceModuleMap.get(moduleNameJLabel.getText());
                if (module == null) module = binaryModuleMap.get(moduleNameJLabel.getText());
                if (module != null && !"fwk".equals(module.group) && module.dynamic == null) {
                    sourceModuleMap.remove(module.simpleName);
                    binaryModuleMap.remove(module.simpleName);
                    if ("main".equals(module.group)) {
                        moduleNameJLabel.setForeground(JBColor.ORANGE);
                        moduleStateComboBox.setSelectedIndex(1);
                        binaryModuleMap.put(module.simpleName, module);
                    } else {
                        moduleNameJLabel.setForeground(JBColor.RED);
                        moduleStateComboBox.setSelectedIndex(2);
                        excludeModuleMap.put(module.simpleName, module);
                    }
                }
            });
        });
        dbbg = new ButtonGroup();
        dbbg.add(dbrb0);
        dbbg.add(dbrb1);
        dbbg.add(dbrb2);
        dbrb0.addActionListener(e -> {
            foreachModules((moduleNameJLabel, moduleStateComboBox) -> {
                Module module = binaryModuleMap.get(moduleNameJLabel.getText());
                if (module == null) module = excludeModuleMap.get(moduleNameJLabel.getText());
                if (module != null && module.dynamic != null) {
                    binaryModuleMap.remove(module.simpleName);
                    excludeModuleMap.remove(module.simpleName);
                    sourceModuleMap.put(module.simpleName, module);
                    moduleNameJLabel.setForeground(JBColor.GREEN);
                    moduleStateComboBox.setSelectedIndex(0);
                }
            });
        });
        dbrb1.addActionListener(e -> {
            foreachModules((moduleNameJLabel, moduleStateComboBox) -> {
                Module module = sourceModuleMap.get(moduleNameJLabel.getText());
                if (module == null) module = excludeModuleMap.get(moduleNameJLabel.getText());
                if (module != null && module.dynamic != null) {
                    sourceModuleMap.remove(module.simpleName);
                    excludeModuleMap.remove(module.simpleName);
                    binaryModuleMap.put(module.simpleName, module);
                    moduleNameJLabel.setForeground(JBColor.ORANGE);
                    moduleStateComboBox.setSelectedIndex(1);
                }
            });

        });
        dbrb2.addActionListener(e -> {
            foreachModules((moduleNameJLabel, moduleStateComboBox) -> {
                Module module = sourceModuleMap.get(moduleNameJLabel.getText());
                if (module == null) module = binaryModuleMap.get(moduleNameJLabel.getText());
                if (module != null && module.dynamic != null) {
                    sourceModuleMap.remove(module.simpleName);
                    binaryModuleMap.remove(module.simpleName);
                    excludeModuleMap.put(module.simpleName, module);
                    moduleNameJLabel.setForeground(JBColor.YELLOW);
                    moduleStateComboBox.setSelectedIndex(2);
                }
            });

        });
    }

    interface onEach {
        void call(JLabel moduleNameJLabel, ComboBox<String> moduleStateComboBox);
    }

    public void foreachModules(onEach o) {
        int componentCount = allModulePanel.getComponentCount();
        for (int i = 0; i < componentCount; ++i) {
            JPanel fieldText = (JPanel) allModulePanel.getComponent(i);
            JLabel moduleNameJLabel = (JLabel) fieldText.getComponent(0);
            ComboBox<String> comboBox = (ComboBox<String>) fieldText.getComponent(1);
            o.call(moduleNameJLabel, comboBox);

        }
    }

    protected OkListener okl = null;
    protected CancelListener cancell = null;


    public interface OkListener {
        boolean call(Result result);
    }

    public interface CancelListener {
        void call();
    }

    public void setOKListener(OkListener l) {
        this.okl = l;
    }

    public void setCancelListener(CancelListener l) {
        this.cancell = l;
    }

    int i = 0;
    int j = 0;

    public void bindPanel(List<Module> modules) {
        GridBagConstraints bagConstraints = new GridBagConstraints();
        bagConstraints.fill = GridBagConstraints.BOTH;
        bagConstraints.insets = JBUI.insets(10);
        for (Module module : modules) {
            JComponent fieldText;
            switch (module.type) {
                case EXCLUDE -> {
                    fieldText = createFieldText(module, JBColor.RED, "exclude");
                    excludeModuleMap.put(module.simpleName, module);
                }
                case BINARY -> {
                    fieldText = createFieldText(module, JBColor.YELLOW, "binary");
                    binaryModuleMap.put(module.simpleName, module);
                }
                case SOURCE -> {
                    fieldText = createFieldText(module, JBColor.GREEN, "source");
                    sourceModuleMap.put(module.simpleName, module);
                }
                default -> {
                    continue;
                }
            }
            bagConstraints.weightx = 1;
            bagConstraints.gridx = i % 4;
            bagConstraints.gridy = j / 4;
            allModulePanel.add(fieldText, bagConstraints);
            i++;
            j++;
        }
    }

    public void bindBuildVariants(String activeBuildVariant, List<String> variants) {
        this.activeBuildVariant = activeBuildVariant;
        GridBagConstraints bagConstraints = new GridBagConstraints();
        bagConstraints.fill = GridBagConstraints.HORIZONTAL;
        //add jlabel
        bagConstraints.weightx = 1;
        JLabel jLabel = new JLabel();
        jLabel.setText(activeBuildVariant);
        jLabel.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel.setFont(new Font("黑体", Font.BOLD, 12));
        jLabel.setForeground(JBColor.GREEN);
        buildVariantsPanel.add(jLabel, bagConstraints);
        ComboBoxModel<String> comboBoxModel = new CollectionComboBoxModel<>(variants);
        ComboBox<String> comboBox = new ComboBox<>(comboBoxModel);
        bagConstraints.weightx = 1;
        comboBox.setEditable(true);
        comboBox.setSelectedItem(activeBuildVariant);
        comboBox.addItemListener(e -> {
            jLabel.setText(e.getItem().toString());
            this.activeBuildVariant = e.getItem().toString();
        });
        buildVariantsPanel.add(comboBox, bagConstraints);
    }

    private JPanel createFieldText(Module module, Color color, String defaultGroupName) {

        JPanel jPanel = new JPanel(new GridBagLayout());
        GridBagConstraints bagConstraints = new GridBagConstraints();
        bagConstraints.fill = GridBagConstraints.BOTH;
        //add jlabel
        bagConstraints.weightx = 1;
        JLabel jLabel = new JLabel();
        jLabel.setText(module.simpleName);
        jLabel.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel.setFont(new Font("黑体", Font.BOLD, 12));
        jLabel.setForeground(color);
        jPanel.add(jLabel, bagConstraints);
        ComboBoxModel<String> comboBoxModel;
        if ("fwk".equalsIgnoreCase(module.group) || "main".equalsIgnoreCase(module.group)) {
            comboBoxModel = new CollectionComboBoxModel<>(Arrays.asList("source", "binary"));
        } else if ("plugin".equalsIgnoreCase(module.format)) {
            comboBoxModel = new CollectionComboBoxModel<>(Arrays.asList("source", "exclude"));
        } else {
            comboBoxModel = new CollectionComboBoxModel<>(Arrays.asList("source", "binary", "exclude"));
        }
        ComboBox<String> comboBox = new ComboBox<>(comboBoxModel);
        comboBox.setSelectedItem(defaultGroupName);
        bagConstraints.weightx = 1;
        comboBox.setEditable(true);
        comboBox.addItemListener(e -> {
            if ("source".equals(comboBox.getSelectedItem())) {
                jLabel.setForeground(JBColor.GREEN);
            } else if ("exclude".equals(comboBox.getSelectedItem())) {
                jLabel.setForeground(JBColor.RED);
            } else if ("binary".equals(comboBox.getSelectedItem())) {
                jLabel.setForeground(JBColor.YELLOW);
            }
        });
        jPanel.add(comboBox, bagConstraints);
//        bagConstraints.weightx = 1;
//        JRadioButton exclude = new JRadioButton();
//        exclude.setText("exclude");
//        exclude.setHorizontalAlignment(SwingConstants.CENTER);
//        exclude.setVerticalAlignment(SwingConstants.CENTER);
//        bagConstraints.gridx = 0;
//        bagConstraints.gridy=1;
//        jPanel.add(exclude,bagConstraints);
//
//        bagConstraints.weightx = 1;
//        JRadioButton source = new JRadioButton();
//        source.setText("source");
//        source.setHorizontalAlignment(SwingConstants.CENTER);
//        source.setVerticalAlignment(SwingConstants.CENTER);
//        bagConstraints.gridx = 1;
//        bagConstraints.gridy=1;
//        jPanel.add(source,bagConstraints);
//
//        bagConstraints.weightx = 1;
//        JRadioButton binary = new JRadioButton();
//        binary.setText("binary");
//        binary.setHorizontalAlignment(SwingConstants.CENTER);
//        binary.setVerticalAlignment(SwingConstants.CENTER);
//        bagConstraints.gridx = 2;
//        bagConstraints.gridy=1;
//        jPanel.add(binary,bagConstraints);
        return jPanel;
    }
}
