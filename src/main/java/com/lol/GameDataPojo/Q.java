package com.lol.GameDataPojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Q {
    public int abilityLevel;
    public String displayName;
    public String id;
    public String rawDescription;
    public String rawDisplayName;
}
