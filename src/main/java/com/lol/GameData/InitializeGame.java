package com.lol.GameData;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lol.Config.*;
import com.lol.GameDataPojo.*;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class InitializeGame {
    
    private DataCurrentGame dataGame;
    private final ObjectMapper mapper = new ObjectMapper();

    public InitializeGame() throws IOException {
        fetchData();
    }

    public void fetchData() throws IOException {
        String localUrl = ConfigLoader.getUrlLocal();
        String jsonResponse = ApiManager.makeApiCall(localUrl);

        if(jsonResponse != null) {
            mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
            mapper.readValue(jsonResponse, DataCurrentGame.class);
        }else{
            System.out.println("Game non trovato");
        }
    }

    public String extractMySummonerName() {
        ActivePlayer activePlayer = dataGame.getActivePlayer();
        return activePlayer.getSummonerName();
    }

    public String getMyChampionName() throws IOException, KeyManagementException, NoSuchAlgorithmException {
        List <AllPlayers> allPlayers = dataGame.getAllPlayers();
    
        for (AllPlayers player : allPlayers) {
            if (player.getSummonerName().equals(extractMySummonerName())) {
                String championName = player.getChampionName();

                if (championName.equalsIgnoreCase("Wukong")) {
                    return "MonkeyKing";
                } else if (championName.equalsIgnoreCase("Renata Glasc")) {
                    return "Renata";
                  }    

                String camelCaseChampionName = CamelCaseConverter.processString(championName);

                return camelCaseChampionName;
            }
        }

        return "Error finding summoner name";
    }

    public String getMyTeam() throws IOException, KeyManagementException, NoSuchAlgorithmException {
        List<AllPlayers> allPlayers = dataGame.getAllPlayers();
        String myChampionName = getMyChampionName();

        for (AllPlayers player : allPlayers) {
            if (player.getChampionName().equals(myChampionName)) {
                return player.getTeam();
            }
        }

        return "Team not found";
    }

    public List<Integer> getChampionLevels() throws IOException, KeyManagementException, NoSuchAlgorithmException {
        List<Integer> levels = new ArrayList<>();

        List<AllPlayers> allPlayers = dataGame.getAllPlayers();
        String myChampName = getMyChampionName();
        String myTeam = getMyTeam();

        for (AllPlayers player : allPlayers) {
            String championName = player.getChampionName();
            String teamName = player.getTeam();

            if (!teamName.equals(myTeam) && !championName.equals(myChampName)) {
                levels.add(player.getLevel());
            }
        }

        return levels;
    }

    public synchronized List<String> getChampionNames() throws IOException, KeyManagementException, NoSuchAlgorithmException {
        List<String> enemyChampionNames = new ArrayList<>();
        
        List <AllPlayers> allPlayers = dataGame.getAllPlayers();
        String myChampName = getMyChampionName();
        String myTeam = getMyTeam();

        for (AllPlayers player : allPlayers) {
            String championName = player.getChampionName();
            String teamName = player.getTeam();

            if (!teamName.equals(myTeam) && !championName.equals(myChampName)) {
                enemyChampionNames.add(championName);
            }
        }

        return enemyChampionNames;
    }

    public List<List<Integer>> getPlayerItemsIds() throws KeyManagementException, NoSuchAlgorithmException, IOException {
        List<List<Integer>> gameItemsIds = new ArrayList<>();
        
        List<AllPlayers> allPlayers = dataGame.getAllPlayers();
        String myChampName = getMyChampionName();
        String myTeam = getMyTeam();

        for (AllPlayers player : allPlayers) {
            String championName = player.getChampionName();
            String teamName = player.getTeam();

            if (!teamName.equals(myTeam) && !championName.equals(myChampName)) {
                List<Item> playerItems = player.getItems();
                List<Integer> itemIds = new ArrayList<>();
                
                for (Item item : playerItems) {
                    itemIds.add(item.getItemID());
                }
                gameItemsIds.add(itemIds);
            }
        }

        return gameItemsIds;
    }
    public JsonNode readChampionFromJson() throws IOException {
        String pathChampions = ConfigLoader.getChampionsPath();
        File fileChampions = new File(pathChampions);
        JsonNode champion = mapper.readValue(fileChampions, JsonNode.class);

        return champion;
    }

    public JsonNode readItemsFromJson() throws IOException {
        String pathItems = ConfigLoader.getItemsPath();
        File fileItems = new File(pathItems);
        JsonNode itemsJson = mapper.readValue(fileItems, JsonNode.class);
        return itemsJson;
    }

    public List<Double> getEnemyBonusResistance(String stat) throws StreamReadException, DatabindException, KeyManagementException, NoSuchAlgorithmException, IOException{
        List<Double> championStats = new ArrayList<>();
        List<List<Integer>> itemsIds = getPlayerItemsIds();

        for (List<Integer> itemForChampion : itemsIds) {
            double championTotalStats = 0.0;

            for (Integer itemId : itemForChampion) {
                JsonNode itemNode = readItemsFromJson().get(String.valueOf(itemId));

                if (itemNode != null) {
                    double value = itemNode.path("stats").path(stat).path("flat").asDouble();
                    championTotalStats += value;
                }
            }
            championStats.add(championTotalStats);
        }

        return championStats;
    }

    public List<Double> getEnemyResistance(String stat) throws KeyManagementException, NoSuchAlgorithmException, IOException {
        List<Integer> levels = getChampionLevels();
        List<String> championsNames = getChampionNames();
        List<Double> bonusResistance = getEnemyBonusResistance(stat);
        List<Double> championStats = new ArrayList<>();
        
        for (String champion : championsNames) {
            double statX = 0.0;
            double statY = 0.0;
            JsonNode championNode = readChampionFromJson().get(String.valueOf(champion));

            if (championNode != null) {
                statX = championNode.path("stats").path(stat).path("flat").asDouble();
                statY = championNode.path("stats").path(stat).path("perLevel").asDouble();
            }

            for (Integer level : levels) {
                double exponent = Math.pow(level, 2);
                double formula = (7/400f * exponent + 267/400f * level - 137/200f);
                double statZ = statX + statY * formula;

                for (Double resistance : bonusResistance) {
                    championStats.add((double) Math.round(statZ + resistance));
                }
            }
        }

        return championStats;
    }

    public double getBonusAD() throws KeyManagementException, NoSuchAlgorithmException, IOException {
        JsonNode myChamp = readChampionFromJson().get(String.valueOf(getMyChampionName()));
        int currentLevel = dataGame.getActivePlayer().getLevel();
        double attackDamage = myChamp.path("stats").path("attackDamage").path("flat").asDouble();
        double attackDamageGrowth = myChamp.path("stats").path("attackDamage").path("perLevel").asDouble();
        double exponent = Math.pow(currentLevel, 2);
        double formula = (7/400f * exponent + 267/400f * currentLevel - 137/200f);
        double statIncreaseAttackDamage = attackDamage + attackDamageGrowth * formula;
        double bonusAD = (dataGame.activePlayer.championStats.getAttackDamage() - statIncreaseAttackDamage);

        return Math.round(bonusAD);
    }
}
