package com.lol.GameData;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


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
public class LocalGameService {
    
    private DataCurrentGame dataGame;
    private final ObjectMapper mapper = new ObjectMapper();

    public LocalGameService() throws IOException {
        fetchData();
    }

    public boolean fetchData() throws IOException {
        try {
            String localUrl = ConfigLoader.getUrlLocal();
            String jsonResponse = ApiManager.makeApiCall(localUrl);

            if(jsonResponse != null) {
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                dataGame = mapper.readValue(jsonResponse, DataCurrentGame.class);
                return dataGame != null;
            } else {
                System.out.println("Partita non trovata o client non in esecuzione");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Optional<DataCurrentGame> getGameData() {
        return Optional.ofNullable(dataGame);
    }

    public Optional<String> getMySummonerName() {
        if (dataGame == null) return Optional.empty();
        return Optional.ofNullable(dataGame.getActivePlayer().getSummonerName());
    }

    public List<AllPlayers> getEnemyPlayers() {
        if (dataGame == null) return new ArrayList<>();
        String mySummonerName = getMySummonerName().orElse("");
        String myTeam = dataGame.getAllPlayers().stream()
                .filter(p -> p.getSummonerName().equalsIgnoreCase(mySummonerName))
                .map(AllPlayers::getTeam).findFirst().orElse("");

        if (myTeam.isEmpty()) return new ArrayList<>();

        return dataGame.getAllPlayers().stream()
                .filter(p -> !p.getTeam().equalsIgnoreCase(myTeam))
                .collect(Collectors.toList());
    }

    public Optional<String> getMyChampionName() throws IOException, KeyManagementException, NoSuchAlgorithmException {
        if (dataGame == null) return Optional.empty();
        String mySummonerName = getMySummonerName().orElse("");
        return dataGame.getAllPlayers().stream()
                .filter(p -> p.getSummonerName().equalsIgnoreCase(mySummonerName))
                .map(AllPlayers::getChampionName)
                .findFirst();
    }

    public String getMyTeam() throws IOException, KeyManagementException, NoSuchAlgorithmException {
        List<AllPlayers> allPlayers = dataGame.getAllPlayers();
        String myChampionName = String.valueOf(getMyChampionName());

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
        String myChampName = String.valueOf(getMyChampionName());
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
        String myChampName = String.valueOf(getMyChampionName());
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
        String myChampName = String.valueOf(getMyChampionName());
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
        if (pathChampions == null || pathChampions.isBlank()) throw new IOException("Percorso champions.json non configurato");
        return mapper.readTree(new File(pathChampions));
    }

    public JsonNode readItemsFromJson() throws IOException {
        String pathItems = ConfigLoader.getItemsPath();
        if (pathItems == null || pathItems.isBlank()) throw new IOException("Percorso items.json non configurato");
        return mapper.readTree(new File(pathItems));
    }

    public List<EnemyChampionStats> getCalculatedEnemyStats() throws IOException {
        List<EnemyChampionStats> allEnemyStats = new ArrayList<>();
        List<AllPlayers> enemies = getEnemyPlayers();
        JsonNode championsData = readChampionFromJson();
        JsonNode itemsData = readItemsFromJson();

        for (AllPlayers enemy : enemies) {
            // Usa i tuoi metodi logici, ma per un singolo nemico alla volta
            double bonusArmor = getEnemyBonusResistanceForPlayer(enemy, "armor", itemsData);
            double totalArmor = getEnemyResistanceForPlayer(enemy, "armor", bonusArmor, championsData);

            double bonusMagicResist = getEnemyBonusResistanceForPlayer(enemy, "magicResistance", itemsData);
            double totalMagicResist = getEnemyResistanceForPlayer(enemy, "magicResistance", bonusMagicResist, championsData);

            allEnemyStats.add(new EnemyChampionStats(enemy.getChampionName(), totalArmor, totalMagicResist));
        }
        return allEnemyStats;
    }

    private double getEnemyBonusResistanceForPlayer(AllPlayers player, String statName, JsonNode itemsData) {
        double championTotalStats = 0.0;
        String itemStatName = statName.equals("magicResistance") ? "spellBlock" : statName;

        if (player.getItems() != null) {
            for (com.lol.GameDataPojo.Item item : player.getItems()) {
                JsonNode itemNode = itemsData.get(String.valueOf(item.getItemID()));
                if (itemNode != null && itemNode.has("stats")) {
                    championTotalStats += itemNode.path("stats").path(itemStatName).path("flat").asDouble();
                }
            }
        }
        return championTotalStats;
    }

    private double getEnemyResistanceForPlayer(AllPlayers player, String statName, double bonusResistance, JsonNode championsData) {
        JsonNode championNode = championsData.get(player.getChampionName());
        if (championNode == null) return 0.0;

        double baseStat = championNode.path("stats").path(statName).path("flat").asDouble();
        double statPerLevel = championNode.path("stats").path(statName).path("perLevel").asDouble();

        int level = player.getLevel();
        double exponent = Math.pow(level, 2);
        double formula = (7.0/400.0 * exponent + 267.0/400.0 * level - 137.0/200.0);
        double statFromLevel = statPerLevel * formula;

        double totalResistance = baseStat + statFromLevel + bonusResistance;
        return Math.round(totalResistance);
    }

    public double getBonusAD() throws KeyManagementException, NoSuchAlgorithmException, IOException {
        if (dataGame == null) return 0.0;
        String myChampion = getMyChampionName().orElse(null);
        if (myChampion == null) return 0.0;

        JsonNode myChampNode = readChampionFromJson().get(myChampion);
        if (myChampNode == null) return 0.0;

        int currentLevel = dataGame.getActivePlayer().getLevel();
        double baseAD  = myChampNode.path("stats").path("attackDamage").path("flat").asDouble();
        double adPerLevel  = myChampNode.path("stats").path("attackDamage").path("perLevel").asDouble();

        double exponent = Math.pow(currentLevel, 2);
        double growthMultiplier = (7/400f * exponent + 267/400f * currentLevel - 137/200f);
        double currentBaseAD  = baseAD  + adPerLevel  * growthMultiplier;
        double totalADFromClient  = (dataGame.getActivePlayer().getChampionStats().getAttackDamage() - currentBaseAD);

        return Math.round(totalADFromClient );
    }
}
