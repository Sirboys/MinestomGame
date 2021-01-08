package ru.azerusteam.game.puzzlewars;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.ObjectEntity;
import net.minestom.server.event.instance.InstanceTickEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.network.ConnectionManager;
import net.minestom.server.storage.StorageManager;
import net.minestom.server.storage.StorageOptions;
import net.minestom.server.storage.systems.FileStorageSystem;
import net.minestom.server.utils.BlockPosition;
import net.minestom.server.utils.Position;
import net.minestom.server.world.DimensionType;
import ru.azerusteam.game.puzzlewars.block.SignBlock;
import ru.azerusteam.game.puzzlewars.block.VanillaBlock;
import ru.azerusteam.game.puzzlewars.block.VanillaBlocks;
import ru.azerusteam.game.puzzlewars.blockentity.BlockEntity;
import ru.azerusteam.game.puzzlewars.blockentity.SignBlockEntity;
import ru.azerusteam.game.puzzlewars.game.GameManager;
import ru.azerusteam.game.puzzlewars.world.AnvilChunkLoader;
import ru.azerusteam.game.puzzlewars.world.VoidChunkLoader;

import java.util.Set;

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
        GameManager gameManager = new GameManager(overworld, new BlockPosition(-86, 110, 68), new BlockPosition(-40, 110, 115), new Position(-60, 111, 80));
        ConnectionManager connectionManager = MinecraftServer.getConnectionManager();
        MojangAuth.init();
        overworld.addEventCallback(InstanceTickEvent.class, event -> {
            gameManager.tick();
        });
        connectionManager.addPlayerInitialization(player -> {
            // Set the spawning instance
            player.addEventCallback(PlayerLoginEvent.class, event -> {
                event.setSpawningInstance(overworld);
                player.setRespawnPoint(new Position((float) -13.5, 113, (float) 53.5, 180, 0));
            });

            // Teleport the player at spawn
            player.addEventCallback(PlayerSpawnEvent.class, event -> {
                player.teleport(new Position((float) -13.5, 113, (float) 53.5, 180, 0));
                player.setGameMode(GameMode.CREATIVE);
                gameManager.playerJoin(player);
            });
            player.addEventCallback(PlayerChatEvent.class, event -> {
                if (event.getMessage().equals("Start")) {
                    gameManager.start();
                }
            });
           player.addEventCallback(PlayerDisconnectEvent.class, event -> {
               gameManager.onLeave(player);
           });
        });
    }
}
