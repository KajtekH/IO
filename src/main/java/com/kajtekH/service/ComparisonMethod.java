package com.kajtekH.service;

import com.github.difflib.patch.Patch;
import com.kajtekH.model.InputFile;

import java.io.IOException;

public abstract class ComparisonMethod {
    protected Patch<String> patch;
    public abstract void compare(InputFile inputFile1, InputFile inputFile2) throws IOException;
    public Patch<String> getDifference() {
        return patch;
    }
}
