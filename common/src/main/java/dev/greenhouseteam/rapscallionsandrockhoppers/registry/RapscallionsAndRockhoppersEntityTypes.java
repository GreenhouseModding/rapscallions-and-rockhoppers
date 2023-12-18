package dev.greenhouseteam.rapscallionsandrockhoppers.registry;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.function.BiConsumer;

public class RapscallionsAndRockhoppersEntityTypes {
    public static final EntityType<Penguin> PENGUIN = EntityType.Builder.of(Penguin::new, MobCategory.CREATURE).sized(0.6F, 1.1F).clientTrackingRange(10).build(RapscallionsAndRockhoppers.asResource("penguin").toString());

    public static void registerEntityTypes(TriConsumer<Registry<EntityType<?>>, ResourceLocation, EntityType<?>> consumer) {
        consumer.accept(BuiltInRegistries.ENTITY_TYPE, RapscallionsAndRockhoppers.asResource("penguin"), PENGUIN);
    }

    public static void createMobAttributes(BiConsumer<EntityType<? extends LivingEntity>, AttributeSupplier> consumer) {
        consumer.accept(PENGUIN, Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 8.0).add(Attributes.MOVEMENT_SPEED, 0.4F).build());
    }
}
