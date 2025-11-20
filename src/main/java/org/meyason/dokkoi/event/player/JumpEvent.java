package org.meyason.dokkoi.event.player;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.goal.Debug;
import org.meyason.dokkoi.goal.Goal;

public class JumpEvent implements Listener {

    public JumpEvent(){}

    @EventHandler
    public void onJump(PlayerJumpEvent event){
        Game game = Game.getInstance();
        Player player = event.getPlayer();
        if(game.getPlayerGoals().containsKey(player)){
            Goal goal = game.getPlayerGoals().get(player);
            if(goal instanceof Debug){
                ((Debug) goal).incrementJumpCount();
            }
        }
    }
}
