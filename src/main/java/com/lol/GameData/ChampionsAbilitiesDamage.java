package com.lol.GameData;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;

@Data
public class ChampionsAbilitiesDamage {

    InitializeGame championList;

    public List<Double> getFinalDamage() throws KeyManagementException, NoSuchAlgorithmException, IOException {
        List<Double> leeSinDamages = getLeeSinDamage();
        List<Double> enemyArmors = championList.getEnemyResistance("armor");
        List<Double> finalDamages = new ArrayList<>();

        int size = Math.min(leeSinDamages.size(), enemyArmors.size());
        for (int i = 0; i < size; i++) {
            double damage = leeSinDamages.get(i) / (1 + (enemyArmors.get(i) / 100f));
            finalDamages.add(damage);
        }
    
        return finalDamages;
    }

    public List<Double> getLeeSinDamage() throws KeyManagementException, NoSuchAlgorithmException, IOException {
        championList = new InitializeGame();
        int qLevel = championList.getDataGame().getActivePlayer().getAbilities().getQ().getAbilityLevel();

        JsonNode leeSinNodeQ = championList.readChampionFromJson().get(String.valueOf("LeeSin"));

        List<Integer> damageQ1 = extractValues(leeSinNodeQ, "Q", 0, 0, 0, 0);
        List<Integer> minDamageQ2 = extractValues(leeSinNodeQ, "Q", 1, 0, 0, 0);
        List<Integer> maxDamageQ2 = extractValues(leeSinNodeQ, "Q", 1, 0, 1, 0);

        double scalingFirstQ = extractScaling(leeSinNodeQ, "Q", 0, 0, 0, 1);
        double scalingSecondQ1 = extractScaling(leeSinNodeQ, "Q", 1, 0, 0, 1);
        double scalingSecondQ2 = extractScaling(leeSinNodeQ, "Q", 1, 0, 1, 1);

        double bonusDamageFirstQ = calculateBonusDamage(championList.getBonusAD(), scalingFirstQ);
        double bonusDamageMinSecondQ = calculateBonusDamage(championList.getBonusAD(), scalingSecondQ1);
        double bonusDamageMaxSecondQ = calculateBonusDamage(championList.getBonusAD(), scalingSecondQ2);

        double finalDamageFirstQ = calculateFinalDamage(qLevel, damageQ1, bonusDamageFirstQ);
        double finalDamageMinSecondQ = calculateFinalDamage(qLevel, minDamageQ2, bonusDamageMinSecondQ);
        double finalDamageMaxSecondQ = calculateFinalDamage(qLevel, maxDamageQ2, bonusDamageMaxSecondQ);

        List<Double> totalDamage = Arrays.asList(finalDamageFirstQ, finalDamageMinSecondQ, finalDamageMaxSecondQ);

        return totalDamage;
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

    private double extractScaling(JsonNode node, String ability, int abilityIndex,int effectsIndex,int levelingIndex, int modfiersIndex) {
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
