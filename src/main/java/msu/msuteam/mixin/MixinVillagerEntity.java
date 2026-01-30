package msu.msuteam.mixin;

import msu.msuteam.config.HonorConfig;
import msu.msuteam.honor.HonorStage;
import msu.msuteam.honor.HonorSystem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class MixinVillagerEntity extends AbstractVillager {
    public MixinVillagerEntity(EntityType<? extends AbstractVillager> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "updateSpecialPrices", at = @At("TAIL"))
    private void honor_prepareOffers(Player player, CallbackInfo ci) {
        HonorStage stage = HonorSystem.getStage(player);

        for (MerchantOffer offer : this.getOffers()) {
            int priceAdjustment = 0;

            switch (stage) {
                case TYRANT:
                    priceAdjustment = 64;
                    break;
                case VILLAIN:
                    priceAdjustment = 20;
                    break;
                case NEUTRAL:
                    break;
                case GOOD:
                    priceAdjustment = -10;
                    break;
                case ALTRUIST:
                    priceAdjustment = -40;
                    break;
            }

            offer.addToSpecialPriceDiff(priceAdjustment);

            MerchantOfferAccessor accessor = (MerchantOfferAccessor) offer;

            if (stage == HonorStage.GOOD) {
                if (accessor.getMaxUses() < 16) {
                    accessor.setMaxUses(16);
                }
            } else if (stage == HonorStage.ALTRUIST) {
                if (accessor.getMaxUses() < 24) {
                    accessor.setMaxUses(24);
                }
            }
        }
    }

    @Override
    public void notifyTrade(MerchantOffer offer) {
        super.notifyTrade(offer);
        if (this.getTradingPlayer() != null) {
            HonorSystem.modifyHonor(this.getTradingPlayer(), HonorConfig.getInstance().tradeReward);
        }
    }
}
