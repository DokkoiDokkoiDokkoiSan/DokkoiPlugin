package org.meyason.dokkoi.event.network;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.meyason.dokkoi.Dokkoi;

import java.util.List;

public class DebugPacketListener implements Listener {

    public void register() {
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();

        pm.addPacketListener(new PacketAdapter(Dokkoi.getInstance(),
                ListenerPriority.NORMAL,
                PacketType.Play.Server.ENTITY_METADATA
        ) {
            @Override
            public void onPacketSending(PacketEvent e) {

                PacketContainer packet = e.getPacket();

                int entityId = packet.getIntegers().read(0);
                List<WrappedDataValue> data = packet.getDataValueCollectionModifier().read(0);

                Bukkit.getLogger().info("=== ENTITY_METADATA ===");
                Bukkit.getLogger().info("Entity ID: " + entityId);

                for (WrappedDataValue v : data) {
                    Bukkit.getLogger().info("Index = " + v.getIndex()
                            + ", Value = " + v.getValue().getClass());
                }
                Bukkit.getLogger().info("======================");
            }
        });
    }

    private void logPacket(String direction, PacketEvent event) {
        Player player = event.getPlayer();
        PacketContainer packet = event.getPacket();

        Bukkit.getLogger().info("----- PACKET -----");
        Bukkit.getLogger().info("Direction: " + direction);
        Bukkit.getLogger().info("Player: " + player.getName());
        Bukkit.getLogger().info("Type: " + packet.getType().name());
        if(packet.getType() == PacketType.Play.Server.ENTITY_METADATA) {
            int entityId = packet.getIntegers().read(0);
            Bukkit.getLogger().info("Entity ID: " + entityId);
        }

        try {
            Bukkit.getLogger().info(packet.toString());
        } catch (Exception e) {
            Bukkit.getLogger().warning("Error reading packet: " + e.getMessage());
        }

        Bukkit.getLogger().info("------------------");
    }
}
