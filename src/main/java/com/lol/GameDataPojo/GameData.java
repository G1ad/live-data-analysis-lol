package com.lol.GameDataPojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameData {

    String gameMode;
    double gameTime;
    String mapName;
    int mapNumber;
    String mapTerrain;

}
