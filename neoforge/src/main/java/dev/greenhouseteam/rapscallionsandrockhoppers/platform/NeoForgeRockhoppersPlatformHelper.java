package dev.greenhouseteam.rapscallionsandrockhoppers.platform;

import com.google.auto.service.AutoService;
import dev.greenhouseteam.rapscallionsandrockhoppers.attachment.BoatLinksAttachment;
import dev.greenhouseteam.rapscallionsandrockhoppers.attachment.PlayerLinksAttachment;
import dev.greenhouseteam.rapscallionsandrockhoppers.network.s2c.RapscallionsAndRockhoppersPacketS2C;
import dev.greenhouseteam.rapscallionsandrockhoppers.network.s2c.SyncBoatLinksAttachmentPacket;
import dev.greenhouseteam.rapscallionsandrockhoppers.network.s2c.SyncPlayerLinksAttachmentPacket;
import dev.greenhouseteam.rapscallionsandrockhoppers.platform.services.IRockhoppersPlatformHelper;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersAttachments;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

@AutoService(IRockhoppersPlatformHelper.class)
public class NeoForgeRockhoppersPlatformHelper implements IRockhoppersPlatformHelper {

    @Override
    public String getPlatformName() {
        return "NeoForge";
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
    public void sendS2CTracking(RapscallionsAndRockhoppersPacketS2C packet, Entity entity) {
        PacketDistributor.TRACKING_ENTITY.with(entity).send(packet);
    }

    @Override
    public BoatLinksAttachment getBoatData(Boat boat) {
        BoatLinksAttachment attachment = boat.getData(RockhoppersAttachments.BOAT_LINKS);
        if (attachment.getProvider() == null)
            attachment.setProvider(boat);
        return attachment;
    }

    @Override
    public void syncBoatData(Boat boat) {
        sendS2CTracking(new SyncBoatLinksAttachmentPacket(boat.getId(), getBoatData(boat)), boat);
    }

    @Override
    public PlayerLinksAttachment getPlayerData(Player player) {
        PlayerLinksAttachment attachment = player.getData(RockhoppersAttachments.PLAYER_LINKS);
        if (attachment.getProvider() == null)
            attachment.setProvider(player);
        return attachment;
    }

    @Override
    public void syncPlayerData(Player player) {
        sendS2CTracking(new SyncPlayerLinksAttachmentPacket(player.getId(), getPlayerData(player)), player);
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