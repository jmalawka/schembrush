package me.km127pl.sb.utilities;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import me.km127pl.sb.SchemBrush;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class SchematicUtility {
    private static final WorldEdit we = WorldEdit.getInstance();
    private final SchemBrush plugin;

    public SchematicUtility(SchemBrush plugin) {
        this.plugin = plugin;
    }

    /**
     * Get a schematic.
     * @param fileName a schematic in the $PLUGIN/schematics folder.
     * @return the clipboard schematic.
     */
    public Clipboard getSchematic(String fileName) {
        File file = new File(we.getSchematicsFolderPath() + "/" + fileName);
        if (!file.exists()) { // check if it maybe exists in our schematic path
            file = new File(this.plugin.getDataFolder().getPath() + "/schematics/" + fileName);
            if (!file.exists()) {
                return null;
            }
        };
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        ClipboardReader reader = null;
        try {
            assert format != null;
            reader = format.getReader(Files.newInputStream(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Clipboard clipboard = null;
        try {
            clipboard = reader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return clipboard;
    }

    /**
     * Paste a schematic at a given position in a world.
     * @param world the world to paste the schematic in,
     * @param pos the position where to place the schematic,
     * @param schem the schematic,
     * @param ignoreAir if the plugin should ignore air in the schematic,
     * @return true if ran successfully.
     */
    public static EditSession pasteSchematic(World world, BlockVector3 pos, Clipboard schem, boolean ignoreAir) {
        EditSession editSession = we.newEditSession(world);
        Operation operation = new ClipboardHolder(schem)
                .createPaste(editSession)
                .to(pos)
                .ignoreAirBlocks(ignoreAir)
                .build();
        Operations.completeBlindly(operation);
        editSession.commit();
        editSession.close();
        return editSession;
    }

    /**
     * Paste a schematic at a given position in a world.
     * @param world the world to paste the schematic in,
     * @param pos the position where to place the schematic,
     * @param schem the schematic,
     * @return true if ran successfully.
     */
    public static EditSession pasteSchematic(World world, BlockVector3 pos, Clipboard schem) {
        EditSession editSession = we.newEditSession(world);
        Operation operation = new ClipboardHolder(schem)
                .createPaste(editSession)
                .to(pos)
                .ignoreAirBlocks(false)
                .build();
        Operations.completeBlindly(operation);
        editSession.commit();
        editSession.close();
        return editSession;
    }

    /**
     * Get all schematics
     * @return the schematics.
     */
    public ArrayList<String> getSchematics() throws IOException {
        ArrayList<String> fileList = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get( this.plugin.getDataFolder().getPath() + "/schematics/"))) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    fileList.add(path.getFileName().toString());
                }
            }
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(String.valueOf(we.getSchematicsFolderPath())))) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    fileList.add(path.getFileName().toString());
                }
            }
        }
        return fileList;
    }
}
