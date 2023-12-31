package dev.greenhouseteam.rapscallionsandrockhoppers.platform;

import com.google.auto.service.AutoService;
import dev.greenhouseteam.rapscallionsandrockhoppers.RockhoppersEntityComponents;
import dev.greenhouseteam.rapscallionsandrockhoppers.componability.IBoatData;
import dev.greenhouseteam.rapscallionsandrockhoppers.componability.IPlayerData;
import dev.greenhouseteam.rapscallionsandrockhoppers.network.RockhoppersPackets;
import dev.greenhouseteam.rapscallionsandrockhoppers.network.s2c.RapscallionsAndRockhoppersPacketS2C;
import dev.greenhouseteam.rapscallionsandrockhoppers.platform.services.IRockhoppersPlatformHelper;
import dev.onyxstudios.cca.internal.entity.CardinalComponentsEntity;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;

@AutoService(IRockhoppersPlatformHelper.class)
public class FabricRockhoppersPlatformHelper implements IRockhoppersPlatformHelper {

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
        RockhoppersPackets.sendS2CTracking(packet, entity);
    }

    @Override
    public IBoatData getBoatData(Boat boat) {
        return boat.getComponent(RockhoppersEntityComponents.BOAT_DATA_COMPONENT);
    }

    @Override
    public IPlayerData getPlayerData(Player player) {
        return player.getComponent(RockhoppersEntityComponents.PLAYER_DATA_COMPONENT);
    }

    @Override
    public boolean runAndIsBreedEventCancelled(Animal parent, Animal otherParent) {
        return false;
    }
}
