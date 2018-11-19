package com.stingaltd.stingaltd.Models;

import java.io.Serializable;

public class Inventory implements Serializable {
    private static final long serialVersionUID = 103L;
    private String code;
    private String description;
    private int inventory_id;
    private int quantity;
    private int quantity_used;

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getQuantityUsed() { return quantity_used; }

    public int getInventoryId() { return inventory_id; }
}