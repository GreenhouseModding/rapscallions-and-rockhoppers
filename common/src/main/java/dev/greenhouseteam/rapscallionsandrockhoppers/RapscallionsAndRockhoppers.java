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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
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

    public static void setCachedPenguinTypeRegistry(Registry<PenguinType> value) {
        cachedPenguinTypeRegistry = value;
    }

    protected static void removeCachedPenguinTypeRegistry() {
        cachedPenguinTypeRegistry = null;
    }

    protected static void onUnload(Entity entity, Level level) {
        if (entity instanceof Penguin penguin) {
            penguin.setBoatToFollow(null);
            GlobalPos home = BrainUtils.getMemory(penguin, MemoryModuleType.HOME);
            if (home != null && level.dimension() == home.dimension() && penguin.blockPosition().distSqr(home.pos()) > 24 * 24) {
                BlockPos randomPos = null;

                if (level instanceof ServerLevel serverLevel) {
                    loadNearbyChunks(home.pos(), serverLevel);
                }
                for (int i = 0; i < 10 || !level.getBlockState(randomPos).isPathfindable(level, randomPos, PathComputationType.WATER); ++i) {
                    randomPos = getRandomPos(penguin, home.pos());
                }
                if (!level.getBlockState(randomPos).isPathfindable(level, randomPos, PathComputationType.WATER)) {
                    randomPos = null;
                }
                if (randomPos == null) {
                    randomPos = home.pos();
                    for (int i = 0; i < 10 || !level.getBlockState(randomPos).isPathfindable(level, randomPos, PathComputationType.LAND); ++i) {
                        randomPos = home.pos().above();
                    }
                }
                Penguin penguin1 = RockhoppersEntityTypes.PENGUIN.create(level);
                if (penguin1 != null) {
                    penguin1.load(penguin.saveWithoutId(new CompoundTag()));
                    penguin1.teleportTo(randomPos.getX(), randomPos.getY(), randomPos.getZ());
                }
                penguin.remove(Entity.RemovalReason.DISCARDED);
                if (level instanceof ServerLevel serverLevel) {
                    unloadChunks(serverLevel);
                }
            }
        } else if (entity instanceof Boat boat) {
            IRockhoppersPlatformHelper.INSTANCE.getBoatData(boat).clearFollowingPenguins();
        }
    }

    public static void loadNearbyChunks(BlockPos pos, ServerLevel level) {
        int xCoord = SectionPos.blockToSectionCoord(pos.getX());
        int zCoord = SectionPos.blockToSectionCoord(pos.getZ());
        for (int x = -1; x < 2; ++x) {
            for (int z = -1; z < 2; ++z) {
                int xSection = xCoord + x;
                int zSection = zCoord + z;
                if (!level.hasChunk(xSection, zSection)) {
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

    // TODO: There's probably a better algorithm for calculating this... Oh well.
    private static BlockPos getRandomPos(Penguin penguin, BlockPos home) {
        int xOffset = penguin.getRandom().nextIntBetweenInclusive(8, 12);
        int zOffset = penguin.getRandom().nextIntBetweenInclusive(8, 12);
        if (penguin.getRandom().nextBoolean()) {
            xOffset = -xOffset;
        }
        if (penguin.getRandom().nextBoolean()) {
            zOffset = -zOffset;
        }
        return new BlockPos(home.getX() + xOffset, home.getY(), home.getZ() + zOffset);
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}