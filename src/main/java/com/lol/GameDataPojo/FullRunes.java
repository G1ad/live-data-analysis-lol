package com.lol.GameDataPojo;

import lombok.Data;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FullRunes {
    public ArrayList<GeneralRune> generalRunes;
    public Keystone keystone;
    public PrimaryRuneTree primaryRuneTree;
    public SecondaryRuneTree secondaryRuneTree;
    public ArrayList<StatRune> statRunes;
}
