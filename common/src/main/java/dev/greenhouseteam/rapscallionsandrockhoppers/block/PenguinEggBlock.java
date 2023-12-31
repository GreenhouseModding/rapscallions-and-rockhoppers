package dev.greenhouseteam.rapscallionsandrockhoppers.block;

import dev.greenhouseteam.rapscallionsandrockhoppers.block.entity.PenguinEggBlockEntity;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class PenguinEggBlock extends Block implements EntityBlock {
    private static final VoxelShape SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 8.0, 11.0);
    private static final VoxelShape PENGUIN_SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 4.0, 11.0);
    public static final IntegerProperty HATCH = BlockStateProperties.HATCH;

    public PenguinEggBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(HATCH, 0));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        if (collisionContext instanceof EntityCollisionContext entityCollisionContext && entityCollisionContext.getEntity() instanceof Penguin) {
            return this.hasCollision ? PENGUIN_SHAPE : Shapes.empty();
        }
        return this.hasCollision ? state.getShape(blockGetter, blockPos) : Shapes.empty();
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
            if (serverLevel.getBlockEntity(blockPos) instanceof PenguinEggBlockEntity penguinEggBlockEntity) {
                var penguinType = penguinEggBlockEntity.getPenguinType(serverLevel);
                penguin.setPenguinType(penguinType);
            }
            serverLevel.addFreshEntity(penguin);
            serverLevel.removeBlock(blockPos, false);
        }
        serverLevel.addDestroyBlockEffect(blockPos, blockState);
        // TODO: Play a sound
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (randomSource.nextInt(600) == 0) {
            // 1/500 chance each random tick of cracking
            crackEgg(blockState, serverLevel, blockPos);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new PenguinEggBlockEntity(blockPos, blockState);
    }
}
