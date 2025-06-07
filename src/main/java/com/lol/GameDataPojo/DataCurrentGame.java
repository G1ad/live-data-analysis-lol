package com.lol.GameDataPojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataCurrentGame {

    public ActivePlayer activePlayer;
    public ArrayList<AllPlayers> allPlayers;
    public Events events;
    public GameData gameData;
}
