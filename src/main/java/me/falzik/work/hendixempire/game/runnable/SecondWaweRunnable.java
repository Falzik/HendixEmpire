package me.falzik.work.hendixempire.game.runnable;

import me.falzik.work.hendixempire.util.ConfigManager;
import me.falzik.work.hendixempire.game.Game;
import me.falzik.work.hendixempire.game.Wawe;
import org.bukkit.scheduler.BukkitRunnable;

public class SecondWaweRunnable extends BukkitRunnable {

    private final Game game;
    private int timer = ConfigManager.instance.getConfig("game.yml").getInt("wawe.second.time");

    public SecondWaweRunnable(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        if (game.getWawe() != Wawe.SECOND) {
            cancel();
            return;
        }
        if (timer-- <= 0) {
            game.setWawe(Wawe.THIRD);
            cancel();
        }
        if (game.getEntities().isEmpty()) {
            game.spawnWawe(Wawe.SECOND);
        }
    }
}
