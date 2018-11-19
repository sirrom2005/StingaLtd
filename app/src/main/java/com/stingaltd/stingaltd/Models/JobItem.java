package com.stingaltd.stingaltd.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobItem implements Serializable {
    private static final long serialVersionUID = 100L;
    private int id;
    private String job_id;
    private String job_type;
    private String title;
    private String description;
    private String customer;
    private String date_added;
    private int complete;
    private int technician_id;
    private List<Inventory> inventory = new ArrayList<>();
    private List<ExpenseAmount> expense = new ArrayList<>();

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

    public List<Inventory> getInventory() { return inventory; }
    public List<ExpenseAmount> getExpenseAmount() { return expense; }
}
