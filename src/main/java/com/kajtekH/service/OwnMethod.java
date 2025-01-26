package com.kajtekH.service;

import com.github.difflib.patch.Chunk;
import com.github.difflib.patch.DeleteDelta;
import com.github.difflib.patch.InsertDelta;
import com.github.difflib.patch.Patch;
import com.kajtekH.model.InputFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class OwnMethod extends ComparisonMethod{
    public void compare(InputFile inputFile1, InputFile inputFile2) throws IOException {

        if(inputFile1 == null || inputFile2 == null)
        {
            throw new IllegalArgumentException("Input files must not be null");
        }
        else {
            List<String> lines1 = Files.readAllLines(java.nio.file.Path.of(inputFile1.getPath()));
            List<String> lines2 = Files.readAllLines(java.nio.file.Path.of(inputFile2.getPath()));

            patch = new Patch<>();

            int len1 = lines1.size();
            int len2 = lines2.size();
            int i = 0, j = 0;

            while (i < len1 || j < len2) {
                if (i < len1 && j < len2 && lines1.get(i).equals(lines2.get(j))) {
                    i++;
                    j++;
                } else if (j >= len2 || (i < len1 && (j >= len2 || !lines1.get(i).equals(lines2.get(j))))) {
                    Chunk<String> original = new Chunk<>(i, new ArrayList<>(List.of(lines1.get(i))));
                    Chunk<String> revised = new Chunk<>(i, new ArrayList<>());
                    patch.addDelta(new DeleteDelta<>(original, revised));
                    i++;
                } else if (i >= len1 || (j < len2 && (i >= len1 || !lines1.get(i).equals(lines2.get(j))))) {
                    Chunk<String> original = new Chunk<>(i, new ArrayList<>());
                    Chunk<String> revised = new Chunk<>(i, new ArrayList<>(List.of(lines2.get(j))));
                    patch.addDelta(new InsertDelta<>(original, revised));
                    j++;
                }
            }
        }
    }
}
