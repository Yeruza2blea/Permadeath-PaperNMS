package dev.yeruza.plugin.permadeath.gaming.client;

import java.util.List;

public interface Medals {
    PlayerMedal SURVIVOR_MEDAL = new PlayerMedal("survivor_medal", "&2Medalla de superviviente", "&c&l☠", List.of("Se consigue sobreviviendo en el día 60"));
    PlayerMedal NEUTRALIZE_THE_BEAST = new PlayerMedal("neutrilize_the_Beast", "Neutralizar a la bestia", "&5&lX", List.of("Matar al Permadeath Demon (Parte 1)"));
    PlayerMedal ENDER_HUNTERS = new PlayerMedal("ender_hunters", "Cazador de Enders", "&6&o[]", List.of(" Matar todos los mobs ender de permadeath en el end"));
    PlayerMedal CHAD_MEDAL = new PlayerMedal("_medal", "Medalla", "", List.of());
    PlayerMedal NEW_BLOOD = new PlayerMedal("new_blood", "New Blood", "&c&l<>", List.of("Matar por segunda vez al Permadeath Demon"));
}
