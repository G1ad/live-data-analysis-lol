package com.lol.ItemStatsPojo;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Item {

    @JsonProperty("name")
    public String name;
    @JsonProperty("id")
    public int id;
    @JsonProperty("tier")
    public int tier;
    @JsonProperty("rank")
    public ArrayList<String> rank;
    @JsonProperty("buildsFrom")
    public ArrayList<Object> buildsFrom;
    @JsonProperty("buildsInto")
    public ArrayList<Integer> buildsInto;
    @JsonProperty("specialRecipe")
    public int specialRecipe;
    @JsonProperty("noEffects")
    public boolean noEffects;
    @JsonProperty("removed")
    public boolean removed;
    @JsonProperty("requiredChampion")
    public String requiredChampion;
    @JsonProperty("requiredAlly")
    public String requiredAlly;
    @JsonProperty("icon")
    public String icon;
    @JsonProperty("simpleDescription")
    public String simpleDescription;
    @JsonProperty("nicknames")
    public ArrayList<Object> nicknames;
    @JsonProperty("passives")
    public ArrayList<Object> passives;
    @JsonProperty("active")
    public ArrayList<Object> active;
    @JsonProperty("stats")
    public Stats stats;
    @JsonProperty("shop")
    public Shop shop;
    @JsonProperty("iconOverlay")
    public boolean iconOverlay;
    
}
