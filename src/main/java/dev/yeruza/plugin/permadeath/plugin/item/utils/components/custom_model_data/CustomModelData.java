package dev.yeruza.plugin.permadeath.plugin.item.utils.components.custom_model_data;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import dev.yeruza.plugin.permadeath.plugin.BukkitBooster;

import java.util.List;

public record CustomModelData(List<String> strings, List<Float> floats, List<Boolean> flags, List<Color> colors) implements BukkitBooster<CustomModelDataComponent> {

    private static <E> List<E> validateAndDefault() {


        return List.of();
    }

    public CustomModelData(Class<?> type, Object ...models) {
        this();
    }

    public CustomModelData() {
        this(List.of(), List.of(), List.of(), List.of());
    }

    public CustomModelData addStrings(String ...strings) {
        this.strings.addAll(List.of(strings));
        return this;
    }

    public CustomModelData addStrings(List<String> strings) {
        this.strings.addAll(strings);
        return this;
    }

    public CustomModelData addFlags(Boolean ...flags) {
        this.flags.addAll(List.of(flags));

        return this;
    }

    public CustomModelData addFlags(List<Boolean> flags) {
        this.flags.addAll(flags);

        return this;
    }

    public CustomModelData addFloats(Float ...floats) {
        this.floats.addAll(List.of(floats));

        return this;
    }

    public CustomModelData addFloats(List<Float> floats) {
        this.floats.addAll(floats);

        return this;
    }

    public CustomModelData addColors(List<Color> colors) {
        this.colors.addAll(colors);

        return this;
    }

    @Override
    public CustomModelDataComponent getBukkit() {


        return Bukkit.getItemFactory().getItemMeta(null).getCustomModelDataComponent();
   }

}
