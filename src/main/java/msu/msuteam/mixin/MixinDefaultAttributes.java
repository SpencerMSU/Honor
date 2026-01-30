package msu.msuteam.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(DefaultAttributes.class)
public class MixinDefaultAttributes {
    @Inject(method = "getSupplier", at = @At("RETURN"))
    private static void honor_getSupplier(EntityType<?> type, CallbackInfoReturnable<AttributeSupplier> cir) {
        if (type.getCategory() == MobCategory.CREATURE || type.getCategory() == MobCategory.WATER_CREATURE) {
             AttributeSupplier supplier = cir.getReturnValue();
             if (supplier == null) return;

             if (!supplier.hasAttribute(Attributes.ATTACK_DAMAGE)) {
                 AttributeSupplierAccessor accessor = (AttributeSupplierAccessor) supplier;
                 Map<Holder<Attribute>, AttributeInstance> instances = accessor.getInstances();

                 // Handle potential immutable map
                 try {
                     // Try adding directly first
                     AttributeInstance instance = new AttributeInstance(Attributes.ATTACK_DAMAGE, i -> {});
                     instance.setBaseValue(1.0);
                     instances.put(Attributes.ATTACK_DAMAGE, instance);
                 } catch (UnsupportedOperationException e) {
                     // Copy and replace
                     Map<Holder<Attribute>, AttributeInstance> newMap = new HashMap<>(instances);
                     AttributeInstance instance = new AttributeInstance(Attributes.ATTACK_DAMAGE, i -> {});
                     instance.setBaseValue(1.0);
                     newMap.put(Attributes.ATTACK_DAMAGE, instance);
                     accessor.setInstances(newMap);
                 }
             }
        }
    }
}
