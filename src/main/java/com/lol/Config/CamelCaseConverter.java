package com.lol.Config;

import java.util.HashSet;
import java.util.Set;

public class CamelCaseConverter {
    
    private static final Set<Character> specialChars;

    static {
        specialChars = new HashSet<>();
        specialChars.add(' ');
        specialChars.add('.');
        specialChars.add('\'');
    }

    private static final Set<String> specialCases;

    static {
        specialCases = new HashSet<>();
        specialCases.add("Bel'Veth");
        specialCases.add("Cho'Gath");
        specialCases.add("Kai'Sa");
        specialCases.add("Kha'Zix");
        specialCases.add("Vel'Koz");
    }

    public static String processString(String input) {
        if (containsSpecialChars(input)) {
            String[] parts = input.split("[ .'\\s]");
            String firstPart = capitalizeFirstLetter(parts[0]);
            String secondPart = capitalizeFirstLetter(parts[1]);

            if (specialCases.contains(input)) {
                secondPart = parts[1].toLowerCase();
            }
            return firstPart + secondPart;
        } else {
            return input;
        }
    }

    private static boolean containsSpecialChars(String input) {
        for (char c : input.toCharArray()) {
            if (specialChars.contains(c)) {
                return true;
            }
        }
        return false;
    }

    private static String capitalizeFirstLetter(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
}
