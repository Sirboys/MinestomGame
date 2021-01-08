package ru.azerusteam.game.puzzlewars.block;

import net.minestom.server.data.Data;
import net.minestom.server.entity.Player;
import net.minestom.server.event.instance.InstanceTickEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.BlockEntityDataPacket;
import net.minestom.server.utils.BlockPosition;
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
    public void onLoad(Instance instance, BlockPosition blockPosition, Data data) {
        BlockEntityDataPacket blockEntityDataPacket = new BlockEntityDataPacket();
        blockEntityDataPacket.action = 9;
        blockEntityDataPacket.blockPosition = blockPosition;

        blockEntityDataPacket.nbtCompound = new NBTCompound();
        for (int i = 0; i < 4; i++) {
            if (data == null) continue;
            blockEntityDataPacket.nbtCompound.setString("Text" + (i+1), (String) Objects.requireNonNull(data.get("Text" + (i + 1))));
        }

        blockEntityDataPacket.nbtCompound.setString("id", "minecraft.sign");
        blockEntityDataPacket.nbtCompound.setInt("x", blockPosition.getX());
        blockEntityDataPacket.nbtCompound.setInt("y", blockPosition.getY());
        blockEntityDataPacket.nbtCompound.setInt("z", blockPosition.getZ());
        instance.getPlayers().forEach(player -> {
            player.getPlayerConnection().sendPacket(blockEntityDataPacket);
            //player.addEventCallback(PlayerMoveEvent.class, event -> {});
        });
    }
    @Override
    public boolean onInteract(Player player, Player.Hand hand, BlockPosition blockPosition, Data data) {
        onLoad(player.getInstance(), blockPosition, data);
        return false;
    }
    /*@Override
    public void createData(Instance instance, BlockPosition blockPosition, Data data) {
        instance.getPlayers().forEach(player -> {
            player.sendJsonMessage((String) data.get("Text2"));
        });
        //player.addPacketToQueue(new BlockEn);
        BlockEntityDataPacket blockEntityDataPacket = new BlockEntityDataPacket();
        blockEntityDataPacket.action = 9;
        blockEntityDataPacket.blockPosition = blockPosition;
        blockEntityDataPacket.nbtCompound = new NBTCompound();
        for (int i = 0; i < 4; i++) {
            blockEntityDataPacket.nbtCompound.setString("Text" + (i+1), (String) Objects.requireNonNull(data.get("Text" + (i + 1))));
        }

        blockEntityDataPacket.nbtCompound.setString("id", "minecraft.sign");
        blockEntityDataPacket.nbtCompound.setInt("x", blockPosition.getX());
        blockEntityDataPacket.nbtCompound.setInt("y", blockPosition.getY());
        blockEntityDataPacket.nbtCompound.setInt("z", blockPosition.getZ());
        instance.getPlayers().forEach(player -> {
            player.getPlayerConnection().sendPacket(blockEntityDataPacket);
        });



    }*/
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
       /* instance.addEventCallback(InstanceTickEvent.class, event -> {
            instance.getPlayers().forEach(player -> {
                player.addEventCallback(PlayerMoveEvent.class, (e) -> {
                    onLoad(instance, position, originalData);
                });
            });
        });*/
        return data;
    }
}
