package com.lol.GameDataPojo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DamageResult {
    private String enemyChampionName;
    private double finalDamageQ1;
    private double finalDamageQ2Min;
    private double finalDamageQ2Max;
}