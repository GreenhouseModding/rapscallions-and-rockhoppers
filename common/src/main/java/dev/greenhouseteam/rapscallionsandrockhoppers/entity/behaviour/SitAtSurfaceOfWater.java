package dev.greenhouseteam.rapscallionsandrockhoppers.entity.behaviour;

import com.mojang.datafixers.util.Pair;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersMemoryModuleTypes;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.Optional;

public class SitAtSurfaceOfWater extends ExtendedBehaviour<Penguin> {
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return List.of(Pair.of(RockhoppersMemoryModuleTypes.CAUGHT_BOBBER, MemoryStatus.VALUE_ABSENT), Pair.of(RockhoppersMemoryModuleTypes.NEAREST_BOBBERS, MemoryStatus.VALUE_PRESENT), Pair.of(MemoryModuleType.IS_IN_WATER, MemoryStatus.VALUE_PRESENT));
    }

    @Override
    protected boolean shouldKeepRunning(Penguin entity) {
        return !BrainUtils.hasMemory(entity, RockhoppersMemoryModuleTypes.CAUGHT_BOBBER);
    }

    protected boolean shouldSit(ServerLevel level, Penguin entity) {
        return entity.getFluidHeight(FluidTags.WATER) < 0.4;
    }

    protected void tick(Penguin entity) {
        if (this.shouldSit((ServerLevel)entity.level(), entity)) {
            entity.getNavigation().stop();
            if (entity.getDeltaMovement().horizontalDistance() < 0.02) {
                entity.setDeltaMovement(Vec3.ZERO);
            }
            if (!BrainUtils.hasMemory(entity, RockhoppersMemoryModuleTypes.NEAREST_BOBBERS)) return;
            Optional<FishingHook> hook = BrainUtils.getMemory(entity, RockhoppersMemoryModuleTypes.NEAREST_BOBBERS).stream().findFirst();
            hook.ifPresent(fishingHook -> entity.lookAt(EntityAnchorArgument.Anchor.EYES, fishingHook.position()));
        } else {
            entity.addDeltaMovement(new Vec3(0.0, 0.04, 0.0));
        }
    }
}
