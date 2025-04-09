package org.loveroo.packetlistener.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PacketReceiveEvent extends PlayerEvent {
    
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private Object packet;
    private boolean isCancelled = false;

    public PacketReceiveEvent(Player _player, Object _packet) {
        super(_player, true);
        packet = _packet;
    }

	@Override
	public HandlerList getHandlers() {
        return HANDLERS_LIST;
	}
    
    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    /* Provides the packet as a generic object (it can be any packet type.)
     * To check and get an instance of the correct packet, do `if(getPacket() instanceof <PACKET> packet) {}` to check and retrieve the correct type
     */
    public Object getPacket() {
        return packet;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean _cancelled) {
        isCancelled = _cancelled;
    }
}
