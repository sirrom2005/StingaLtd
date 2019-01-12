package com.stingaltd.stingaltd.Models;

import java.io.Serializable;

public class Expenses implements Serializable {
    private static final long serialVersionUID = 105L;
    private int id;
    private String name;

    public int getId() { return id; }
    public String getName() {
        return name;
    }
}
