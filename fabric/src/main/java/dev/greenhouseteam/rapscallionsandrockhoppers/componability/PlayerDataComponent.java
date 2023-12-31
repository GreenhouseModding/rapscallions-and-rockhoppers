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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerDataComponent implements AutoSyncedComponent, IPlayerData {
    private final Set<UUID> linkedBoats = new HashSet<>();
    private final Player provider;

    public PlayerDataComponent(Player player) {
        this.provider = player;
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

    @Override
    public Set<Boat> getLinkedBoats() {
        return this.getLinkedBoatUUIDs().stream().map(uuid -> {
            Entity entity = EntityGetUtil.getEntityFromUuid(this.provider.level(), uuid);
            if (entity instanceof Boat boat) {
                return boat;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toSet());
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

    @Override
    public void sync() {
        RockhoppersEntityComponents.PLAYER_DATA_COMPONENT.sync(this.provider);
    }

    public void invalidateNonExistentBoats() {
        this.getLinkedBoatUUIDs().removeIf(uuid -> this.getLinkedBoats().stream().noneMatch(boat -> boat.getUUID() == uuid && !boat.isRemoved()));
    }
}
