package com.lol.ChampionStatsPojo;

import java.util.ArrayList;
import lombok.Data;

@Data
public class Skin{
    public String name;
    public int id;
    public boolean isBase;
    public String availability;
    public String formatName;
    public boolean lootEligible;
    public Object cost;
    public int sale;
    public String distribution;
    public String rarity;
    public ArrayList<Chroma> chromas;
    public String lore;
    public String release;
    public ArrayList<String> set;
    public String splashPath;
    public String uncenteredSplashPath;
    public String tilePath;
    public String loadScreenPath;
    public String loadScreenVintagePath;
    public boolean newEffects;
    public boolean newAnimations;
    public boolean newRecall;
    public boolean newVoice;
    public boolean newQuotes;
    public ArrayList<String> voiceActor;
    public ArrayList<String> splashArtist;
}
