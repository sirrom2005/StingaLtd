package com.stingaltd.stingaltd.Models;

import java.io.Serializable;

public class ExpenseAmount implements Serializable {
    private static final long serialVersionUID = 110L;
    private int expense_id;
    private double amount;

    public int getId() { return expense_id; }
    public double getAmount() {
        return amount;
    }
}