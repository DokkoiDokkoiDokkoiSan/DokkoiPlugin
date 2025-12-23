package org.meyason.dokkoi.event.network;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.network.PacketProcess;

public class TransferPacketEvent extends PacketAdapter implements Listener {
    public TransferPacketEvent(Plugin plugin) {
        super(plugin, PacketType.Play.Server.ENTITY_METADATA);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer pk = event.getPacket().deepClone();
        Entity entity = pk.getEntityModifier(event).read(0);
        if(!(entity instanceof Player player)) return;
        if(event.getPlayer().getUniqueId().equals(player.getUniqueId())) return;
        if(player.getGameMode().equals(GameMode.CREATIVE)) return;
        if(
                Game.getInstance().getGameStatesManager().getGameState().equals(GameState.WAITING)
                || Game.getInstance().getGameStatesManager().getGameState().equals(GameState.END)
        ){
            pk = PacketProcess.showNameTag(player, null);
        }else if(
                (Game.getInstance().getGameStatesManager().getGameState().equals(GameState.PREP)
                || Game.getInstance().getGameStatesManager().getGameState().equals(GameState.MATCHING)
                || Game.getInstance().getGameStatesManager().getGameState().equals(GameState.IN_GAME)
                || Game.getInstance().getGameStatesManager().getGameState().equals(GameState.PRE_END))
        ){
            pk = PacketProcess.hideNameTag(player, null);
        }else{
            pk = PacketProcess.showNameTag(player, null);
            Dokkoi.getInstance().getLogger().warning("未知のゲームステートにより、名前表示処理が正常に行われませんでした。処理は継続されます。");
            Dokkoi.getInstance().getLogger().warning("現在のゲームステート: " + Game.getInstance().getGameStatesManager().getGameState());
            Dokkoi.getInstance().getLogger().warning("org.meyason.dokkoi.event.network.TransferPacketEvent::onPacketSending [line 43]");
        }
        event.setPacket(pk);
    }
}
