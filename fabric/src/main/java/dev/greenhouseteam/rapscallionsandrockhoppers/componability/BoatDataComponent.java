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
import java.util.UUID;

public class BoatDataComponent implements AutoSyncedComponent, IBoatData {
    private final List<UUID> penguins = new ArrayList<>();
    @Unique
    private @Nullable UUID nextLinkedBoat;
    @Unique
    private @Nullable UUID previousLinkedBoat;
    @Unique
    private @Nullable UUID linkedPlayer;
    private Boat provider;

    public BoatDataComponent(Boat boat) {
        this.provider = boat;
    }

    @Override
    public @Nullable Boat getProvider() {
        return this.provider;
    }

    @Override
    public @Nullable UUID getNextLinkedBoatUuid() {
        return nextLinkedBoat;
    }

    @Override
    public @Nullable UUID getPreviousLinkedBoatUuid() {
        return previousLinkedBoat;
    }

    @Override
    public @Nullable UUID getLinkedPlayerUuid() {
        return linkedPlayer;
    }

    @Override
    public @Nullable Boat getNextLinkedBoat() {
        Entity entity = EntityGetUtil.getEntityFromUuid(this.provider.level(), nextLinkedBoat);
        if (entity instanceof Boat boat) {
            return boat;
        }
        return null;
    }

    @Override
    public @Nullable Boat getPreviousLinkedBoat() {
        Entity entity = EntityGetUtil.getEntityFromUuid(this.provider.level(), previousLinkedBoat);
        if (entity instanceof Boat boat) {
            return boat;
        }
        return null;
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
    public void setNextLinkedBoat(@Nullable UUID boat) {
        this.nextLinkedBoat = boat;
    }

    @Override
    public void setPreviousLinkedBoat(@Nullable UUID boat) {
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
