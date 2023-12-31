package dev.greenhouseteam.rapscallionsandrockhoppers.registry;

import dev.greenhouseteam.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import dev.greenhouseteam.rapscallionsandrockhoppers.componability.BoatDataAttachment;
import dev.greenhouseteam.rapscallionsandrockhoppers.componability.PlayerDataAttachment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class RockhoppersAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, RapscallionsAndRockhoppers.MOD_ID);

    public static final Supplier<AttachmentType<BoatDataAttachment>> BOAT_DATA = ATTACHMENT_TYPES.register(
            BoatDataAttachment.ID.getPath(), () -> AttachmentType.serializable(BoatDataAttachment::new).build());
    public static final Supplier<AttachmentType<PlayerDataAttachment>> PLAYER_DATA = ATTACHMENT_TYPES.register(
            PlayerDataAttachment.ID.getPath(), () -> AttachmentType.serializable(PlayerDataAttachment::new).build());

    public static void init(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }

}
