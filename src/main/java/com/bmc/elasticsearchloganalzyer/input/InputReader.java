package com.bmc.elasticsearchloganalzyer.input;

import com.bmc.elasticsearchloganalzyer.model.CsvFile;
import com.bmc.elasticsearchloganalzyer.model.LogFile;
import org.apache.tools.ant.DirectoryScanner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;

@Component
public class InputReader {


    private ArrayList<LogFile> logFiles = new ArrayList<>();
    private ArrayList<CsvFile> csvFiles = new ArrayList<>();
    private CsvFile metricFile;

    public void createLogFilesList(String ...dirPath) {
        for (String pattern : dirPath) {
            DirectoryScanner scanner = new DirectoryScanner();
            DirectoryScanner csvScanner = new DirectoryScanner();
            DirectoryScanner metricsScanner = new DirectoryScanner();
            scanner.setIncludes(new String[]{"**/*.log", "**/*.txt"});
            csvScanner.setIncludes(new String[]{"**/params-table.csv","**/confreg-table.csv"});
            metricsScanner.setIncludes(new String[]{"**/metric_data.*"});
            metricsScanner.setBasedir(pattern);
            scanner.setBasedir(pattern);
            csvScanner.setBasedir(pattern);
            scanner.setCaseSensitive(false);
            csvScanner.setCaseSensitive(false);

            scanner.scan();
            String[] files = scanner.getIncludedFiles();
            for (String file : files) {
                LogFile logFile = new LogFile();
                logFile.setLogName(file);
                logFile.setFile(new File(pattern + File.separator + file));
                logFiles.add(logFile);
            }

            csvScanner.scan();
            String[] csvFilesScan = csvScanner.getIncludedFiles();
            for (String csvfile : csvFilesScan) {
                CsvFile csvFile = new CsvFile();
                csvFile.setCsvName(csvfile);
                csvFile.setFile(new File(pattern + File.separator + csvfile));
                csvFiles.add(csvFile);
            }

            metricsScanner.scan();
            String[] metricsFilesScan = metricsScanner.getIncludedFiles();
            for (String csvfile : metricsFilesScan) {
                metricFile = new CsvFile();
                metricFile.setCsvName(csvfile);
                metricFile.setFile(new File(pattern + File.separator + csvfile));
            }

        }
    }

    public CsvFile getMetricFile() {
        return metricFile;
    }

    public ArrayList<LogFile> getLogFiles() {
        return logFiles;
    }

    public ArrayList<CsvFile> getCsvFiles() {
        return csvFiles;
    }

}
