package com.bmc.elasticsearchloganalzyer.model;

import java.io.File;

public class LogFile {
    private String logName;
    private File file;
    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
