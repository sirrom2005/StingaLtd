package com.stingaltd.stingaltd.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Account implements Serializable {
    private static final long serialVersionUID = 101L;
    private int technician_id;
    private String fname;
    private String lname;
    private String email;
    private String pass;
    private String mobile_number;
    private List<Expenses> expenses = new ArrayList<>();
    private Map<String, String[]> gallery_lable = new HashMap<>();

    public int getTechnicianId() { return technician_id; }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getEmail() {
        return email;
    }

    public String getPass() {
        return pass;
    }

    public String getMobileNumber() {
        return mobile_number;
    }

    public Map<String, String[]> getGalleryLable() {
        return gallery_lable;
    }

    public List<Expenses> getExpenses() {
        return expenses;
    }
}
