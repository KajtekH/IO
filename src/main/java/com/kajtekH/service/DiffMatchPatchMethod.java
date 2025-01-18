package com.kajtekH.service;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.Patch;
import com.kajtekH.model.InputFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class DiffMatchPatchMethod {
    private Patch<String> patch;

    public void compare(InputFile inputFile1, InputFile inputFile2) throws IOException {
        List<String> lines1 = Files.readAllLines(java.nio.file.Path.of(inputFile1.getPath()));
        List<String> lines2 = Files.readAllLines(java.nio.file.Path.of(inputFile2.getPath()));
        patch = DiffUtils.diff(lines1, lines2);
    }

    public Patch<String> getDifference() {
        return patch;
    }
}
