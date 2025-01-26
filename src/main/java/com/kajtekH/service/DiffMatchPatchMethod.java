package com.kajtekH.service;

import com.github.difflib.DiffUtils;
import com.kajtekH.model.InputFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class DiffMatchPatchMethod extends ComparisonMethod{

    public void compare(InputFile inputFile1, InputFile inputFile2) throws IOException {

        if(inputFile1 == null || inputFile2 == null)
        {
            throw new IllegalArgumentException("Input files must not be null");
        }
        else {
            List<String> lines1 = Files.readAllLines(java.nio.file.Path.of(inputFile1.getPath()));
            List<String> lines2 = Files.readAllLines(java.nio.file.Path.of(inputFile2.getPath()));
            patch = DiffUtils.diff(lines1, lines2);
        }
    }
}
