package msu.msuteam.mixin;

import msu.msuteam.config.HonorConfig;
import msu.msuteam.honor.HonorSystem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Raid.class)
public abstract class MixinRaid {
    @Shadow private ServerLevel level;
    @Shadow public abstract net.minecraft.core.BlockPos getCenter();

    @Unique
    private boolean honor_lossHandled = false;
    @Unique
    private boolean honor_winHandled = false;

    @Inject(method = "tick", at = @At("HEAD"))
    private void honor_tick(CallbackInfo ci) {
        if (this.level.isClientSide) return;

        Object statusObj = ((RaidAccessor) (Object) this).getStatus();
        String statusName = statusObj.toString();

        if (statusName.equals("LOSS") && !honor_lossHandled) {
            honor_lossHandled = true;
            applyHonorChange(HonorConfig.getInstance().raidLossPenalty);
        }

        if (statusName.equals("VICTORY") && !honor_winHandled) {
             honor_winHandled = true;
             applyHonorChange(HonorConfig.getInstance().raidWinReward);
        }
    }

    @Unique
    private void applyHonorChange(int amount) {
        List<Player> players = this.level.getEntitiesOfClass(Player.class,
            new net.minecraft.world.phys.AABB(getCenter()).inflate(64.0));

        for (Player player : players) {
            HonorSystem.modifyHonor(player, amount);
        }
    }
}
