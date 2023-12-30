package dev.greenhouseteam.rapscallionsandrockhoppers.componability;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerDataAttachment implements IPlayerData, INBTSerializable<CompoundTag> {
    @Unique
    private @Nullable UUID linkedBoat;

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
    public @Nullable UUID getLinkedBoatUUID() {
        return this.linkedBoat;
    }

    @Override
    public @Nullable void setLinkedBoat(UUID boat) {
        this.linkedBoat = boat;
    }
}