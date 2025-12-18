package org.meyason.dokkoi.network;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import net.minecraft.world.entity.Pose;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;

public class PacketProcess {

    public static PacketContainer hideNameTag(Player player, @Nullable PacketContainer packet) {
        List<WrappedDataValue> wrappedDataValues;
        if(packet == null) {
            packet = PacketData.create(player);
            wrappedDataValues = WrappedDataWatcher.getEntityWatcher(player).toDataValueCollection();
        }else{
            wrappedDataValues = packet.getDataValueCollectionModifier().read(0);
        }
        wrappedDataValues.add(new WrappedDataValue(0, WrappedDataWatcher.Registry.get(Byte.class), (byte)0x02));
        wrappedDataValues.add(new WrappedDataValue(6, WrappedDataWatcher.Registry.get(Pose.class), Pose.CROUCHING));
        packet.getDataValueCollectionModifier().write(0, wrappedDataValues);
        return packet;
    }

    public static PacketContainer showNameTag(Player player, @Nullable PacketContainer packet) {
        List<WrappedDataValue> wrappedDataValues;
        if(packet == null) {
            packet = PacketData.create(player);
            wrappedDataValues = WrappedDataWatcher.getEntityWatcher(player).toDataValueCollection();
        }else{
            wrappedDataValues = packet.getDataValueCollectionModifier().read(0);
        }
        wrappedDataValues.add(new WrappedDataValue(0, WrappedDataWatcher.Registry.get(Byte.class), (byte)0x00));
        packet.getDataValueCollectionModifier().write(0, wrappedDataValues);
        return packet;
    }
}
