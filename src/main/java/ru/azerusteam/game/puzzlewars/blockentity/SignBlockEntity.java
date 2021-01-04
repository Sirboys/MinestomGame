package ru.azerusteam.game.puzzlewars.blockentity;

import net.minestom.server.utils.BlockPosition;

public class SignBlockEntity extends BlockEntity{
    public SignBlockEntity(BlockPosition position) {
        super(position);

    }
    public void setLine(int number, String line) {
        set("Text" + (number), line, String.class);
    }
}
