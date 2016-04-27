package com.pear.shopz.objects;

/**
 * Created by johnlarmie on 2016-04-26.
 */
public class Item {

    private String name;
    private String aisle;

    public Item(String name, String aisle) {
        this.name = name;
        this.aisle = aisle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAisle() {
        return aisle;
    }

    public void setAisle(String aisle) {
        this.aisle = aisle;
    }
}
