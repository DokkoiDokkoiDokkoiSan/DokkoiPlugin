package org.meyason.dokkoi.job;

import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.meyason.dokkoi.constants.GoalList;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.goal.Goal;

import java.util.List;

public class IronMaiden extends Job {

    public IronMaiden() {
        super("鉄処女", "鉄処女", 30, 200);
        passive_skill_name = "こっち見ろ！ばか！";
        normal_skill_name = "あっち見ろ！あほ！";
        ultimate_skill_name = "あれ見てみろ！かす！";

        skillSound = Sound.ITEM_TRIDENT_RETURN;
        skillVolume = 1.0f;
        skillPitch = 1.0f;
        ultimateSkillSound = Sound.ITEM_TRIDENT_THROW;
        ultimateSkillVolume = 1.0f;
        ultimateSkillPitch = 1.0f;
    }

    public void setPlayer(Game game, Player player){
        this.game = game;
        this.player = player;
        this.goals = List.of(
                GoalList.LASTMAN
        );
    }

    public void attachGoal(Goal goal){
        this.goal = goal;
        if(goal.tier == Tier.TIER_1){
            twiceCoolTimeSkill();
        }
        passive_skill_description = List.of(
                Component.text("§bプレイヤーのことを見ている間そのプレイヤーの視線を自分に固定させる。")
        );

        normal_skill_description = List.of(
                Component.text("§bパッシブの視線誘導を10秒間180°後ろに視線を固定に変更する。"),
                Component.text("§cCT " + getCoolTimeSkill() + "秒")
        );

        ultimate_skill_description = List.of(
                Component.text("§bレイピアを手に入れる。"),
                Component.text("§bレイピアを投げて着弾した位置から半径10m以内にいるプレイヤーの視線をレイピアに固定し続ける。"),
                Component.text("§bまた、自分は視線固定の効果を受けない。"),
                Component.text("§cCT " + getCoolTimeSkillUltimate() + "秒")
        );
    }

    public void ready(){}

    public void passive(){
        Vector lookVec = this.player.getEyeLocation().getDirection().normalize();
    }
}
