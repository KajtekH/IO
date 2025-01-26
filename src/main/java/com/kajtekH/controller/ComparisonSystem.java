package com.kajtekH.controller;

import com.github.difflib.patch.Patch;
import com.kajtekH.model.InputFile;
import com.kajtekH.model.OutputFile;
import com.kajtekH.service.ComparisonMethod;
import com.kajtekH.service.DiffMatchPatchMethod;
import com.kajtekH.service.OwnMethod;

import java.io.IOException;

public class ComparisonSystem {
    private InputFile inputFile1;
    private InputFile inputFile2;
    private OutputFile outputFile;
    private ComparisonMethod comparisonMethod;

    public ComparisonSystem() {

        //comparisonMethod = new OwnMethod();
        comparisonMethod = new DiffMatchPatchMethod();
    }

    public void setInputFile1(InputFile inputFile1) {

        if(inputFile1 == null)
        {
            throw new IllegalArgumentException("Input file cannot be null");
        }
        else {
            this.inputFile1 = inputFile1;
        }

    }

    public void setInputFile2(InputFile inputFile2) {

        if(inputFile2 == null)
        {
            throw new IllegalArgumentException("Input file cannot be null");
        }
        else {
            this.inputFile2 = inputFile2;
        }
    }

    public void setOutputFile(OutputFile outputFile) {

        if(outputFile == null)
        {
            throw new IllegalArgumentException("Output file cannot be null");
        }
        else {
            this.outputFile = outputFile;
        }
    }

    public void compareFiles() throws IOException {
        comparisonMethod.compare(inputFile1, inputFile2);
    }

    public Patch<String> getDifferences() {

        return comparisonMethod.getDifference();
    }

    public InputFile getInputFile1() {
        return inputFile1;
    }

    public InputFile getInputFile2() {
        return inputFile2;
    }
}
