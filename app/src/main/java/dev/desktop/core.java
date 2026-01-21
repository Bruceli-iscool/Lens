package dev.desktop;

import javax.swing.*;

public class core extends JFrame{
    JFrame frame;
    public core(String windowName) {
        this.frame = new JFrame(windowName);
        this.frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
}