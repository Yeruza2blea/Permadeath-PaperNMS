package dev.yeruza.plugin.permadeath.data;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.plugin.item.PermadeathItems;
import dev.yeruza.plugin.permadeath.plugin.item.armor.ArmorKit;
import dev.yeruza.plugin.permadeath.plugin.item.armor.ArmorKits;

import java.io.File;
import java.util.function.Consumer;

public class RecipeManager extends SettingsManager {
    private int day;

    public RecipeManager(Permadeath plugin, long day) {
        this(plugin);
        this.day = (int) day;
    }

    public RecipeManager(Permadeath plugin) {
        super(plugin, new File(plugin.getDataFolder(), "lang/server/recipes"));
    }

    public void registerRecipes() {
        try {
            registerSuperGoldenApplePlus();
            registerHyperGoldenApplePlus();
            registerShulkerBoxUncraft();
            registerEndRelic();
            registerNetheriteIngotPure();
        } catch (IllegalStateException ex) {

        }
    }

    public void registerRecipes(Integer day) {
        if (day == 40) {
            registerRecipe(PermadeathItems.END_RELIC, (recipe) -> {
                recipe.shape(" S ", " D ", " S ");
                recipe.setIngredient('S', Material.SHULKER_SHELL);
                recipe.setIngredient('D', Material.DIAMOND_BLOCK);
            });
            registerSuperGoldenApplePlus();
            registerHyperGoldenApplePlus();
            registerShulkerBoxUncraft();
            registerEndRelic();
            registerNetheriteIngotPure();
        }

        if (day == 60) {
            registerBeginningRelic();
            registerLifeOrb();
            registerInfernalNetheriteElytra();
        }
    }

    private void registerRecipe(ItemStack result, Consumer<ShapedRecipe> consumer) {

        
        ShapedRecipe recipe = new ShapedRecipe(Permadeath.withCustomNamespace(""), result);
        consumer.accept(recipe);
        plugin.getServer().addRecipe(recipe);
    }

    public ShapedRecipe createRecipe(String key, ItemStack item) {

        return new ShapedRecipe(Permadeath.withCustomNamespace(key), item);
    }

    private void registerInfernalNetheriteElytra() {
        NamespacedKey key = Permadeath.withCustomNamespace("infernal_netherite_elytra");
        SmithingTransformRecipe recipe = new SmithingTransformRecipe(
                key,
                PermadeathItems.INFERNAL_NETHERITE_ELYTRA,
                new RecipeChoice.MaterialChoice(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                new RecipeChoice.MaterialChoice(Material.ELYTRA),
                new RecipeChoice.ExactChoice(PermadeathItems.INFERNAL_NETHERITE_BLOCK)
        );

        plugin.getServer().addRecipe(recipe);
    }

    private void registerEndRelic() {
        NamespacedKey key = Permadeath.withCustomNamespace("end_relic");
        ShapedRecipe recipe = new ShapedRecipe(key, PermadeathItems.END_RELIC);

        recipe.shape(" S ", " D ", " S ");
        recipe.setIngredient('S', Material.SHULKER_SHELL);
        recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        plugin.getServer().addRecipe(recipe);
    }

    private void registerBeginningRelic() {
        NamespacedKey key = Permadeath.withCustomNamespace("beginning_relic");
        ShapedRecipe recipe = new ShapedRecipe(key, PermadeathItems.BEGINNING_RELIC);
        recipe.shape("SBS", "BDB", "SBS");
        recipe.setIngredient('B', Material.DIAMOND_BLOCK);
        recipe.setIngredient('D', new RecipeChoice.ExactChoice(PermadeathItems.END_RELIC));
        recipe.setIngredient('S', Material.SHULKER_SHELL);
        
        plugin.getServer().addRecipe(recipe);
    }

    private void registerInfiniteRelic() {
        NamespacedKey key = Permadeath.withDefaultNamespace("infinite_relic");
        ShapedRecipe recipe = new ShapedRecipe(key, PermadeathItems.INFINITE_RELIC);

        recipe.shape("QBQ", "SDS", "QBQ");
        recipe.setIngredient('Q', Material.NETHERITE_BLOCK);
        recipe.setIngredient('S', Material.SHULKER_BOX);

    }

    private void registerNetheriteIngotPure() {
        NamespacedKey key = Permadeath.withCustomNamespace("pure_netherite_ingot");
        ShapedRecipe recipe = new ShapedRecipe(key, PermadeathItems.PURE_NETHERITE_INGOT);
        recipe.shape("NNN", "BTN", "GGG");

        recipe.setIngredient('N', Material.NETHERITE_SCRAP);
        recipe.setIngredient('T', Material.TOTEM_OF_UNDYING);
        recipe.setIngredient('G', Material.GOLD_INGOT);
        plugin.getServer().addRecipe(recipe);
    }

    private void registerHyperGoldenApplePlus() {
        NamespacedKey key = Permadeath.withCustomNamespace("hyper_golden_apple_plus");
        ShapedRecipe recipe = new ShapedRecipe(key, PermadeathItems.HYPER_GOLDEN_APPLE_PLUS);
        recipe.shape("GGG", "GAG", "GGG");
        recipe.setIngredient('G', Material.GOLD_BLOCK);
        recipe.setIngredient('A', Material.GOLDEN_APPLE);

        plugin.getServer().addRecipe(recipe);

    }


    private void registerShulkerBoxUncraft() {
        NamespacedKey key = Permadeath.withCustomNamespace("shulker_box_uncraft");
        ShapelessRecipe recipe = new ShapelessRecipe(key, new ItemStack(Material.SHULKER_BOX, 2));

        plugin.getServer().addRecipe(recipe.addIngredient(Material.SHULKER_BOX));
    }

    private void registerSuperGoldenApplePlus() {
        NamespacedKey key = Permadeath.withCustomNamespace("super_golden_apple_plus");
        ShapedRecipe recipe = new ShapedRecipe(key, PermadeathItems.SUPER_GOLDEN_APPLE_PLUS);
        recipe.shape("GGG", "GAG", "GGG");
        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('A', Material.GOLDEN_APPLE);

        plugin.getServer().addRecipe(recipe);
    }

    private void registerLifeOrb() {
        NamespacedKey key = Permadeath.withCustomNamespace("life_orb");
        ShapedRecipe recipe = new ShapedRecipe(key, PermadeathItems.ORB_LIFE);

        recipe.shape("DGB", "RSE", "NOL");
        recipe.setIngredient('D', Material.DIAMOND);
        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('B', Material.BONE_BLOCK);
        recipe.setIngredient('R', Material.BLAZE_ROD);
        recipe.setIngredient('S', Material.HEART_OF_THE_SEA);
        recipe.setIngredient('E', Material.END_STONE);
        recipe.setIngredient('N', Material.NETHER_BRICKS);
        recipe.setIngredient('O', Material.OBSIDIAN);
        recipe.setIngredient('L', Material.LAPIS_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    private void registerAlmorityArmor() {
        ArmorPieces pieces = new ArmorPieces(ArmorKits.ALMORITY);
        pieces.setTemplate(PermadeathItems.ALMORITY_BLOCK);
                
        plugin.getServer().addRecipe(pieces.recipe);


    }

    protected void registerItemStack(NamespacedKey id) {

    }

    public static class ArmorPieces {
        private String name;
        private ItemStack armor;
        private SmithingTransformRecipe recipe;

        private ItemStack template;
        private ItemStack base;
        private ItemStack addition;

        public ArmorPieces(ArmorKit kit) {
        }


        public void setTemplate(@NotNull ItemStack template) {
            this.template = template;
        }

        public void setBase(@NotNull ItemStack base) {
            this.base = base;
        }

        public void setAddition(@NotNull ItemStack addition) {
            this.addition = addition;
        }
    }
}
