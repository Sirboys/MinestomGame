package ru.azerusteam.game.puzzlewars;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.network.ConnectionManager;
import net.minestom.server.storage.StorageManager;
import net.minestom.server.storage.StorageOptions;
import net.minestom.server.storage.systems.FileStorageSystem;
import net.minestom.server.utils.Position;
import net.minestom.server.world.DimensionType;
import ru.azerusteam.game.puzzlewars.block.SignBlock;
import ru.azerusteam.game.puzzlewars.block.VanillaBlock;
import ru.azerusteam.game.puzzlewars.block.VanillaBlocks;
import ru.azerusteam.game.puzzlewars.blockentity.SignBlockEntity;
import ru.azerusteam.game.puzzlewars.world.AnvilChunkLoader;
import ru.azerusteam.game.puzzlewars.world.VoidChunkLoader;

public class PlayerInit {
    public static InstanceContainer overworld;

    public static void init() {
        overworld = MinecraftServer.getInstanceManager().createInstanceContainer(); // TODO: configurable

        StorageManager storageManager = MinecraftServer.getStorageManager();
        storageManager.defineDefaultStorageSystem(FileStorageSystem::new);
        overworld.enableAutoChunkLoad(true);
        overworld.setChunkGenerator(new VoidChunkLoader());
        //overworld.setChunkGenerator(noiseTestGenerator);
        //overworld.setData(new SerializableDataImpl());
        //overworld.setExplosionSupplier(explosionGenerator);
        overworld.setChunkLoader(new AnvilChunkLoader(storageManager.getLocation("region")));
        ConnectionManager connectionManager = MinecraftServer.getConnectionManager();
        MojangAuth.init();
        connectionManager.addPlayerInitialization(player -> {
            // Set the spawning instance
            player.addEventCallback(PlayerLoginEvent.class, event -> {
                event.setSpawningInstance(overworld);
                player.setRespawnPoint(new Position((float) -13.5, 113, (float) 53.5, 180, 0));
            });

            // Teleport the player at spawn
            player.addEventCallback(PlayerSpawnEvent.class, event -> {
                player.teleport(new Position((float) -13.5, 113, (float) 53.5, 180, 0));
                player.setGameMode(GameMode.ADVENTURE);
                Position position = player.getPosition();
                position.add(0, 0,0);
                SignBlockEntity signBlockEntity = new SignBlockEntity(position.toBlockPosition());
                signBlockEntity.set("Color", "black", String.class);
                signBlockEntity.set("id", "minecraft:sign");
                signBlockEntity.setLine(1, "{\"text\":\"Hellow\"}");
                System.out.println(signBlockEntity.getKeys());
                overworld.setBlockData(position.toBlockPosition().getX(), position.toBlockPosition().getY(), position.toBlockPosition().getZ(), signBlockEntity);
                overworld.setCustomBlock(position.toBlockPosition().getX(), position.toBlockPosition().getY(), position.toBlockPosition().getZ(), VanillaBlocks.ACACIA_SIGN.getInstance().getCustomBlockId());
                //overworld.setBlock(position.toBlockPosition(), VanillaBlocks.ACACIA_SIGN);


            });
        });

    }
}
