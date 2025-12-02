package org.meyason.dokkoi.network;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import org.bukkit.entity.Player;

public class PacketProcess {

    public static PacketContainer hideNameTag(Player player, PacketContainer packet){
        WrappedDataWatcher watcher = new WrappedDataWatcher(player);
        WrappedDataWatcherObject byteMeta = new WrappedDataWatcherObject(0, Registry.get(Byte.class));
        WrappedDataWatcherObject boolMeta = new WrappedDataWatcherObject(6, Registry.get(Boolean.class));
        watcher.setObject(byteMeta, (byte) 0x02);
        watcher.setObject(boolMeta, false);
        packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
        return packet;
    }
}
