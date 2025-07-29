package dev.yeruza.plugin.permadeath.plugin.item.utils.components;

import net.minecraft.world.food.FoodProperties;

public record Food(int nutrition, float saturation, boolean canAlwaysEat) {
    static FoodProperties properties;

    public Food(int nutrition, float saturation) {
        this(nutrition, saturation, false);
    }
}
