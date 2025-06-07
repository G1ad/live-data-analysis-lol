package com.lol.ItemStatsPojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Prices{
    @JsonProperty("total")
    public int total;
    @JsonProperty("combined")
    public int combined;
    @JsonProperty("sell")
    public int sell;

}
