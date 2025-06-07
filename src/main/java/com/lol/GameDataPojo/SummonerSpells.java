package com.lol.GameDataPojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SummonerSpells {
    public SummonerSpellOne summonerSpellOne;
    public SummonerSpellTwo summonerSpellTwo;
}
