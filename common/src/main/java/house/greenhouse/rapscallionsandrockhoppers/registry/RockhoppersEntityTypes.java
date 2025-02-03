package house.greenhouse.rapscallionsandrockhoppers.registry;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.entity.Penguin;
import house.greenhouse.rapscallionsandrockhoppers.platform.RockhoppersPlatform;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class RockhoppersEntityTypes {
    public static final EntityType<Penguin> PENGUIN = EntityType.Builder.of(Penguin::new, MobCategory.CREATURE).sized(0.6F, 1.1F).clientTrackingRange(10).build(getKey(() -> RapscallionsAndRockhoppers.asResource("penguin").toString()));

    @Nullable
    private static String getKey(Supplier<String> key) {
        return RapscallionsAndRockhoppers.getHelper().getPlatform() == RockhoppersPlatform.FABRIC ? null : key.get();
    }

    public static void registerEntityTypes() {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, RapscallionsAndRockhoppers.asResource("penguin"), PENGUIN);
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
