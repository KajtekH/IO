package com.kajtekH.model;

public class InputFile extends File {
    public InputFile(String path) {

        if(path == null || path.isEmpty())
        {
            throw new IllegalArgumentException("path is null or empty");
        }
        else {
            this.path = path;
        }
    }
}
