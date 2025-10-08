package me.falzik.work.hendixempire.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.logging.Logger;

public class ChatUtil {

    private static String prefix;
    private static String errorPrefix;

    // Setup plugin need if you use util for config, or you need to get logger
    private static JavaPlugin plugin;

    public static String translateCodes(String value) {
        return ChatColor.translateAlternateColorCodes('&', value);
    }

    public static void sendMessage(CommandSender sender, String message) {
        if(prefix != null) {
            sender.sendMessage(translateCodes(prefix + message));
        } else {
            sender.sendMessage(translateCodes(message));
        }
    }

    public static void sendErrorMessage(CommandSender sender, String message) {
        if(errorPrefix != null) {
            sender.sendMessage(translateCodes(errorPrefix + message));
        } else {
            sender.sendMessage(translateCodes(message));
            System.out.println(message + " (prefix is null!)");
        }
    }

    /**
     * i = start time title
     * i2 = all time title
     * i3 = end title time
     */
    public static void sendTitle(Player player, String title, String subTitle, int i, int i2, int i3) {
        player.sendTitle(title, subTitle, i, i2, i3);
    }

    public static void sendTitle(Player player, String title, String subTitle) {
        player.showTitle(Title.title(Component.text(translateCodes(title)), Component.text(translateCodes(subTitle))));
    }

    public static void sendActionBar(Player player, String value) {
        player.sendActionBar(Component.text(translateCodes(value)));
    }

    public static void setPrefix(String prefix) {
        ChatUtil.prefix = translateCodes(prefix);
    }

    public static void setErrorPrefix(String errorPrefix) {
        ChatUtil.errorPrefix = translateCodes(errorPrefix);
    }

    public static Logger getLogger() {
        return plugin.getLogger();
    }

    public static void setPlugin(JavaPlugin plugin) {
        ChatUtil.plugin = plugin;
    }

    public static Object get(String path) {
        if(plugin == null) return null;
        return plugin.getConfig().get(path);
    }

    public static Object get(String configName, String path) {
        if(plugin == null) return null;
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/", path);
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        return configuration.get(path);
    }

    public static void set(String path, Object object) {
        if(plugin == null) return;
        plugin.getConfig().set(path, object);
    }

    public static void set(String configName, String path, Object object) {
        if(plugin == null) return;
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/", configName);
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        yamlConfiguration.set(path, object);
        try {
            yamlConfiguration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
