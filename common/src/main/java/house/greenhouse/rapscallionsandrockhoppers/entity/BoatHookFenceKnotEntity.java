package house.greenhouse.rapscallionsandrockhoppers.entity;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersEntityTypes;
import house.greenhouse.rapscallionsandrockhoppers.registry.RockhoppersItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.BlockAttachedEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class BoatHookFenceKnotEntity extends BlockAttachedEntity {
    public BoatHookFenceKnotEntity(EntityType<? extends BoatHookFenceKnotEntity> entityType, Level level) {
        super(entityType, level);
    }
    
    public static BoatHookFenceKnotEntity create(Level level, BlockPos blockPos) {
        var entity = new BoatHookFenceKnotEntity(RockhoppersEntityTypes.BOAT_HOOK_FENCE_KNOT, level);
        entity.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        return entity;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (this.level().isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            boolean attachedToFence = false;
            double x = getX();
            double y = getY();
            double z = getZ();
            double max = 10.0D;
            AABB checkBox = new AABB(x - max, y - max, z - max, x + max, y + max, z + max);

            List<Boat> list = this.level().getEntitiesOfClass(Boat.class, checkBox);

            for (Boat boat : list) {
                var boatData = RapscallionsAndRockhoppers.getHelper().getBoatData(boat);
                if (boatData.getLinkedPlayer() == player) {
                    boatData.setHookKnotUuid(this.getUUID());
                    boatData.setLinkedPlayer(null);
                    boatData.sync();
                    attachedToFence = true;
                }
            }

            boolean removedFromFence = false;
            if (!attachedToFence) {
                this.discard();
                if (player.getAbilities().instabuild) {
                    for (Boat boat : list) {
                        var boatData = RapscallionsAndRockhoppers.getHelper().getBoatData(boat);
                            if (boatData.getHookKnot() == this) {
                                boatData.setHookKnotUuid(null);
                                boatData.sync();
                                removedFromFence = true;
                                boat.spawnAtLocation(RockhoppersItems.BOAT_HOOK);
                                
                        }
                    }
                }
            }

            if (attachedToFence || removedFromFence) {
                this.gameEvent(GameEvent.BLOCK_ATTACH, player);
            }

            return InteractionResult.CONSUME;
        }
    }
    public static BoatHookFenceKnotEntity getOrCreate(Level level, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        Iterator<BoatHookFenceKnotEntity> boatHooks = level.getEntitiesOfClass(BoatHookFenceKnotEntity.class, 
                new AABB((double)x - 1.0, (double)y - 1.0, (double)z - 1.0,
                        (double)x + 1.0, (double)y + 1.0, (double)z + 1.0)
                ).iterator();
        BoatHookFenceKnotEntity boathookEntity;
        do {
            if (!boatHooks.hasNext()) {
                BoatHookFenceKnotEntity newBoatKnot = BoatHookFenceKnotEntity.create(level, pos);
                level.addFreshEntity(newBoatKnot);
                return newBoatKnot;
            }

            boathookEntity = boatHooks.next();
        } while (!boathookEntity.getPos().equals(pos));

        return boathookEntity;
    }

    public void playPlacementSound() {
        this.playSound(SoundEvents.LEASH_KNOT_PLACE, 1.0F, 1.0F);
    }

    @Override
    protected void recalculateBoundingBox() {
        this.setPosRaw(this.pos.getX() + 0.5d, this.pos.getY() + 0.375, this.pos.getZ() + 0.5);
        double d0 = this.getType().getWidth() / 2.0d;
        double d1 = this.getType().getHeight();
        this.setBoundingBox(new AABB(this.getX() - d0, this.getY(), this.getZ() - d0, this.getX() + d0, this.getY() + d1, this.getZ() + d0));
    }
    
    @Override
    public boolean survives() {
        return this.level().getBlockState(this.pos).is(BlockTags.FENCES);
    }

    @Override
    public void dropItem(@Nullable Entity entity) {
        this.playSound(SoundEvents.LEASH_KNOT_BREAK, 1.0F, 1.0F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 1024.0d;
    }

    @Override
    public @Nullable ItemStack getPickResult() {
        return new ItemStack(RockhoppersItems.BOAT_HOOK);
    }
}
