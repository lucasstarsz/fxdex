package io.github.lucasstarsz.fxdex.misc;

public class DatabaseSetup {

    public static final String CreateDexes = """
            CREATE TABLE IF NOT EXISTS `dexes` (
                `apiDexName` tinytext PRIMARY KEY NOT NULL,
                `uiName` tinytext NOT NULL,
                `apiUrl` tinytext NOT NULL
            );""";

    public static final String CreateNatDexToName = """
            CREATE TABLE IF NOT EXISTS `natDexToName` (
                `nationalDexNumber` int PRIMARY KEY NOT NULL,
                `apiMonName` tinytext UNIQUE NOT NULL
            );""";

    public static final String CreateNameToNatDex = """
            CREATE TABLE IF NOT EXISTS `nameToNatDex` (
                `apiMonName` tinytext PRIMARY KEY NOT NULL,
                `nationalDexNumber` int UNIQUE NOT NULL,
                FOREIGN KEY (`apiMonName`) REFERENCES `pokemonByDex` (`apiMonName`),
                FOREIGN KEY (`nationalDexNumber`) REFERENCES `natDexToName` (`nationalDexNumber`)
            );""";

    public static final String CreatePokemonNamesByDex = """
            CREATE TABLE IF NOT EXISTS `pokemonByDex` (
                `apiDexName` tinytext NOT NULL,
                `apiMonName` tinytext NOT NULL,
                `dexNumber` int NOT NULL,
                PRIMARY KEY(apiDexName, apiMonName),
                FOREIGN KEY (`apiDexName`) REFERENCES `dexes` (`apiDexName`),
                FOREIGN KEY (`apiMonName`) REFERENCES `natDexToName` (`apiMonName`)
            );""";

    public static final String CreateDexEntries = """
            CREATE TABLE IF NOT EXISTS `dexEntries` (
                `nationalDexNumber` int PRIMARY KEY NOT NULL,
                `name` tinytext NOT NULL,
                `speciesUrl` tinytext NOT NULL,
                `genus` tinytext NOT NULL,
                `generation` int NOT NULL,
                FOREIGN KEY (`nationalDexNumber`) REFERENCES `natDexToName` (`nationalDexNumber`),
                FOREIGN KEY (`name`) REFERENCES `natDexToName` (`apiMonName`)
            );""";

    public static final String CreateEggGroups = """
            CREATE TABLE IF NOT EXISTS `eggGroups` (
                `nationalDexNumber` int PRIMARY KEY NOT NULL,
                `eggGroupsString` tinytext NOT NULL,
                FOREIGN KEY (`nationalDexNumber`) REFERENCES `dexEntries` (`nationalDexNumber`)
            );""";

    public static final String CreateFlavorTexts = """
            CREATE TABLE IF NOT EXISTS `flavorTexts` (
                `nationalDexNumber` int NOT NULL,
                `region` tinytext NOT NULL,
                `regionText` tinytext NOT NULL,
                PRIMARY KEY (`nationalDexNumber`, `region`),
                FOREIGN KEY (`nationalDexNumber`) REFERENCES `dexEntries` (`nationalDexNumber`),
                FOREIGN KEY (`region`) REFERENCES `dexes` (`apiDexName`)
            );""";

    public static String[] getStartupStatements() {
        return new String[]{
                CreateDexes,
                CreateNatDexToName,
                CreateNameToNatDex,
                CreatePokemonNamesByDex,
                CreateDexEntries,
                CreateEggGroups,
                CreateFlavorTexts
        };
    }
}
