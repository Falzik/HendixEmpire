package me.falzik.work.hendixempire.game.listener;

import me.falzik.work.hendixempire.HendixEmpire;
import me.falzik.work.hendixempire.game.Game;
import me.falzik.work.hendixempire.util.ChatUtil;
import me.falzik.work.hendixempire.util.ConfigManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;


public class GameListener implements Listener {

    @EventHandler
    public void on(EntityDamageByEntityEvent e) {
        final Entity entity = e.getDamager();
        final Game game = HendixEmpire.getInstance().getGame();

        if(game.getEntities().contains(entity)) {
            double multiply = ConfigManager.instance.getConfig("game.yml").getDouble("wawe." +
                    game.getWawe().name().toLowerCase() + ".mob_damage_multiply");
            e.setDamage(e.getDamage() * multiply);
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        HendixEmpire.getInstance().getGame().getEntities().remove(e.getEntity());
    }

    @EventHandler
    public void on(InventoryOpenEvent e) {
        final Player player = (Player) e.getPlayer();
        final Inventory inventory = e.getInventory();

        if(HendixEmpire.getInstance().getGame().getChest().getBlockInventory().equals(inventory)) {
            boolean isOpen = HendixEmpire.getInstance().getGame().getChest().getMetadata("isOpen").get(0).asBoolean();

            if(!isOpen) {
                e.setCancelled(true);
                ChatUtil.sendMessage(player, ConfigManager.instance.getConfig("messages.yml").getString("chest.not_open").replace("%prefix%",
                        ConfigManager.instance.getConfig("messages.yml").getString("prefix")));
            }
        }
    }

    @EventHandler
    public void onCombust(EntityCombustEvent e) {
        final Game game = HendixEmpire.getInstance().getGame();
        if (game.getEntities().contains(e.getEntity())) {
            e.setCancelled(true);
        }
    }

}
