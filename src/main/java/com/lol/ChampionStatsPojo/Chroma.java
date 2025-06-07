package com.lol.ChampionStatsPojo;

import java.util.ArrayList;
import lombok.Data;

@Data
public class Chroma{
    public String name;
    public int id;
    public String chromaPath;
    public ArrayList<String> colors;
    public ArrayList<Description> descriptions;
    public ArrayList<Rarity> rarities;
}
