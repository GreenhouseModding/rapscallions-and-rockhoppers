package dev.greenhouseteam.rapscallionsandrockhoppers.componability;

import dev.greenhouseteam.rapscallionsandrockhoppers.RockhoppersEntityComponents;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BoatDataComponent implements AutoSyncedComponent, IBoatData {
    private final List<UUID> penguins = new ArrayList<>();
    @Unique
    private @Nullable Boat nextLinkedBoat;
    @Unique
    private @Nullable Boat previousLinkedBoat;
    @Unique
    private @Nullable Player linkedPlayer;
    private Boat provider;

    public BoatDataComponent(Boat boat) {
        this.provider = boat;
    }

    @Override
    public @Nullable Boat getNextLinkedBoat() {
        return nextLinkedBoat;
    }

    @Override
    public @Nullable Boat getPreviousLinkedBoat() {
        return previousLinkedBoat;
    }

    @Override
    public @Nullable Player getLinkedPlayer() {
        return linkedPlayer;
    }

    @Override
    public void setLinkedPlayer(@Nullable Player player) {
        this.linkedPlayer = player;
    }

    @Override
    public void setNextLinkedBoat(Boat boat) {
        this.nextLinkedBoat = boat;
    }

    @Override
    public void setPreviousLinkedBoat(Boat boat) {
        this.previousLinkedBoat = boat;
    }

    @Override
    public List<UUID> getFollowingPenguins() {
        return List.copyOf(this.penguins);
    }

    @Override
    public int penguinCount() {
        return this.penguins.size();
    }

    @Override
    public void addFollowingPenguin(UUID penguinUUID) {
        this.penguins.add(penguinUUID);
    }

    @Override
    public void removeFollowingPenguin(UUID penguinUUID) {
        this.penguins.remove(penguinUUID);
    }

    @Override
    public void clearFollowingPenguins() {
        this.penguins.clear();
    }

    @Override
    public void sync() {
        RockhoppersEntityComponents.BOAT_ATTACHMENTS_COMPONENT.sync(this.provider);
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        this.deserialize(tag);
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        this.serialize(tag);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayer player) {
        return PlayerLookup.tracking(this.provider).contains(player);
    }
}
