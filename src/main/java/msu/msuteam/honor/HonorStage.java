package msu.msuteam.honor;

public enum HonorStage {
    TYRANT,
    VILLAIN,
    NEUTRAL,
    GOOD,
    ALTRUIST;

    public static HonorStage getStage(int honor) {
        if (honor < 200) return TYRANT;
        if (honor < 400) return VILLAIN;
        if (honor < 600) return NEUTRAL;
        if (honor < 800) return GOOD;
        return ALTRUIST;
    }
}
