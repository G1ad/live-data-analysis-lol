package com.lol;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import com.lol.Config.*;
import com.lol.GameData.AllChampionsJson;
import com.lol.GameData.LocalGameService;
import com.lol.GameData.ItemsJson;
import com.merakianalytics.orianna.Orianna;
import com.merakianalytics.orianna.types.common.Platform;

import static javafx.application.Application.launch;

public class Main {
    public static void main(String[] args) throws IOException, KeyManagementException, NoSuchAlgorithmException, InterruptedException {
        SSLUtil.turnOffSslChecking();
        Orianna.setDefaultPlatform(Platform.EUROPE_WEST);
        AllChampionsJson championsFileDownload = new AllChampionsJson();
        ItemsJson itemsFileDownload = new ItemsJson();

        LocalGameService game = new LocalGameService();

        System.out.println(game.getDataGame().allPlayers.get(0).runes);

        //championsFileDownload.fetchDataAndWriteToFile();
        //itemsFileDownload.fetchDataAndWriteToFile();
    }
}