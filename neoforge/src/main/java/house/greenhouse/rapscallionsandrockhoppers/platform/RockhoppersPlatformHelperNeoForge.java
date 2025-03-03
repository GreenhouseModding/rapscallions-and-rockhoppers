package house.greenhouse.rapscallionsandrockhoppers.platform;

import house.greenhouse.rapscallionsandrockhoppers.attachment.BoatLinksAttachment;
import house.greenhouse.rapscallionsandrockhoppers.attachment.BoatPenguinsAttachment;
import house.greenhouse.rapscallionsandrockhoppers.attachment.PlayerLinksAttachment;
import house.greenhouse.rapscallionsandrockhoppers.network.s2c.SyncBoatLinksAttachmentPacketS2C;
import house.greenhouse.rapscallionsandrockhoppers.network.s2c.SyncBoatPenguinsAttachmentPacketS2C;
import house.greenhouse.rapscallionsandrockhoppers.network.s2c.SyncPlayerLinksAttachmentPacketS2C;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersAttachments;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class RockhoppersPlatformHelperNeoForge implements RockhoppersPlatformHelper {

    @Override
    public RockhoppersPlatform getPlatform() {
        return RockhoppersPlatform.NEOFORGE;
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @Override
    public void sendS2CTracking(CustomPacketPayload packet, Entity entity) {
        PacketDistributor.sendToPlayersTrackingEntity(entity, packet);
    }

    @Override
    public boolean hasBoatData(Boat boat) {
        return boat.hasData(RockhoppersAttachments.BOAT_LINKS);
    }

    @Override
    public BoatLinksAttachment getBoatData(Boat boat) {
        return boat.getData(RockhoppersAttachments.BOAT_LINKS);
    }

    @Override
    public void removeBoatData(Boat boat) {
        boat.removeData(RockhoppersAttachments.BOAT_LINKS);
    }

    @Override
    public void syncBoatData(Boat boat) {
        sendS2CTracking(new SyncBoatLinksAttachmentPacketS2C(boat.getId(), boat.getExistingData(RockhoppersAttachments.BOAT_LINKS)), boat);
    }

    @Override
    public boolean hasPlayerData(Player player) {
        return player.hasData(RockhoppersAttachments.PLAYER_LINKS);
    }

    @Override
    public PlayerLinksAttachment getPlayerData(Player player) {
        return player.getData(RockhoppersAttachments.PLAYER_LINKS);
    }

    @Override
    public void removePlayerData(Player player) {
        player.removeData(RockhoppersAttachments.PLAYER_LINKS);
    }

    @Override
    public void syncPlayerData(Player player) {
        sendS2CTracking(new SyncPlayerLinksAttachmentPacketS2C(player.getId(), player.getExistingData(RockhoppersAttachments.PLAYER_LINKS)), player);
    }

    @Override
    public boolean hasBoatPenguinData(Boat boat) {
        return boat.hasData(RockhoppersAttachments.BOAT_PENGUINS);
    }

    @Override
    public BoatPenguinsAttachment getBoatPenguinData(Boat boat) {
        BoatPenguinsAttachment attachment = boat.getData(RockhoppersAttachments.BOAT_PENGUINS);
        if (attachment.getProvider() == null)
            attachment.setProvider(boat);
        return attachment;
    }

    @Override
    public void removeBoatPenguinData(Boat boat) {
        boat.removeData(RockhoppersAttachments.BOAT_PENGUINS);
    }

    @Override
    public void syncBoatPenguinData(Boat boat) {
        sendS2CTracking(new SyncBoatPenguinsAttachmentPacketS2C(boat.getId(), boat.getExistingData(RockhoppersAttachments.BOAT_PENGUINS)), boat);
    }

    @Override
    public boolean runAndIsBreedEventCancelled(Animal parent, Animal otherParent) {
        BabyEntitySpawnEvent event = new BabyEntitySpawnEvent(parent, otherParent, null);
        return event.isCanceled();
    }

    @Override
    public CompoundTag getLegacyTagStart(CompoundTag entityTag) {
        return entityTag.getCompound("neoforge:attachments");
    }
}