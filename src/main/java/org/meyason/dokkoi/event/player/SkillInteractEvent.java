package org.meyason.dokkoi.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.item.jobitem.Skill;
import org.meyason.dokkoi.job.Job;

public class SkillInteractEvent{

    public static void onSkillInteract(PlayerInteractEvent event, String itemID){
        Game game = Game.getInstance();
        Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if(game.getGameStatesManager().getGameState() == GameState.IN_GAME) {

            event.setCancelled(true);
            GameStatesManager manager = game.getGameStatesManager();
            Job job = manager.getPlayerJobs().get(player.getUniqueId());

            // スキル発動
            if (itemID.equals(Skill.id)) {
                job.executeSkill();
            } else {
                job.executeUltimateSkill();
            }
        }
    }
}
