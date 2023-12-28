package dev.greenhouseteam.rapscallionsandrockhoppers;

import dev.greenhouseteam.rapscallionsandrockhoppers.entity.PenguinType;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.RockhoppersResourceKeys;
import dev.greenhouseteam.rdpr.api.IReloadableRegistryCreationHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RapscallionsAndRockhoppers {

    public static final String MOD_ID = "rapscallionsandrockhoppers";
    public static final String MOD_NAME = "Rapscallions and Rockhoppers";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    private static Registry<PenguinType> cachedPenguinTypeRegistry = null;

    public static void init() {

    }

    public static void createRDPRContents(IReloadableRegistryCreationHelper helper) {
        helper.fromExistingRegistry(RockhoppersResourceKeys.PENGUIN_TYPE_REGISTRY);
    }

    /**
     * Gets the cached PenguinType registry.
     * This should only be used when you're working with methods that don't have access to
     * the {@link net.minecraft.world.level.Level}.
     * If you're on the client, don't use or set this value, as you can reference the
     * client's registry access at any time.
     *
     * @return The PenguinType registry.
     */
    public static Registry<PenguinType> getCachedPenguinTypeRegistry() {
        return cachedPenguinTypeRegistry;
    }

    protected static void setCachedPenguinTypeRegistry(Registry<PenguinType> value) {
        cachedPenguinTypeRegistry = value;
    }

    protected static void removeCachedPenguinTypeRegistry() {
        cachedPenguinTypeRegistry = null;
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}