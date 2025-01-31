package dev.greenhouseteam.rapscallionsandrockhoppers;

import dev.greenhouseteam.rapscallionsandrockhoppers.platform.RockhoppersPlatformHelperFabric;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class RapscallionsAndRockhoppersFabricPre implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        RapscallionsAndRockhoppers.setHelper(new RockhoppersPlatformHelperFabric());
    }
}
