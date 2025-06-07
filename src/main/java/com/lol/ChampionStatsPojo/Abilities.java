package com.lol.ChampionStatsPojo;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Abilities{
    @JsonProperty("P") 
    public ArrayList<P> p;
    @JsonProperty("Q") 
    public ArrayList<Q> q;
    @JsonProperty("W") 
    public ArrayList<W> w;
    @JsonProperty("E") 
    public ArrayList<E> e;
    @JsonProperty("R") 
    public ArrayList<R> r;
}
