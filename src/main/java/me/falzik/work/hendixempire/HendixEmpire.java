package me.falzik.work.hendixempire;

import me.falzik.work.hendixempire.game.Game;
import me.falzik.work.hendixempire.command.GameCommand;
import me.falzik.work.hendixempire.game.listener.GameListener;
import me.falzik.work.hendixempire.util.ChatUtil;
import me.falzik.work.hendixempire.util.ConfigManager;
import me.falzik.work.hendixempire.util.Hologram;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

public final class HendixEmpire extends JavaPlugin {

    private static HendixEmpire instance;

    private Game game;

    public static HendixEmpire getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        ConfigManager.instance.configSetup("game.yml", this);
        ConfigManager.instance.configSetup("messages.yml", this);

        saveDefaultConfig();

        ChatUtil.setPlugin(this);

        this.game = new Game();

        getServer().getPluginManager().registerEvents(new GameListener(), this);
        getCommand("game").setExecutor(new GameCommand());
    }

    public Game getGame() {
        return game;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Hologram.destroyAll();
    }
}
