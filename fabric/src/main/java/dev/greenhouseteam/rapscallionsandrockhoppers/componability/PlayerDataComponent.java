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
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PlayerDataComponent implements AutoSyncedComponent, IPlayerData {
    private final List<UUID> linkedBoats = new ArrayList<>();
    private final Player provider;

    public PlayerDataComponent(Player player) {
        this.provider = player;
    }

    @Override
    public List<UUID> getLinkedBoatUUIDs() {
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
    public @Nullable List<Boat> getLinkedBoats() {
        return this.getLinkedBoatUUIDs().stream().map(uuid -> {
            Entity entity = EntityGetUtil.getEntityFromUuid(this.provider.level(), uuid);
            if (entity instanceof Boat boat) {
                return boat;
            }
            return null;
        }).filter(Objects::nonNull).toList();
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
    public void sync() {
        RockhoppersEntityComponents.PLAYER_DATA_COMPONENT.sync(this.provider);
    }
}
