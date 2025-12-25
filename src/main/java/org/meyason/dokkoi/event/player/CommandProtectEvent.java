package org.meyason.dokkoi.event.player;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameState;

public class CommandProtectEvent implements Listener {

    /**
     * プレイヤーがコマンドを実行する前にチェック
     * ゲーム中に危険なコマンドをブロックする
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().toLowerCase();

        if (isDangerousCommand(command)) {
            event.setCancelled(true);
            player.sendMessage(Component.text("§c[システム] kill @e コマンドは使用できません。"));
        }
    }


    /**
     * 危険なコマンドかどうかをチェック
     */
    private boolean isDangerousCommand(String command) {
        // スラッシュを除去
        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        // killコマンドのチェック
        if (command.startsWith("kill")) {
            // "/kill @e" や "/kill @a" などのセレクタを含む場合
            if (command.contains("@e")) {
                return true;
            }
        }


        return false;
    }
}

