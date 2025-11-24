package org.meyason.dokkoi.job;

import io.papermc.paper.entity.LookAnchor;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.constants.GoalList;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.job.Rapier;

import java.util.List;

public class IronMaiden extends Job {

    public boolean isUsingSkill = false;

    private int count = 0;

    public IronMaiden() {
        super("鉄処女", "鉄処女", 30, 200);
        passive_skill_name = "こっち見ろ！ばか！";
        normal_skill_name = "あっち見ろ！あほ！";
        ultimate_skill_name = "あれ見てみろ！かす！";

        skillSound = Sound.ITEM_TRIDENT_RETURN;
        skillVolume = 1.0f;
        skillPitch = 1.0f;
        ultimateSkillSound = Sound.ENTITY_ITEM_PICKUP;
        ultimateSkillVolume = 1.0f;
        ultimateSkillPitch = 1.0f;
    }

    public void setPlayer(Game game, Player player){
        this.game = game;
        this.player = player;
        this.goals = List.of(
                GoalList.TIER3KILLER
        );
    }

    public int getCount(){
        return count;
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

    public void ready(){passive();}

    public void passive(){
        BukkitRunnable passiveTask = new BukkitRunnable(){
            @Override
            public void run(){
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
                Vector lookVec = player.getEyeLocation().getDirection().normalize();

                World world = player.getWorld();
                RayTraceResult entityResult = world.rayTraceEntities(
                        player.getEyeLocation(),
                        lookVec,
                        500,
                        e->!e.equals(player)
                );
                RayTraceResult blockResult = world.rayTraceBlocks(
                        player.getEyeLocation(),
                        lookVec,
                        500
                );

                if(entityResult != null) {
                    Entity target = entityResult.getHitEntity();
                    if (blockResult != null) {
                        if (entityResult.getHitPosition().distanceSquared(player.getEyeLocation().toVector()) >
                                blockResult.getHitPosition().distanceSquared(player.getEyeLocation().toVector())) {
                            target = null;
                        }
                    }
                    if (target instanceof Player targetPlayer) {
                        Vector toTargetVec = targetPlayer.getEyeLocation().toVector().subtract(player.getEyeLocation().toVector()).normalize();
                        if (!isUsingSkill) {
                            toTargetVec.multiply(-1);
                        }
                        Location targetLookAt = player.getEyeLocation().setDirection(toTargetVec);
                        targetLookAt.setX(player.getLocation().getX());
                        targetLookAt.setY(player.getLocation().getY());
                        targetLookAt.setZ(player.getLocation().getZ());
                        targetPlayer.teleport(targetLookAt);
                        targetPlayer.playSound(targetPlayer, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                        if(isUsingSkill){
                            targetPlayer.sendMessage(Component.text("§c[鉄処女]あっち見ろ！あほ！"));
                        }else {
                            targetPlayer.sendMessage(Component.text("§c[鉄処女]こっち見ろ！ばか！"));
                        }
                        count++;
                    }
                }
            }
        };
        passiveTask.runTaskTimer(Dokkoi.getInstance(), 0L, 20L);
    }

    public void skill(){
        isUsingSkill = true;
        new BukkitRunnable(){
            @Override
            public void run(){
                isUsingSkill = false;
            }
        }.runTaskLater(Dokkoi.getInstance(), 10 * 20L);
    }

    public void ultimate(){
        CustomItem rapier = GameItem.getItem(GameItemKeyString.RAPIER);
        if(rapier == null){
            this.player.sendMessage("§6エラーが発生しました．管理者に連絡してください：レイピア取得失敗");
            return;
        }
        ItemStack itemStack = rapier.getItem();
        PlayerInventory inv = player.getInventory();
        if(rapier instanceof Rapier rapierItem){
            rapierItem.setPlayer(game, player);
        }
        inv.addItem(itemStack);
    }
}
