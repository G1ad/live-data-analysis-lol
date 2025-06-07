package com.lol.ItemStatsPojo;

import java.util.ArrayList;

import lombok.Data;

@Data
public class Shop{
    public Prices prices;
    public boolean purchasable;
    public ArrayList<Object> tags;
}
