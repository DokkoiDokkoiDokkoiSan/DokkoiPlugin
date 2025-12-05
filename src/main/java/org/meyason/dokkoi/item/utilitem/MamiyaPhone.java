package org.meyason.dokkoi.item.utilitem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class MamiyaPhone extends CustomItem {


    public static final String id = "mamiya_phone";

    public MamiyaPhone() {
        super(id, "§b間宮君の携帯電話", ItemStack.of(Material.AMETHYST_SHARD), 1);
        List<Component> lore = List.of(
                Component.text("§5間宮君が使っていた携帯電話。"),
                Component.text("§5持っていると黒波動の発生源を抑えてくれそうな気がする。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5手に持ちながらチャットをすると§a高島ちゃんの携帯電話§5を持っているプレイヤーのみにチャットを送信する。")
        );
        setDescription(lore);
    }

    @Override
    protected void registerItemFunction() {
        default_setting = (item) -> {
            ItemMeta meta = item.getItemMeta();
            if(meta != null){
                item.setItemMeta(meta);
            }
            return item;
        };
    }

}
