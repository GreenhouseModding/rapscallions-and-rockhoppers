package dev.greenhouseteam.rapscallionsandrockhoppers.platform;

import com.google.auto.service.AutoService;
import dev.greenhouseteam.rapscallionsandrockhoppers.network.RapscallionsAndRockhoppersPacketHandler;
import dev.greenhouseteam.rapscallionsandrockhoppers.network.s2c.RapscallionsAndRockhoppersPacketS2C;
import dev.greenhouseteam.rapscallionsandrockhoppers.platform.services.IPlatformHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@AutoService(IPlatformHelper.class)
public class NeoForgePlatformHelper implements IPlatformHelper {

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
        RapscallionsAndRockhoppersPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), packet);
    }

    @Override
    public boolean runAndIsBreedEventCancelled(Animal parent, Animal otherParent) {
        BabyEntitySpawnEvent event = new BabyEntitySpawnEvent(parent, otherParent, null);
        return event.isCanceled();
    }
}