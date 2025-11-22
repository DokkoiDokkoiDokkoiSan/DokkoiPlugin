package org.meyason.dokkoi.job;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GoalList;
import org.meyason.dokkoi.event.player.DeathEvent;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.scheduler.SkillScheduler;

import java.util.List;

public class Executor extends Job{

    public Executor() {
        super("執行者", "執行者", 30, 200);
    }

    public void setPlayer(Game game, Player player){
        this.game = game;
        this.player = player;
        this.goals = List.of(
                GoalList.LASTMAN
        );
    }

    public void skill(Player target){
        Integer value = game.getGameStatesManager().getKillCounts().get(player);
        int killCount = value.intValue();
        int damage;
        if(killCount == 0){
            damage = 5;
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 3, 1));
        }else if(killCount == 1){
            damage = 10;
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 5, 1));
            target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 5, 1));
        }else if(killCount == 2){
            damage = 20;
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 10, 1));
            target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 10, 1));
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10, 1));
        }else{
            damage = 300;
        }
        if(damage < target.getHealth()){
            target.setHealth(target.getHealth() - damage);
        }else{
            DeathEvent.kill(target, player);
        }
        return;
    }

    public void ultimate(){
        for(Player player : game.getGameStatesManager().getAlivePlayers()){
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 10, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10, 1));
        }
        player.setWalkSpeed(0.8f);
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setWalkSpeed(0.2f);
            }
        }.runTaskLater(Dokkoi.getInstance(), 20L * 10L);
    }
}
