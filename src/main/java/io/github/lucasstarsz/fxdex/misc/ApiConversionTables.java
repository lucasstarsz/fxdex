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

    public static final String National = "national";
    public static final String Kanto = "kanto";
    public static final String OriginalJohto = "original-johto";
    public static final String Hoenn = "hoenn";
    public static final String OriginalSinnoh = "original-sinnoh";
    public static final String ExtendedSinnoh = "extended-sinnoh";
    public static final String UpdatedJohto = "updated-johto";
    public static final String OriginalUnova = "original-unova";
    public static final String UpdatedUnova = "updated-unova";
    public static final String ConquestGallery = "conquest-gallery";
    public static final String KalosCentral = "kalos-central";
    public static final String KalosCoastal = "kalos-coastal";
    public static final String KalosMountain = "kalos-mountain";
    public static final String UpdatedHoenn = "updated-hoenn";
    public static final String OriginalAlola = "original-alola";
    public static final String OriginalMelemele = "original-melemele";
    public static final String OriginalAkala = "original-akala";
    public static final String OriginalUlaula = "original-ulaula";
    public static final String OriginalPoni = "original-poni";
    public static final String UpdatedAlola = "updated-alola";
    public static final String UpdatedMelemele = "updated-melemele";
    public static final String UpdatedAkala = "updated-akala";
    public static final String UpdatedUlaula = "updated-ulaula";
    public static final String UpdatedPoni = "updated-poni";
    public static final String LetsGoKanto = "letsgo-kanto";
    public static final String Galar = "galar";
    public static final String IsleOfArmor = "isle-of-armor";
    public static final String CrownTundra = "crown-tundra";
    public static final String Hisui = "hisui";
    public static final String Paldea = "paldea";
    public static final String Kitakami = "kitakami";
    public static final String Blueberry = "blueberry";

    public static final Map<String, String> DexNameMap = Map.ofEntries(
            Map.entry(National, "National Dex"),
            Map.entry(Kanto, "Kanto (Red/Blue/Yellow/Green)"),
            Map.entry(OriginalJohto, "Johto (Gold/Silver/Crystal)"),
            Map.entry(Hoenn, "Hoenn (Ruby/Sapphire/Emerald)"),
            Map.entry(OriginalSinnoh, "Sinnoh (Diamond/Pearl)"),
            Map.entry(ExtendedSinnoh, "Sinnoh (Platinum)"),
            Map.entry(UpdatedJohto, "Johto (HeartGold/SoulSilver/Crystal)"),
            Map.entry(OriginalUnova, "Unova (Black/White)"),
            Map.entry(UpdatedUnova, "Unova (Black2/White2)"),
            Map.entry(ConquestGallery, "Conquest (Pokemon Conquest)"),
            Map.entry(KalosCentral, "Central Kalos (X/Y)"),
            Map.entry(KalosCoastal, "Coastal Kalos (X/Y)"),
            Map.entry(KalosMountain, "Mountain Kalos (X/Y)"),
            Map.entry(UpdatedHoenn, "Hoenn (Omega Ruby/Alpha Sapphire)"),
            Map.entry(OriginalAlola, "Alola (Sun/Moon)"),
            Map.entry(OriginalMelemele, "Alola-Melemele (Sun/Moon)"),
            Map.entry(OriginalAkala, "Alola-Akala (Sun/Moon)"),
            Map.entry(OriginalUlaula, "Alola-Ulaula (Sun/Moon)"),
            Map.entry(OriginalPoni, "Alola-Poni (Sun/Moon)"),
            Map.entry(UpdatedAlola, "Alola (Ultra Sun/Ultra Moon)"),
            Map.entry(UpdatedMelemele, "Alola-Melemele (Ultra Sun/Ultra Moon)"),
            Map.entry(UpdatedAkala, "Alola-Akala (Ultra Sun/Ultra Moon)"),
            Map.entry(UpdatedUlaula, "Alola-Ulaula (Ultra Sun/Ultra Moon)"),
            Map.entry(UpdatedPoni, "Alola-Poni (Ultra Sun/Ultra Moon)"),
            Map.entry(LetsGoKanto, "Let's Go - Kanto (Let's go Pikachu/Eevee)"),
            Map.entry(Galar, "Galar (Sword/Shield)"),
            Map.entry(IsleOfArmor, "Galar - Isle of Armor (Sword/Shield DLC)"),
            Map.entry(CrownTundra, "Galar - Crown Tundra (Sword/Shield DLC)"),
            Map.entry(Hisui, "Hisui (Legends: Arceus)"),
            Map.entry(Paldea, "Paldea (Scarlet/Violet)"),
            Map.entry(Kitakami, "Paldea-Kitakami (Scarlet/Violet DLC)"),
            Map.entry(Blueberry, "Paldea-Blueberry (Scarlet/Violet DLC)")
    );

    public static final Map<String, String> DexUiToApiNameMap = Map.ofEntries(
            Map.entry("National Dex", National),
            Map.entry("Kanto (Red/Blue/Yellow/Green)", Kanto),
            Map.entry("Johto (Gold/Silver/Crystal)", OriginalJohto),
            Map.entry("Hoenn (Ruby/Sapphire/Emerald)", Hoenn),
            Map.entry("Sinnoh (Diamond/Pearl)", OriginalSinnoh),
            Map.entry("Sinnoh (Platinum)", ExtendedSinnoh),
            Map.entry("Johto (HeartGold/SoulSilver/Crystal)", UpdatedJohto),
            Map.entry("Unova (Black/White)", OriginalUnova),
            Map.entry("Unova (Black2/White2)", UpdatedUnova),
            Map.entry("Conquest (Pokemon Conquest)", ConquestGallery),
            Map.entry("Central Kalos (X/Y)", KalosCentral),
            Map.entry("Coastal Kalos (X/Y)", KalosCoastal),
            Map.entry("Mountain Kalos (X/Y)", KalosMountain),
            Map.entry("Hoenn (Omega Ruby/Alpha Sapphire)", UpdatedHoenn),
            Map.entry("Alola (Sun/Moon)", OriginalAlola),
            Map.entry("Alola-Melemele (Sun/Moon)", OriginalMelemele),
            Map.entry("Alola-Akala (Sun/Moon)", OriginalAkala),
            Map.entry("Alola-Ulaula (Sun/Moon)", OriginalUlaula),
            Map.entry("Alola-Poni (Sun/Moon)", OriginalPoni),
            Map.entry("Alola (Ultra Sun/Ultra Moon)", UpdatedAlola),
            Map.entry("Alola-Melemele (Ultra Sun/Ultra Moon)", UpdatedMelemele),
            Map.entry("Alola-Akala (Ultra Sun/Ultra Moon)", UpdatedAkala),
            Map.entry("Alola-Ulaula (Ultra Sun/Ultra Moon)", UpdatedUlaula),
            Map.entry("Alola-Poni (Ultra Sun/Ultra Moon)", UpdatedPoni),
            Map.entry("Let's Go - Kanto (Let's go Pikachu/Eevee)", LetsGoKanto),
            Map.entry("Galar (Sword/Shield)", Galar),
            Map.entry("Galar - Isle of Armor (Sword/Shield DLC)", IsleOfArmor),
            Map.entry("Galar - Crown Tundra (Sword/Shield DLC)", CrownTundra),
            Map.entry("Hisui (Legends: Arceus)", Hisui),
            Map.entry("Paldea (Scarlet/Violet)", Paldea),
            Map.entry("Paldea-Kitakami (Scarlet/Violet DLC)", Kitakami),
            Map.entry("Paldea-Blueberry (Scarlet/Violet DLC)", Blueberry)
    );

    public static final Map<String, Integer> DexNameToIdMap = Map.ofEntries(
            // the PokéAPI pokédex list starts at index 1
            Map.entry(National, 1),
            Map.entry(Kanto, 2),
            Map.entry(OriginalJohto, 3),
            Map.entry(Hoenn, 4),
            Map.entry(OriginalSinnoh, 5),
            Map.entry(ExtendedSinnoh, 6),
            Map.entry(UpdatedJohto, 7),
            Map.entry(OriginalUnova, 8),
            Map.entry(UpdatedUnova, 9),
            // at index nine of the PokéAPI JSON, the dex number increases its offset by 1
            // https://pokeapi.co/api/v2/pokedex/?offset=0&limit=32
            Map.entry(ConquestGallery, 11),
            Map.entry(KalosCentral, 12),
            Map.entry(KalosCoastal, 13),
            Map.entry(KalosMountain, 14),
            Map.entry(UpdatedHoenn, 15),
            Map.entry(OriginalAlola, 16),
            Map.entry(OriginalMelemele, 17),
            Map.entry(OriginalAkala, 18),
            Map.entry(OriginalUlaula, 19),
            Map.entry(OriginalPoni, 20),
            Map.entry(UpdatedAlola, 21),
            Map.entry(UpdatedMelemele, 22),
            Map.entry(UpdatedAkala, 23),
            Map.entry(UpdatedUlaula, 24),
            Map.entry(UpdatedPoni, 25),
            Map.entry(LetsGoKanto, 26),
            Map.entry(Galar, 27),
            Map.entry(IsleOfArmor, 28),
            Map.entry(CrownTundra, 29),
            Map.entry(Hisui, 30),
            Map.entry(Paldea, 31),
            Map.entry(Kitakami, 32),
            Map.entry(Blueberry, 33)
    );
}
