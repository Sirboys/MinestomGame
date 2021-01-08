package ru.azerusteam.game.puzzlewars;

import net.minestom.server.MinecraftServer;
import net.minestom.server.extras.optifine.OptifineSupport;
import net.minestom.server.instance.block.BlockManager;
import net.minestom.server.instance.block.CustomBlock;
import ru.azerusteam.game.puzzlewars.block.VanillaBlocks;

public class GameServer {
    public static void main(String[] args) {
        MinecraftServer minecraftServer = MinecraftServer.init();
        BlockManager blockManager = MinecraftServer.getBlockManager();

        VanillaBlocks.registerAll(MinecraftServer.getConnectionManager(), blockManager);
        OptifineSupport.enable();
        PlayerInit.init();

        minecraftServer.start("localhost", 25565);
    }
}
