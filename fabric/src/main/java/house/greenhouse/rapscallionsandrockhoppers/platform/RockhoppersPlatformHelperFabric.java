package house.greenhouse.rapscallionsandrockhoppers.platform;

import house.greenhouse.rapscallionsandrockhoppers.attachment.BoatLinksAttachment;
import house.greenhouse.rapscallionsandrockhoppers.attachment.BoatPenguinsAttachment;
import house.greenhouse.rapscallionsandrockhoppers.attachment.PlayerLinksAttachment;
import house.greenhouse.rapscallionsandrockhoppers.network.RockhoppersPackets;
import house.greenhouse.rapscallionsandrockhoppers.network.s2c.SyncBoatLinksAttachmentPacketS2C;
import house.greenhouse.rapscallionsandrockhoppers.network.s2c.SyncBoatPenguinsAttachmentPacketS2C;
import house.greenhouse.rapscallionsandrockhoppers.network.s2c.SyncPlayerLinksAttachmentPacketS2C;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersAttachments;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;

import java.util.Optional;

public class RockhoppersPlatformHelperFabric implements RockhoppersPlatformHelper {

    @Override
    public RockhoppersPlatform getPlatform() {
        return RockhoppersPlatform.FABRIC;
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public void sendS2CTracking(CustomPacketPayload packet, Entity entity) {
        RockhoppersPackets.sendS2CTracking(packet, entity);
    }

    @Override
    public boolean hasBoatData(Boat boat) {
        return boat.hasAttached(RockhoppersAttachments.BOAT_LINKS);
    }

    @Override
    public BoatLinksAttachment getBoatData(Boat boat) {
        return boat.getAttachedOrCreate(RockhoppersAttachments.BOAT_LINKS);
    }

    @Override
    public void removeBoatData(Boat boat) {
        boat.removeAttached(RockhoppersAttachments.BOAT_LINKS);
    }

    @Override
    public void syncBoatData(Boat boat) {
        sendS2CTracking(new SyncBoatLinksAttachmentPacketS2C(boat.getId(), Optional.ofNullable(boat.getAttached(RockhoppersAttachments.BOAT_LINKS))), boat);
    }

    @Override
    public boolean hasPlayerData(Player player) {
        return player.hasAttached(RockhoppersAttachments.PLAYER_LINKS);
    }

    @Override
    public PlayerLinksAttachment getPlayerData(Player player) {
        return player.getAttachedOrCreate(RockhoppersAttachments.PLAYER_LINKS);
    }

    @Override
    public void removePlayerData(Player player) {
        player.removeAttached(RockhoppersAttachments.PLAYER_LINKS);
    }

    @Override
    public void syncPlayerData(Player player) {
        sendS2CTracking(new SyncPlayerLinksAttachmentPacketS2C(player.getId(), Optional.ofNullable(player.getAttached(RockhoppersAttachments.PLAYER_LINKS))), player);
    }

    @Override
    public boolean hasBoatPenguinData(Boat boat) {
        return boat.hasAttached(RockhoppersAttachments.BOAT_PENGUINS);
    }

    @Override
    public BoatPenguinsAttachment getBoatPenguinData(Boat boat) {
        BoatPenguinsAttachment attachment = boat.getAttachedOrCreate(RockhoppersAttachments.BOAT_PENGUINS);
        if (attachment.getProvider() == null)
            attachment.setProvider(boat);
        return attachment;
    }

    @Override
    public void removeBoatPenguinData(Boat boat) {
        boat.removeAttached(RockhoppersAttachments.BOAT_PENGUINS);
    }

    @Override
    public void syncBoatPenguinData(Boat boat) {
        sendS2CTracking(new SyncBoatPenguinsAttachmentPacketS2C(boat.getId(), Optional.ofNullable(boat.getAttached(RockhoppersAttachments.BOAT_PENGUINS))), boat);
    }

    @Override
    public boolean runAndIsBreedEventCancelled(Animal parent, Animal otherParent) {
        return false;
    }

    @Override
    public CompoundTag getLegacyTagStart(CompoundTag entityTag) {
        return entityTag.getCompound("cardinal_components");
    }
}
