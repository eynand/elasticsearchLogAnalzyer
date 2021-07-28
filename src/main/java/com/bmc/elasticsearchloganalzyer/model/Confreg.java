package com.bmc.elasticsearchloganalzyer.model;

import com.opencsv.bean.CsvBindByName;

public class Confreg extends CsvLine {

    @CsvBindByName(column = "MACHINE_NAME")
    private String machineName;

    @CsvBindByName(column = "PROCESS_COMMAND")
    private String processCommand;

    @CsvBindByName(column = "CURRENT_STATE")
    private String currentState;

    @CsvBindByName(column = "DESIRED_STATE")
    private String desiredState;

    @CsvBindByName(column = "LAST_UPDATE")
    private String lastUpdate;


    public String getMachineName() {
        return machineName;
    }

    public String getProcessCommand() {
        return processCommand;
    }

    public String getCurrentState() {
        return currentState;
    }

    public String getDesiredState() {
        return desiredState;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }
}
