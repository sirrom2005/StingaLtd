package com.stingaltd.stingaltd.Models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Account implements Serializable {
    private static final long serialVersionUID = 101L;
    public int technician_id;
    public String fname;
    public String lname;
    public String email;
    public String mobile_number;
    public Map<String, String[]> gallery_lable = new HashMap<>();

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

    public String getMobileNumber() {
        return mobile_number;
    }

    public Map<String, String[]> getGalleryLable() {
        return gallery_lable;
    }
}
