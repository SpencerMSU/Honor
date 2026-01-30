package msu.msuteam.mixin;

import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MerchantOffer.class)
public interface MerchantOfferAccessor {
    @Accessor("maxUses")
    int getMaxUses();

    @Accessor("maxUses")
    void setMaxUses(int maxUses);
}
