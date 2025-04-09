package org.loveroo.packetlistener;

import java.lang.reflect.Field;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.loveroo.packetlistener.Events.PacketHandler;
import io.netty.channel.Channel;
import net.minecraft.network.NetworkManager;
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
}
