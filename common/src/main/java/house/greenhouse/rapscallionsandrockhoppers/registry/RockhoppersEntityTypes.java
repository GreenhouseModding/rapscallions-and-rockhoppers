package house.greenhouse.rapscallionsandrockhoppers.registry;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.entity.Penguin;
import house.greenhouse.rapscallionsandrockhoppers.util.RegisterFunction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.function.BiConsumer;

public class RockhoppersEntityTypes {
    public static final EntityType<Penguin> PENGUIN = EntityType.Builder.of(Penguin::new, MobCategory.CREATURE).sized(0.6F, 1.1F).clientTrackingRange(10).build(RapscallionsAndRockhoppers.asResource("penguin").toString());

    public static void registerEntityTypes(RegisterFunction<EntityType<?>> function) {
        function.register(BuiltInRegistries.ENTITY_TYPE, RapscallionsAndRockhoppers.asResource("penguin"), PENGUIN);
    }

    public static void createMobAttributes(BiConsumer<EntityType<? extends LivingEntity>, AttributeSupplier> consumer) {
        consumer.accept(PENGUIN, Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.12F)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.STEP_HEIGHT, 1.0F)
                .build());
    }
}
