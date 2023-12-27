package dev.greenhouseteam.rapscallionsandrockhoppers.block;

import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PenguinEggBlock extends Block {
    private static final VoxelShape SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 8.0, 11.0);
    public static final IntegerProperty HATCH = BlockStateProperties.HATCH;

    public PenguinEggBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(HATCH, 0));
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(HATCH);
    }

    public static void crackEgg(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos) {
        int hatch = blockState.getValue(HATCH);
        if (hatch < 2) {
            serverLevel.setBlock(blockPos, blockState.setValue(HATCH, hatch + 1), 2);
        } else {
            Penguin penguin = RockhoppersEntityTypes.PENGUIN.create(serverLevel);
            if (penguin == null) {
                return;
            }
            penguin.setAge(-24000);
            penguin.moveTo(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0.0f, 0.0f);
            serverLevel.addFreshEntity(penguin);
            serverLevel.removeBlock(blockPos, false);
        }
        serverLevel.addDestroyBlockEffect(blockPos, blockState);
        // TODO: Play a sound
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (randomSource.nextInt(1500) == 0) {
            // 1/1500 chance each random tick of cracking
            crackEgg(blockState, serverLevel, blockPos);
        }
    }
}
