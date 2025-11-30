package org.meyason.dokkoi.item.jobitem;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class DrugRecipe extends CustomItem {

    private Game game;
    private Player player;

    public static final String id = "drug_recipe";

    public DrugRecipe() {
        super(id, "おくすり手帳", ItemStack.of(Material.WRITTEN_BOOK), 1);
        isUnique = true;
        List<Component> lore = List.of(
                Component.text("§5お薬の作り方が書いてある手帳。なんと現実でもこのレシピで同じ薬が作れるらしい。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5本を開くと§9『ツヨクナール』『カタクナール』『キズキエール』『ハヤクナール』『コレハマール』§5の作成に必要な材料が記入されている。")
        );
        setDescription(lore);
    }

    @Override
    protected void registerItemFunction() {
        default_setting = (item) -> {
            BookMeta meta = (BookMeta) item.getItemMeta();
            if(meta != null){
                meta.addPage(
                        "§9『ツヨクナール』§a\nステーキ×1\nパンプキンパイ×1\n生鱈×1",
                        "§9『カタクナール』§a\n焼き鳥×1\nパン×1\n生鮭×1",
                        "§9『キズキエール』§a\n金のスイカ×1\nベイクドポテト×1\n生鮭×1",
                        "§9『ハヤクナール』§a\n金のニンジン×1\n焼き豚×1\n生鱈×1",
                        "§9『コレハマール』§a\n金のニンジン×1\n金のスイカ×1\nパンプキンパイ×1"
                );
                item.setItemMeta(meta);
            }
            return item;
        };
    }

    public void setPlayer(Game game, Player player){
        this.game = game;
        this.player = player;
        player.sendMessage(Component.text("§aおくすり手帳§bを手に入れた！"));
    }


}
