package com.jamesfchen.manager;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.CollectionComboBoxModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Map;
import java.util.List;

import static com.jamesfchen.manager.NotificationUtil.showNotification;


public class Dashboard extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTabbedPane tabbedPane;
    private JPanel allModulePanel;
    private JPanel settingsPanel;
    private JPanel buildVariantsPanel;
    private JPanel moduleSettings;
    private JPanel hotkeys;
    private JRadioButton sb1;
    private JRadioButton fwkrb1;
    private JRadioButton sb2;
    private JRadioButton sb3;
    private JRadioButton fwkrb2;
    private JRadioButton db1;
    private JRadioButton db2;
    private String activeBuildVariant;
    private ButtonGroup fwbg;
    private ButtonGroup sbbg;
    private ButtonGroup dbbg;

    public Dashboard() {
        setSize(new Dimension(1000, 697));
        setLocationRelativeTo(null);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        buttonOK.addActionListener(e -> {
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
                    }else {
                        binarysb.append(moduleName.getText());
                        binarysb.append(",");
                    }
                }
                result.sourceModules = sourcesb.toString();
                result.excludeModules = excludesb.toString();
                result.binaryModules = binarysb.toString();
                okl.call(result);
            }
            dispose();
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
        fwbg.add(fwkrb1);
        fwbg.add(fwkrb2);
        sbbg = new ButtonGroup();
        sbbg.add(sb1);
        sbbg.add(sb2);
        sbbg.add(sb3);
        dbbg = new ButtonGroup();
        dbbg.add(db1);
        dbbg.add(db2);
    }

    protected OkListener okl = null;
    protected CancelListener cancell = null;


    public interface OkListener {
        void call(Result result);
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

    public void bindExcludePanel(Map<String, Module> moduleMap) {
        bindPanel(moduleMap, entry -> {
            JPanel fieldText = createFieldText(entry, Color.red, "exclude");
            return fieldText;
        });
    }

    public void bindSourcePanel(Map<String, Module> moduleMap) {
        bindPanel(moduleMap, entry -> createFieldText(entry, Color.green, "source"));
    }

    public void bindBinaryPanel(Map<String, Module> moduleMap) {
        bindPanel(moduleMap, entry -> createFieldText(entry, Color.orange, "binary"));
    }
    public void bindBuildVariants(String activeBuildVariant,List<String> variants){
        this.activeBuildVariant = activeBuildVariant;
        GridBagConstraints bagConstraints = new GridBagConstraints();
        bagConstraints.fill = GridBagConstraints.HORIZONTAL;
        //add jlabel
        bagConstraints.weightx = 1;
        JLabel jLabel = new JLabel();
        jLabel.setText(activeBuildVariant);
        jLabel.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel.setFont(new Font("黑体", 1, 12));
        jLabel.setForeground(Color.green);
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
//    public void bindBuildArtifacts(String buildArtifact, List<String> buildArtifacts){
//        this.activeBuildArtifact = buildArtifact;
//        GridBagConstraints bagConstraints = new GridBagConstraints();
//        bagConstraints.fill = GridBagConstraints.HORIZONTAL;
//        //add jlabel
//        bagConstraints.weightx = 1;
//        JLabel jLabel = new JLabel();
//        jLabel.setText(buildArtifact);
//        jLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        jLabel.setFont(new Font("黑体", 1, 12));
//        jLabel.setForeground(Color.green);
//        buildArtifactsPanel.add(jLabel, bagConstraints);
//        ComboBoxModel<String> comboBoxModel = new CollectionComboBoxModel<>(buildArtifacts);
//        ComboBox<String> comboBox = new ComboBox<>(comboBoxModel);
//        bagConstraints.weightx = 1;
//        comboBox.setEditable(true);
//        comboBox.setSelectedItem(buildArtifact);
//        comboBox.addItemListener(e -> {
//            jLabel.setText(e.getItem().toString());
//            this.activeBuildArtifact = e.getItem().toString();
//        });
//        buildArtifactsPanel.add(comboBox, bagConstraints);
//
//    }
    private interface Callback {
        JComponent call(Map.Entry<String, Module> entry);
    }

    int i = 0;
    int j = 0;

    public void bindPanel(Map<String, Module> moduleMap, Callback cb) {
        if (moduleMap == null || moduleMap.size() == 0) return;
        GridBagConstraints bagConstraints = new GridBagConstraints();
        bagConstraints.fill = GridBagConstraints.BOTH;
        bagConstraints.insets = new Insets(10, 10, 10, 10);
        for (Map.Entry<String, Module> entry : moduleMap.entrySet()) {
            JComponent fieldText = cb.call(entry);
            bagConstraints.weightx = 1;
            bagConstraints.gridx = i % 4;
            bagConstraints.gridy = j / 4;
            allModulePanel.add(fieldText, bagConstraints);
            i++;
            j++;
        }
    }

    private JPanel createFieldText(Map.Entry<String, Module> entry, Color color, String defaultGroupName) {

        JPanel jPanel = new JPanel(new GridBagLayout());
        GridBagConstraints bagConstraints = new GridBagConstraints();
        bagConstraints.fill = GridBagConstraints.BOTH;
        //add jlabel
        bagConstraints.weightx = 1;
        JLabel jLabel = new JLabel();
        jLabel.setText(entry.getKey());
        jLabel.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel.setFont(new Font("黑体", 1, 12));
        jLabel.setForeground(color);
        jPanel.add(jLabel, bagConstraints);
        ComboBoxModel<String> comboBoxModel;
        if ("fwk".equalsIgnoreCase(entry.getValue().group) || "home".equalsIgnoreCase(entry.getValue().group)) {
            comboBoxModel = new CollectionComboBoxModel<>(Arrays.asList("source", "binary"));
        }else if ("plugin".equalsIgnoreCase(entry.getValue().format)){
            comboBoxModel = new CollectionComboBoxModel<>(Arrays.asList("source", "exclude"));
        } else {
            comboBoxModel = new CollectionComboBoxModel<>(Arrays.asList("source", "binary","exclude"));
        }
        ComboBox<String> comboBox = new ComboBox<>(comboBoxModel);
        comboBox.setSelectedItem(defaultGroupName);
        bagConstraints.weightx = 1;
        comboBox.setEditable(true);
        comboBox.addItemListener(e -> {
            if ("source".equals(comboBox.getSelectedItem())) {
                jLabel.setForeground(Color.green);
            } else if ("exclude".equals(comboBox.getSelectedItem())) {
                jLabel.setForeground(Color.red);
            } else if ("binary".equals(comboBox.getSelectedItem())) {
                jLabel.setForeground(Color.orange);
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
