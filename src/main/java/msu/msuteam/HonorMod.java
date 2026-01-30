package msu.msuteam;

import msu.msuteam.config.HonorConfig;
import msu.msuteam.honor.HonorSystem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HonorMod implements ModInitializer {
	public static final String MOD_ID = "honormod";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final AttachmentType<Integer> HONOR = AttachmentRegistry.create(
			ResourceLocation.fromNamespaceAndPath(MOD_ID, "honor"),
			builder -> builder
					.persistent(com.mojang.serialization.Codec.INT)
					.initializer(() -> 500)
					.copyOnDeath()
					.syncWith(ByteBufCodecs.INT, AttachmentSyncPredicate.targetOnly())
	);

	public static final ResourceKey<Enchantment> LIGHT_OF_VIRTUE = ResourceKey.create(
			Registries.ENCHANTMENT,
			ResourceLocation.fromNamespaceAndPath(MOD_ID, "light_of_virtue")
	);

	@Override
	public void onInitialize() {
		HonorConfig.load();

		ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
			if (entity instanceof Player player) {
				if (killedEntity instanceof Villager) {
					HonorSystem.modifyHonor(player, HonorConfig.getInstance().killVillagerPenalty);
				} else if (killedEntity.getType().getCategory() == MobCategory.MONSTER) {
					HonorSystem.modifyHonor(player, HonorConfig.getInstance().killHostileReward);
				} else if (killedEntity.getType().getCategory() == MobCategory.CREATURE ||
						killedEntity.getType().getCategory() == MobCategory.WATER_CREATURE ||
						killedEntity.getType().getCategory() == MobCategory.AMBIENT) {

					boolean hasEnchantment = false;
					var registry = world.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
					var entry = registry.getHolder(LIGHT_OF_VIRTUE);
					if (entry.isPresent()) {
						if (EnchantmentHelper.getItemEnchantmentLevel(entry.get(), player.getMainHandItem()) > 0) {
							hasEnchantment = true;
						}
					}

					if (!hasEnchantment) {
						HonorSystem.modifyHonor(player, HonorConfig.getInstance().killPassivePenalty);
					}
				}
			}
		});

		LOGGER.info("Hello Fabric world!");
	}
}
