package org.meyason.dokkoi.item.utilitem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class TakashimaPhone extends CustomItem {

    public static final String id = "takashima_phone";

    public TakashimaPhone() {
        super(id, "§a高島ちゃんの携帯電話", ItemStack.of(Material.AMETHYST_SHARD), 1);
        List<Component> lore = List.of(
                Component.text("§5高島ちゃんが使っていた携帯電話。"),
                Component.text("§5製作者が自分の部屋の引き出し漁ってたら出てきたらしい。"),
                Component.text("§5なんか血ついてね？"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5手に持ちながらチャットをすると§a間宮君の携帯電話§5を持っているプレイヤーのみにチャットを送信する。")
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
