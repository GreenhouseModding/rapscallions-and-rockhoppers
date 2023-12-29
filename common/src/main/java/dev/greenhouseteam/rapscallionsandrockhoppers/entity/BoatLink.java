package dev.greenhouseteam.rapscallionsandrockhoppers.entity;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.Nullable;

public interface BoatLink {
    double HOOK_DAMPENING_FACTOR = 0.2d;

    @Nullable Boat getNextLinkedBoat();
    @Nullable Boat getPreviousLinkedBoat();
    @Nullable Player getLinkedPlayer();
    void setLinkedPlayer(@Nullable Player player);

    void setNextLinkedBoat(Boat boat);
    void setPreviousLinkedBoat(Boat boat);
    default boolean canLinkTo(Boat otherBoat) {
        // This means the back of the current boat is free to be linked from.
        if (getPreviousLinkedBoat() != null) return true;
        // This means the front of the other boat is free to be linked to.
        return ((BoatLink)otherBoat).getNextLinkedBoat() == null;
    }
}
