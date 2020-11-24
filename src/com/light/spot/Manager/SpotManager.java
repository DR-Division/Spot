package com.light.spot.Manager;

import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.light.spot.PacketAdapter.PacketManager;
import com.light.spot.Spot;
import com.mysql.jdbc.Wrapper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SpotManager {

    private static SpotManager instance;
    private PacketAdapter spotAdapter;
    private Spot Plugin;
    private HashMap<UUID, UUID> targetList;

    static {
        instance = new SpotManager();
    }

    private SpotManager(){
        Plugin = JavaPlugin.getPlugin(Spot.class);
        spotAdapter = new PacketManager(Plugin, ListenerPriority.LOWEST, PacketType.Play.Client.ADVANCEMENTS, PacketType.Play.Server.ENTITY_METADATA);
        targetList = new HashMap<>();
    }

    public static SpotManager getInstance(){
        return instance;
    }

    public void turnOn(){
        ProtocolLibrary.getProtocolManager().addPacketListener(spotAdapter);
    }

    public void turnOff(){
        ProtocolLibrary.getProtocolManager().removePacketListener(spotAdapter);
    }

    public boolean isOn(Player p){
        return targetList.get(p.getUniqueId()) != null;
    }

    public UUID getEntityUUID(Player p){
        if (isOn(p))
            return targetList.get(p.getUniqueId());
        return null;
    }

    public void addPlayer(Player p, UUID target){
        targetList.put(p.getUniqueId(), target);
    }

    public void removePlayer(Player p){
        targetList.remove(p.getUniqueId());
    }

    public Entity getEntity(Player p){
        int i,size;
        List<Block> blocks = p.getLineOfSight(null, 100);
        size = blocks.size();
        for (i = 0; i < size; i++){
            for (Entity entity : p.getWorld().getNearbyEntities(blocks.get(i).getLocation(), 1,1,1)){
                if (!p.getName().equalsIgnoreCase(entity.getName())) {
                    return entity;
                }
            }
        }
        return null;
    }

    public void setGlow(Player p){
        Bukkit.getScheduler().runTask(Plugin, ()->{
            Entity entity = getEntity(p);
            removeGlow(p);
            if (entity == null){
                addPlayer(p, UUID.randomUUID());
                p.sendMessage("§6[!] §c스팟 대상이 감지되지 않았습니다.");
            }
            else{
                addPlayer(p, entity.getUniqueId());
                p.sendMessage("§6[!] §b스팟 대상이 §c" + entity.getName() + "§b(으)로 지정되었습니다.");
                WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata();
                WrappedDataWatcher dataWatcher = WrappedDataWatcher.getEntityWatcher(entity);
                List<WrappedWatchableObject> objects = dataWatcher.getWatchableObjects();
                objects.get(0).setValue((byte) ((byte) objects.get(0).getValue() | 0x40));
                metadata.setMetadata(objects);
                metadata.setEntityID(entity.getEntityId());
                metadata.sendPacket(p);


            }
        });
    }

    public void removeGlow(Player p){
        if (isOn(p)) {
            Entity entity = Bukkit.getEntity(getEntityUUID(p));
            if (entity != null) {
                entity.setFireTicks(5);
            }
        }
    }
}
