package house.greenhouse.rapscallionsandrockhoppers.block.entity;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.entity.Penguin;
import house.greenhouse.rapscallionsandrockhoppers.entity.PenguinType;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersBlockEntityTypes;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersDataComponents;
import house.greenhouse.rapscallionsandrockhoppers.util.RockhoppersResourceKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PenguinEggBlockEntity extends BlockEntity {
    public ResourceLocation penguinType = RapscallionsAndRockhoppers.asResource("rockhopper");
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
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("penguin_type", penguinType.toString());
    }


    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("penguin_type")) {
            penguinType = ResourceLocation.tryParse(tag.getString("penguin_type"));
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(RockhoppersDataComponents.PENGUIN_TYPE, this.penguinType);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.penguinType = componentInput.getOrDefault(RockhoppersDataComponents.PENGUIN_TYPE, this.penguinType);
    }
}
