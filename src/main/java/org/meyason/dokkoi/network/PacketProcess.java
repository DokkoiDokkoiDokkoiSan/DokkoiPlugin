package org.meyason.dokkoi.network;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import net.minecraft.world.entity.Pose;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;

public class PacketProcess {

    public static PacketContainer hideNameTag(Entity entity, @Nullable PacketContainer packet) {
        List<WrappedDataValue> wrappedDataValues;
        if(packet == null) {
            packet = PacketData.create(entity);
            wrappedDataValues = WrappedDataWatcher.getEntityWatcher(entity).toDataValueCollection();
        }else{
            wrappedDataValues = packet.getDataValueCollectionModifier().read(0);
        }
        byte flags = getCurrentEntityFlags(wrappedDataValues);
        flags |= 0x02;
        wrappedDataValues.add(new WrappedDataValue(0, WrappedDataWatcher.Registry.get(Byte.class), flags));
        if(entity instanceof Player){
            wrappedDataValues.add(new WrappedDataValue(6, WrappedDataWatcher.Registry.get(Pose.class), Pose.CROUCHING));
        }
        packet.getDataValueCollectionModifier().write(0, wrappedDataValues);
        return packet;
    }

    public static PacketContainer showNameTag(Entity entity, @Nullable PacketContainer packet) {
        List<WrappedDataValue> wrappedDataValues;
        if(packet == null) {
            packet = PacketData.create(entity);
            wrappedDataValues = WrappedDataWatcher.getEntityWatcher(entity).toDataValueCollection();
        }else{
            wrappedDataValues = packet.getDataValueCollectionModifier().read(0);
        }
        byte flags = getCurrentEntityFlags(wrappedDataValues);
        flags &= ~0x02;
        wrappedDataValues.add(new WrappedDataValue(0, WrappedDataWatcher.Registry.get(Byte.class), flags));
        packet.getDataValueCollectionModifier().write(0, wrappedDataValues);
        return packet;
    }

    private static byte getCurrentEntityFlags(List<WrappedDataValue> values) {
        for (WrappedDataValue v : values) {
            if (v.getIndex() == 0 && v.getValue() instanceof Byte b) {
                return b;
            }
        }
        return 0;
    }
}
