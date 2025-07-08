package org.loveroo.packetlistener;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.loveroo.packetlistener.Events.PacketHandler;
import org.loveroo.packetlistener.Events.PacketReceiveEvent;

import io.netty.channel.Channel;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.network.protocol.game.ServerboundClientTickEndPacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemFromBlockPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemFromEntityPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;

public class PacketListener extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        // in case the plugin is loaded while players are already online, register all online players
        for(Player _player : Bukkit.getOnlinePlayers()) {
            registerPlayer(_player);
        }

        // BukkitRunnable run = new BukkitRunnable() {
        //     @Override
        //     public void run() {
        //         for(Player _player : Bukkit.getOnlinePlayers()) {
        //             _player.setSprinting(true);
        //         }
        //     }
        // };

        // run.runTaskTimer(this, 0, 1);

        getLogger().info("Listening to packets since 1987! [made by roo]");
    }

    @EventHandler
    private void registerOnConnect(PlayerJoinEvent event) {
        registerPlayer(event.getPlayer());
    }

    private void registerPlayer(Player _player) {
        try {
            Channel _channel = getChannel(_player);

            // packet_handler is the name of the pipeline where minecraft handled the packets.
            // we want to run our code before this

            // our player name is used because that's what minecraft does :P
            _channel.pipeline().addBefore("packet_handler", _player.getName(), new PacketHandler(_player));
        }
        catch(Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to register player! " + e);
        }
    }

    // this is only needed if the plugin is disabled while the server is running.
    // otherwise, the packet handler gets unregistered when... the player disconnects (ending the connection)
    private void unregisterPlayer(Player _player) {
        try {
            Channel _channel = getChannel(_player);

            // remove all instances of our custom packet handler to stop them from running
            _channel.pipeline().remove(PacketHandler.class);
        }
        catch(Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to unregister player! " + e);
        }
    }

    private Channel getChannel(Player _player) throws NoSuchFieldException, IllegalAccessException {
        ServerPlayer _sp = ((CraftPlayer)_player).getHandle();

        // access the connection field (private)
        Field _getNM = ServerCommonPacketListenerImpl.class.getField("connection");
        _getNM.setAccessible(true);

        // access the io netty connection (private) (but in this case, a wrapped version for minecraft called NetworkManager)
        NetworkManager _nm = (NetworkManager)_getNM.get(_sp.connection);
        Field _getINC = NetworkManager.class.getField("channel");
        _getINC.setAccessible(true);

        // return the channel
        return (Channel)_getINC.get(_nm);
    }
    
    @Override
    public void onDisable() {
        // just in case the plugin is disabled while the server isn't stopped, unregister all players so our code stops running
        for(Player _player : Bukkit.getOnlinePlayers()) {
            unregisterPlayer(_player);
        }
    }

    // public void modifyServerPlayerPosition(PacketReceiveEvent event) {
    //     // if the packet is not our desired packet type, we return
    //     if(!(event.getPacket() instanceof ServerboundMovePlayerPacket packet))
    //         return;
      
    //     try {
    //       // we want to modify the x variable in this packet
    //       Field setX = packet.getClass().getField("x");
    //       setX.setAccessible(true); // allows this private final field to be accessible to us
      
    //       // set all player move packets to be offset by 1 on the x axis
    //       setX.set(packet, packet.x + 1);
    //     }
    //     catch(Exception e) {
    //       // in case our field doesn't exist, log it so we know
    //       Bukkit.getLogger().log(Level.SEVERE, "Failed to modify `ServerboundMovePlayerPacket` packet! " + e);
    //     }
    //   }

    // @EventHandler
    // public void checkAdvancements(PacketReceiveEvent event) {
    //     if(!(event.getPacket() instanceof ServerboundSeenAdvancementsPacket _packet)) {
    //         return;
    //     }

    //     event.getPlayer().sendMessage("keybind!");

    //     // ClientboundUpdateAdvancementsPacket updateAdvancements = new ClientboundUpdateAdvancementsPacket(true, List.of(), Set.of(), Map.of());
    //     ClientboundContainerClosePacket close = new ClientboundContainerClosePacket(0);
    //     ServerPlayer _sp = ((CraftPlayer)event.getPlayer()).getHandle();
    //     _sp.connection.sendPacket(close);
    //     // _sp.connection.sendPacket(updateAdvancements);
    // }

    // @EventHandler
    // public void pickBlock(PacketReceiveEvent event) {
    //     if(!(event.getPacket() instanceof ServerboundPickItemFromBlockPacket) && !(event.getPacket() instanceof ServerboundPickItemFromEntityPacket)) {
    //         return;
    //     }

    //     if(event.getPlayer().isSneaking()) {
    //         return;
    //     }

    //     event.getPlayer().sendMessage("keybind!");
    //     event.setCancelled(true);
    // }

    // @EventHandler
    // public void fuckThisShit(PacketReceiveEvent event) {
    //     event.getPlayer().sendMessage("keybind!");
    //     event.setCancelled(true);
    // }

    // @EventHandler
    // public void checkSpint(PacketReceiveEvent event) {
    //     if(!(event.getPacket() instanceof ServerboundPlayerInputPacket _packet)) {
    //         return;
    //     }

    //     Bukkit.getLogger().info("" + _packet.input());

    //     // ClientboundUpdateAdvancementsPacket updateAdvancements = new ClientboundUpdateAdvancementsPacket(true, List.of(), Set.of(), Map.of());
    //     // if(_packet.input().sprint()) {
    //     //     Bukkit.getLogger().info("activate");
    //     // }
    //     // _sp.connection.sendPacket(updateAdvancements);
    // }
}
