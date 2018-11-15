package com.cyberark.m3lclient;

public class Result {
    private boolean valid;

    public boolean getValid()
    {
        return Boolean.valueOf(this.valid);
    }

    public void setValid(String valid) {
        this.valid = Boolean.valueOf(valid);
    }
}
