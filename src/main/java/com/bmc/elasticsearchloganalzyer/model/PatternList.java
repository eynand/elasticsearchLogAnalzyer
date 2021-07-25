package com.bmc.elasticsearchloganalzyer.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "patterns")
public class PatternList {
    private List<String> datePatterns;
    private List<String> dateFormats;

    public List<String> getDatePatterns() {
        return datePatterns;
    }

    public void setDatePatterns(List<String> datePatterns) {
        this.datePatterns = datePatterns;
    }

    public List<String> getDateFormats() {
        return dateFormats;
    }

    public void setDateFormats(List<String> dateFormats) {
        this.dateFormats = dateFormats;
    }
}
