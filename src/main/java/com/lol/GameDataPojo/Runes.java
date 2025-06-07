package com.lol.GameDataPojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Runes {
    public Keystone keystone;
    public PrimaryRuneTree primaryRuneTree;
    public SecondaryRuneTree secondaryRuneTree;
}
