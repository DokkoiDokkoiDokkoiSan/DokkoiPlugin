package org.meyason.dokkoi.job;

import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.meyason.dokkoi.constants.GoalList;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.goal.Goal;

import java.util.List;

public class Prayer extends Job {

    public Prayer() {
        super("信仰者", "パチカス", 5, 300);

        passive_skill_name = "パチンカス";
        normal_skill_name = "全回転";
        ultimate_skill_name = "50:50";

        skillSound = Sound.UI_TOAST_CHALLENGE_COMPLETE;
        skillVolume = 1.0f;
        skillPitch = 1.2f;

        ultimateSkillSound = Sound.BLOCK_PORTAL_TRAVEL;
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
                Component.text("§bガチャ回転数が10回溜まる度に与ダメージが1と最大体力が10増加する。"),
                Component.text("§b開けたことのないチェストを開く度に『ガチャポイント』が貰える。")
        );

        normal_skill_description = List.of(
                Component.text("§bガチャポイントを消費してガチャガチャを引くことが出来る。"),
                Component.text("§bガチャガチャからはランダムでアイテムが出てくる。"),
                Component.text("§bまた、出たアイテムのレアリティによって周りの敵にデバフを与える。"),
                Component.text("§cCT " + getCoolTimeSkill() + "秒"),
                Component.text("§6レアリティ一覧"),
                Component.text("§fR(50%): §2半径20m以内の敵に固定1ダメージ与える。"),
                Component.text("§9SR(30%): §2半径20m以内の敵に移動速度低下Lv2を10秒間与える。"),
                Component.text("§5UR(15%): §2半径20m以内の敵の与ダメージを10秒間1固定にする。"),
                Component.text("§6LR(4%): §2自分が10秒間攻撃、状態異常効果を受けなくなる。"),
                Component.text("§dKETSU(0.9%): §2半径20m以内の敵に行動不能を10秒間与える。")
        );

        ultimate_skill_description = List.of(
                Component.text("§b自分とランダムなプレイヤーを異空間に転移させ、どちらかが死ぬ。"),
                Component.text("§cここでの死亡は殺害判定に含まれない。")
        );
    }

    public void ready(){}
}
