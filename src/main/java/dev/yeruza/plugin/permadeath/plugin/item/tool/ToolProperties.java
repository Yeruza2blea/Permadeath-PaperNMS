package dev.yeruza.plugin.permadeath.plugin.item.tool;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import dev.yeruza.plugin.permadeath.plugin.item.ItemProperties;

import java.util.function.Function;

public class ToolProperties extends ItemProperties {
    public static ItemStack simple(Material tool) {
        return tool.asItemType().createItemStack();

    }

    public static ItemStack toolWithData(ItemStack tool, Function<ToolProperties, ItemProperties> context) {
        return toolWithData(tool.getType(), context);
    }

    public static ItemStack toolWithData(Material tool, Function<ToolProperties, ItemProperties> context) {
        return context.apply(new ToolProperties(tool))
                .build();
    }

    public ToolProperties(Material material) {
        super(material);
    }

    public ToolProperties(ToolKit kit, ToolType type, String name, float damage, float cooldown) {
        super(type.getMaterial());

        switch (type) {
            case SWORD -> kit.createSwordAttributes(this, name, damage, cooldown);
            case AXE, PICKAXE, SHOVEL, HOE -> kit.createToolAttributes(this, name, damage, cooldown, 0.0F);
        }
    }
}
