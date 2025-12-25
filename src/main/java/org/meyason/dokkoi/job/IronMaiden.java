package org.meyason.dokkoi.job;

import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
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
import org.meyason.dokkoi.exception.NoGameItemException;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.goal.MassTierKiller;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.jobitem.Rapier;

import java.util.List;

public class IronMaiden extends Job {

    public boolean isUsingSkill = false;

    private Rapier hadRapier;

    private int count = 0;

    public IronMaiden() {
        super("鉄処女", "鉄処女", 30, 200);
        passive_skill_name += "§7こっち見ろ！ばか！";
        normal_skill_name += "§3あっち見ろ！あほ！";
        ultimate_skill_name += "§6あれ見てみろ！かす！";

        skillSound = Sound.ENTITY_ELDER_GUARDIAN_CURSE;
        skillVolume = 1.0f;
        skillPitch = 1.0f;
        ultimateSkillSound = Sound.ENTITY_ITEM_PICKUP;
        ultimateSkillVolume = 1.0f;
        ultimateSkillPitch = 1.0f;
        setRemainCoolTimeSkillUltimate(200);
    }

    public void setPlayer(Game game, Player player){
        this.game = game;
        this.player = player;
        this.goals = List.of(
                GoalList.MASSTIERKILLER,
                GoalList.MAIDENGAZER,
                GoalList.POLICE
        );
    }

    public void addCount(){
        count += 1;
    }
    public int getCount(){
        return count / 2;
    }

    public Rapier getRapier(){return hadRapier;}

    public void attachGoal(Goal goal){
        this.goal = goal;
        if(goal.tier == Tier.TIER_1){
            twiceCoolTimeSkill();
        }
        passive_skill_description = List.of(
                Component.text("§5プレイヤーのことを見ている間そのプレイヤーの視線を自分に固定させる。")
        );

        normal_skill_description = List.of(
                Component.text("§5パッシブの視線誘導を10秒間180°後ろに視線を固定に変更する。"),
                Component.text("§cCT " + getCoolTimeSkill() + "秒")
        );

        ultimate_skill_description = List.of(
                Component.text("§5レイピアを手に入れる。"),
                Component.text("§5レイピアを投げて着弾した位置から半径10m以内にいるプレイヤーの視線をレイピアに固定し続ける。"),
                Component.text("§5また、自分は視線固定の効果を受けない。"),
                Component.text("§cCT " + getCoolTimeSkillUltimate() + "秒")
        );
    }

    public void ready(){
        if(goal instanceof MassTierKiller massTierKiller){
            massTierKiller.updateList();
        }
        passive();
    }

    public void passive(){
        BukkitRunnable passiveTask = new BukkitRunnable(){
            @Override
            public void run(){
                if(!game.getGameStatesManager().getAlivePlayers().contains(player.getUniqueId())){
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
                        if(targetPlayer.getGameMode() != GameMode.SPECTATOR) {
                            Vector toTargetVec = targetPlayer.getEyeLocation().toVector().subtract(player.getEyeLocation().toVector());

                            // ゼロベクトルの場合はスキップ（同じ位置にいる場合）
                            if (toTargetVec.lengthSquared() < 0.0001) {
                                return;
                            }

                            toTargetVec.normalize();
                            if (!isUsingSkill) {
                                toTargetVec.multiply(-1);
                            }

                            Location targetLookAt = targetPlayer.getEyeLocation().setDirection(toTargetVec);
                            targetLookAt.setX(targetPlayer.getLocation().getX());
                            targetLookAt.setY(targetPlayer.getLocation().getY());
                            targetLookAt.setZ(targetPlayer.getLocation().getZ());

                            // pitch/yawが有限値かチェック
                            if (Double.isFinite(targetLookAt.getYaw()) && Double.isFinite(targetLookAt.getPitch())) {
                                targetPlayer.teleport(targetLookAt);
                                targetPlayer.playSound(targetPlayer, Sound.ENTITY_VILLAGER_NO, 0.2f, 1.0f);
                                if (isUsingSkill) {
                                    targetPlayer.sendActionBar(Component.text("§c[鉄処女]あっち見ろ！あほ！"));
                                } else {
                                    targetPlayer.sendActionBar(Component.text("§c[鉄処女]こっち見ろ！ばか！"));
                                }
                                count += 1;
                            }
                        }
                    }
                }
            }
        };
        passiveTask.runTaskTimer(Dokkoi.getInstance(), 0L, 10L);
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
        try {
            CustomItem rapier = GameItem.getItem(Rapier.id);
            ItemStack itemStack = rapier.getItem();
            PlayerInventory inv = player.getInventory();

            if (hadRapier != null) {
                if (inv.contains(hadRapier.getItem())) {
                    this.player.sendMessage("§6レイピアを先に投擲してください。");
                    return;
                }
            }

            if (rapier instanceof Rapier rapierItem) {
                rapierItem.setPlayer(game, player);
                this.hadRapier = rapierItem;
            }
            inv.addItem(itemStack);

        } catch (NoGameItemException e) {
            player.sendMessage(Component.text("§cレイピアの取得に失敗しました。運営に報告してください。"));
            e.printStackTrace();
        }
    }
}
