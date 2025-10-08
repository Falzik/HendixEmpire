package me.falzik.work.hendixempire.game;

import me.falzik.work.hendixempire.util.ConfigManager;
import me.falzik.work.hendixempire.util.ChatUtil;
import me.falzik.work.hendixempire.HendixEmpire;
import me.falzik.work.hendixempire.game.runnable.FirstWaweRunnable;
import me.falzik.work.hendixempire.game.runnable.SecondWaweRunnable;
import me.falzik.work.hendixempire.game.runnable.ThirdWaweRunnable;
import me.falzik.work.hendixempire.util.Hologram;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Game {

    private final YamlConfiguration messagesConfig = ConfigManager.instance.getConfig("messages.yml");
    private final YamlConfiguration gameConfig = ConfigManager.instance.getConfig("game.yml");
    private final FileConfiguration config = HendixEmpire.getInstance().getConfig();

    private Wawe wawe;

    private final String prefix = messagesConfig.getString("prefix");

    private final List<Entity> entities = new ArrayList<>();

    private Location location;

    private boolean active = false;
    private Chest chest;

    public Game() {
    }

    public void startPreGame() {
        active = true;
        Bukkit.getOnlinePlayers().forEach(player -> {
            ChatUtil.sendMessage(player, messagesConfig.getString("start_game.pre_start")
                    .replace("%prefix%", prefix));
            player.playSound(player.getLocation(), Sound.valueOf(messagesConfig.getString("start_game.pre_start_sound")), 1f, 1f);
        });

        new BukkitRunnable() {
            int timer = messagesConfig.getInt("start_game.time_for_start");
            int firstMessageTimer = messagesConfig.getInt("event.mysterious_sound.time");
            int secondMessageTimer = messagesConfig.getInt("event.strange_sound.time");
            @Override
            public void run() {
                if(timer-- <= 0) {
                    startGame();
                    cancel();
                } else if(firstMessageTimer-- == 0) {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        ChatUtil.sendMessage(player, messagesConfig.getString("event.mysterious_sound.message"));
                    });
                } else if(secondMessageTimer-- == 0) {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        ChatUtil.sendMessage(player, messagesConfig.getString("event.strange_sound.message"));
                    });
                }
            }
        }.runTaskTimer(HendixEmpire.getInstance(), 0L, 20L);
    }

    public void startGame() {
        final World world = Bukkit.getWorld(gameConfig.getString("world"));
        final int radius = config.getInt("search-radius");

        Random random = new Random();
        int x = random.nextInt(radius * 2) - radius;
        int z = random.nextInt(radius * 2) - radius;
        int y = world.getHighestBlockYAt(x, z);

        location = new Location(world, x + 0.5, y + 1, z + 0.5);

        String message = messagesConfig.getString("start_game.start_game")
                .replace("%prefix%", prefix)
                .replace("%x%", String.valueOf(x))
                .replace("%y%", String.valueOf(y))
                .replace("%z%", String.valueOf(z));
        Bukkit.getOnlinePlayers().forEach(p -> {
            ChatUtil.sendMessage(p, message);
            p.playSound(p.getLocation(), Sound.valueOf(messagesConfig.getString("start_game.pre_start_sound")), 1f, 1f);
        });

        setWawe(Wawe.FIRST);
    }

    public Wawe getWawe() {
        return wawe;
    }

    public void setWawe(Wawe wawe) {
        this.wawe = wawe;
        entities.clear();

        switch (wawe) {
            case FIRST:
                spawnWawe(wawe);
                new FirstWaweRunnable(this).runTaskTimer(HendixEmpire.getInstance(), 0L, 20L);
                Bukkit.getOnlinePlayers().forEach(player -> {
                    ChatUtil.sendMessage(player, messagesConfig.getString("wawe.first").replace("%prefix%",
                            messagesConfig.getString("prefix")));
                });
                break;
            case SECOND:
                spawnWawe(wawe);
                new SecondWaweRunnable(this).runTaskTimer(HendixEmpire.getInstance(), 0L, 20L);
                Bukkit.getOnlinePlayers().forEach(player -> {
                    ChatUtil.sendMessage(player, messagesConfig.getString("wawe.second").replace("%prefix%",
                            messagesConfig.getString("prefix")));
                });
                break;
            case THIRD:
                spawnWawe(wawe);
                new ThirdWaweRunnable(this).runTaskTimer(HendixEmpire.getInstance(), 0L, 20L);
                Bukkit.getOnlinePlayers().forEach(player -> {
                    ChatUtil.sendMessage(player, messagesConfig.getString("wawe.third").replace("%prefix%",
                            messagesConfig.getString("prefix")));
                });
                break;
        }
    }

    public void spawnWawe(Wawe wawe) {
        String basePath = "wawe." + wawe.name().toLowerCase() + ".";
            ConfigurationSection waweSection = gameConfig.getConfigurationSection(basePath);
            if (waweSection == null) {
                return;
            }
            double healthMultiply = waweSection.getDouble("mob_health_multiply");
            double speedMultiply = waweSection.getDouble("mob_speed_multiply");
            List<Map<?, ?>> mobs = waweSection.getMapList("mobs");
            for (Map<?, ?> map : mobs) {
                Object typeObj = map.get("type");
                if (typeObj == null) continue;
                int count = map.get("count") instanceof Number ? ((Number) map.get("count")).intValue() : 1;
                EntityType type;
                try {
                    type = EntityType.valueOf(typeObj.toString().toUpperCase());
                } catch (IllegalArgumentException ex) {
                    continue;
                }
                for (int i = 0; i < count; i++) {
                    Entity entity = location.getWorld().spawnEntity(location, type);
                    if (entity instanceof Zombie zombie) {
                        zombie.setShouldBurnInDay(false);
                    } else if (entity instanceof Skeleton skeleton) {
                        skeleton.setShouldBurnInDay(false);
                    }
                    if (entity instanceof LivingEntity living) {
                        AttributeInstance maxHealth = living.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                        AttributeInstance maxSpeed = living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                        if (maxHealth != null && maxSpeed != null) {
                            maxHealth.setBaseValue(maxHealth.getBaseValue() * healthMultiply);
                            living.setHealth(maxHealth.getBaseValue());

                            maxSpeed.setBaseValue(maxHealth.getBaseValue() * speedMultiply);
                        }
                    }
                    entities.add(entity);
                }
            }
        }

    public void spawnChest() {
        Block block = location.getBlock();

        ConfigurationSection raritiesSection = gameConfig.getConfigurationSection("chest.rarities");
        if (raritiesSection == null || raritiesSection.getKeys(false).isEmpty()) {
            block.setType(Material.AIR);
            return;
        }

        block.setType(Material.CHEST);

        if(block.getState() instanceof Chest chest) {
            this.chest = chest;
        }

        block.setMetadata("isOpen", new FixedMetadataValue(HendixEmpire.getInstance(), false));

        int totalSeconds = config.getInt("chest-timer", 600);

        Hologram holo = new Hologram(
                List.of(String.format("%d:%02d", totalSeconds / 60, totalSeconds % 60)),
                location.clone().add(0, 1.3, 0)
        );

        new BukkitRunnable() {
            int timeLeft = totalSeconds;

            @Override
            public void run() {
                if (timeLeft <= 0) {
                    holo.destroy();
                    block.setMetadata("isOpen", new FixedMetadataValue(HendixEmpire.getInstance(), true));
                    active = false;
                    cancel();
                    return;
                }

                String timeFormatted = String.format("%02d:%02d", timeLeft / 60, timeLeft % 60);
                holo.setText(List.of(timeFormatted));

                Location center = block.getLocation().add(0.5, 0.5, 0.5);

                double offset = 0.7;
                Location left = center.clone().add(offset, 0, 0);
                Location right = center.clone().add(-offset, 0, 0);

                Particle.DustOptions dust = new Particle.DustOptions(Color.RED, 1.5f);

                for (double y = 0; y <= 1.2; y += 0.2) {
                    center.getWorld().spawnParticle(Particle.REDSTONE, left.clone().add(0, y, 0), 1, dust);
                    center.getWorld().spawnParticle(Particle.REDSTONE, right.clone().add(0, y, 0), 1, dust);
                }

                timeLeft--;
            }
        }.runTaskTimer(HendixEmpire.getInstance(), 0L, 20L);


        double totalChance = 0d;
        for (String key : raritiesSection.getKeys(false)) {
            totalChance += raritiesSection.getDouble(key + ".chance");
        }

        double roll = new Random().nextDouble() * totalChance;
        String chosen = null;
        double cumulative = 0d;
        for (String key : raritiesSection.getKeys(false)) {
            cumulative += raritiesSection.getDouble(key + ".chance");
            if (roll <= cumulative) {
                chosen = key;
                break;
            }
        }

        if (chosen == null) {
            chosen = raritiesSection.getKeys(false).iterator().next();
        }

        ConfigurationSection raritySection = raritiesSection.getConfigurationSection(chosen);
        String rarityName = raritySection.getString("name", chosen);
        String msg = messagesConfig.getString("chest.spawn")
                .replace("%prefix%", prefix)
                .replace("%rarity%", rarityName);
        Bukkit.getOnlinePlayers().forEach(p -> ChatUtil.sendMessage(p, msg));

        Chest chest = (Chest) block.getState();
        Inventory inventory = chest.getBlockInventory();
        ConfigurationSection itemsSection = raritySection.getConfigurationSection("items");
        if (itemsSection != null) {
            Random random = new Random();
            for (String itemKey : itemsSection.getKeys(false)) {
                ConfigurationSection item = itemsSection.getConfigurationSection(itemKey);
                if (item == null) {
                    continue;
                }
                String materialName = item.getString("material");
                if (materialName == null) {
                    continue;
                }
                Material material = Material.matchMaterial(materialName);
                if (material == null) {
                    continue;
                }
                int amount = item.getInt("amount", 1);
                double chance = item.getDouble("chance", 100d);
                if (random.nextDouble() * 100d <= chance) {
                    ItemStack itemStack = new ItemStack(material, amount);
                    ItemMeta itemMeta = itemStack.getItemMeta();

                    if(item.contains("enchantments")) {
                        ConfigurationSection enchantSection = raritySection.getConfigurationSection("items." + itemKey + ".enchantments");
                        for (String enchantKey : enchantSection.getKeys(false)) {
                            Enchantment enchantment = Enchantment.getByName(enchantKey);
                            int level = enchantSection.getInt(enchantKey + ".level");
                            if(enchantment != null) {
                                itemMeta.addEnchant(enchantment, level, true);
                            }
                        }
                    }

                    itemStack.setItemMeta(itemMeta);
                    inventory.setItem(getFreeSlot(inventory), itemStack);
                }
            }
        }
    }

    private int getFreeSlot(Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if(inventory.getItem(i) == null) {
                return i;
            }
        }

        return Integer.MAX_VALUE;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isActive() {
        return active;
    }

    public Chest getChest() {
        return chest;
    }
}
