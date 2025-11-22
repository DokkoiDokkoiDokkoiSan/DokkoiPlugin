package org.meyason.dokkoi.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.goal.Debug;
import org.meyason.dokkoi.goal.Goal;

public class SneakEvent implements Listener {

    public SneakEvent(){}

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event){
        Game game = Game.getInstance();
        Player player = event.getPlayer();
        if(game.getGameStatesManager().getPlayerGoals().containsKey(player)){
            Goal goal = game.getGameStatesManager().getPlayerGoals().get(player);
            if(goal instanceof Debug){
                ((Debug) goal).incrementSneakCount();
            }
        }

    }
}
