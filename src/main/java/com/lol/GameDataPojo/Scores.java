package com.lol.GameDataPojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Scores {
    public int assists;
    public int creepScore;
    public int deaths;
    public int kills;
    public double wardScore;
}
