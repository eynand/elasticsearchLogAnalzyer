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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class LogAnalyzer {
    @Autowired
    InputReader inputReader;
    @Autowired
    LogParser logParser;
    @Autowired
    ElasticClient elasticClient;

    private ExecutorService executorService = Executors.newFixedThreadPool(4);
    private CountDownLatch countDownLatch;

    public void analyze() throws InterruptedException {
        countDownLatch = new CountDownLatch(inputReader.getLogFiles().size());
        for (LogFile logFile : inputReader.getLogFiles()) {
            executorService.submit(() -> {
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
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        System.out.println("Finish Analyzing Log Files");
    }
}
