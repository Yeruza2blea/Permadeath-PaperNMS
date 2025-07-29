package dev.yeruza.plugin.permadeath.plugin.item.utils.components.consumable;


import dev.yeruza.plugin.permadeath.plugin.BukkitBooster;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;

public enum Animation implements BukkitBooster<ItemUseAnimation> {
    DRINK(ItemUseAnimation.DRINK),
    EAT(ItemUseAnimation.EAT),
    NONE(ItemUseAnimation.NONE),
    BLOCK(ItemUseAnimation.BLOCK),
    BOW(ItemUseAnimation.BOW),
    BRUSH(ItemUseAnimation.BRUSH),
    CROSSBOW(ItemUseAnimation.CROSSBOW),
    SPEAR(ItemUseAnimation.SPEAR),
    SPYGLASS(ItemUseAnimation.SPYGLASS),
    TOOT_HORN(ItemUseAnimation.TOOT_HORN);

    private final ItemUseAnimation animation;

    Animation(ItemUseAnimation animation) {
        this.animation = animation;
    }

    @Override
    public ItemUseAnimation getBukkit() {
        return animation;
    }
}
