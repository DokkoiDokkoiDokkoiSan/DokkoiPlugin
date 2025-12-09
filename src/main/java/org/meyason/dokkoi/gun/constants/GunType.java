package org.meyason.dokkoi.gun.constants;

public enum GunType {

    HG("ハンドガン"),
    SMG("サブマシンガン"),
    AR("アサルトライフル"),
    EXPLOSIVE("爆発系");

    private final String displayName;

    GunType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
