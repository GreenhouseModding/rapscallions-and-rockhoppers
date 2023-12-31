package dev.greenhouseteam.rapscallionsandrockhoppers.componability;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

import java.util.Set;
import java.util.UUID;

public class PlayerDataAttachment implements IPlayerData, INBTSerializable<CompoundTag> {
    @Unique
    private @Nullable Set<UUID> linkedBoats;

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        this.serialize(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.deserialize(tag);
    }


    @Override
    public Set<UUID> getLinkedBoatUUIDs() {
        return this.linkedBoats;
    }

    @Override
    public void addLinkedBoat(UUID boat) {
        this.linkedBoats.add(boat);
    }

    @Override
    public void removeLinkedBoat(UUID boat) {
        this.linkedBoats.remove(boat);
    }

    @Override
    public void clearLinkedBoats() {
        this.linkedBoats.clear();
    }
}