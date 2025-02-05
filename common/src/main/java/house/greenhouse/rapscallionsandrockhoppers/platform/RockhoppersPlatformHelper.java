package house.greenhouse.rapscallionsandrockhoppers.platform;

import house.greenhouse.rapscallionsandrockhoppers.attachment.BoatLinksAttachment;
import house.greenhouse.rapscallionsandrockhoppers.attachment.BoatPenguinsAttachment;
import house.greenhouse.rapscallionsandrockhoppers.attachment.PlayerLinksAttachment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;

public interface RockhoppersPlatformHelper {
    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    RockhoppersPlatform getPlatform();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {

        return isDevelopmentEnvironment() ? "development" : "production";
    }

    void sendS2CTracking(CustomPacketPayload packet, Entity entity);

    BoatLinksAttachment getBoatData(Boat boat);

    void syncBoatData(Boat boat);

    PlayerLinksAttachment getPlayerData(Player player);

    void syncPlayerData(Player player);

    BoatPenguinsAttachment getBoatPenguinData(Boat boat);
    
    void syncBoatPenguinData(Boat boat);

    boolean runAndIsBreedEventCancelled(Animal parent, Animal otherParent);

    CompoundTag getLegacyTagStart(CompoundTag entityTag);
}