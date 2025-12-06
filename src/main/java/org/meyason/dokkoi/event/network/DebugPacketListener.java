package org.meyason.dokkoi.event.network;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.network.PacketData;
import org.meyason.dokkoi.network.PacketSender;

import java.util.List;

public class DebugPacketListener implements Listener {

    public void register() {
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();

        pm.addPacketListener(new PacketAdapter(Dokkoi.getInstance(),
                ListenerPriority.NORMAL,
                PacketType.Play.Server.PLAYER_INFO,
                PacketType.Play.Client.SETTINGS
        ) {
            @Override
            public void onPacketSending(PacketEvent e) {

                PacketContainer packet = e.getPacket();
                if(!packet.getType().equals(PacketType.Play.Server.PLAYER_INFO)) {
                    return;
                }
                Object obj = packet.getHandle();
                if(obj instanceof ClientboundPlayerInfoUpdatePacket){
                    StructureModifier<List<PlayerInfoData>> playerinfo = packet.getPlayerInfoDataLists();
                    playerinfo.read(0).forEach(data -> {;
                        Bukkit.getLogger().info((data == null) ? "null":"なんかしら");
                    });
//                    for(PlayerInfoData infoData : infoDataList){
//                        WrappedGameProfile profile = infoData.getProfile();
//                        PacketContainer pk = PacketData.create(PacketType.Play.Server.PLAYER_INFO);
//                        profile.withName("[DEBUG] FakeName");
//                        PlayerInfoData sendPk = new PlayerInfoData(null, infoData.getLatency(), infoData.getGameMode(), infoData.getDisplayName());
//                        pk.getPlayerInfoDataLists().write(0, List.of(sendPk));
//                    }
                }
            }

            @Override
            public void onPacketReceiving(PacketEvent e) {
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
