package com.kajtekH.view;

import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import com.kajtekH.controller.ComparisonSystem;
import com.kajtekH.model.InputFile;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class UserInterface extends JFrame {
    private final JTextField filePath1;
    private final JTextField filePath2;
    private final JTextArea fileContent1;
    private final JTextArea fileContent2;
    private final ComparisonSystem comparisonSystem;

    public UserInterface() {
        comparisonSystem = new ComparisonSystem();
        setTitle("File Comparer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(2, 2));
        filePath1 = new JTextField("No file selected");
        filePath1.setEditable(false);
        JButton openFile1Button = new JButton("Open File 1");
        openFile1Button.addActionListener(new OpenFileActionListener(1));

        filePath2 = new JTextField("No file selected");
        filePath2.setEditable(false);
        JButton openFile2Button = new JButton("Open File 2");
        openFile2Button.addActionListener(new OpenFileActionListener(2));

        topPanel.add(filePath1);
        topPanel.add(openFile1Button);
        topPanel.add(filePath2);
        topPanel.add(openFile2Button);

        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2));
        fileContent1 = new JTextArea();
        fileContent1.setEditable(false);
        fileContent2 = new JTextArea();
        fileContent2.setEditable(false);

        centerPanel.add(new JScrollPane(fileContent1));
        centerPanel.add(new JScrollPane(fileContent2));

        add(centerPanel, BorderLayout.CENTER);

        JButton compareButton = new JButton("Compare");
        compareButton.addActionListener(new CompareActionListener());
        add(compareButton, BorderLayout.SOUTH);
    }

    private class OpenFileActionListener implements ActionListener {
        private final int fileNumber;

        public OpenFileActionListener(int fileNumber) {
            this.fileNumber = fileNumber;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (fileNumber == 1) {
                    InputFile inputFile1 = new InputFile(selectedFile.getAbsolutePath());
                    comparisonSystem.setInputFile1(inputFile1);
                    filePath1.setText(inputFile1.getPath());
                    displayFileContent(selectedFile, fileContent1);
                } else {
                    InputFile inputFile2 = new InputFile(selectedFile.getAbsolutePath());
                    comparisonSystem.setInputFile2(inputFile2);
                    filePath2.setText(inputFile2.getPath());
                    displayFileContent(selectedFile, fileContent2);
                }
            }
        }
    }

    private void displayFileContent(File file, JTextArea textArea) {
        try {
            List<String> lines = java.nio.file.Files.readAllLines(file.toPath());
            textArea.setText(String.join("\n", lines));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class CompareActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                comparisonSystem.compareFiles();
                Patch<String> patch = comparisonSystem.getDifferences();

                JFrame diffFrame = new JFrame("Differences");
                diffFrame.setSize(800, 600);
                diffFrame.setLayout(new BorderLayout());

                JPanel diffPanel = new JPanel();
                diffPanel.setLayout(new BoxLayout(diffPanel, BoxLayout.Y_AXIS));

                for (AbstractDelta<String> delta : patch.getDeltas()) {
                    JPanel deltaPanel = new JPanel();
                    deltaPanel.setLayout(new BoxLayout(deltaPanel, BoxLayout.Y_AXIS));

                    JLabel changeLabel = new JLabel("Change at line " + (delta.getSource().getPosition() + 1));
                    deltaPanel.add(changeLabel);

                    for (String line : delta.getSource().getLines()) {
                        JLabel sourceLine = new JLabel("- " + line);
                        sourceLine.setForeground(Color.RED);
                        deltaPanel.add(sourceLine);
                    }

                    for (String line : delta.getTarget().getLines()) {
                        JLabel targetLine = new JLabel("+ " + line);
                        targetLine.setForeground(Color.GREEN);
                        deltaPanel.add(targetLine);
                    }

                    diffPanel.add(deltaPanel);
                }

                JScrollPane scrollPane = new JScrollPane(diffPanel);
                diffFrame.add(scrollPane, BorderLayout.CENTER);

                diffFrame.setVisible(true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


}