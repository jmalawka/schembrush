package me.km127pl.sb.brush;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.command.tool.brush.Brush;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import me.km127pl.sb.utilities.SchematicUtility;

import java.util.Objects;

public class SingleSchematicBrush implements Brush {
    private final boolean ignoreAir;
    private final BlockVector3 offset;

    public SingleSchematicBrush(Boolean ignoreAir, BlockVector3 offset) {
        this.ignoreAir = ignoreAir;
        this.offset = offset;
    }


    @Override
    public void build(EditSession editSession, BlockVector3 position, Pattern pattern, double size) throws MaxChangedBlocksException {
        ClipboardHolder clipboards = Objects.requireNonNull(editSession.getActor()).getSession().getClipboard();
        if (clipboards.getClipboards().isEmpty()) {
            return;
        }
        // get a random element from the clipboard
        Clipboard clipboard = clipboards.getClipboards().get((int) (Math.random() * clipboards.getClipboards().toArray().length));

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
