package org.meyason.dokkoi.network;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

import java.util.List;

public class PacketData {

    public static PacketContainer create(Player player){
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        List<WrappedDataValue> wrappedDataValues = WrappedDataWatcher.getEntityWatcher(player).toDataValueCollection();
        packet.getDataValueCollectionModifier().write(0, wrappedDataValues);
        packet.getIntegers().write(0, player.getEntityId());
        return packet;
    }
}
