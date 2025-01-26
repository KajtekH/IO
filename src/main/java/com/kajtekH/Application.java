package com.kajtekH;

import javax.swing.*;
import com.kajtekH.view.UserInterface;

public class Application extends JFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserInterface userInterface = new UserInterface();
            userInterface.setVisible(true);
        });
    }
}