package io.github.lucasstarsz.fxdex.misc;

import java.util.Map;

public class ApiConversionTables {

    public static final Map<String, Integer> RomanNumeralMap = Map.of(
            "i", 1,
            "ii", 2,
            "iii", 3,
            "iv", 4,
            "v", 5,
            "vi", 6,
            "vii", 7,
            "viii", 8,
            "ix", 9,
            "x", 10
    );

    public static final Map<String, String> PokedexNameMap = Map.ofEntries(
            Map.entry("national", "National Dex"),
            Map.entry("kanto", "Kanto (Red/Blue/Yellow/Green)"),
            Map.entry("original-johto", "Johto (Gold/Silver/Crystal)"),
            Map.entry("hoenn", "Hoenn (Ruby/Sapphire/Emerald)"),
            Map.entry("original-sinnoh", "Sinnoh (Diamond/Pearl)"),
            Map.entry("extended-sinnoh", "Sinnoh (Platinum)"),
            Map.entry("updated-johto", "Johto (HeartGold/SoulSilver/Crystal)"),
            Map.entry("original-unova", "Unova (Black/White)"),
            Map.entry("updated-unova", "Unova (Black2/White2)"),
            Map.entry("conquest-gallery", "Conquest (Pokemon Conquest)"),
            Map.entry("kalos-central", "Central Kalos (X/Y)"),
            Map.entry("kalos-coastal", "Coastal Kalos (X/Y)"),
            Map.entry("kalos-mountain", "Mountain Kalos (X/Y)"),
            Map.entry("updated-hoenn", "Hoenn (Omega Ruby/Alpha Sapphire)"),
            Map.entry("original-alola", "Alola (Sun/Moon)"),
            Map.entry("original-melemele", "Alola-Melemele (Sun/Moon)"),
            Map.entry("original-akala", "Alola-Akala (Sun/Moon)"),
            Map.entry("original-ulaula", "Alola-Ulaula (Sun/Moon)"),
            Map.entry("original-poni", "Alola-Poni (Sun/Moon)"),
            Map.entry("updated-alola", "Alola (Ultra Sun/Ultra Moon)"),
            Map.entry("updated-melemele", "Alola-Melemele (Ultra Sun/Ultra Moon)"),
            Map.entry("updated-akala", "Alola-Akala (Ultra Sun/Ultra Moon)"),
            Map.entry("updated-ulaula", "Alola-Ulaula (Ultra Sun/Ultra Moon)"),
            Map.entry("updated-poni", "Alola-Poni (Ultra Sun/Ultra Moon)"),
            Map.entry("letsgo-kanto", "Let's Go - Kanto (Let's go Pikachu/Eevee)"),
            Map.entry("galar", "Galar (Sword/Shield)"),
            Map.entry("isle-of-armor", "Galar - Isle of Armor (Sword/Shield DLC)"),
            Map.entry("crown-tundra", "Galar - Crown Tundra (Sword/Shield DLC)"),
            Map.entry("hisui", "Hisui (Legends: Arceus)"),
            Map.entry("paldea", "Paldea (Scarlet/Violet)"),
            Map.entry("kitakami", "Paldea - Kitakami (Scarlet/Violet DLC)"),
            Map.entry("blueberry", "Paldea - Blueberry (Scarlet/Violet DLC)")
    );
}
