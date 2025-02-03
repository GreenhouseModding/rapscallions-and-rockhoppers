package house.greenhouse.rapscallionsandrockhoppers.registry;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.entity.PenguinVariant;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

public class RockhoppersDataComponents {

    public static final DataComponentType<Holder<PenguinVariant>> PENGUIN_TYPE = DataComponentType.<Holder<PenguinVariant>>builder()
            .persistent(PenguinVariant.CODEC)
            .networkSynchronized(PenguinVariant.STREAM_CODEC)
            .build();

    public static void registerDataComponents() {
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, RapscallionsAndRockhoppers.asResource("penguin_type"), PENGUIN_TYPE);
    }
}
