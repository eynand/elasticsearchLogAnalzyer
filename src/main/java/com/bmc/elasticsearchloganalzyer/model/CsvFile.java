package com.bmc.elasticsearchloganalzyer.model;

import java.io.File;

public class CsvFile {

    private String csvName;
    private File file;

    public void setCsvName(String csvName) {
        this.csvName = csvName;
    }


    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
