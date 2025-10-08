package me.falzik.work.hendixempire.command;

import me.falzik.work.hendixempire.util.ChatUtil;
import me.falzik.work.hendixempire.HendixEmpire;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GameCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("start")) {
            HendixEmpire.getInstance().getGame().startPreGame();
            ChatUtil.sendMessage(sender, "Ивент запускается...");
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length == 1) {
            return List.of("start");
        }

        return null;
    }
}
