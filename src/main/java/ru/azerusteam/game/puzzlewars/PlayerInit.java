package ru.azerusteam.game.puzzlewars;

import dev.sejtam.entities.projectile.EntityFireworkRocket;
import net.minestom.server.MinecraftServer;
import net.minestom.server.chat.ChatColor;
import net.minestom.server.chat.ColoredText;
import net.minestom.server.effects.Effects;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.entity.type.decoration.EntityItemFrame;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.instance.InstanceTickEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.attribute.ItemAttribute;
import net.minestom.server.item.firework.FireworkEffect;
import net.minestom.server.item.firework.FireworkEffectType;
import net.minestom.server.item.metadata.FireworkMeta;
import net.minestom.server.item.metadata.ItemMeta;
import net.minestom.server.listener.manager.PacketListenerManager;
import net.minestom.server.network.ConnectionManager;
import net.minestom.server.network.packet.client.play.ClientStatusPacket;
import net.minestom.server.network.packet.client.status.PingPacket;
import net.minestom.server.network.packet.client.status.StatusRequestPacket;
import net.minestom.server.network.packet.server.play.ChangeGameStatePacket;
import net.minestom.server.network.packet.server.play.EntityStatusPacket;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.storage.StorageManager;
import net.minestom.server.storage.systems.FileStorageSystem;
import net.minestom.server.utils.Position;
import net.minestom.server.utils.Vector;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.text.translate.UnicodeEscaper;
import org.apache.commons.text.translate.UnicodeUnescaper;
import ru.azerusteam.game.puzzlewars.config.ServerProperties;
import ru.azerusteam.game.puzzlewars.game.GameManager;
import ru.azerusteam.game.puzzlewars.inventory.ShopInventory;
import ru.azerusteam.game.puzzlewars.lobby.LobbyManager;
import ru.azerusteam.game.puzzlewars.wineffect.FireworkRocket;
import ru.azerusteam.game.puzzlewars.wineffect.FireworkRockets;
import ru.azerusteam.game.puzzlewars.world.AnvilChunkLoader;
import ru.azerusteam.game.puzzlewars.world.VoidChunkLoader;

public class PlayerInit {
    public static InstanceContainer overworld;

    public static void init(ServerProperties serverProperties) {
        overworld = MinecraftServer.getInstanceManager().createInstanceContainer(); // TODO: configurable
        StorageManager storageManager = MinecraftServer.getStorageManager();
        storageManager.defineDefaultStorageSystem(FileStorageSystem::new);
        overworld.enableAutoChunkLoad(true);
        overworld.setChunkGenerator(new VoidChunkLoader());
        overworld.setChunkLoader(new AnvilChunkLoader(storageManager.getLocation("region")));

        ConnectionManager connectionManager = MinecraftServer.getConnectionManager();
        //GameManager gameManager = new GameManager(new Position(1057.5, 111, 1001.5, 90, 21), overworld);
        LobbyManager lobbyManager = new LobbyManager(overworld, new Position(1057.5, 111, 1001.5, 90, 21), serverProperties);
        overworld.addEventCallback(InstanceTickEvent.class, event -> {
            lobbyManager.onTick();
        });

        connectionManager.addPlayerInitialization(player -> {
            if (overworld.getPlayers().size() >= 80) {
                player.kick("Server is FULL!");
            }
            // Set the spawning instance
            player.addEventCallback(PlayerLoginEvent.class, event -> {
                event.setSpawningInstance(overworld);
                player.setRespawnPoint(new Position((float) 0, 113, (float) 0, 180, 0));

            });
            player.addEventCallback(PlayerBlockInteractEvent.class, event -> {
                if (player.getGameMode().equals(GameMode.SPECTATOR)) {
                    event.setCancelled(true);
                }
            });
            player.addEventCallback(PlayerBlockPlaceEvent.class, event -> {
                event.setCancelled(true);
            });
            player.addEventCallback(ItemDropEvent.class, event -> {
                event.setCancelled(true);
            });
            player.addEventCallback(PlayerSwapItemEvent.class, event -> {
                event.setCancelled(true);
            });
            player.addEventCallback(InventoryPreClickEvent.class, event -> {
                ShopInventory.onClickItem(event);
                lobbyManager.onClickInventory(event);
            });

            player.addEventCallback(PlayerEntityInteractEvent.class, event -> {
                Entity target = event.getTarget();
                if (event.getHand().equals(Player.Hand.OFF)) return;
                if (target.getEntityType().equals(EntityType.ITEM_FRAME)) {
                    EntityItemFrame entityItemFrame = (EntityItemFrame) target;
                    entityItemFrame.setRotation(entityItemFrame.getRotation().rotateClockwise());
                }
            });
            player.addEventCallback(EntityAttackEvent.class, event -> {
                if (event.getEntity().getEntityType().equals(EntityType.PLAYER)) {
                    if (event.getTarget().getEntityType().equals(EntityType.ITEM_FRAME)) {
                        EntityItemFrame entityItemFrame = (EntityItemFrame) event.getTarget();
                        entityItemFrame.setRotation(entityItemFrame.getRotation().rotateCounterClockwise());
                    }
                }
            });
            player.addEventCallback(PlayerUseItemEvent.class, event -> {
                lobbyManager.onUseItem(event);
                //todo
            });
            player.addEventCallback(PlayerChatEvent.class, event -> {
                if (!player.getUsername().equals("Sirboys")) {
                    return;
                }
                String[] args = event.getMessage().split(" ");
                if (args[0].equalsIgnoreCase("start")) {
                    return;
                }
                EntityFireworkRocket entityFireworkRocket;
                if (args[0].equalsIgnoreCase("fw")) {
                    FireworkRockets.summonCircle(overworld, player.getPosition(), 2);
                    /*FireworkEffect effect = new FireworkEffect(true, true, FireworkEffectType.CREEPER_SHAPED, ChatColor.BRIGHT_GREEN, ChatColor.BLACK);
                    FireworkMeta fireworkMeta = new FireworkMeta();
                    fireworkMeta.setFlightDuration(((byte) 3));
                    fireworkMeta.addFireworkEffect(effect);
                    ItemStack firework = new ItemStack(Material.FIREWORK_ROCKET, (byte) 1);
                    firework.setItemMeta(fireworkMeta);
                    FireworkRocket fireworkRocket = new FireworkRocket(overworld, player.getPosition(), firework);*/
                }
                if (args.length <= 2) return;
                if (args[0].equalsIgnoreCase("weird")) {
                    event.setCancelled(true);
                    try {
                        ChangeGameStatePacket changeGameStatePacket = new ChangeGameStatePacket();
                        changeGameStatePacket.reason = ChangeGameStatePacket.Reason.valueOf(args[1]);
                        changeGameStatePacket.value = Float.parseFloat(args[2]);
                        player.sendPacketToViewersAndSelf(changeGameStatePacket);
                    } catch (Exception e) {
                        player.sendMessage("something went wrong");
                        e.printStackTrace();
                    }
                }
            });
            player.addEventCallback(PlayerSpawnEvent.class, event -> {
                player.teleport(new Position((float) 1058, 111, (float) 1001, 180, 0));
                player.setGameMode(GameMode.CREATIVE);
                //gameManager.onJoin(player); //todo
                lobbyManager.onServerJoin(event);
            });
           player.addEventCallback(PlayerDisconnectEvent.class, event -> {
               //gameManager.onLeave(player); //todo
               lobbyManager.onServerLeave(event);
           });
        });

    }
}
