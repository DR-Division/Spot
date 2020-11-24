package com.light.spot.PacketAdapter;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.light.spot.Manager.SpotManager;
import com.light.spot.Spot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import com.comphenix.packetwrapper.*;

import java.util.List;

public class PacketManager extends PacketAdapter{

    private Spot Plugin;

    public PacketManager(Plugin plugin, ListenerPriority listenerPriority, PacketType... types) {
        super(plugin, listenerPriority, types);
        this.Plugin = (Spot) plugin;
    }

    @Override
    public void onPacketReceiving(PacketEvent event){
        if (event.getPacketType() == PacketType.Play.Client.ADVANCEMENTS){
            PacketContainer container = event.getPacket().deepClone();
            WrapperPlayClientAdvancements advancements = new WrapperPlayClientAdvancements(container);
            if (advancements.getAction() == WrapperPlayClientAdvancements.Status.OPENED_TAB) {
                if (SpotManager.getInstance().isOn(event.getPlayer())) {
                    Player p = event.getPlayer();
                    p.closeInventory();
                    SpotManager.getInstance().setGlow(p);
                }
            }
        }
    }
    @Override
    public void onPacketSending(PacketEvent event){
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA){
            Player p = event.getPlayer();
            if (SpotManager.getInstance().isOn(p) && SpotManager.getInstance().getEntityUUID(p) != null){
                WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata(event.getPacket().deepClone());
                Entity eventEntity = metadata.getEntity(event);
                if (eventEntity.getUniqueId().equals(SpotManager.getInstance().getEntityUUID(p))){
                    List<WrappedWatchableObject> metas = metadata.getMetadata();
                    Object object = metas.get(0).getValue();
                    if ((object instanceof Byte)){
                        metadata.getMetadata().get(0).setValue((byte) ((byte) metas.get(0).getValue() | 0x40));
                    }
                    event.setPacket(metadata.getHandle());
                }
            }
        }
    }
}
