package com.kajtekH.model;

public abstract class File {
    protected String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {

        if(path == null || path.isEmpty())
        {
            throw new IllegalArgumentException("path is null or empty");
        }
        else {
            this.path = path;
        }
    }
}
