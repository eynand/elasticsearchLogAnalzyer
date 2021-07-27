
package com.bmc.elasticsearchloganalzyer.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.opencsv.bean.CsvBindByPosition;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class Metric extends CsvLine {
    //20210416132105
    final static private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");


    @CsvBindByPosition(position = 1)
    private String name;

    @CsvBindByPosition(position = 2)
    private String componentType;

    @CsvBindByPosition(position = 3)
    private String componentName;

    @CsvBindByPosition(position = 4)
    private String hostname;

    @CsvBindByPosition(position = 7)
    private String value;

    @CsvBindByPosition(position = 10)
    private String metricTimeString;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @JsonIgnore
    public String getMetricTimeString() {
        return metricTimeString;
    }

    @JsonGetter("timestamp")
    public long getTimestamp() {
        try {

            return TimeUnit.SECONDS.toMillis(LocalDateTime.parse(metricTimeString, dateFormatter).toEpochSecond(ZoneOffset.UTC));
        } catch (Exception ex) {
            return -1L;
        }

    }


    public void setMetricTimeString(String metricTimeString) {
        this.metricTimeString = metricTimeString;
    }
}