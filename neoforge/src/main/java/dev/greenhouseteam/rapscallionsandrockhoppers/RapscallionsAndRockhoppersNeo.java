package dev.greenhouseteam.rapscallionsandrockhoppers;

import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersAttachments;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(RapscallionsAndRockhoppers.MOD_ID)
public class RapscallionsAndRockhoppersNeo {
    public RapscallionsAndRockhoppersNeo(IEventBus modBus) {
        RapscallionsAndRockhoppers.init();
        RockhoppersAttachments.init(modBus);

    }
}