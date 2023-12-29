package dev.greenhouseteam.rapscallionsandrockhoppers;

import com.mojang.datafixers.util.Pair;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.platform.services.IRockhoppersPlatformHelper;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersMemoryModuleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
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

    public static void init() {

    }

    protected static void onUnload(Entity entity, Level level) {
        if (entity instanceof Penguin penguin) {
            GlobalPos home = BrainUtils.getMemory(penguin, MemoryModuleType.HOME);
            if (home != null && level.dimension() == home.dimension() && penguin.blockPosition().distSqr(home.pos()) > 24 * 24) {
                int xCoord = SectionPos.blockToSectionCoord(home.pos().getX());
                int zCoord = SectionPos.blockToSectionCoord(home.pos().getZ());
                for (int x = -1; x < 2; ++x) {
                    for (int z = -1; z < 2; ++z) {
                        int xSection = xCoord + x;
                        int zSection = zCoord + z;
                        if (!level.hasChunk(xSection, zSection)) {
                            level.getChunk(xSection, zSection).setLoaded(true);
                            PENGUIN_LOADED_CHUNKS.add(Pair.of(xSection, zSection));
                        }
                    }
                }
                BlockPos randomPos = null;

                for (int i = 0; i < 10 || !level.getBlockState(randomPos).isPathfindable(level, randomPos, PathComputationType.WATER); ++i) {
                    randomPos = getRandomPos(penguin, home.pos());
                }
                if (!level.getBlockState(randomPos).isPathfindable(level, randomPos, PathComputationType.WATER)) {
                    randomPos = null;
                }
                if (randomPos != null)
                    penguin.teleportTo(randomPos.getX(), randomPos.getY(), randomPos.getZ());
                else {
                    randomPos = home.pos();
                    for (int i = 0; i < 10 || !level.getBlockState(randomPos).isPathfindable(level, randomPos, PathComputationType.LAND); ++i) {
                        randomPos = home.pos().above();
                    }
                    penguin.teleportTo(randomPos.getX(), randomPos.getY(), randomPos.getZ());
                }

                PENGUIN_LOADED_CHUNKS.forEach(pair -> level.getChunk(pair.getFirst(), pair.getSecond()).setLoaded(false));
            }
            penguin.setBoatToFollow(null);
        } else if (entity instanceof Boat boat) {
            IRockhoppersPlatformHelper.INSTANCE.getBoatData(boat).clearFollowingPenguins();
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