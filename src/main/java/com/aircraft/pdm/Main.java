package com.aircraft.pdm;

import com.aircraft.pdm.ui.AppFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fall back to default look and feel.
        }
        SwingUtilities.invokeLater(() -> new AppFrame(args).setVisible(true));
    }
}
