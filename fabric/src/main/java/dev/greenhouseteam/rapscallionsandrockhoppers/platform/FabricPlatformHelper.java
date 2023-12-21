package dev.greenhouseteam.rapscallionsandrockhoppers.platform;

import com.google.auto.service.AutoService;
import dev.greenhouseteam.rapscallionsandrockhoppers.network.RapscallionsAndRockhoppersPackets;
import dev.greenhouseteam.rapscallionsandrockhoppers.network.s2c.RapscallionsAndRockhoppersPacketS2C;
import dev.greenhouseteam.rapscallionsandrockhoppers.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.Entity;

@AutoService(IPlatformHelper.class)
public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
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
    public void sendS2CTracking(RapscallionsAndRockhoppersPacketS2C packet, Entity entity) {
        RapscallionsAndRockhoppersPackets.sendS2CTracking(packet, entity);
    }
}
