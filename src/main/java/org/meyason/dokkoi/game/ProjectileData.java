package org.meyason.dokkoi.game;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

public class ProjectileData {

    public Player attacker;

    public Projectile projectile;

    public String customItemName;

    public long throwTime; // 投げた時刻（ミリ秒）

    public ProjectileData(Player attacker, Projectile projectile, String customItemName) {
        this.projectile = projectile;
        this.attacker = attacker;
        this.customItemName = customItemName;
        this.throwTime = System.currentTimeMillis();
    }

    public Player getAttacker() {return attacker;}

    public Projectile getProjectile() {return projectile;}

    public void updateProjectile(Projectile projectile) {this.projectile = projectile;}

    public String getCustomItemName() {return customItemName;}

    public long getThrowTime() {return throwTime;}

    public long getElapsedTicks() {
        return (System.currentTimeMillis() - throwTime) / 50; // ミリ秒をティックに変換 (1tick = 50ms)
    }

}
