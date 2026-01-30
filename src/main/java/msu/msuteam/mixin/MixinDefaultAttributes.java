package msu.msuteam.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DefaultAttributes.class)
public class MixinDefaultAttributes {
    // Важно: cancellable = true позволяет нам использовать setReturnValue
    @Inject(method = "getSupplier", at = @At("RETURN"), cancellable = true)
    private static void honor_getSupplier(EntityType<?> type, CallbackInfoReturnable<AttributeSupplier> cir) {
        // Проверяем категорию моба
        if (type.getCategory() == MobCategory.CREATURE || type.getCategory() == MobCategory.WATER_CREATURE) {
            AttributeSupplier originalSupplier = cir.getReturnValue();
            if (originalSupplier == null) return;

            // Если у моба еще нет атрибута атаки
            if (!originalSupplier.hasAttribute(Attributes.ATTACK_DAMAGE)) {
                
                // 1. Создаем новый билдер атрибутов
                AttributeSupplier.Builder newBuilder = AttributeSupplier.builder();

                // 2. Получаем доступ к старым атрибутам через наш аксессор
                AttributeSupplierAccessor accessor = (AttributeSupplierAccessor) originalSupplier;
                var existingInstances = accessor.getInstances();

                // 3. Копируем все существующие атрибуты в новый билдер
                // Мы берем сам атрибут (getAttribute) и его базовое значение (getBaseValue)
                for (var instance : existingInstances.values()) {
                    newBuilder.add(instance.getAttribute(), instance.getBaseValue());
                }

                // 4. Добавляем наш недостающий атрибут урона
                newBuilder.add(Attributes.ATTACK_DAMAGE, 1.0);

                // 5. Подменяем возвращаемое значение на наш новый объект
                cir.setReturnValue(newBuilder.build());
            }
        }
    }
}