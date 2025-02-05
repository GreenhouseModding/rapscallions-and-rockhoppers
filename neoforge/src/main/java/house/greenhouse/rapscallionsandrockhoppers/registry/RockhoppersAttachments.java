package house.greenhouse.rapscallionsandrockhoppers.registry;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.attachment.BoatLinksAttachment;
import house.greenhouse.rapscallionsandrockhoppers.attachment.BoatPenguinsAttachment;
import house.greenhouse.rapscallionsandrockhoppers.attachment.PlayerLinksAttachment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class RockhoppersAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, RapscallionsAndRockhoppers.MOD_ID);

    public static final Supplier<AttachmentType<BoatLinksAttachment>> BOAT_LINKS = ATTACHMENT_TYPES.register(
            BoatLinksAttachment.ID.getPath(), () -> AttachmentType.builder(iAttachmentHolder -> new BoatLinksAttachment())
                    .serialize(BoatLinksAttachment.CODEC)
                    .build());
    public static final Supplier<AttachmentType<PlayerLinksAttachment>> PLAYER_LINKS = ATTACHMENT_TYPES.register(
            PlayerLinksAttachment.ID.getPath(), () -> AttachmentType.builder(iAttachmentHolder -> new PlayerLinksAttachment())
                    .serialize(PlayerLinksAttachment.CODEC)
                    .build());
    public static final Supplier<AttachmentType<BoatPenguinsAttachment>> BOAT_PENGUINS = ATTACHMENT_TYPES.register(
            BoatLinksAttachment.ID.getPath(), () -> AttachmentType.builder(iAttachmentHolder -> new BoatPenguinsAttachment())
                    .serialize(BoatPenguinsAttachment.CODEC)
                    .build());

    public static void init(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }

}
