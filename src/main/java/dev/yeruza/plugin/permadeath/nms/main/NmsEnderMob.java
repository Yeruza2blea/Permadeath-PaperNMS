package dev.yeruza.plugin.permadeath.nms.main;

public interface NmsEnderMob {
    boolean teleport();


    enum Type {
        CREEPER,
        BLAZE;
    }
}
