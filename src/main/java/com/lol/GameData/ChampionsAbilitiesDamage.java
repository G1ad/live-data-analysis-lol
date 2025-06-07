package com.lol.GameData;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import com.lol.GameDataPojo.DamageResult;
import com.lol.GameDataPojo.EnemyChampionStats;
import lombok.Data;

@Data
public class ChampionsAbilitiesDamage {

    private final LocalGameService gameService;

    public ChampionsAbilitiesDamage(LocalGameService gameService) {
        this.gameService = gameService;
    }

    public List<DamageResult> calculateLeeSinDamageOnEnemies() throws IOException, NoSuchAlgorithmException, KeyManagementException {
        List<DamageResult> finalResults = new ArrayList<>();

        if (!gameService.getMyChampionName().orElse("").equalsIgnoreCase("LeeSin")) {
            return finalResults;
        }

        List<Double> rawDamages = getLeeSinRawQDamage();
        if (rawDamages.isEmpty()) return finalResults;

        double rawDamageQ1 = rawDamages.get(0);
        double rawDamageQ2Min = rawDamages.get(1);
        double rawDamageQ2Max = rawDamages.get(2);

        List<EnemyChampionStats> enemyStatsList = gameService.getCalculatedEnemyStats();

        for (EnemyChampionStats enemy : enemyStatsList) {
            double enemyArmor = enemy.getArmor();

            double damageMultiplier = 100.0 / (100.0 + enemyArmor);

            double finalDamageQ1 = rawDamageQ1 * damageMultiplier;
            double finalDamageQ2Min = rawDamageQ2Min * damageMultiplier;
            double finalDamageQ2Max = rawDamageQ2Max * damageMultiplier;

            finalResults.add(new DamageResult(
                    enemy.getChampionName(),
                    Math.round(finalDamageQ1),
                    Math.round(finalDamageQ2Min),
                    Math.round(finalDamageQ2Max)
            ));
        }

        return finalResults;
    }

    private List<Double> getLeeSinRawQDamage() throws IOException, NoSuchAlgorithmException, KeyManagementException {
        int qLevel = gameService.getGameData()
                .flatMap(data -> Optional.ofNullable(data.getActivePlayer()))
                .flatMap(player -> Optional.ofNullable(player.getAbilities()))
                .map(abilities -> abilities.getQ().getAbilityLevel())
                .orElse(0);

        if (qLevel == 0) return new ArrayList<>();

        JsonNode leeSinNode = gameService.readChampionFromJson().get("LeeSin");
        if (leeSinNode == null) throw new IOException("Dati di Lee Sin non trovati in champions.json");

        double bonusAD = gameService.getBonusAD();

        // Calcolo Q1
        List<Integer> baseDamageQ1 = extractValues(leeSinNode, "Q", 0, 0, 0, 0);
        double scalingQ1 = getScaling(leeSinNode, "Q", 0, 0, 0, 1);
        double finalDamageQ1 = calculateFinalDamage(qLevel, baseDamageQ1, calculateBonusDamage(bonusAD, scalingQ1));

        // Calcolo Q2
        List<Integer> minDamageQ2 = extractValues(leeSinNode, "Q", 1, 0, 0, 0);
        double scalingMinQ2 = getScaling(leeSinNode, "Q", 1, 0, 0, 1);
        double finalDamageMinQ2 = calculateFinalDamage(qLevel, minDamageQ2, calculateBonusDamage(bonusAD, scalingMinQ2));

        List<Integer> maxDamageQ2 = extractValues(leeSinNode, "Q", 1, 0, 1, 0);
        double scalingMaxQ2 = getScaling(leeSinNode, "Q", 1, 0, 1, 1);
        double finalDamageMaxQ2 = calculateFinalDamage(qLevel, maxDamageQ2, calculateBonusDamage(bonusAD, scalingMaxQ2));

        return Arrays.asList(finalDamageQ1, finalDamageMinQ2, finalDamageMaxQ2);
    }

    private List<Integer> extractValues(JsonNode node, String ability, int abilityIndex,int effectsIndex,int levelingIndex, int modfiersIndex) {
        List<Integer> valuesList = new ArrayList<>();
        JsonNode valuesNode = node.path("abilities").path(ability).get(abilityIndex).path("effects").get(effectsIndex).path("leveling")
                .get(levelingIndex).path("modifiers").get(modfiersIndex).path("values");

        for (JsonNode value : valuesNode) {
            valuesList.add(value.asInt());
        }
        return valuesList;
    }

    private double getScaling(JsonNode node, String ability, int abilityIndex, int effectsIndex, int levelingIndex, int modfiersIndex) {
        JsonNode values = node.path("abilities").path(ability).get(abilityIndex).path("effects").get(effectsIndex).path("leveling")
                .get(levelingIndex).path("modifiers").get(modfiersIndex).path("values");
        return values.isArray() && values.size() > 0 ? values.get(0).asDouble() : 0;
    }

    private double calculateBonusDamage(double bonusAD, double scaling) {
        return (bonusAD * scaling) / 100;
    }

    private double calculateFinalDamage(int qLevel, List<Integer> damage, double bonusDamage) {
        if (qLevel > 0 && qLevel <= damage.size()){
            return damage.get(qLevel - 1) + bonusDamage;
        }
        return 0;
    }
}
