package dev.yeruza.plugin.permadeath.gaming.achievements;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import dev.yeruza.plugin.permadeath.Permadeath;

import java.util.UUID;

public final class Achievement {
    private NamespacedKey key;
    private String icon;
    private String message;
    private Style style;

    private Player player;

    public Achievement(Permadeath plugin, Player player, ItemStack icon, String message, Style style) {
        this.key = new NamespacedKey(plugin, UUID.randomUUID().toString());
        this.icon = icon.getItemMeta().getCustomModelDataComponent().getStrings().getFirst();
        this.message = message;
        this.style = style;

        this.player = player;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {

        }, 10L);
    }

    public Achievement(Permadeath plugin, Player player, String icon, String message, Style style) {
        this.key = new NamespacedKey(plugin, UUID.randomUUID().toString());
        this.icon = icon;
        this.message = message;
        this.style = style;

        this.player = player;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {

        }, 10L);
    }

    public void createAdvancement() {
        JsonObject advancement = new JsonObject();
        JsonObject criteriaData = new JsonObject();
        JsonObject triggersimple = new JsonObject();

        triggersimple.addProperty("trigger","minecraft:impossible");
        criteriaData.add("trigger", triggersimple);


        JsonObject iconData = new JsonObject();
        iconData.addProperty("item", NamespacedKey.minecraft(icon).toString());

        JsonObject titleData = new JsonObject();
        titleData.addProperty("text", message.replace('|', '\n'));

        JsonObject descriptionData = new JsonObject();
        descriptionData.addProperty("text", "");

        JsonObject displayData = new JsonObject();
        displayData.addProperty("background", "minecraft:textures/gui/advancements/backgrounds/adventure.png");
        displayData.add("icon", iconData);
        displayData.add("title", titleData);
        displayData.add("description", descriptionData);
        displayData.addProperty("frame", style.name().toLowerCase());
        displayData.addProperty("announce_to_chat", false);
        displayData.addProperty("show_toast", true);
        displayData.addProperty("hidden", true);

        JsonArray arrayData = new JsonArray();
        JsonArray triggerArr = new JsonArray();
        triggerArr.add("trigger");
        arrayData.add(triggerArr);

        advancement.add("criteria", criteriaData);
        advancement.add("display", displayData);
        advancement.add("requeriments", arrayData);

        Bukkit.getUnsafe().loadAdvancement(key, advancement.toString());
    }

    public void grantAdvancement(Player player) {
        player.getAdvancementProgress(Bukkit.getAdvancement(key)).awardCriteria("trigger");
    }

    public void revokeAdvancement(Player player) {
        player.getAdvancementProgress(Bukkit.getAdvancement(key)).revokeCriteria("trigger");
    }

    public enum Style {
        GOAL,
        TASK,
        CHALLENGE;
    }
}
