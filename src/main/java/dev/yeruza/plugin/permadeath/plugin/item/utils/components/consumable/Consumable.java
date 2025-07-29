package dev.yeruza.plugin.permadeath.plugin.item.utils.components.consumable;

import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.keys.SoundEventKeys;
import org.bukkit.Sound;


import java.util.List;

public record Consumable(float seconds, Animation animation, TypedKey<Sound> eatSound, boolean hasConsumeParticles, List<ConsumeEffect> effects) {
    public static final float SECONDS = 3.0F;
    public static final TypedKey<Sound> EAT_SOUND = SoundEventKeys.ENTITY_GENERIC_EAT;

    public Consumable(List<ConsumeEffect> effects) {
        this(SECONDS, Animation.EAT, EAT_SOUND, true, effects);
    }

    public Consumable addSeconds(float seconds) {
        return new Consumable(seconds, animation, eatSound, hasConsumeParticles, effects);
    }

    public Consumable addAnimation(Animation animation) {
        return new Consumable(seconds, animation, eatSound, hasConsumeParticles, effects);
    }

    public Consumable addAnimation(TypedKey<Sound> eatSound) {
        return new Consumable(seconds, animation, eatSound, hasConsumeParticles, effects);
    }

    public Consumable addConsumeParticles() {
        return new Consumable(seconds, animation, eatSound, true, effects);
    }

    public Consumable addEffects(List<ConsumeEffect> effects) {
        return new Consumable(seconds, animation, eatSound, hasConsumeParticles, effects);
    }


}
