package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.meyason.dokkoi.game.Game;

public class Debug extends  Goal{

    private int sneakCount = 0;
    private int jumpCount = 0;

    public Debug() {
        super("debug", "デバッグ用，スニークすれば達成，ジャンプすると失敗");
    }

    @Override
    public void setGoal(Game game, Player player){
        this.game = game;
        this.player = player;
    }

    @Override
    public void addItem(){
        return;
    }

    @Override
    public boolean isAchieved(){
        player.sendMessage(Component.text("Sneak Count: " + sneakCount + ", Jump Count: " + jumpCount));
        if(jumpCount > 0){
            return false;
        } else {
            return sneakCount >= 1;
        }
    }

    public int getSneakCount(){return sneakCount;}
    public int getJumpCount(){return jumpCount;}
    public void incrementSneakCount(){sneakCount++;}
    public void incrementJumpCount(){jumpCount++;}
}
