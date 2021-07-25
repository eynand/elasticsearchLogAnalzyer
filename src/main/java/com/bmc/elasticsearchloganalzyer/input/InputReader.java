package com.bmc.elasticsearchloganalzyer.input;

import com.bmc.elasticsearchloganalzyer.model.LogFile;
import org.apache.tools.ant.DirectoryScanner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;

@Component
public class InputReader {


    private ArrayList<LogFile> logFiles = new ArrayList<>();

    public void createLogFilesList(String ...dirPath) {
        for (String pattern : dirPath) {
            DirectoryScanner scanner = new DirectoryScanner();
            scanner.setIncludes(new String[]{"**/*.log","**/*.txt"});
            scanner.setBasedir(pattern);
            scanner.setCaseSensitive(false);
            scanner.scan();
            String[] files = scanner.getIncludedFiles();
            for (String file:files) {
                LogFile logFile = new LogFile();
                logFile.setLogName(file);
                logFile.setFile(new File(pattern + File.separator + file));
                logFiles.add(logFile);
            }
        }
    }

    public ArrayList<LogFile> getLogFiles() {
        return logFiles;
    }
}
