package msu.msuteam.mixin;

import msu.msuteam.honor.HonorStage;
import msu.msuteam.honor.HonorSystem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Player.class)
public abstract class MixinPlayerEntity extends LivingEntity {
    protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void honor_tick(CallbackInfo ci) {
        if (this.level().isClientSide) return;
        if (this.tickCount % 20 != 0) return;

        Player player = (Player) (Object) this;
        HonorStage stage = HonorSystem.getStage(player);

        if (stage == HonorStage.TYRANT) {
            List<Mob> mobs = this.level().getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(10.0), mob -> {
                MobCategory group = mob.getType().getCategory();
                return group != MobCategory.CREATURE && group != MobCategory.WATER_CREATURE && group != MobCategory.AMBIENT;
            });

            for (Mob mob : mobs) {
                mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 0, false, false));
            }
        } else if (stage == HonorStage.ALTRUIST) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 0, false, false, true));

            List<Mob> mobs = this.level().getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(10.0), mob -> {
                return mob.getType().getCategory() == MobCategory.MONSTER;
            });

            for (Mob mob : mobs) {
                mob.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0, false, false));
            }
        }
    }
}
