package com.bmc.elasticsearchloganalzyer.model;

import com.opencsv.bean.CsvBindByName;

public class Parameter extends CsvLine {

    @CsvBindByName(column = "PTYPE")
    private String pType;

    @CsvBindByName(column = "PNAME")
    private String pName;

    @CsvBindByName(column = "PVALUE")
    private String pValue;

    @CsvBindByName(column = "DATEW")
    private String pDate;

    public String getpType() {
        return pType;
    }

    public String getpName() {
        return pName;
    }

    public String getpValue() {
        return pValue;
    }

    public String getpDate() {
        return pDate;
    }

}





