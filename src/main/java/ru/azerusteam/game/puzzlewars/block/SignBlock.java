package ru.azerusteam.game.puzzlewars.block;

import net.minestom.server.data.Data;
import net.minestom.server.entity.Player;
import net.minestom.server.event.instance.InstanceTickEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.BlockEntityDataPacket;
import net.minestom.server.utils.BlockPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import ru.azerusteam.game.puzzlewars.blockentity.SignBlockEntity;

import java.util.Objects;

public class SignBlock extends VanillaBlock{
    public SignBlock(Block baseBlock) {
        super(baseBlock);
    }

    @Override
    protected BlockPropertyList createPropertyValues() {
        return new BlockPropertyList().intRange("rotation", 0, 15).booleanProperty("waterlogged");
    }
    @Override
    public boolean onInteract(Player player, Player.Hand hand, BlockPosition blockPosition, Data data) {
        return true;
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
    @Override
    public void writeBlockEntity(@NotNull BlockPosition position, @Nullable Data blockData, @NotNull NBTCompound nbt) {
        for (int i = 0; i < 4; i++) {
            if (blockData == null) continue;
            nbt.setString("Text" + (i+1), (String) Objects.requireNonNull(blockData.get("Text" + (i + 1))));
        }
        nbt.setString("id", "minecraft:sign");
    }
}
