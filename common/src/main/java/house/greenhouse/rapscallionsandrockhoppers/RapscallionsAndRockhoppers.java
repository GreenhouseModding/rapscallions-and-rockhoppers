package house.greenhouse.rapscallionsandrockhoppers;

import com.mojang.datafixers.util.Pair;
import house.greenhouse.rapscallionsandrockhoppers.entity.PenguinType;
import house.greenhouse.rapscallionsandrockhoppers.platform.RockhoppersPlatformHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class RapscallionsAndRockhoppers {

    public static final String MOD_ID = "rapscallionsandrockhoppers";
    public static final String MOD_NAME = "Rapscallions and Rockhoppers";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    private static RockhoppersPlatformHelper helper;

    private static final List<Pair<Integer, Integer>> PENGUIN_LOADED_CHUNKS = new ArrayList<>();
    private static Registry<PenguinType> biomePopulationPenguinTypeRegistry = null;
    private static boolean hasBiomePopulationBeenHandled;

    public static void init() {

    }

    public static RockhoppersPlatformHelper getHelper() {
        return helper;
    }

    public static void setHelper(RockhoppersPlatformHelper helper) {
        RapscallionsAndRockhoppers.helper = helper;
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

    public static void setBiomePopulationPenguinTypeRegistry(WritableRegistry<PenguinType> value) {
        if (hasBiomePopulationBeenHandled) {
            return;
        } else {
            hasBiomePopulationBeenHandled = true;
        }
        biomePopulationPenguinTypeRegistry = value;
    }

    protected static void removeCachedPenguinTypeRegistry(boolean resetState) {
        biomePopulationPenguinTypeRegistry = null;
        if (resetState) {
            hasBiomePopulationBeenHandled = false;
        }
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
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}