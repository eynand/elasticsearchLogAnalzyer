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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LogParser {

    @Autowired
    PatternList patternList;
    private ArrayList<Pattern> patterns;
    private ArrayList<DateTimeFormatter> dateTimeFormatters;
    final static String levelRgx = "(?<level>INFO|ERROR|WARN|TRACE|DEBUG|FATAL)";
    final static String textRgx = "(?<text>(.*))";
    private static Pattern PatternOnlyText = Pattern.compile(textRgx);

    public LogLine parse(String line, String logName) {
        Matcher matcher;
        for (Pattern pattern : patterns) {
            matcher = pattern.matcher(line);
            if (matcher.find()) {
                LogLine logLine = new LogLine();
                for (DateTimeFormatter dateFormat : dateTimeFormatters) {
                    try {
                        logLine.setTimestamp(TimeUnit.SECONDS.toMillis(LocalDateTime.parse(matcher.group("timestamp").replace("\t", " "), dateFormat).toEpochSecond(ZoneOffset.UTC)));
                        break;
                    } catch (Exception ex) {

                    }
                }
                logLine.setLogName(logName);
                try
                {
                    if (matcher.group("level") != null) {
                        logLine.setLevel(matcher.group("level"));
                    }
                }
                catch (Exception ex) {

                }

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
    public void createParsers() {
        patterns = new ArrayList<>();
        dateTimeFormatters = new ArrayList<>();
        for (String linePattern : patternList.getLinePatterns()) {
            for (String datePattern : patternList.getDatePatterns()) {
                patterns.add(Pattern.compile(linePattern.replace("$DATE", datePattern).replace("$TEXT", textRgx).replace("$LEVEL", levelRgx)));
            }
        }

        for (String dateFormat : patternList.getDateFormats()) {
            dateTimeFormatters.add(DateTimeFormatter.ofPattern(dateFormat));
        }
    }

}
