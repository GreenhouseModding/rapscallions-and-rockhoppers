package house.greenhouse.rapscallionsandrockhoppers.util;

import house.greenhouse.rapscallionsandrockhoppers.mixin.client.ClientLevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class EntityGetUtil {
    public static Entity getEntityFromUuid(Level level, UUID uuid) {
        if (!level.isClientSide())
            return ((ServerLevel)level).getEntity(uuid);

        return ((ClientLevelAccessor)level).rapscallionsandrockhoppers$invokeGetEntities().get(uuid);
    }
}
