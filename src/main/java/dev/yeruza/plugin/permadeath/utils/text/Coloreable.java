package dev.yeruza.plugin.permadeath.utils.text;

import me.clip.placeholderapi.libs.kyori.adventure.text.format.NamedTextColor;

public class Coloreable {
    private final int rgb;
    private final String name;

    public static final Coloreable BLACK = new Coloreable("black", 0);
    public static final Coloreable DARK_BLUE = new Coloreable("dark_blue", 170);
    public static final Coloreable DARK_GREEN = new Coloreable("dark_green", 43520);
    public static final Coloreable DARK_AQUA = new Coloreable("dark_aqua", 43690);
    public static final Coloreable DARK_RED = new Coloreable("dark_red", 11141120);
    public static final Coloreable DARK_PURPLE = new Coloreable("dark_purple", 11141290);
    public static final Coloreable GOLD = new Coloreable("gold", 16755200);
    public static final Coloreable GRAY = new Coloreable("gray", 11184810);
    public static final Coloreable DARK_GRAY = new Coloreable("dark_gray", 5592405);
    public static final Coloreable BLUE = new Coloreable("blue", 5592575);
    public static final Coloreable GREEN = new Coloreable("green", 5635925);
    public static final Coloreable AQUA = new Coloreable("aqua", 5636095);
    public static final Coloreable RED = new Coloreable("red", 16733525);
    public static final Coloreable LIGHT_PURPLE = new Coloreable("light_purple", 16733695);
    public static final Coloreable YELLOW = new Coloreable("yellow", 16777045);
    public static final Coloreable WHITE = new Coloreable("white", 16777215);

    public Coloreable(String name, int rgb) {
        this.name = name;
        this.rgb = rgb;

        NamedTextColor color;
    }
}
