package house.greenhouse.rapscallionsandrockhoppers.item;

import house.greenhouse.rapscallionsandrockhoppers.RapscallionsAndRockhoppers;
import house.greenhouse.rapscallionsandrockhoppers.entity.BoatHookFenceKnotEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class BoatHookItem extends Item {
    public BoatHookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        if (blockstate.is(BlockTags.FENCES)) {
            Player player = context.getPlayer();
            if (!level.isClientSide && player != null) {
                attemptCreateBoatKnot(player, level, blockpos);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    public static InteractionResult attemptCreateBoatKnot(Player player, Level level, BlockPos pos) {
        BoatHookFenceKnotEntity boathookFenceKnot = null;
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        double max = 10.0D;
        AABB checkBox = new AABB(x - max, y - max, z - max, x + max, y + max, z + max);

        List<Boat> list = level.getEntitiesOfClass(Boat.class, checkBox);


        Boat boat;
        for (Boat value : list) {
            boat = value;
            var boatData = RapscallionsAndRockhoppers.getHelper().getBoatData(boat);
            if (boathookFenceKnot == null) {
                boathookFenceKnot = BoatHookFenceKnotEntity.getOrCreate(level, pos);
                boathookFenceKnot.playPlacementSound();
            }
            if (boatData.getLinkedPlayer() == player) {
                boatData.setHookKnotUuid(boathookFenceKnot.getUUID());
                boatData.setLinkedPlayer(null);
                var playerData = RapscallionsAndRockhoppers.getHelper().getPlayerData(player);
                playerData.removeLinkedBoat(boat.getUUID());
                boatData.sync();
                playerData.sync();
            }
        }

        if (!list.isEmpty()) {
            level.gameEvent(GameEvent.BLOCK_ATTACH, pos, GameEvent.Context.of(player));
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }
}
