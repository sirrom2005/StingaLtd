package com.stingaltd.stingaltd.Models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class JobItem implements Serializable {
    private static final long serialVersionUID = 100L;
    private int id;
    private String job_id;
    private String job_type;
    private String title;
    private String description;
    private String customer;
    private String start_date;
    private String complete_date;
    private String technician_note;
    private int complete;
    private int technician_id;
    private List<Inventory> inventory = new ArrayList<>();
    private List<ExpenseAmount> expense = new ArrayList<>();
    private List<RodReading> rod_reading = new ArrayList<>();

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

    public String getStartDate() {
        return start_date;
    }

    public int getComplete() {
        return complete;
    }

    public String getTechnicianNote() {
        return technician_note;
    }

    public int getTechnicianId() {
        return technician_id;
    }

    public List<Inventory> getInventory() { return inventory; }
    public List<ExpenseAmount> getExpenseAmount() { return expense; }
    public List<RodReading> getRodReading() { return rod_reading; }

    public void setTechnicianNote(String technician_note) {
        this.technician_note = technician_note;
    }

    public void setCompleteDate() {
        this.complete_date = new SimpleDateFormat("yyyy-MM-dd HHmmss", Locale.US).format(new Date());
    }
}
