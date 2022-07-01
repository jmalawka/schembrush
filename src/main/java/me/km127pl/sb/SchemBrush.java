package me.km127pl.sb;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import me.km127pl.sb.commands.BrushCommand;
import me.km127pl.sb.utilities.SchematicUtility;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public final class SchemBrush extends JavaPlugin {
    public SchematicUtility schemUtility;
    // player name, clipboard[]
    public static HashMap<String, ArrayList<Clipboard>> playerSchematics = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        if (!getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
            getLogger().warning("WorldEdit is not installed on this Server!");
            return;
        }

        saveDefaultConfig();

        File schematicsFolder = new File (this.getDataFolder(), "schematics");
        if (!schematicsFolder.exists()) schematicsFolder.mkdirs();

        this.schemUtility = new SchematicUtility(this);
        BrushCommand brushCommand = new BrushCommand(this);

        getCommand("schembrush").setExecutor(brushCommand);
        getCommand("schembrush").setTabCompleter(brushCommand);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public SchematicUtility getSchemUtility() {
        return this.schemUtility;
    }
}
