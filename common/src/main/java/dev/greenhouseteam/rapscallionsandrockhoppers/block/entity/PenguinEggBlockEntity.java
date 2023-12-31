package dev.greenhouseteam.rapscallionsandrockhoppers.block.entity;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.PenguinType;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersBlockEntityTypes;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.RockhoppersResourceKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PenguinEggBlockEntity extends BlockEntity {
    public ResourceLocation penguinType = new ResourceLocation("rapscallionsandrockhoppers:rockhopper");
    public PenguinEggBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(RockhoppersBlockEntityTypes.PENGUIN_EGG, blockPos, blockState);
    }

    public void setTypeFromParents(Penguin parent1, Penguin parent2) {
        if (this.getLevel() == null) return;
        setPenguinType(this.getLevel().random.nextBoolean() ? parent1.getTrueType() : parent2.getTrueType());
    }

    public void setPenguinType(ResourceLocation penguinType) {
        this.penguinType = penguinType;
    }

    public PenguinType getPenguinType(ServerLevel level) {
        try {
            return level.registryAccess().registryOrThrow(RockhoppersResourceKeys.PENGUIN_TYPE_REGISTRY).get(ResourceKey.create(RockhoppersResourceKeys.PENGUIN_TYPE_REGISTRY, penguinType));
        } catch (Exception e) {
            RapscallionsAndRockhoppers.LOG.error("Could not get penguin type for BlockEntity at " + this.getBlockPos() + " with penguin type " + penguinType.toString());
            return PenguinType.MISSING;
        }
    }


    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("penguin_type", penguinType.toString());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("penguin_type")) {
            penguinType = new ResourceLocation(tag.getString("penguin_type"));
        }
    }
}
