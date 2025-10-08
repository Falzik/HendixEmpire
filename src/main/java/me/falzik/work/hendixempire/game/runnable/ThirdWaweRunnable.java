package me.falzik.work.hendixempire.game.runnable;

import me.falzik.work.hendixempire.util.ConfigManager;
import me.falzik.work.hendixempire.game.Game;
import me.falzik.work.hendixempire.game.Wawe;
import org.bukkit.scheduler.BukkitRunnable;

public class ThirdWaweRunnable extends BukkitRunnable {

    private final Game game;
    private int timer = ConfigManager.instance.getConfig("game.yml").getInt("wawe.third.time");

    public ThirdWaweRunnable(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        if (game.getWawe() != Wawe.THIRD) {
            cancel();
            return;
        }
        if (timer-- <= 0) {
            game.spawnChest();
            cancel();
        }
        if (game.getEntities().isEmpty()) {
            game.spawnWawe(Wawe.THIRD);
        }
    }
}
