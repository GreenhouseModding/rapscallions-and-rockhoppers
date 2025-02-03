package house.greenhouse.rapscallionsandrockhoppers.block.entity;

import house.greenhouse.rapscallionsandrockhoppers.entity.Penguin;
import house.greenhouse.rapscallionsandrockhoppers.entity.PenguinVariant;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersBlockEntityTypes;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersDataComponents;
import house.greenhouse.rapscallionsandrockhoppers.util.RockhoppersResourceKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PenguinEggBlockEntity extends BlockEntity {
    public Holder<PenguinVariant> babyVariant;
    public PenguinEggBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(RockhoppersBlockEntityTypes.PENGUIN_EGG, blockPos, blockState);
    }

    public void setTypeFromParents(Penguin parent1, Penguin parent2) {
        if (this.getLevel() == null) return;
        setPenguinVariant(this.getLevel().random.nextBoolean() ? parent1.getTrueType() : parent2.getTrueType());
    }

    public void setPenguinVariant(Holder<PenguinVariant> variant) {
        this.babyVariant = variant;
    }

    public Holder<PenguinVariant> getPenguinVariant() {
        return babyVariant;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (babyVariant != null) {
            tag.put("penguin_type", PenguinVariant.CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, registries), babyVariant).getOrThrow());
        } else {
            tag.put("penguin_type", PenguinVariant.CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, registries), registries.lookupOrThrow(RockhoppersResourceKeys.PENGUIN_VARIANT).getOrThrow(RockhoppersResourceKeys.PenguinTypeKeys.ROCKHOPPER)).getOrThrow());
        }
    }


    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("penguin_type"))
            babyVariant = PenguinVariant.CODEC.decode(RegistryOps.create(NbtOps.INSTANCE, registries), tag.get("penguin_type")).getOrThrow().getFirst();
        else
            babyVariant = registries.lookupOrThrow(RockhoppersResourceKeys.PENGUIN_VARIANT).getOrThrow(RockhoppersResourceKeys.PenguinTypeKeys.ROCKHOPPER);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(RockhoppersDataComponents.PENGUIN_TYPE, this.babyVariant);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.babyVariant = componentInput.getOrDefault(RockhoppersDataComponents.PENGUIN_TYPE, this.babyVariant);
    }
}
