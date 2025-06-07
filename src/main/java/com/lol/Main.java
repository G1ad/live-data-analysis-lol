package com.lol;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lol.Config.*;
import com.lol.GameData.AllChampionsJson;
import com.lol.GameData.ChampionList;
import com.lol.GameData.ChampionsAbilitiesDamage;
import com.lol.GameData.ItemsJson;
import com.merakianalytics.orianna.Orianna;
import com.merakianalytics.orianna.types.common.Platform;

public class Main {
    public static void main(String[] args) throws IOException, KeyManagementException, NoSuchAlgorithmException, InterruptedException {
        SSLUtil.turnOffSslChecking();
        Orianna.setDefaultPlatform(Platform.EUROPE_WEST);
        
        ChampionList championList = new ChampionList();
        ChampionsAbilitiesDamage abilitiesDamage = new ChampionsAbilitiesDamage();
       // System.out.println(championList.getEnemyBonusResistance("attackDamage"));
        System.out.println(abilitiesDamage.getFinalDamage());
    }
}