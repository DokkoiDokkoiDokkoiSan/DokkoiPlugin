package org.meyason.dokkoi.job;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.meyason.dokkoi.constants.GoalList;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.util.CalculateAreaPlayers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Photographer extends Job {

    private ArrayList<UUID> takenPhotoPlayersUUID;
    private boolean isTwoShotPhotoTaken;

    public Photographer() {
        super("写真家", "写真家", 3, 200);
        passive_skill_name = "§9§l戦場§r§7カメラマン";
        normal_skill_name = "§9§l渡部陽一 §r§cf§6e§ea§at§r.§l§b西§d野§9カ§1ナ";
        ultimate_skill_name = "§6一旦全員晒してみた";

        skillSound = Sound.ENTITY_SPLASH_POTION_BREAK;
        skillVolume = 1.0f;
        skillPitch = 0.8f;

        ultimateSkillSound = Sound.ENTITY_ELDER_GUARDIAN_CURSE;
        ultimateSkillVolume = 1.0f;
        ultimateSkillPitch = 0.8f;

        setRemainCoolTimeSkillUltimate(200);

        this.isTwoShotPhotoTaken = false;
    }

    private boolean canAddTakenPhotoPlayerNewly(UUID targetPlayer){
        return !this.takenPhotoPlayersUUID.contains(targetPlayer);
    }

    private void addTakenPhotoPlayer(UUID targetPlayer){
        if(!this.takenPhotoPlayersUUID.contains(targetPlayer)){
            this.takenPhotoPlayersUUID.add(targetPlayer);
        }
    }

    public int getTakenPhotoPlayersCount(){
        return this.takenPhotoPlayersUUID.size();
    }

    private void removeTakenPhotoPlayer(UUID targetPlayer){
        this.takenPhotoPlayersUUID.remove(targetPlayer);
    }

    public boolean isTakenPhotoPlayer(UUID targetPlayer){
        return this.takenPhotoPlayersUUID.contains(targetPlayer);
    }

    public boolean isTwoShotPhotoTaken(){
        return this.isTwoShotPhotoTaken;
    }

    public void passive(){

    }

    @Override
    public void setPlayer(Game game, Player player) {
        this.game = game;
        this.player = player;
        this.takenPhotoPlayersUUID = new ArrayList<>();
        this.goals = List.of(
                GoalList.createDefender(),
                GoalList.createPhotoAllPlayer(),
                GoalList.createTakeTwoShot()
        );
    }

    @Override
    public void attachGoal(Goal goal) {
        this.goal = goal;
        if(goal.tier == Tier.TIER_1){
            twiceCoolTimeSkill();
        }
        passive_skill_description = List.of(
                Component.text("§5カメラで撮影した人間の数でステータスが変動する。"),
                Component.text("§a0人　与ダメージ0固定、移動速度低下Lv1が常時付与されている。"),
                Component.text("§a1～3人　与ダメージが1増加が常時付与されている。"),
                Component.text("§a4～6人　与ダメージが2増加、移動速度上昇Lv1が常時付与されている。"),
                Component.text("§a7～8人　与ダメージが4増加、移動速度上昇Lv1が常時付与されている。"),
                Component.text("§a9人　与ダメージが5増加、移動速度上昇Lv2が常時付与されている。また、自分以外の全てのプレイヤーを常時発光させる。")
        );

        normal_skill_description = List.of(
                Component.text("§5使用すると自分の前方10m以内の範囲を撮影する。撮影範囲に入っていたプレイヤーは発光を5秒受ける"),
                Component.text("§cCT " + getCoolTimeSkill() + "秒")
        );

        ultimate_skill_description = List.of(
                Component.text("§5今までカメラで撮影した写真に写っているプレイヤーに移動不能を○(対象人数×2)秒間与える。"),
                Component.text("§cCT " + getCoolTimeSkillUltimate() + "秒")
        );
    }

    @Override
    public void ready() {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 1));
        game.getGameStatesManager().setIsEnableAttack(player.getUniqueId(), false);
    }

    private void updatePassive(){
        if(takenPhotoPlayersUUID.isEmpty()) return;
        if(this.takenPhotoPlayersUUID.size() == 1){
            this.player.removePotionEffect(PotionEffectType.SLOWNESS);
            game.getGameStatesManager().setIsEnableAttack(player.getUniqueId(), true);
            game.getGameStatesManager().addAdditionalDamage(this.player.getUniqueId(), 1);
        } else if(this.takenPhotoPlayersUUID.size() == 4){
            this.player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
            game.getGameStatesManager().addAdditionalDamage(this.player.getUniqueId(), 1);
        } else if(this.takenPhotoPlayersUUID.size() == 7){
            game.getGameStatesManager().addAdditionalDamage(this.player.getUniqueId(), 2);
        } else if(this.takenPhotoPlayersUUID.size() == 9) {
            this.player.removePotionEffect(PotionEffectType.SPEED);
            this.player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
            game.getGameStatesManager().addAdditionalDamage(this.player.getUniqueId(), 1);
            for(UUID uuid : this.game.getGameStatesManager().getAlivePlayers()){
                if(uuid.equals(this.player.getUniqueId())) continue;
                Player targetPlayer = Bukkit.getPlayer(uuid);
                if(targetPlayer == null) continue;
                targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 1));
            }
        }
    }

    public void skill(){
        if(this.player.isSneaking()){
            ArrayList<UUID> targets = new ArrayList<>();
            this.player.sendMessage(Component.text("§a=====写真を撮影していないプレイヤー一覧====="));
            for(UUID uuid : this.game.getGameStatesManager().getJoinedPlayers()){
                if(this.isTakenPhotoPlayer(uuid)) continue;
                if(uuid.equals(this.player.getUniqueId())) continue;
                targets.add(uuid);
            }
            if(targets.size() == this.game.getGameStatesManager().getJoinedPlayers().size()){
                this.player.sendActionBar(Component.text("§a全員の写真を撮影済みだ！"));
                return;
            }
            for(UUID uuid : targets){
                this.player.sendMessage(Component.text("§d" + Objects.requireNonNull(Bukkit.getPlayer(uuid)).getName()));
            }
        }else{
            ArrayList<Player> playerInSight = CalculateAreaPlayers.getPlayersInSight(this.game, this.player, 180);
            int quantityPlayers = playerInSight.size();
            if(quantityPlayers == 0){
                this.player.sendActionBar(Component.text("§c写真に誰も写っていない..."));
                return;
            }
            if(quantityPlayers >= 2 && !this.isTwoShotPhotoTaken){
                this.isTwoShotPhotoTaken = true;
            }
            this.player.sendMessage(Component.text("§a=====撮影結果====="));
            for(Player p : playerInSight){
                if(this.canAddTakenPhotoPlayerNewly(p.getUniqueId())){
                    this.addTakenPhotoPlayer(p.getUniqueId());
                    this.updatePassive();
                }else{
                    this.addTakenPhotoPlayer(p.getUniqueId());
                }
                p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 5*20, 1));
                this.player.sendMessage(Component.text("§6" + p.getName() + "§r§aの写真を撮影した！"));
            }
        }
    }

    public boolean canUseUltimate(){
        return !this.takenPhotoPlayersUUID.isEmpty();
    }

    public void ultimate(){
        int affectedSeconds = this.takenPhotoPlayersUUID.size() * 2;
        if(affectedSeconds == 0){
            this.player.sendActionBar(Component.text("§c写真に写っているプレイヤーがいない..."));
            return;
        }
        for(UUID uuid : this.takenPhotoPlayersUUID){
            Player targetPlayer = Bukkit.getPlayer(uuid);
            if(targetPlayer == null) continue;
            targetPlayer.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.SLOWNESS, affectedSeconds * 20, 255));
            targetPlayer.sendMessage(Component.text("§c写真が晒され、移動不能になった！"));
        }
        this.player.sendMessage(Component.text("§a写真に写っているプレイヤー全員に移動不能を" + affectedSeconds + "秒間付与した！"));
    }
}
