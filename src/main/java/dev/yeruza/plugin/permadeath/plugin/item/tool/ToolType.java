package dev.yeruza.plugin.permadeath.plugin.item.tool;

import org.bukkit.Material;

public enum ToolType {
    SWORD(Material.NETHERITE_SWORD, "sword", 1),
    AXE(Material.NETHERITE_AXE, "axe", 2),
    PICKAXE(Material.NETHERITE_PICKAXE, "pickaxe", 3),
    SHOVEL(Material.NETHERITE_SHOVEL, "shovel", 4),
    HOE(Material.NETHERITE_HOE, "hoe", 5);

    private final Material type;
    private final String name;
    private int pos;


    ToolType(Material type, String name) {
        this.type = type;
        this.name = name;
    }

    ToolType(Material type, String name, int pos) {
        this.type = type;
        this.name = name;
        this.pos = pos;
    }

    public String getId() {
        return name;
    }

    public Material getMaterial() {
        return type;
    }


    public int getPos() {
        return pos - 1;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
