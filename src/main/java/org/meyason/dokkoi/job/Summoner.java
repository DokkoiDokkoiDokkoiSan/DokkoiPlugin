package org.meyason.dokkoi.job;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.constants.GoalList;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.exception.NoGameItemException;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.jobitem.Hikakin;
import org.meyason.dokkoi.item.jobitem.SummonersBrave;
import org.meyason.dokkoi.util.CalculateAreaPlayers;

import java.util.List;
import java.util.UUID;

public class Summoner extends Job {

    public Summoner() {
        super(
            "交霊師",
            "§d死者を蘇生する。",
            40,
            300
        );
        passive_skill_name += "§7死者との交流";
        normal_skill_name += "§3死者の呪術";
        ultimate_skill_name += "§6死者蘇生";
        skillSound = Sound.ENTITY_WARDEN_AMBIENT;
        skillVolume = 1.0f;
        skillPitch = 0.8f;

        ultimateSkillSound = Sound.ITEM_TOTEM_USE;
        ultimateSkillVolume = 10.0f;
        ultimateSkillPitch = 1.0f;
        setRemainCoolTimeSkillUltimate(300);
    }

    public void setPlayer(Game game, Player player){
        this.game = game;
        this.player = player;
        this.goals = List.of(
                GoalList.LASTMAN,
                GoalList.FIFTYPERCENT,
                GoalList.SKELETONSLAYER
        );
        game.getGameStatesManager().setExistSummoner(true);
    }


    public void attachGoal(Goal goal){
        this.goal = goal;
        if(goal.tier == Tier.TIER_1){
            twiceCoolTimeSkill();
        }
        passive_skill_description = List.of(
                Component.text("§5儀礼剣を所持している。死んだ人間が増えるごとに固定与ダメージが1増加する。"),
                Component.text("§5死んだ人間×1、死亡した内藤はカウントされない。")
        );

        normal_skill_description = List.of(
                Component.text("§5半径10m以内のプレイヤーに移動不能を死亡者数秒時間与える。"),
                Component.text("§5死んだ人間×1、死亡した内藤はカウントされない。"),
                Component.text("§cCT " + getCoolTimeSkill() + "秒")
        );

        ultimate_skill_description = List.of(
                Component.text("§5死んだ人間全員を『内藤』として自分の位置に復活させる。"),
                Component.text("§5内藤はアイテムを回収できない。"),
                Component.text("§5内藤は火かき棒を所持している。HPが0になるか、残り時間が1秒になるとスペクテイターに戻る。"),
                Component.text("§cCT " + getCoolTimeSkillUltimate() + "秒")
        );
    }

    public void ready(){
        CustomItem customItem;
        try {
            customItem = GameItem.getItem(SummonersBrave.id);
        } catch (NoGameItemException e) {
            this.player.sendMessage("§4エラーが発生しました。運営に報告してください。：Hikakin取得失敗");
            return;
        }
        ItemStack item = customItem.getItem();
        player.getInventory().addItem(item);
    }


    public void passive(Player targetPlayer) {
        if(game.getGameStatesManager().isNaito(targetPlayer.getUniqueId())){
            return;
        }
        game.getGameStatesManager().addAdditionalDamage(player.getUniqueId(), 1);
        player.sendMessage(Component.text("§b死霊の力により与ダメージが1増加した！"));
    }

    public boolean onSkillTrigger(){
        int deadCount = game.getGameStatesManager().getKillerList().size();
        int freezeTime = deadCount * 20;
        List<Player> players = CalculateAreaPlayers.getPlayersInArea(game, player, player.getLocation(), 10);
        for(Player p : players) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, freezeTime, 255));
            p.sendActionBar(Component.text("§c交霊師の呪術により移動不能になった！"));
        }

        return true;
    }

    public boolean onSkillUltimateTrigger(){
        List<UUID> targetPlayers = game.getGameStatesManager().getVictims();
        if (targetPlayers.isEmpty()) {
            player.sendActionBar(Component.text("§c召喚できる対象がいない。"));
            return false;
        }

        NamespacedKey serialKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.UNIQUE_ITEM);
        for(UUID uuid : targetPlayers){
            Player targetPlayer = Bukkit.getPlayer(uuid);
            if(targetPlayer == null) continue;
            targetPlayer.teleport(player.getLocation());
            targetPlayer.setMaxHealth(40.0);
            targetPlayer.setHealth(targetPlayer.getMaxHealth());
            targetPlayer.setFoodLevel(20);
            targetPlayer.setGameMode(GameMode.ADVENTURE);
            targetPlayer.setCustomName("§6内藤");
            game.getGameStatesManager().addNaito(uuid);

            CustomItem customItem;
            try {
                customItem = GameItem.getItem(Hikakin.id);
            } catch (NoGameItemException e) {
                targetPlayer.sendMessage("§4エラーが発生しました。運営に報告してください。：Hikakin取得失敗");
                continue;
            }
            Hikakin hikakin = (Hikakin) customItem;
            hikakin.setPlayer(game, targetPlayer);
            ItemStack item = hikakin.getItem();
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            String serialUUID = container.get(serialKey, PersistentDataType.STRING);
            game.getGameStatesManager().addCustomItemToSerialMap(serialUUID, hikakin);

            targetPlayer.getInventory().clear();
            targetPlayer.getInventory().addItem(item);

            targetPlayer.sendMessage(Component.text("§a交霊師によって§l§6内藤§r§aとして復活させられた！"));
        }

        return true;
    }
}
