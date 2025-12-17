package org.meyason.dokkoi.network;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import net.minecraft.world.entity.Pose;
import org.bukkit.entity.Player;

import java.util.List;

public class PacketProcess {

    public static PacketContainer hideNameTag(Player player, PacketContainer packet) {
        List<WrappedDataValue> wrappedDataValues = WrappedDataWatcher.getEntityWatcher(player).toDataValueCollection();
        wrappedDataValues.remove(0);
        wrappedDataValues.remove(6);
        wrappedDataValues.add(new WrappedDataValue(0, WrappedDataWatcher.Registry.get(Byte.class), (byte)0x02));
        wrappedDataValues.add(new WrappedDataValue(6, WrappedDataWatcher.Registry.get(Pose.class), Pose.CROUCHING));
        packet.getDataValueCollectionModifier().write(0, wrappedDataValues);
        return packet;
    }
}
