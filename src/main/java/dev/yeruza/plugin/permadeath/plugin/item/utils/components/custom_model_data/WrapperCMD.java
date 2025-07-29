package dev.yeruza.plugin.permadeath.plugin.item.utils.components.custom_model_data;

import org.bukkit.Color;

import java.util.List;

public class WrapperCMD {
    public static List<String> create(String ...values) {
        return List.of(values);
    }

    public static List<Float> create(Float ...values) {
        return List.of(values);
    }

    public static List<Boolean> create(Boolean ...values) {
        return List.of(values);
    }

    public static List<Color> create(Color ...values) {
        return List.of(values);
    }
}
