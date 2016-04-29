package com.pear.shopz.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by johnlarmie on 2016-04-26.
 */
public class Item implements Parcelable{

    private String name;
    private String aisle;

    public Item(String name, String aisle) {
        this.name = name;
        this.aisle = aisle;
    }

    public Item(Parcel in) {
        name = in.readString();
        aisle = in.readString();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(aisle);
    }
}
