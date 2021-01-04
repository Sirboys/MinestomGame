package ru.azerusteam.game.puzzlewars.block;

import net.minestom.server.data.Data;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.BlockPosition;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import ru.azerusteam.game.puzzlewars.blockentity.SignBlockEntity;

public class SignBlock extends VanillaBlock{
    public SignBlock(Block baseBlock) {
        super(baseBlock);
    }
    @Override
    public Data readBlockEntity(NBTCompound nbt, Instance instance, BlockPosition position, Data originalData) {
        SignBlockEntity data;
        if (originalData instanceof SignBlockEntity) {
            data = (SignBlockEntity) originalData;
        } else {
            data = new SignBlockEntity(position);
        }
        for (int i = 1; i <= 4; i++) {
            String textLineKey = "Text" + i;
            if (nbt.containsKey(textLineKey)) {
                data.setLine(i, nbt.getString(textLineKey));
            }
        }
        return data;
    }
}
