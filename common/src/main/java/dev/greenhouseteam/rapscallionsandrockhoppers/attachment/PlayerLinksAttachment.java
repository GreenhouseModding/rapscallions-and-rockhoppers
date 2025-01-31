package dev.greenhouseteam.rapscallionsandrockhoppers.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.platform.RockhoppersPlatformHelper;
import dev.greenhouseteam.rapscallionsandrockhoppers.util.EntityGetUtil;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerLinksAttachment {
    public static final ResourceLocation ID = RapscallionsAndRockhoppers.asResource("boat_hook_player");
    private Set<UUID> linkedBoats;
    private @Nullable Player instance;

    public static final Codec<PlayerLinksAttachment> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            UUIDUtil.CODEC_SET.fieldOf("linked_boats").forGetter(PlayerLinksAttachment::getLinkedBoatUUIDs)
    ).apply(inst, PlayerLinksAttachment::new));

    public PlayerLinksAttachment() {
        this.linkedBoats = new HashSet<>();
    }

    public void setFrom(PlayerLinksAttachment other) {
        this.linkedBoats = other.linkedBoats;
    }

    public PlayerLinksAttachment(Set<UUID> linkedBoats) {
        this.linkedBoats = linkedBoats;
    }

    public Set<UUID> getLinkedBoatUUIDs() {
        return this.linkedBoats;
    }

    public void addLinkedBoat(UUID boat) {
        this.linkedBoats.add(boat);
    }

    public void removeLinkedBoat(UUID boat) {
        this.linkedBoats.remove(boat);
    }

    public void clearLinkedBoats() {
        this.linkedBoats.clear();
    }

    public @Nullable Player getProvider() {
        return instance;
    }

    public void setProvider(Player player) {
        instance = player;
    }

    public Set<Boat> getLinkedBoats() {
        if (getProvider() == null)
            return Set.of();
        return this.getLinkedBoatUUIDs().stream().map(uuid -> {
            Entity entity = EntityGetUtil.getEntityFromUuid(this.getProvider().level(), uuid);
            if (entity instanceof Boat boat) {
                return boat;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public void invalidateNonExistentBoats() {
        this.getLinkedBoatUUIDs().removeIf(uuid -> this.getLinkedBoats().stream().noneMatch(boat -> boat.getUUID() == uuid && !boat.isRemoved()));
    }

    public void deserializeLegacyData(CompoundTag tag) {
        this.clearLinkedBoats();
        if (tag.contains("linked_boat", Tag.TAG_LIST)) {
            ListTag linkedBoats = tag.getList("linked_boat", Tag.TAG_INT_ARRAY);
            for (Tag linkedBoat : linkedBoats) {
                this.addLinkedBoat(NbtUtils.loadUUID(linkedBoat));
            }
        }
    }

    public void sync() {
        if (getProvider() == null || getProvider().level().isClientSide())
            return;
        RapscallionsAndRockhoppers.getHelper().syncPlayerData(getProvider());
    }
}
