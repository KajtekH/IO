package com.kajtekH.model;

public class OutputFile extends File {
    public OutputFile(String path) {

        if(path == null || path.isEmpty())
        {
            throw new IllegalArgumentException("path is null or empty");
        }
        else {
            this.path = path;
        }
    }
}