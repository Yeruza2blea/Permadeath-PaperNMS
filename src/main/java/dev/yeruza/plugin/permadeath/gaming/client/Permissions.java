package dev.yeruza.plugin.permadeath.gaming.client;

public enum Permissions  {
    ADMIN("admin"),
    MOD("mod"),
    USER("user"),
    FLY("special-rank","fly", false),
    BAN_PLAYER("staff","ban-player", true),
    KICK_PLAYER("staff","kick-player", true),
    TP_PLAYER("staff","tp-player", true),
    CHANGE_NICKNAME("user", "change-nickname", false),
    TALK_PRIVATE("user", "talk-private", false),
    MANAGE_NICKNAME("staff", "manage-nickname", true),
    MANAGE_SCOREBOARD("staff", "manage-scoreboard", true),
    MANAGE_TABLIST("staff","manage-tablist", true);

    private String perm;
    private String prefix;

    private String description;

    private boolean needOp;

    private String path;

    Permissions(String rank) {
        path = String.format("permadeath.%s", rank);
    }

    Permissions(String prefix, String perm, boolean needOp) {
        this.prefix = prefix;
        this.perm = perm;
        this.needOp = needOp;

        path = String.format("permadeath.%s.%s", prefix, perm);
    }

    public boolean isOp() {
        return needOp;
    }

    public String getPath() {
        return path;
    }
}
