package org.meyason.dokkoi.constants;

public enum Tier {
    TIER_1("アルティメットの使用が不可能。スキルのCDが二倍になる。tier3から受けるダメージが三倍になる。選択費用:50LP　勝利時:200LP", 50, 200, 3.0),
    TIER_2("アルティメットの使用が不可能。tier3から受けるダメージが二倍になる。選択費用:20LP　勝利時:100LP", 20, 100, 2.0),
    TIER_3("HPが0になるダメージを受けたとき、そのダメージを無効化してマップ内のどこかにランダムでtpする。選択費用:0LP　勝利時:20LP", 0, 20, 1.0);

    private final String description;
    private final long cost;
    private final long award;
    private final double damageMultiplier;

    Tier(String description, long cost, long award, double damageMultiplier) {
        this.description = description;
        this.cost = cost;
        this.award = award;
        this.damageMultiplier = damageMultiplier;
    }

    public String getDescription() {
        return description;
    }

    public long getCost() {
        return cost;
    }

    public long getAward() {
        return award;
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

}
