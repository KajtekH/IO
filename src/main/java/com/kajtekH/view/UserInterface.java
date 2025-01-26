package com.kajtekH.view;

import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import com.kajtekH.controller.ComparisonSystem;
import com.kajtekH.model.InputFile;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class UserInterface extends JFrame {
    private final JTextField filePath1;
    private final JTextField filePath2;
    private final JTextArea fileContent1;
    private final JTextArea fileContent2;
    private final ComparisonSystem comparisonSystem;
    private final List<String> mergedContent;

    public UserInterface() {
        comparisonSystem = new ComparisonSystem();
        mergedContent = new ArrayList<>();
        setTitle("File Comparer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel topPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        filePath1 = new JTextField("No file selected");
        filePath1.setEditable(false);
        JButton openFile1Button = new JButton("Open File 1");
        openFile1Button.setToolTipText("Select the first file to compare");
        openFile1Button.addActionListener(new OpenFileActionListener(1));

        filePath2 = new JTextField("No file selected");
        filePath2.setEditable(false);
        JButton openFile2Button = new JButton("Open File 2");
        openFile2Button.setToolTipText("Select the second file to compare");
        openFile2Button.addActionListener(new OpenFileActionListener(2));

        topPanel.add(filePath1);
        topPanel.add(openFile1Button);
        topPanel.add(filePath2);
        topPanel.add(openFile2Button);

        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        fileContent1 = new JTextArea();
        fileContent1.setEditable(false);
        fileContent1.setBorder(BorderFactory.createTitledBorder("File 1 Content"));
        fileContent1.setFont(new Font("Monospaced", Font.PLAIN, 16));
        fileContent2 = new JTextArea();
        fileContent2.setEditable(false);
        fileContent2.setBorder(BorderFactory.createTitledBorder("File 2 Content"));
        fileContent2.setFont(new Font("Monospaced", Font.PLAIN, 16));

        centerPanel.add(new JScrollPane(fileContent1));
        centerPanel.add(new JScrollPane(fileContent2));

        add(centerPanel, BorderLayout.CENTER);

        JButton compareButton = new JButton("Compare");
        compareButton.setToolTipText("Compare the selected files");
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
                long startTime = System.nanoTime();
                Runtime runtime = Runtime.getRuntime();
                runtime.gc();
                long memoryUsedBefore = runtime.totalMemory() - runtime.freeMemory();
                
                comparisonSystem.compareFiles();
                Patch<String> patch = comparisonSystem.getDifferences();

                List<String> lines1 = java.nio.file.Files.readAllLines(
                        java.nio.file.Path.of(comparisonSystem.getInputFile1().getPath())
                );
                
                long endTime = System.nanoTime();
                long memoryUsedAfter = runtime.totalMemory() - runtime.freeMemory();
                
                long elapsedTimeMillis = (endTime - startTime) / 1_000_000;
                long memoryUsedBytes = memoryUsedAfter - memoryUsedBefore;
                
                JOptionPane.showMessageDialog(
                    null,
                    "Time: " + elapsedTimeMillis + " ms\n" +
                    "Memory: " + memoryUsedBytes / 1024 + " kB",
                    "Informations",
                    JOptionPane.INFORMATION_MESSAGE
                );
                
                mergedContent.clear();
                int logicalPosition = 0;

                JFrame diffFrame = new JFrame("Differences");
                diffFrame.setSize(800, 600);
                diffFrame.setLayout(new BorderLayout(10, 10));
                diffFrame.setLocationRelativeTo(null);
                
                JPanel diffPanel = new JPanel();
                diffPanel.setLayout(new BoxLayout(diffPanel, BoxLayout.Y_AXIS));
                
                JButton editButton = new JButton("Edit manually");
                editButton.setToolTipText("Edit file manually");
                editButton.addActionListener(new EditManuallyFileActionListener());
                diffPanel.add(editButton);
                
                for (AbstractDelta<String> delta : patch.getDeltas()) {
                    int startPosition = delta.getSource().getPosition();

                    if (logicalPosition < startPosition) {
                        mergedContent.addAll(lines1.subList(logicalPosition, startPosition));
                    }

                    logicalPosition = startPosition + delta.getSource().size();

                    JPanel deltaPanel = createDeltaPanel(delta, startPosition);
                    diffPanel.add(deltaPanel);
                }

                if (logicalPosition < lines1.size()) {
                    mergedContent.addAll(lines1.subList(logicalPosition, lines1.size()));
                }

                JButton saveButton = new JButton("Save Output File");
                saveButton.setToolTipText("Save the merged content to a file");
                saveButton.addActionListener(new SaveOutputFileActionListener());
                diffPanel.add(saveButton);

                JScrollPane scrollPane = new JScrollPane(diffPanel);
                diffFrame.add(scrollPane, BorderLayout.CENTER);
                diffFrame.setVisible(true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private JPanel createDeltaPanel(AbstractDelta<String> delta, int startPosition) {
        JPanel deltaPanel = new JPanel();
        deltaPanel.setLayout(new BoxLayout(deltaPanel, BoxLayout.Y_AXIS));
        deltaPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel changeLabel = new JLabel("Change at line " + (startPosition + 1));
        deltaPanel.add(changeLabel);

        for (String line : delta.getSource().getLines()) {
            JPanel sourcePanel = getJPanel(startPosition, line);
            deltaPanel.add(sourcePanel);
        }

        for (String line : delta.getTarget().getLines()) {
            JPanel targetPanel = new JPanel(new BorderLayout(10, 10));
            JLabel targetLine = new JLabel("+ " + line);
            targetLine.setForeground(Color.GREEN);

            JButton targetButton = new JButton("Keep");
            targetButton.setToolTipText("Keep this line from the target file");
            targetButton.addActionListener(e -> {
                if (startPosition <= mergedContent.size()) {
                    mergedContent.add(startPosition, line);
                } else {
                    mergedContent.add(line);
                }
                updateMergedContent();
                targetButton.setEnabled(false);
            });

            targetPanel.add(targetLine, BorderLayout.CENTER);
            targetPanel.add(targetButton, BorderLayout.EAST);
            deltaPanel.add(targetPanel);
        }

        return deltaPanel;
    }

    private JPanel getJPanel(int startPosition, String line) {
        JPanel sourcePanel = new JPanel(new BorderLayout(10, 10));
        JLabel sourceLine = new JLabel("- " + line);
        sourceLine.setForeground(Color.RED);

        JButton sourceButton = new JButton("Keep");
        sourceButton.setToolTipText("Keep this line from the source file");
        sourceButton.addActionListener(e -> {
            if (startPosition <= mergedContent.size()) {
                mergedContent.add(startPosition, line);
            } else {
                mergedContent.add(line);
            }
            updateMergedContent();
            sourceButton.setEnabled(false);
        });

        sourcePanel.add(sourceLine, BorderLayout.CENTER);
        sourcePanel.add(sourceButton, BorderLayout.EAST);
        return sourcePanel;
    }

    private void updateMergedContent() {
        Set<String> uniqueLines = new LinkedHashSet<>(mergedContent);
        mergedContent.clear();
        mergedContent.addAll(uniqueLines);
        Collections.sort(mergedContent);
    }

    private class SaveOutputFileActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
            int returnValue = fileChooser.showSaveDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    java.nio.file.Files.write(file.toPath(), mergedContent);
                    JOptionPane.showMessageDialog(null, "File saved successfully!");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    private class EditManuallyFileActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFrame editFrame = new JFrame("Edit Merged Content");
            editFrame.setSize(800, 600);
            editFrame.setLayout(new BorderLayout(10, 10));
            editFrame.setLocationRelativeTo(null);

            JTextArea textArea = new JTextArea(String.join("\n", mergedContent));
            JScrollPane scrollPane = new JScrollPane(textArea);

            JPanel buttonPanel = new JPanel();
            JButton saveButton = new JButton("Save Changes");
            JButton cancelButton = new JButton("Cancel");

            saveButton.addActionListener(e1 -> {
                mergedContent.clear();
                mergedContent.addAll(Arrays.asList(textArea.getText().split("\n")));
                JOptionPane.showMessageDialog(editFrame, "Changes saved successfully!");
                editFrame.dispose();
            });

            cancelButton.addActionListener(e1 -> editFrame.dispose());

            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            editFrame.add(scrollPane, BorderLayout.CENTER);
            editFrame.add(buttonPanel, BorderLayout.SOUTH);

            editFrame.setVisible(true);
        }
    }
}