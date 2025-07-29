package dev.yeruza.plugin.permadeath.worlds.beginning;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import dev.yeruza.plugin.permadeath.plugin.item.PermadeathItems;

import java.util.*;

public class BeginningLootTable {
    private List<Integer> randomLoc = new ArrayList<>();
    private List<String> chances;
    private List<Material> rolled;
    private SplittableRandom random;

    private List<ItemStack> rolledLoot;
    private Map<ItemStack, String> chanceLoot;

    public BeginningLootTable() {
        for (int i = 0; i < 27; ++i)
            randomLoc.add(i);

        chanceLoot = new HashMap<>();
        rolledLoot = new ArrayList<>();
        chances = new ArrayList<>();
        rolled = new ArrayList<>();
        random = new SplittableRandom();


        addItem(Material.ENCHANTED_GOLDEN_APPLE,1, 1, 2);
        addItem(Material.GOLD_INGOT, 5, 50, 60);
        addItem(Material.GOLDEN_APPLE, 60, 1, 8);
        addItem(Material.DIAMOND, 60, 16, 24);
        addItem(Material.ARROW, 10, 10, 16);
        addItem(Material.FIREWORK_ROCKET, 20, 55, 64);
        addItem(Material.TOTEM_OF_UNDYING, 5, 1, 2);
        addItem(Material.STRUCTURE_VOID, 1, 1, 3);
        addItem(Material.EXPERIENCE_BOTTLE, 12, 9, 56);
        addItem(Material.NETHERITE_INGOT, 57, 16, 24);
        addItem(Material.ENDER_PEARL, 15, 2, 5);
        addItem(PermadeathItems.SUPER_GOLDEN_APPLE_PLUS, 5, 1, 3);
    }

    protected void populateChest(Chest chest, TheBeginningListener dimension) {
        World world = chest.getWorld();
        Inventory inventory = chest.getBlockInventory();
        if (!world.getPersistentDataContainer().has(dimension.getKey())) return;
        if (inventory.getViewers().isEmpty()) return;

        this.roll(chest);
    }

    public void addItem(ItemStack item, int chance, int min, int max) {
        chanceLoot.put(item, "|" + chance + "|" + min + "|" + max);
    }

    private void addItem(Material mat, int chance, int min, int max) {
        addItem(mat.asItemType().createItemStack(),chance, min, max);
    }

    public void roll(Chest c) {
        int rollTimes = random.nextInt(3) + 1;
        for (int i = 0; i < rollTimes; i++)
            generate(c);
    }

    private void generate(Chest chest) {
        int added;
        for (Map.Entry<ItemStack, String> map : chanceLoot.entrySet()) {
            String[] splitter = map.getValue().split("\\|");

            Inventory content = chest.getBlockInventory();

            ItemStack stack = map.getKey();

            added = 0;

            if ((random.nextInt(100) + 1) <= getChance(splitter) && !rolled.contains(parseMat(splitter))) {
                if (stack.getType() == Material.TOTEM_OF_UNDYING || stack.getType() == Material.STRUCTURE_VOID) {
                    content.setItem(randomLoc.get(added), new ItemStack(stack));
                    return;
                }
                int amount = generateValue(getMin(splitter), getMax(splitter));

                stack.setAmount(amount);
                ItemStack item = new ItemStack(stack);
                content.setItem(randomLoc.get(added), item);

                try {
                    int x = amount + getMin(splitter) / 2;
                    ItemStack item2 = new ItemStack(item.getType(), x);

                    int r = random.nextInt(5) + 1;
                    int slot = (random.nextBoolean() ? -1 : 1) * r;

                    content.setItem(randomLoc.get(added + slot), item2);
                } catch (Exception ignored) {}

                added++;
                if (added >= content.getSize() - 1)
                    break;

                rolledLoot.add(item);
            }
        }


        Iterator<String> iterator = chances.iterator();
        int added1;
        while (iterator.hasNext()) {
            String[] split = String.valueOf(iterator.next()).split("\\|");
            Inventory inventory = chest.getBlockInventory();

            Collections.shuffle(randomLoc);

            added1 = 0;
            if ((random.nextInt(100) + 1) <= getChance(split) && !rolled.contains(parseMat(split))) {
                if (parseMat(split) == Material.TOTEM_OF_UNDYING || parseMat(split) == Material.STRUCTURE_VOID) {
                    inventory.setItem(randomLoc.get(added1), new ItemStack(parseMat(split)));
                    return;
                }
                int amount = generateValue(getMin(split), getMax(split));
                ItemStack item = new ItemStack(parseMat(split), amount);
                inventory.setItem(randomLoc.get(added1), item);

                try {
                    int x = amount + getMin(split) / 2;
                    ItemStack item2 = new ItemStack(item.getType(), x);

                    int r = random.nextInt(5) + 1;
                    int slot = (random.nextBoolean() ? -1 : 1) * r;

                    inventory.setItem(randomLoc.get(added1 + slot), item2);
                } catch (Exception ignored) {}

                added1++;
                if (added1 >= inventory.getSize() - 1) break;

                rolled.add(item.getType());
            }
        }
    }

    private boolean hasSlot(Inventory inventory) {
        boolean active = false;
        for (int i = 0; i < inventory.getSize(); i++)
            if (inventory.getItem(i) == null)
                active = true;

        return active;
    }

    public void parse(String[] format) {
        int change = Integer.parseInt(format[1]);
        int min = Integer.parseInt(format[2]);
        int max = Integer.parseInt(format[3]);


    }

    private int getMin(String[] min) {
        return Integer.parseInt(min[2]);
    }

    private int getMax(String[] max) {
        return Integer.parseInt(max[3]);
    }

    private int getChance(String[] chance) {
        return Integer.parseInt(chance[1]);
    }

    private Material parseMatMap(ItemStack obj) {
        return  obj.getType();
    }

    private Material parseMat(String[] material) {
        return Material.valueOf(material[0]);
    }

    private int generateValue(int min, int max) {
        return random.nextInt(max - min) + random.nextInt(min) + 1;
    }
}
