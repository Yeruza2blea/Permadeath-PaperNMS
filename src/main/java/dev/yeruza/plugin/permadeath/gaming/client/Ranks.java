package dev.yeruza.plugin.permadeath.gaming.client;

import dev.yeruza.plugin.permadeath.Permadeath;

import java.util.List;

public interface Ranks {
    PlayerRank OWNER = createRank("owner", 'd',List.of(""));
    PlayerRank ADMIN = createRank("admin", 'x', List.of(""));
    PlayerRank MOD = createRank("mod", 'z', List.of(""));
    PlayerRank VIP = createRank("vip", 'y', List.of(""));
    PlayerRank MEMBER_PLUS = createRank("member_plus", '0', List.of(""));
    PlayerRank MEMBER = createRank("member", 'j', List.of(" "));


    private static PlayerRank createRank(String id, char code, List<String> lore) {
        return new PlayerRank(Permadeath.withCustomNamespace(id), code, lore, List.of());
    }
}
