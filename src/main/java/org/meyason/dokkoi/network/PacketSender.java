package org.meyason.dokkoi.network;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

public class PacketSender {

    public static void sendPacket(Player player, PacketContainer packet){
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }
}
