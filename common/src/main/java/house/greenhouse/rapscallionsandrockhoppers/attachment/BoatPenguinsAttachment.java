package house.greenhouse.rapscallionsandrockhoppers.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BoatPenguinsAttachment {

    public static final ResourceLocation ID = RapscallionsAndRockhoppers.asResource("boat_penguins");

    private Set<UUID> penguins;
    private @Nullable Boat instance;
    public static final Codec<BoatPenguinsAttachment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC_SET.fieldOf("penguins").forGetter(BoatPenguinsAttachment::getFollowingPenguins)
    ).apply(instance, BoatPenguinsAttachment::new));

    
    public BoatPenguinsAttachment() {
        this.penguins = new HashSet<>();
    }
    
    public BoatPenguinsAttachment(Set<UUID> penguins) {
        this.penguins = penguins;
    }
    
    public void setFrom(BoatPenguinsAttachment other) {
        this.penguins = other.penguins;
    }

    public Set<UUID> getFollowingPenguins() {
        return Set.copyOf(this.penguins);
    }

    public int penguinCount() {
        return this.penguins.size();
    }

    public void addFollowingPenguin(UUID penguinUUID) {
        this.penguins.add(penguinUUID);
    }

    public void removeFollowingPenguin(UUID penguinUUID) {
        this.penguins.remove(penguinUUID);
    }

    public @Nullable Boat getProvider() {
        return instance;
    }

    public void setProvider(Boat boat) {
        if (instance != null)
            return;
        instance = boat;
    }

    public void sync() {
        if (getProvider() == null || getProvider().level().isClientSide())
            return;
        RapscallionsAndRockhoppers.getHelper().syncBoatPenguinData(getProvider());
    }

    public void clearFollowingPenguins() {
        this.penguins.clear();
    }
}
