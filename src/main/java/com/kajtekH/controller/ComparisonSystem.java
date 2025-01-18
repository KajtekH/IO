package com.kajtekH.controller;

import com.github.difflib.patch.Patch;
import com.kajtekH.model.InputFile;
import com.kajtekH.model.OutputFile;
import com.kajtekH.service.DiffMatchPatchMethod;

import java.io.File;
import java.io.IOException;

public class ComparisonSystem {
    private InputFile inputFile1;
    private InputFile inputFile2;
    private OutputFile outputFile;
    private DiffMatchPatchMethod diffMatchPatchMethod;

    public ComparisonSystem() {
        diffMatchPatchMethod = new DiffMatchPatchMethod();
    }

    public void setInputFile1(InputFile inputFile1) {
        this.inputFile1 = inputFile1;
    }

    public void setInputFile2(InputFile inputFile2) {
        this.inputFile2 = inputFile2;
    }

    public void setOutputFile(OutputFile outputFile) {
        this.outputFile = outputFile;
    }

    public void compareFiles() throws IOException {
        diffMatchPatchMethod.compare(inputFile1, inputFile2);
    }

    public Patch<String> getDifferences() {
        return diffMatchPatchMethod.getDifference();
    }

    public InputFile getInputFile1() {
        return inputFile1;
    }

    public InputFile getInputFile2() {
        return inputFile2;
    }
}
