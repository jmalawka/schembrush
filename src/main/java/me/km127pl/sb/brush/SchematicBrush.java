package me.km127pl.sb.brush;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.command.tool.brush.Brush;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import me.km127pl.sb.SchemBrush;
import me.km127pl.sb.utilities.SchematicUtility;

import java.util.ArrayList;
import java.util.Objects;

public class SchematicBrush implements Brush {
    private final boolean ignoreAir;
    private final BlockVector3 offset;

    public SchematicBrush(Boolean ignoreAir, BlockVector3 offset) {
        this.ignoreAir = ignoreAir;
        this.offset = offset;
    }


    @Override
    public void build(EditSession editSession, BlockVector3 position, Pattern pattern, double size) throws MaxChangedBlocksException {
        // check if any schematics are present
        if (!(SchemBrush.playerSchematics.containsKey(Objects.requireNonNull(editSession.getActor()).getName()))) {
            //System.out.println("Exiting brush because the player " + editSession.getActor().getDisplayName() + " does not have any schematics.");
            return;
        }
        ArrayList<Clipboard> schematics = SchemBrush.playerSchematics.get(editSession.getActor().getName());

        // get a random schematic
        Clipboard clipboard = schematics.get((int) (Math.random() * schematics.size()));

        // paste the schematic
        position = position.add(this.offset);
        EditSession pasteSession = SchematicUtility.pasteSchematic(editSession.getWorld(), position, clipboard, this.ignoreAir);

        // add the schematic to the player's session
        if (editSession.getActor().isPlayer()) {
            Player p = (Player) editSession.getActor();
            p.getSession().remember(pasteSession);
        }
    }

}
