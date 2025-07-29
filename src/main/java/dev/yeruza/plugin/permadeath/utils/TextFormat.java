package dev.yeruza.plugin.permadeath.utils;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.ScopedComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import dev.yeruza.plugin.permadeath.Permadeath;

import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class TextFormat {
    private static final LegacyComponentSerializer legacy = LegacyComponentSerializer.legacy('&');
    public static final Pattern HEX_COLOR_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");

    public static final String S_HEADER = "" + ChatColor.RESET + ChatColor.UNDERLINE + ChatColor.RESET;
    public static final String S_FOOTER = "" + ChatColor.RESET + ChatColor.ITALIC + ChatColor.RESET;

    public static Title createTitle(String title, String subTitle, long fadeIn, long stay, long fadeOut) {
        return Title.title(write(title), write(subTitle), Title.Times.times(Duration.ofSeconds(fadeIn), Duration.ofSeconds(stay), Duration.ofSeconds(fadeOut)));
    }

    public static Title createTitle(String title, String subTitle, Title.Times times) {
        return Title.title(write(title), write(subTitle), times);
    }

    public static Title createTitle(String title, String subTitle) {
        return Title.title(write(title), write(subTitle));
    }

    public static TextComponent withCodef(String content, Object... args) {
        return write(String.format(content, args));
    }

    public static TextComponent write(StringBuilder extra) {
        return write(extra.toString());
    }

    public static TextComponent write(String content) {
        TextComponent component = legacy.deserialize(content);

        return component;
    }

    public static TextComponent showWithPrefix(String message) {
        return write(Permadeath.showPrefix() + " " + message);
    }

    public static TextComponent showWithPrefixf(String message, Object ...args) {
        return withCodef(Permadeath.showPrefix() + " " + message, args);
    }

    public static List<TextComponent> withCodes(String ...args) {
        return Stream.of(args).map(TextFormat::write).toList();
    }

    public static TextComponent writef(String ...args) {
        List<TextComponent> text = Stream.of(args)
            .map(TextFormat::write)
            .map(ScopedComponent::appendNewline)
            .toList();

        return Component.textOfChildren(text.toArray(ComponentLike[]::new));
    }

    public static List<TextComponent> write(List<String> content) {
        return content.stream().map(TextFormat::write).toList();
    }

    public static Location parseCoords(String coords) {
        String[] format = coords.split("\\|");


        World world = Bukkit.getWorld(format[0]);
        double x = Double.parseDouble(format[1]);
        double y = Double.parseDouble(format[2]);
        double z = Double.parseDouble(format[3]);


        return new Location(world, x, y, z);
    }

    public static String parseLocation(Location coords) {
        return coords.getWorld().getName() + "|" + coords.getX() + "|" + coords.getY() + "|" + coords.getZ();
    }

    public static String parseTime(int secs) {
        if (secs < 0) return String.valueOf(secs);

        int remainder = secs % 86400;

        int days = secs / 86400;
        int hours = remainder / 3600;
        int minutes = (remainder / 60) - (hours * 60);
        int seconds = (remainder % 3600) - (minutes * 60);

        if (days > 0) {
            return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format("%dh %dm %ds",hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }

    private static long parse(String ms) {
        final int SECOND = 1000;
        final int MINUTE = SECOND * 60;
        final int HOUR = MINUTE * 60;
        final int DAY = HOUR * 24;
        final int WEEK = DAY * 7;
        final long YEAR = (DAY * (long) 325.5);
        // "^(-?(?:\d+)?\.?\d+) * (milliseconds?|msecs?|ms|seconds?|secs?|s|minutes?|mins?|m|hours?|hrs?|h|days?|d|weeks?|w|years?|yrs?|y)?$";
        /* (-?(?:\d+)?\.?\d+) */
        String regex = "^(-?(?:\\d+)?\\.?\\d+)*(milliseconds?|msecs?|ms?|seconds?|secs?|s|minutes?|mins?|m|hours?|hrs?|h|days?|d|weeks?|w|years?|yrs?|y)?$";

        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(ms);

        if (!match.find()) return 0;

        long num = Long.parseLong(match.group(1));
        String type = match.group(2).toLowerCase();

        return switch (type) {
            case "years", "year", "yrs", "yr", "y" -> num * YEAR;
            case "weeks", "week", "w" -> num * WEEK;
            case "days", "day", "d" -> num * DAY;
            case "hours", "hour", "hrs", "hr", "h" -> num * HOUR;
            case "minutes", "minute", "mins", "min", "m" -> num * MINUTE;
            case "seconds", "second", "secs", "sec", "s" -> num * SECOND;
            case "milliseconds", "millisecond", "msecs", "msec", "ms" -> num;
            default -> 0;
        };
    }

    public static String parseInterval(int time) {
        int hrs = time / 3600;
        int minAndSecs = time % 3600;
        int min = minAndSecs / 60;
        int sec = minAndSecs % 60;

        if (hrs > 0) {
            return String.format("%02d:%02d:%02d", hrs, min, sec);
        } else {
            return String.format("%02d:%02d", min, sec);
        }

    }
}
