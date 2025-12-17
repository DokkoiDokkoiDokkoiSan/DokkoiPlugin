package org.meyason.dokkoi.job;

import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GoalList;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.goal.Goal;

import java.util.List;

public class Sniper extends Job {

    public Sniper() {
        super(
                "狙撃手",
                "§d遠距離攻撃のスペシャリスト。",
                30,
                200
        );
        passive_skill_name += "§7日本製武器";
        normal_skill_name += "§3私の魂への狙い澄ました一撃";
        ultimate_skill_name += "§6俺自身が壁になることだ";
        skillSound = Sound.ENTITY_WARDEN_NEARBY_CLOSEST;
        skillVolume = 1.0f;
        skillPitch = 1.0f;

        ultimateSkillSound = Sound.ENTITY_PHANTOM_BITE;
        ultimateSkillVolume = 1.0f;
        ultimateSkillPitch = 1.0f;
        setRemainCoolTimeSkillUltimate(200);
    }

    public void setPlayer(Game game, Player player){
        this.game = game;
        this.player = player;
        this.goals = List.of(
                GoalList.KILLER,
                GoalList.ASSASIN,
                GoalList.ESCAPEFROMUNKOV
        );
    }


    public void attachGoal(Goal goal){
        this.goal = goal;
        if(goal.tier == Tier.TIER_1){
            twiceCoolTimeSkill();
        }
        passive_skill_description = List.of(
                Component.text("§5アイテムの弓と銃の攻撃に固定1ダメージを追加する。")
        );

        normal_skill_description = List.of(
                Component.text("§5次に撃つ弓か銃の1発のダメージに固定10ダメージを追加する。"),
                Component.text("§5当たった対象に10秒間の移動速度低下Lv2を付与する。"),
                Component.text("§cCT " + getCoolTimeSkill() + "秒")
        );

        ultimate_skill_description = List.of(
                Component.text("§5自身に移動不能、透明化、ダメージ無効を20秒間与える。"),
                Component.text("§cCT " + getCoolTimeSkillUltimate() + "秒")
        );
    }

    public void ready(){
    }

    public void skill(){
        game.getGameStatesManager().setSniperSkillActive(true);
    }

    public void ultimate(){
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 20, Integer.MAX_VALUE));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 20, Integer.MAX_VALUE));

        game.getGameStatesManager().addDamageCutPercent(player.getUniqueId(), 100);

        new BukkitRunnable(){
            @Override
            public void run(){
                if(!player.isOnline()){
                    return;
                }
                game.getGameStatesManager().removeDamageCutPercent(player.getUniqueId());
            }
        }.runTaskLater(Dokkoi.getInstance(), 20 * 20);
    }
}
