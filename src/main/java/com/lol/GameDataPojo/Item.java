package com.lol.GameDataPojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {

    public String displayName;
    public int itemID;
    public int count;
    public int slot;
    public int price;
    public boolean canUse;
    public boolean consumable;
    public String rawDescription;
    public String rawDisplayName;
}

