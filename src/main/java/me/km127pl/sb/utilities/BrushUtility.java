package me.km127pl.sb.utilities;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.command.tool.InvalidToolBindException;
import com.sk89q.worldedit.command.tool.brush.Brush;
import com.sk89q.worldedit.session.SessionOwner;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BrushUtility {
    private static final WorldEdit we = WorldEdit.getInstance();

    /**
     * Set a brush for a player,
     * @param player the player,
     * @param brush the brush to set,
     * @return true if set correctly.
     */

    public static boolean setBrush(Player player, Brush brush) {
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        try {
            getLocalSession(player).getBrushTool(BukkitAdapter.asItemType(itemInMainHand.getType())).setBrush(brush, "schembrush.use");
        } catch (InvalidToolBindException e) {
            return false;
        }
        return true;
    }

    private static LocalSession getLocalSession(Player player) {
        BukkitPlayer bukkitPlayer = BukkitAdapter.adapt(player);

        return we.getSessionManager().get((SessionOwner)bukkitPlayer);
    }
}
