package org.meyason.dokkoi.goal;

import org.bukkit.entity.Player;

public class LastMan extends Goal {

    public LastMan() {
        super("LastMan", "最後の一人になるまで生き残ろう！");
    }

    @Override
    public boolean isAchieved(Player player) {
        return false;
    }
}
