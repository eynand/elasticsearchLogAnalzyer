package com.bmc.elasticsearchloganalzyer;

import com.bmc.elasticsearchloganalzyer.elasticsearch.ElasticClient;
import com.bmc.elasticsearchloganalzyer.input.InputReader;
import com.bmc.elasticsearchloganalzyer.model.LogFile;
import com.bmc.elasticsearchloganalzyer.model.LogLine;
import com.bmc.elasticsearchloganalzyer.parser.LogParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;

@Service
public class LogAnalyzer {
    @Autowired
    InputReader inputReader;
    @Autowired
    LogParser logParser;
    @Autowired
    ElasticClient elasticClient;

    public void analyze() {
        for (LogFile logFile : inputReader.getLogFiles()) {
            System.out.println("Analyzing log - " + logFile.getLogName());
            try {
                BufferedReader reader;
                reader = new BufferedReader(new FileReader(
                        logFile.getFile()), 32768);
                String strLine;
                /* read log line by line */
                while ((strLine = reader.readLine()) != null) {
                    LogLine logLine = logParser.parse(strLine, logFile.getLogName());
                    if (logLine != null && logLine.getMessage() != null && logLine.getTimestamp() != 0) {
                        elasticClient.sendLogTime(logLine);
                    }
                    if (logLine != null && logLine.getMessage() != null) {
                        elasticClient.sendLog(logLine);
                    }
                }
                reader.close();
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Finish Analyzing Log Files");
    }


}
