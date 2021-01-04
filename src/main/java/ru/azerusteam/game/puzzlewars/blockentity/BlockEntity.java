package ru.azerusteam.game.puzzlewars.blockentity;


import net.minestom.server.data.SerializableDataImpl;
import net.minestom.server.utils.BlockPosition;

/**
 * Base class used to represent Block Entities
 */
public class BlockEntity extends SerializableDataImpl {
    private final BlockPosition position;

    public BlockEntity(BlockPosition position) {
        this.position = position;
        set("x", position.getX(), Integer.class);
        set("y", position.getY(), Integer.class);
        set("z", position.getZ(), Integer.class);
    }

    public BlockPosition getPosition() {
        return position;
    }
}