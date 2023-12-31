package dev.greenhouseteam.rapscallionsandrockhoppers.componability;

import dev.greenhouseteam.rapscallionsandrockhoppers.RockhoppersEntityComponents;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.EntityGetUtil;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BoatDataComponent implements AutoSyncedComponent, IBoatData {
    private final Set<UUID> penguins = new HashSet<>();
    private final Set<UUID> nextLinkedBoats = new HashSet<>();
    private final Set<UUID> previousLinkedBoats = new HashSet<>();
    @Unique
    private @Nullable UUID linkedPlayer;
    private final Boat provider;

    public BoatDataComponent(Boat boat) {
        this.provider = boat;
    }

    @Override
    public @Nullable Boat getProvider() {
        return this.provider;
    }

    @Override
    public Set<UUID> getNextLinkedBoatUuids() {
        return nextLinkedBoats;
    }

    @Override
    public void clearNextLinkedBoatUuids() {
        this.nextLinkedBoats.clear();
    }

    @Override
    public Set<UUID> getPreviousLinkedBoatUuids() {
        return previousLinkedBoats;
    }

    @Override
    public void clearPreviousLinkedBoatUuids() {
        this.previousLinkedBoats.clear();
    }

    @Override
    public @Nullable UUID getLinkedPlayerUuid() {
        return linkedPlayer;
    }

    @Override
    public Set<Boat> getNextLinkedBoats() {
        return this.getNextLinkedBoatUuids().stream().map(uuid -> {
            Entity entity = EntityGetUtil.getEntityFromUuid(this.provider.level(), uuid);
            if (entity instanceof Boat boat) {
                return boat;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    public Set<Boat> getPreviousLinkedBoats() {
        return this.getPreviousLinkedBoatUuids().stream().map(uuid -> {
            Entity entity = EntityGetUtil.getEntityFromUuid(this.provider.level(), uuid);
            if (entity instanceof Boat boat) {
                return boat;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    public @Nullable Player getLinkedPlayer() {
        Entity entity = EntityGetUtil.getEntityFromUuid(this.provider.level(), linkedPlayer);
        if (entity instanceof Player player) {
            return player;
        }
        return null;
    }

    @Override
    public void setLinkedPlayer(@Nullable UUID player) {
        this.linkedPlayer = player;
    }

    @Override
    public void addNextLinkedBoat(@Nullable UUID boat) {
        this.nextLinkedBoats.add(boat);
    }

    @Override
    public void removeNextLinkedBoat(@Nullable UUID boat) {
        this.nextLinkedBoats.remove(boat);
    }

    @Override
    public void addPreviousLinkedBoat(@Nullable UUID boat) {
        this.previousLinkedBoats.add(boat);
    }

    @Override
    public void removePreviousLinkedBoat(@Nullable UUID boat) {
        this.previousLinkedBoats.remove(boat);
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
    public boolean shouldSyncWith(ServerPlayer player) {
        return PlayerLookup.tracking(this.provider).contains(player);
    }

    @Override
    public void sync() {
        RockhoppersEntityComponents.BOAT_DATA_COMPONENT.sync(this.provider);
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        this.deserialize(tag);
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        this.serialize(tag);
    }

}
