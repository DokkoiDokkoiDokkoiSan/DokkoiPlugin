package org.meyason.dokkoi.job;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.constants.GoalList;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.event.player.DeathEvent;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Lonely extends Job {

    public boolean isUltimateActive = false;
    public void setUltimateActive(boolean isUltimateActive) {return;}

    public Lonely() {
        super("孤独者", "ぼっち", 50, 5);

        passive_skill_name = "30歳独身";
        normal_skill_name = "人、発見！退却～！ｗ";
        ultimate_skill_name = "誰にも会いたくね～ｗ";

        skillSound = Sound.ENTITY_SILVERFISH_AMBIENT;
        skillVolume = 1.0f;
        skillPitch = 0.8f;

        ultimateSkillSound = Sound.ENTITY_SILVERFISH_DEATH;
        ultimateSkillVolume = 1.0f;
        ultimateSkillPitch = 0.9f;

    }

    public void setPlayer(Game game, Player player){
        this.game = game;
        this.player = player;
        this.goals = List.of(
                GoalList.LASTMAN,
                GoalList.SHADOW
        );
    }

    public void attachGoal(Goal goal){
        this.goal = goal;
        if(goal.tier == Tier.TIER_1){
            twiceCoolTimeSkill();
        }
        passive_skill_description = List.of(
                Component.text("§b30秒間ダメージを受けず、攻撃をしないでいると自動的に透明化する。\n"),
                Component.text("§b攻撃を受けるか攻撃をすると解除される。")
        );

        normal_skill_description = List.of(
                Component.text("§b自分の位置の半径15m以内にいるプレイヤーに盲目と発光を10秒間付与する。\n"),
                Component.text("§cCT " + getCoolTimeSkill() + "秒\n")
        );

        ultimate_skill_description = List.of(
                Component.text("§b次の強制発光を自分のみ無効化出来る。\n"),
                Component.text("§cマッチ中一度だけ発動可能\n")
        );
    }

    public void ready(){
        passive();
    }

    public void skill(){
        List<Player> targetPlayers = new ArrayList<>();
        Vector center = player.getLocation().toVector();
        for(Player p : game.getGameStatesManager().getAlivePlayers()){
            if(p.getUniqueId().equals(player.getUniqueId())){
                continue;
            }
            Vector target = p.getLocation().toVector();
            if(center.distance(target) <= 15.0){
                targetPlayers.add(p);
            }
        }

        for(Player target : targetPlayers){
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10 * 20, 1));
            target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 10 * 20, 1));
        }
    }

    public void ultimate(){
        this.isUltimateActive = true;
        // アルティメットアイテムを削除
        for(ItemStack item : player.getInventory().getContents()){
            if(item == null) continue;
            if(item.getItemMeta() != null){
                ItemMeta meta = item.getItemMeta();
                PersistentDataContainer container = meta.getPersistentDataContainer();
                NamespacedKey itemKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
                if(GameItem.isCustomItem(item)){
                    if(Objects.equals(container.get(itemKey, PersistentDataType.STRING), GameItemKeyString.ULTIMATE_SKILL)){
                        item.setAmount(0);
                        break;
                    }
                }
            }
        }
    }

    public void passive(){
        BukkitRunnable passiveTask = new BukkitRunnable(){
            int count = 0;
            boolean isHide = false;
            @Override
            public void run() {
                if(!game.getGameStatesManager().getAlivePlayers().contains(player)){
                    player.removePotionEffect(PotionEffectType.INVISIBILITY);
                    this.cancel();
                    return;
                }
                if(game.getGameStatesManager().getGameState() != GameState.IN_GAME){
                    player.removePotionEffect(PotionEffectType.INVISIBILITY);
                    this.cancel();
                    return;
                }

                if(!game.getGameStatesManager().getDamagedPlayers().contains(player) && !game.getGameStatesManager().getAttackedPlayers().contains(player)){
                    count++;
                    if(count >= 30){
                        if(!isHide){
                            player.sendMessage(Component.text("§a[孤独者] §b気配が薄れていく……"));
                            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false));
                            isHide = true;
                        }
                    }
                }else{
                    count = 0;
                    if(player.hasPotionEffect(PotionEffectType.INVISIBILITY)){
                        if(isHide) {
                            player.sendMessage(Component.text("§c[孤独者] §bばれてしまった。透明化が解除された。"));
                            player.removePotionEffect(PotionEffectType.INVISIBILITY);
                            isHide = false;
                        }
                    }
                }
            }
        };
        passiveTask.runTaskTimer(Dokkoi.getInstance(), 0L, 20L);
    }
}
