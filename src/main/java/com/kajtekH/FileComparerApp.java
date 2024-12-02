package com.kajtekH;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import com.github.difflib.*;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import com.github.difflib.patch.PatchFailedException;

public class FileComparerApp extends JFrame {
    private JTextField filePath1;
    private JTextField filePath2;
    private JTextArea fileContent1;
    private JTextArea fileContent2;
    private File file1;
    private File file2;

    private List<String> selectedAdditions = new ArrayList<>();
    private List<String> selectedDeletions = new ArrayList<>();
    private List<AbstractDelta<String>> deltas;

    public FileComparerApp() {
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
                    file1 = selectedFile;
                    filePath1.setText(file1.getAbsolutePath());
                    displayFileContent(file1, fileContent1);
                } else {
                    file2 = selectedFile;
                    filePath2.setText(file2.getAbsolutePath());
                    displayFileContent(file2, fileContent2);
                }
            }
        }
    }

    private void displayFileContent(File file, JTextArea textArea) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            textArea.setText(String.join("\n", lines));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class CompareActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (file1 != null && file2 != null) {
                try {
                    List<String> lines1 = Files.readAllLines(file1.toPath());
                    List<String> lines2 = Files.readAllLines(file2.toPath());
                    Patch<String> patch = DiffUtils.diff(lines1, lines2);
                    deltas = patch.getDeltas();

                    JFrame diffFrame = new JFrame("Differences");
                    diffFrame.setSize(800, 600);
                    diffFrame.setLayout(new BorderLayout());

                    JPanel diffPanel = new JPanel();
                    diffPanel.setLayout(new BoxLayout(diffPanel, BoxLayout.Y_AXIS));

                    for (int i = 0; i < deltas.size(); i++) {
                        AbstractDelta<String> delta = deltas.get(i);
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

                        JButton keepSourceButton = new JButton("Keep Source");
                        keepSourceButton.addActionListener(new MarkChangeActionListener(i, "source"));
                        deltaPanel.add(keepSourceButton);

                        JButton keepTargetButton = new JButton("Keep Target");
                        keepTargetButton.addActionListener(new MarkChangeActionListener(i, "target"));
                        deltaPanel.add(keepTargetButton);

                        diffPanel.add(deltaPanel);
                    }

                    JScrollPane scrollPane = new JScrollPane(diffPanel);
                    diffFrame.add(scrollPane, BorderLayout.CENTER);

                    JButton saveButton = new JButton("Save Combined File");
                    saveButton.addActionListener(new SaveCombinedFileActionListener(lines1, lines2));
                    diffFrame.add(saveButton, BorderLayout.SOUTH);

                    diffFrame.setVisible(true);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select both files to compare.");
            }
        }
    }

    private class MarkChangeActionListener implements ActionListener {
        private final int deltaIndex;
        private final String version;

        public MarkChangeActionListener(int deltaIndex, String version) {
            this.deltaIndex = deltaIndex;
            this.version = version;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (deltaIndex >= 0 && deltaIndex < deltas.size()) {
                AbstractDelta<String> delta = deltas.get(deltaIndex);
                if (version.equals("source")) {
                    selectedDeletions.addAll(delta.getSource().getLines());
                } else if (version.equals("target")) {
                    selectedAdditions.addAll(delta.getTarget().getLines());
                }
            } else {
                System.err.println("Invalid delta index: " + deltaIndex);
            }
        }
    }
    private class SaveCombinedFileActionListener implements ActionListener {
        private final List<String> lines1;
        private final List<String> lines2;

        public SaveCombinedFileActionListener(List<String> lines1, List<String> lines2) {
            this.lines1 = lines1;
            this.lines2 = lines2;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                List<String> combinedLines = new ArrayList<>(lines1);
                for (AbstractDelta<String> delta : deltas) {
                    if (selectedAdditions.containsAll(delta.getTarget().getLines())) {
                        combinedLines.addAll(delta.getTarget().getPosition(), delta.getTarget().getLines());
                    } else if (selectedDeletions.containsAll(delta.getSource().getLines())) {
                        combinedLines.removeAll(delta.getSource().getLines());
                    }
                }
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
                int returnValue = fileChooser.showSaveDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File outputFile = fileChooser.getSelectedFile();
                    Files.write(outputFile.toPath(), combinedLines);
                    JOptionPane.showMessageDialog(null, "File saved successfully.");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FileComparerApp app = new FileComparerApp();
            app.setVisible(true);
        });
    }
}