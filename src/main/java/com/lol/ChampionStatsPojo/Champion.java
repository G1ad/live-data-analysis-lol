package com.lol.ChampionStatsPojo;

import java.util.ArrayList;
import lombok.Data;

@Data
public class Champion {
    public int id;
    public String key;
    public String name;
    public String title;
    public String fullName;
    public String icon;
    public String resource;
    public String attackType;
    public String adaptiveType;
    public Stats stats;
    public ArrayList<String> roles;
    public AttributeRatings attributeRatings;
    public Abilities abilities;
    public String releaseDate;
    public String releasePatch;
    public String patchLastChanged;
    public Price price;
    public String lore;
    public String faction;
    public ArrayList<Skin> skins;
}
