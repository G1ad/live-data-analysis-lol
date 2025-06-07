package com.lol.GameDataPojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lol.Config.CamelCaseConverter;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AllPlayers {

    public String championName;
    public boolean isBot;
    public boolean isDead;
    public List<Item> items;
    public int level;
    public String position;
    public String rawChampionName;
    public double respawnTimer;
    public Runes runes;
    public Scores scores;
    public int skinID;
    public String summonerName;
    public SummonerSpells summonerSpells;
    public String team;

    public String getChampionName() {
        return CamelCaseConverter.processString(championName);
    }

    public boolean isBot() {
        return isBot;
    }

    public boolean isDead() {
        return isDead;
    }

    public List<Item> getItems() {
        return items;
    }

    public int getLevel() {
        return level;
    }

    public String getPosition() {
        return position;
    }

    public String getRawChampionName() {
        return rawChampionName;
    }

    public double getRespawnTimer() {
        return respawnTimer;
    }

    public Runes getRunes() {
        return runes;
    }

    public Scores getScores() {
        return scores;
    }

    public int getSkinID() {
        return skinID;
    }

    public String getSummonerName() {
        return summonerName;
    }

    public SummonerSpells getSummonerSpells() {
        return summonerSpells;
    }

    public String getTeam() {
        return team;
    }
}
