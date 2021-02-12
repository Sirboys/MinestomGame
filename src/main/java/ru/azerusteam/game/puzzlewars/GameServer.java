package ru.azerusteam.game.puzzlewars;

import com.mojang.authlib.GameProfile;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.data.DataManager;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.bungee.BungeeCordProxy;
import net.minestom.server.extras.optifine.OptifineSupport;
import net.minestom.server.instance.MinestomBasicChunkLoader;
import net.minestom.server.instance.block.BlockManager;
import org.apache.logging.log4j.core.jmx.Server;
import ru.azerusteam.game.puzzlewars.block.VanillaBlocks;
import ru.azerusteam.game.puzzlewars.config.ConfigManager;
import ru.azerusteam.game.puzzlewars.config.ServerProperties;
import ru.azerusteam.game.puzzlewars.data.GameProfileData;

import java.util.Arrays;
import java.util.Optional;

public class GameServer {
    public static boolean IN_IDE = false;
    public static void main(String[] args) {
        for (String arg : args) {
            IN_IDE = arg.equals("test");
            if (IN_IDE) break;
        }
        Optional<String> allTestArgs = Arrays.stream(args).filter(s -> s.equalsIgnoreCase("test")).findFirst();
        if (allTestArgs.isPresent()) {
            System.out.println(allTestArgs.get());
            IN_IDE = true;
        }
        MinecraftServer minecraftServer = MinecraftServer.init();
        MinecraftServer.setBrandName("AzerusCore");
        BlockManager blockManager = MinecraftServer.getBlockManager();
        DataManager dataManager = MinecraftServer.getDataManager();
        dataManager.registerType(GameProfile.class, new GameProfileData());
        VanillaBlocks.registerAll(MinecraftServer.getConnectionManager(), blockManager);
        OptifineSupport.enable();
        //MojangAuth.init();
        ServerProperties serverProperties = ConfigManager.parsePropertiesConfig();
        if (serverProperties == null) {
            serverProperties = ServerProperties.createDefaultConfig();
        }
        if (serverProperties.isEnableProxy()) {
            BungeeCordProxy.enable();
        }
        if (serverProperties.isOnline()) {
            MojangAuth.init();
        }
        PlayerInit.init(serverProperties);
        minecraftServer.start("0.0.0.0", serverProperties.getPort(), (playerConnection, responseData) -> {
            responseData.setName(MinecraftServer.VERSION_NAME);
            responseData.setMaxPlayer(80);
            responseData.setOnline(MinecraftServer.getConnectionManager().getOnlinePlayers().size());
            responseData.setDescription("Good this");
            responseData.setFavicon("data:image/png;base64,<data>");
        });
    }
}
