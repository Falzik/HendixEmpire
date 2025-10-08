package me.falzik.work.hendixempire.game.runnable;

import me.falzik.work.hendixempire.game.Game;
import me.falzik.work.hendixempire.game.Wawe;
import me.falzik.work.hendixempire.util.ConfigManager;
import org.bukkit.scheduler.BukkitRunnable;

public class FirstWaweRunnable extends BukkitRunnable {

    private final Game game;

    private int timer = ConfigManager.instance.getConfig("game.yml").getInt("wawe.first.time");

    public FirstWaweRunnable(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        if (game.getWawe() != Wawe.FIRST) {
            cancel();
            return;
        }
        if (timer-- <= 0) {
            game.setWawe(Wawe.SECOND);
            cancel();
        }

        if (game.getEntities().isEmpty()) {
            game.spawnWawe(Wawe.FIRST);
        }
    }
}
