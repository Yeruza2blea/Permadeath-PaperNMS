package dev.yeruza.plugin.permadeath.gaming.client;

import dev.yeruza.plugin.permadeath.Permadeath;

import java.util.List;

public record Permission(String permissionKey, boolean isOp, List<Permission> expansive) {

    public String getPath() {
        return Permadeath.PLUGIN_ID + "." + permissionKey;
    }
}
