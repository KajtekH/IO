package com.kajtekH;

import javax.swing.*;
import com.kajtekH.view.UserInterface;

public class FileComparerApp extends JFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserInterface app = new UserInterface();
            app.setVisible(true);
        });
    }
}