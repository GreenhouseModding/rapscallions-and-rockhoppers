package house.greenhouse.rapscallionsandrockhoppers;

import house.greenhouse.rapscallionsandrockhoppers.platform.RockhoppersPlatformHelperNeoForge;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersAttachments;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersBiomeModifiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(RapscallionsAndRockhoppers.MOD_ID)
public class RapscallionsAndRockhoppersNeoForge {
    public RapscallionsAndRockhoppersNeoForge(IEventBus eventBus) {
        RapscallionsAndRockhoppers.init(new RockhoppersPlatformHelperNeoForge());
        RockhoppersAttachments.init(eventBus);
        RockhoppersBiomeModifiers.register(eventBus);
    }
}