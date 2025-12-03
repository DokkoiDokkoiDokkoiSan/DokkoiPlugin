package org.meyason.dokkoi.item.battleitem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class RedHelmet extends CustomItem {

    public static final String id = "red_helmet";

    public RedHelmet() {
        super(
                id,
                "§c§l赤い帽子",
                ItemStack.of(Material.LEATHER_HELMET),
                1);
        List<Component> lore = List.of(
                Component.text("§5関係ない人を殺そうとした人がつける帽子"),
                Component.text("§5サイズが合ってなくて結構きつい。脱げない。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5着用中は勝利条件に関係ない殺人が可能になるが、全員にノーペナルティで殺されるようになる。殺した相手には30LPを付与する。"),
                Component.text("§5また、着用中は毒Lv255が常時付与される。この毒は役職、アイテム効果などで防ぐことは出来ない。脱ぐことは出来ない。")
        );
        setDescription(lore);
    }

    @Override
    protected void registerItemFunction() {
        default_setting = (item) -> {
            ItemMeta meta = item.getItemMeta();
            if(meta instanceof LeatherArmorMeta leatherMeta){
                leatherMeta.setColor(org.bukkit.Color.RED);
                leatherMeta.addEnchant(Enchantment.BINDING_CURSE, 1, true); // ついでに呪い
                item.setItemMeta(leatherMeta);
            }
            return item;
        };
    }

    public void setPlayerHead(Player player) {
        // もし既に頭装備がある場合は外してドロップさせる
        NamespacedKey key = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
        ItemStack currentHelmet = player.getInventory().getHelmet();
        if (currentHelmet != null && currentHelmet.getType() != Material.AIR) {
            ItemMeta meta = currentHelmet.getItemMeta();
            if(meta != null){
                PersistentDataContainer container = meta.getPersistentDataContainer();
                if(container.has(key, org.bukkit.persistence.PersistentDataType.STRING)){
                    String tag = container.get(key, org.bukkit.persistence.PersistentDataType.STRING);
                    if(tag != null && tag.equals(RedHelmet.id)){
                        // 既に赤い帽子を装備している場合は何もしない
                        return;
                    }
                }
            }
            player.getWorld().dropItemNaturally(player.getLocation(), currentHelmet);
        }
        ItemStack newHelmet = this.baseItem.clone();
        ItemMeta meta = newHelmet.getItemMeta();
        if(meta instanceof LeatherArmorMeta leatherMeta){
            leatherMeta.setColor(org.bukkit.Color.RED);
            leatherMeta.addEnchant(Enchantment.BINDING_CURSE, 1, true); // ついでに呪い
            newHelmet.setItemMeta(leatherMeta);
        }
        player.getInventory().setHelmet(newHelmet);
        player.setMaxHealth(1);
        player.setHealth(1);
        //毒
        player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, Integer.MAX_VALUE, 255, false, false));
    }
}
