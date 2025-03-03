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

import java.util.Set;

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
                if (attemptCreateBoatKnot(player, level, blockpos).consumesAction()) {
                    if (!player.getAbilities().instabuild)
                        context.getItemInHand().shrink(1);
                }
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    public static InteractionResult attemptCreateBoatKnot(Player player, Level level, BlockPos pos) {
        if (!RapscallionsAndRockhoppers.getHelper().hasPlayerData(player))
            return InteractionResult.PASS;
        BoatHookFenceKnotEntity boatHookFenceKnot = null;

        var playerData = RapscallionsAndRockhoppers.getHelper().getPlayerData(player);
        Set<Boat> set = playerData.getLinkedBoats(level);

        Boat boat;
        for (Boat value : set) {
            boat = value;
            var boatData = RapscallionsAndRockhoppers.getHelper().getBoatData(boat);
            if (boatHookFenceKnot == null) {
                boatHookFenceKnot = BoatHookFenceKnotEntity.getOrCreate(level, pos);
                boatHookFenceKnot.playPlacementSound();
            }
            boatData.setHookKnotUuid(boatHookFenceKnot.getUUID());
            boatData.setLinkedPlayerUuid(null);
            playerData.removeLinkedBoat(boat.getUUID());
            if (!boatData.hasData())
                RapscallionsAndRockhoppers.getHelper().removeBoatData(boat);
            if (playerData.getLinkedBoatUUIDs().isEmpty())
                RapscallionsAndRockhoppers.getHelper().removePlayerData(player);
            if (!boat.level().isClientSide())
                RapscallionsAndRockhoppers.getHelper().syncBoatData(boat);
        }
        if (!player.level().isClientSide())
            RapscallionsAndRockhoppers.getHelper().syncPlayerData(player);

        if (!set.isEmpty()) {
            level.gameEvent(GameEvent.BLOCK_ATTACH, pos, GameEvent.Context.of(player));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
