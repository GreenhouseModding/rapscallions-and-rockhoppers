package dev.greenhouseteam.rapscallionsandrockhoppers;

import dev.greenhouseteam.rdpr.api.IReloadableRegistryCreationHelper;
import dev.greenhouseteam.rdpr.api.entrypoint.ReloadableRegistryPlugin;

public class RockhoppersRDPREntrypoint implements ReloadableRegistryPlugin {
    @Override
    public void createContents(IReloadableRegistryCreationHelper helper) {
        RapscallionsAndRockhoppers.createRDPRContents(helper);
    }
}
