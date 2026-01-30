package msu.msuteam.honor;

import msu.msuteam.HonorMod;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class HonorSystem {
    public static int getHonor(Player player) {
        return player.getAttachedOrCreate(HonorMod.HONOR);
    }

    public static void setHonor(Player player, int value) {
        int clamped = Mth.clamp(value, 0, 1000);
        player.setAttached(HonorMod.HONOR, clamped);
    }

    public static void modifyHonor(Player player, int amount) {
        setHonor(player, getHonor(player) + amount);
    }

    public static HonorStage getStage(Player player) {
        return HonorStage.getStage(getHonor(player));
    }
}
