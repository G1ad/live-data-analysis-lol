package com.lol.GameDataPojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SummonerSpellOne {
    public String displayName;
    public String rawDescription;
    public String rawDisplayName;
}
