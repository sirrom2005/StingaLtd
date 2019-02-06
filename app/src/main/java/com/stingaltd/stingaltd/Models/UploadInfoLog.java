package com.stingaltd.stingaltd.Models;

import java.io.Serializable;

public class UploadInfoLog implements Serializable {
    private static final long serialVersionUID = 202L;
    private String TaskName;
    private String Value;

    public UploadInfoLog(String taskName, String value) {
        TaskName = taskName;
        Value = value;
    }

    public String getKey() {
        return TaskName;
    }
    public String getValue() {
        return Value;
    }
}
