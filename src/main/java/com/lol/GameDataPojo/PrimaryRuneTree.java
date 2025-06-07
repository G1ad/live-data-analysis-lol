package com.lol.GameDataPojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrimaryRuneTree {
    public String displayName;
    public int id;
    public String rawDescription;
    public String rawDisplayName;
}
