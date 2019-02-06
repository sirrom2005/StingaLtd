package com.stingaltd.stingaltd.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RodReading implements Serializable {
    private static final long serialVersionUID = 404L;
    private String name;
    private String amount;

    public RodReading(String key, String value) {
        this.name  = key;
        this.amount= value;
    }

    public void setValue(String value) { this.amount = value; }
    public String getValue() { return amount; }
    public String getKey() {
        return name;
    }
}
