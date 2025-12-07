package org.meyason.dokkoi.constants;

public enum GameState {
    WAITING("待機中"),
    MATCHING("マッチング中"),
    PREP("準備フェーズ"),
    IN_GAME("ゲーム中"),
    PRE_END("ゲーム終了(集計中)"),
    END("リザルト");

    private final String displayName;

    GameState(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
