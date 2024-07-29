package io.github.lucasstarsz.fxdex.database;

public class DatabaseSetup {

    public static final String CreateDexes = """
            CREATE TABLE IF NOT EXISTS `dexes` (
                `apiName` tinytext PRIMARY KEY NOT NULL,
                `uiName` tinytext NOT NULL,
                `apiUrl` tinytext NOT NULL
            );""";

    public static final String CreateDexPokemon = """
            CREATE TABLE IF NOT EXISTS `dexPokemon` (
                `nationalDexNumber` int PRIMARY KEY NOT NULL,
                `apiDexName` tinytext UNIQUE NOT NULL
            );""";

    public static final String CreatePokemonNamesToDex = """
            CREATE TABLE IF NOT EXISTS `pokemonNamesToDex` (
                `apiDexName` tinytext PRIMARY KEY NOT NULL,
                `nationalDexNumber` int UNIQUE NOT NULL,
                FOREIGN KEY (`apiDexName`) REFERENCES `dexPokemon` (`apiDexName`),
                FOREIGN KEY (`nationalDexNumber`) REFERENCES `dexPokemon` (`nationalDexNumber`)
            );""";

    public static final String CreateDexEntries = """
            CREATE TABLE IF NOT EXISTS `dexEntries` (
                `nationalDexNumber` int PRIMARY KEY NOT NULL,
                `name` tinytext NOT NULL,
                `speciesUrl` tinytext NOT NULL,
                `genus` tinytext NOT NULL,
                `generation` int NOT NULL,
                FOREIGN KEY (`nationalDexNumber`) REFERENCES `dexPokemon` (`nationalDexNumber`),
                FOREIGN KEY (`name`) REFERENCES `dexPokemon` (`apiDexName`)
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
                FOREIGN KEY (`region`) REFERENCES `dexes` (`apiName`)
            );""";

    public static String[] getStartupStatements() {
        return new String[]{
                CreateDexes,
                CreateDexPokemon,
                CreatePokemonNamesToDex,
                CreateDexEntries,
                CreateEggGroups,
                CreateFlavorTexts
        };
    }
}
