package com.jamesfchen.manager;

import javax.swing.*;
import java.awt.*;

public class FieldText extends JPanel{
    private JRadioButton sourceRadioButton;
    private JRadioButton excludeRadioButton;
    private JRadioButton binaryRadioButton;
    private JLabel moduleName;


    public void setText(String text,Color c) {
        moduleName.setText(text);
        moduleName.setHorizontalAlignment(SwingConstants.CENTER);
        moduleName.setFont(new Font("黑体", 1, 12));
        moduleName.setForeground(c);
    }

    public static void main(String[] args) {
        FieldText text = new FieldText();

    }
}
