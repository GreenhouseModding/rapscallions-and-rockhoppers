package house.greenhouse.rapscallionsandrockhoppers.registry;

import house.greenhouse.rapscallionsandrockhoppers.attachment.BoatLinksAttachment;
import house.greenhouse.rapscallionsandrockhoppers.attachment.PlayerLinksAttachment;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public class RockhoppersAttachments {
    public static final AttachmentType<BoatLinksAttachment> BOAT_LINKS = AttachmentRegistry.<BoatLinksAttachment>builder()
            .persistent(BoatLinksAttachment.CODEC)
            .initializer(BoatLinksAttachment::new)
            .buildAndRegister(BoatLinksAttachment.ID);
    public static final AttachmentType<PlayerLinksAttachment> PLAYER_LINKS = AttachmentRegistry.<PlayerLinksAttachment>builder()
            .persistent(PlayerLinksAttachment.CODEC)
            .initializer(PlayerLinksAttachment::new)
            .buildAndRegister(PlayerLinksAttachment.ID);

    public static void init() {
    }

}
