package com.lol.Config;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    private static final Properties properties = new Properties();

    static {
        try(InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")){
            if(input == null) {
                System.out.println("Sorry, unable to find config.properties");
            }
            properties.load(input);
        }catch(Exception exception) {
            exception.printStackTrace();
        }
    }

    public static String getAllChampions(){
        return properties.getProperty("URL_ALL_CHAMPIONS");
    }

    public static String getAllItems(){
        return properties.getProperty("URL_ITEMS");
    }

    public static String getChampionsPath(){
        return properties.getProperty("CHAMPIONS_FILE_PATH");
    }
    public static String getItemsPath(){
        return properties.getProperty("ITEMS_FILE_PATH");
    }

    public static String getUrlLocal(){
        return properties.getProperty("URL_LOCAL");
    }
}