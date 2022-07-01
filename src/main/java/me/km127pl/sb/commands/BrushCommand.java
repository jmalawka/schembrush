package me.km127pl.sb.commands;

import com.sk89q.worldedit.command.tool.brush.Brush;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import me.km127pl.sb.SchemBrush;
import me.km127pl.sb.brush.SchematicBrush;
import me.km127pl.sb.brush.SingleSchematicBrush;
import me.km127pl.sb.utilities.BrushUtility;
import me.km127pl.sb.utilities.MessageUtility;
import me.km127pl.sb.utilities.SchematicUtility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class BrushCommand implements CommandExecutor, TabCompleter {
    final SchemBrush plugin;

    public BrushCommand(SchemBrush plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtility.format(Objects.requireNonNull(this.plugin.getConfig().getString("messages.player_only"))));
            return true;
        }
        Player player = (Player)sender;
        if (!player.hasPermission("schembrush.command.brush")) {
            sender.sendMessage(MessageUtility.format(Objects.requireNonNull(this.plugin.getConfig().getString("messages.no_perm"))));
            return true;
        }
        FileConfiguration conf = this.plugin.getConfig();

        if (args.length == 0) {
            SingleSchematicBrush schemBrush = new SingleSchematicBrush(conf.getBoolean("settings.ignore_air"), BlockVector3.at(conf.getInt("settings.offset.x"), conf.getInt("settings.offset.y"), conf.getInt("settings.offset.z")));
            boolean success = BrushUtility.setBrush(player, (Brush) schemBrush);
            sender.sendMessage(MessageUtility.format(Objects.requireNonNull(conf.getString("messages.success"))));
            return success;
        }

        if (args[0].equals("help")) {
            List<String> helpMessage = plugin.getConfig().getStringList("messages.help");
            helpMessage.forEach((hm) -> {
                sender.sendMessage(MessageUtility.format(hm));
            });
            return true;
        }

        ArrayList<String> schematics = new ArrayList<>(Arrays.asList(args[0].split(",")));
        if (args.length >= 2) {
            schematics.addAll(List.of(args));
        }

        if (schematics.size() < 1) {
            sender.sendMessage(MessageUtility.format(Objects.requireNonNull(conf.getString("messages.error"))));
        }

        ArrayList<Clipboard> schems = new ArrayList<>();
        schematics.forEach((schema) -> {
            Clipboard cb = plugin.getSchemUtility().getSchematic(schema);
            if (cb == null) // null when the file does not exist.
                return;
            schems.add(cb);
        });

        SchemBrush.playerSchematics.put(player.getName(), schems);
        SchematicBrush schemBrush = new SchematicBrush(conf.getBoolean("settings.ignore_air"), BlockVector3.at(conf.getInt("settings.offset.x"), conf.getInt("settings.offset.y"), conf.getInt("settings.offset.z")));
        boolean success = BrushUtility.setBrush(player, (Brush) schemBrush);

        sender.sendMessage(MessageUtility.format(Objects.requireNonNull(conf.getString("messages.success_multi"))).replace("%n", String.valueOf(schems.size())));
        return true;


    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command != this.plugin.getCommand("schembrush")) return null;
        ArrayList<String> completions = null;
        try {
            completions = new ArrayList<>(plugin.getSchemUtility().getSchematics());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // sub commands
        completions.add("help");
        return completions;
    }
}
