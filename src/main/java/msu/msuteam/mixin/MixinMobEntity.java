package msu.msuteam.mixin;

import msu.msuteam.config.HonorConfig;
import msu.msuteam.honor.HonorStage;
import msu.msuteam.honor.HonorSystem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MixinMobEntity extends LivingEntity {
    protected MixinMobEntity(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void honor_setTarget(LivingEntity target, CallbackInfo ci) {
        if ((Object)this instanceof IronGolem) {
            if (target instanceof Player player) {
                if (HonorSystem.getStage(player) == HonorStage.ALTRUIST) {
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void honor_tick(CallbackInfo ci) {
        if (this.level().isClientSide) return;

        Mob mob = (Mob) (Object) this;

        // Iron Golem Creation Reward
        if (mob instanceof IronGolem golem) {
            if (this.tickCount == 1 && golem.isPlayerCreated()) {
                Player player = this.level().getNearestPlayer(this, 10.0);
                if (player != null) {
                    HonorSystem.modifyHonor(player, HonorConfig.getInstance().createGolemReward);
                }
            }
        }

        if (mob.getTarget() != null) return;

        if (this.tickCount % 20 != 0) return;

        Player player = this.level().getNearestPlayer(this, 10.0);
        if (player == null) return;

        HonorStage stage = HonorSystem.getStage(player);
        MobCategory group = this.getType().getCategory();

        boolean aggressive = false;

        if (stage == HonorStage.TYRANT) {
            if (group == MobCategory.CREATURE || group == MobCategory.WATER_CREATURE) {
                aggressive = true;
            }
        }

        if (stage == HonorStage.TYRANT || stage == HonorStage.VILLAIN) {
             if (stage == HonorStage.VILLAIN) {
                 Object thisObj = this;
                 if (thisObj instanceof net.minecraft.world.entity.animal.Wolf ||
                     thisObj instanceof net.minecraft.world.entity.animal.Bee ||
                     thisObj instanceof net.minecraft.world.entity.animal.Panda ||
                     thisObj instanceof net.minecraft.world.entity.animal.PolarBear ||
                     thisObj instanceof net.minecraft.world.entity.animal.horse.Llama ||
                     thisObj instanceof net.minecraft.world.entity.animal.Dolphin) {
                     aggressive = true;
                 }
             } else {
                 if (group == MobCategory.CREATURE || group == MobCategory.WATER_CREATURE) {
                     aggressive = true;
                 }
             }
        }

        if (aggressive) {
            mob.setTarget(player);
        }
    }
}
