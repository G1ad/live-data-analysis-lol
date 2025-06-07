package com.lol.GameData;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lol.Config.ApiManager;
import com.lol.Config.ConfigLoader;
import com.lol.Config.ObjectMapperSingeltone;

import lombok.Data;

@Data
public class AllChampionsJson {
    
    public void fetchDataAndWriteToFile() throws IOException, KeyManagementException, NoSuchAlgorithmException {
        String allChampions = ConfigLoader.getAllChampions();
        ObjectMapper mapper = ObjectMapperSingeltone.getInstance();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        JsonNode jsonNode = mapper.readTree(ApiManager.makeApiCall(allChampions));

        writeJsonToFile(jsonNode, "AllChampions.txt");
    }

    private void writeJsonToFile(JsonNode jsonNode, String fileName) throws IOException {
        Path resourcesPath = Paths.get("src", "main", "resources");
        Path filePath = resourcesPath.resolve(fileName);

        byte[] jsonDataBytes = jsonNode.toString().getBytes(StandardCharsets.UTF_8.name());

        Files.write(filePath, jsonDataBytes, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
