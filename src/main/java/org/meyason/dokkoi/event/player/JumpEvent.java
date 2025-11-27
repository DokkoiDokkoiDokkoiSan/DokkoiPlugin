package org.meyason.dokkoi.event.player;

import net.kyori.adventure.text.Component;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.goal.Debug;
import org.meyason.dokkoi.goal.Goal;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.entity.MoveEntityEvent;

public class JumpEvent implements EventListener<MoveEntityEvent> {

    @Override
    public void handle(MoveEntityEvent event) throws Exception {
        if(event.entity() instanceof Player player){
            player.sendMessage(Component.text("aaaa"));
        }
    }
}
