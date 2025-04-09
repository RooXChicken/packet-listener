package org.loveroo.packetlistener.Events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/*
 * This class is a wrapper for ChannelDuplexHandler.
 * It fires Bukkit events and stops the packets from being processed if they get cancelled
 */

public class PacketHandler extends ChannelDuplexHandler {
    
    private Player player;

    public PacketHandler(Player _player) {
        player = _player;
    }

    @Override
    public void channelRead(ChannelHandlerContext _context, Object _object) throws Exception {
        PacketReceiveEvent _event = new PacketReceiveEvent(player, _object);
        Bukkit.getPluginManager().callEvent(_event);

        if(_event.isCancelled())
            return;

        super.channelRead(_context, _object);
    }

    @Override
    public void write(ChannelHandlerContext _ctx, Object _msg, ChannelPromise _promise) throws Exception {
        PacketSendEvent _event = new PacketSendEvent(player, _msg);
        Bukkit.getPluginManager().callEvent(_event);

        if(_event.isCancelled())
            return;

        super.write(_ctx, _msg, _promise);
    }
}
