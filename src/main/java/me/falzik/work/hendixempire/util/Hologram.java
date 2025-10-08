package me.falzik.work.hendixempire.util;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Hologram {

    private final List<ArmorStand> entites = new ArrayList<>();
    private List<String> text;
    private final Location location;

    private static final List<Hologram> holograms = new ArrayList<>();

    public Hologram(List<String> text, Location location) {
        this.text = text;
        this.location = location.add(new Vector(0, -2, 0));

        overrideHologram();

        holograms.add(this);
    }

    public void setText(List<String> text) {
        this.text = text;

        overrideHologram();
    }

    private void overrideHologram() {
        for (int i = 0; i < text.size(); i++) {
            if (entites.size() > i) {
                entites.get(i).setCustomName(ChatUtil.translateCodes(text.get(i)));
            }
            else {
                ArmorStand armorStand = location.getWorld().spawn(location.add(new Vector(0, i * 0.3, 0)), ArmorStand.class);

                armorStand.setCustomName(ChatUtil.translateCodes(text.get(i)));
                armorStand.setCustomNameVisible(true);
                armorStand.setGravity(false);
                armorStand.setVisible(false);
                armorStand.setInvulnerable(true);

                entites.add(armorStand);
            }
        }


        Iterator<ArmorStand> iterator = entites.subList(text.size(), entites.size()).stream().iterator();

        while (iterator.hasNext()) {
            iterator.next().remove();
        }
    }

    public void destroy() {
        for (ArmorStand armorStand: entites) {
            armorStand.remove();
        }
        holograms.remove(this);
    }

    public static void destroyAll() {
        for (Hologram hologram : holograms) {
            hologram.destroy();
        }
    }
}
