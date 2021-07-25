package com.bmc.elasticsearchloganalzyer.parser;

import com.bmc.elasticsearchloganalzyer.model.LogLine;
import com.bmc.elasticsearchloganalzyer.model.PatternList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LogParser {

    @Autowired
    PatternList patternList;
    private ArrayList<Pattern> patterns;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy\tHH:mm:ss.SSS");
    final static String timestampRgx = "(?<timestamp>\\d{2}/\\d{2}/\\d{4}\\s+\\d{2}:\\d{2}:\\d{2}\\.\\d+)";
    //final static String timestampRgx = "(?<timestamp>\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})";
    final static String levelRgx = "(?<level>INFO|ERROR|WARN|TRACE|DEBUG|FATAL)";
    final static String classRgx = "\\[(?<class>[^\\]]+)]";
    final static String threadRgx = "\\[(?<thread>[^\\]]+)]";
    final static String textRgx = "(?<text>(.*))";
    private static Pattern PatternOnlyText = Pattern.compile(textRgx);

    public LogLine parse(String line, String logName) {
        Matcher matcher;
        for (Pattern pattern: patterns) {
            matcher = pattern.matcher(line);
            if (matcher.find()) {
                LogLine logLine = new LogLine();
                for (String dateFormat: patternList.getDateFormatts()) {
                    try {
                        logLine.setTimestamp(TimeUnit.SECONDS.toMillis(LocalDateTime.parse(matcher.group("timestamp").replace("\t"," "), DateTimeFormatter.ofPattern(dateFormat)).toEpochSecond(ZoneOffset.UTC)));
                        break;
                    }
                    catch (Exception ex)
                    {

                    }
                }
                logLine.setLogName(logName);
                //logLine.setLevel(matcher.group(1));
                logLine.setMessage(matcher.group("text"));
                return logLine;
            }
        }
        matcher = PatternOnlyText.matcher(line);
        if (matcher.find()) {
            LogLine logLine = new LogLine();
            logLine.setLogName(logName);
            logLine.setMessage(matcher.group("text"));
            return logLine;
        }
        return null;
    }

    @PostConstruct
    public void createParsers(){
        patterns = new ArrayList<>();
        for (String timestampPattern : patternList.getDatePatterns()) {
            patterns.add(Pattern.compile(timestampPattern + "\\s+" + textRgx));
        }

    }

}
