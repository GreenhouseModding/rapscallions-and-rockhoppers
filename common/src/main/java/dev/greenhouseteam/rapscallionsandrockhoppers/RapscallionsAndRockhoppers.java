package dev.greenhouseteam.rapscallionsandrockhoppers;

import com.mojang.datafixers.util.Pair;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.PenguinType;
import dev.greenhouseteam.rapscallionsandrockhoppers.platform.services.IRockhoppersPlatformHelper;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersEntityTypes;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.RockhoppersResourceKeys;
import dev.greenhouseteam.rdpr.api.IReloadableRegistryCreationHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class RapscallionsAndRockhoppers {

    public static final String MOD_ID = "rapscallionsandrockhoppers";
    public static final String MOD_NAME = "Rapscallions and Rockhoppers";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    private static final List<Pair<Integer, Integer>> PENGUIN_LOADED_CHUNKS = new ArrayList<>();
    private static Registry<PenguinType> biomePopulationPenguinTypeRegistry = null;
    private static boolean hasBiomePopulationBeenHandled;

    public static void init() {

    }

    public static void createRDPRContents(IReloadableRegistryCreationHelper helper) {
        helper.fromExistingRegistry(RockhoppersResourceKeys.PENGUIN_TYPE_REGISTRY);
    }

    /**
     * Gets the cached PenguinType registry.
     * This should only be used at biome modification time, where a {@link net.minecraft.world.level.Level}
     * is not accessible. This will be null after biome modification.
     * If you're on the client, don't use or set this value, as you can reference the
     * client's registry access at any time.
     *
     * @return The PenguinType registry.
     */
    public static Registry<PenguinType> getBiomePopulationPenguinTypeRegistry() {
        return biomePopulationPenguinTypeRegistry;
    }

    public static void setBiomePopulationPenguinTypeRegistry(Registry<PenguinType> value) {
        if (hasBiomePopulationBeenHandled) {
            return;
        } else {
            hasBiomePopulationBeenHandled = true;
        }
        biomePopulationPenguinTypeRegistry = value;
    }

    protected static void removeCachedPenguinTypeRegistry() {
        biomePopulationPenguinTypeRegistry = null;
    }

    public static void loadNearbyChunks(BlockPos pos, ServerLevel level) {
        int xCoord = SectionPos.blockToSectionCoord(pos.getX());
        int zCoord = SectionPos.blockToSectionCoord(pos.getZ());
        for (int x = -1; x < 2; ++x) {
            for (int z = -1; z < 2; ++z) {
                int xSection = xCoord + x;
                int zSection = zCoord + z;
                if (level.getChunk(xSection, zSection, ChunkStatus.FULL, true) == null && !PENGUIN_LOADED_CHUNKS.contains(Pair.of(xSection, zSection))) {
                    level.setChunkForced(xSection, zSection, true);
                    PENGUIN_LOADED_CHUNKS.add(Pair.of(xSection, zSection));
                }
            }
        }
    }


    public static void unloadChunks(ServerLevel level) {
        for (Pair<Integer, Integer> pair : PENGUIN_LOADED_CHUNKS) {
            level.setChunkForced(pair.getFirst(), pair.getSecond(), false);
        }
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}