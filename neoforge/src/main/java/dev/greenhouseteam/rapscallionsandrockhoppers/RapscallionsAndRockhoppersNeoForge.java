package dev.greenhouseteam.rapscallionsandrockhoppers;

import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersBiomeModifiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(RapscallionsAndRockhoppers.MOD_ID)
public class RapscallionsAndRockhoppersNeoForge {
    public RapscallionsAndRockhoppersNeoForge(IEventBus eventBus) {
        RapscallionsAndRockhoppers.init();
        RockhoppersBiomeModifiers.register(eventBus);
    }
}