package com.stingaltd.stingaltd.Models;

import java.io.Serializable;

public class JobItem implements Serializable {
    private static final long serialVersionUID = 100L;
    public int id;
    public String job_id;
    public String job_type;
    public String title;
    public String description;
    public String customer;
    public String date_added;
    public int complete;
    public int technician_id;

    public int getId() {
        return id;
    }

    public String getJob_id() {
        return job_id;
    }

    public String getJob_type() {
        return job_type;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCustomer() {
        return customer;
    }

    public String getAssign_date() {
        return date_added;
    }

    public int getComplete() {
        return complete;
    }

    public int getTechnicianId() {
        return technician_id;
    }
}
