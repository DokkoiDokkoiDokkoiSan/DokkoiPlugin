package org.meyason.dokkoi.network;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

import java.util.List;

public class PacketData {

    public static PacketContainer create(PacketType packetType){
        return new PacketContainer(packetType);
    }
}
