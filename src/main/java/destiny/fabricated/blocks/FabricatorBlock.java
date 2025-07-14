package destiny.fabricated.blocks;

import destiny.fabricated.block_entities.FabricatorBlockEntity;
import destiny.fabricated.init.BlockEntityInit;
import destiny.fabricated.util.MathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class FabricatorBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final VoxelShape SHAPE_NORTH = MathUtil.buildShape(
            Block.box(1, -1, 13, 15, 17, 16)
    );
    public static final VoxelShape SHAPE_SOUTH = MathUtil.buildShape(
            Block.box(1, -1, 0, 15, 17, 3)
    );
    public static final VoxelShape SHAPE_WEST = MathUtil.buildShape(
            Block.box(13, -1, 1, 16, 17, 15)
    );
    public static final VoxelShape SHAPE_EAST = MathUtil.buildShape(
            Block.box(0, -1, 1, 3, 17, 15)
    );

    public FabricatorBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.getBlockEntity(pPos) instanceof FabricatorBlockEntity fabricator)
        {
            if(fabricator.isOpen)
                return InteractionResult.PASS;

            if (pPlayer.getMainHandItem().getItem() == Items.STICK)
            {
                fabricator.openUpgradesMenu(pPlayer, pLevel, pPos, fabricator);
                return InteractionResult.SUCCESS;
            }
            else if(!fabricator.getRecipeTypes().isEmpty())
            {
                fabricator.openCraftingMenu(pPlayer, pLevel, pPos, fabricator);
                return InteractionResult.SUCCESS;
            }
        }

        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder)
    {
        blockStateBuilder.add(WATERLOGGED, FACING);
        super.createBlockStateDefinition(blockStateBuilder);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext)
    {
        switch (pState.getValue(FACING)) {
            case NORTH:
                return SHAPE_NORTH;
            case SOUTH:
                return SHAPE_SOUTH;
            case EAST:
                return SHAPE_EAST;
            case WEST:
                return SHAPE_WEST;
            default:
                return SHAPE_NORTH;
        }
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext)
    {
        LevelAccessor accessor = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();
        return this.defaultBlockState().setValue(WATERLOGGED, accessor.getFluidState(pos).getType() == Fluids.WATER).setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos)
    {
        if (!isOnBlock(pLevel, pPos, pState)) {
            pLevel.destroyBlock(pPos, true);
        }

        if (pState.getValue(WATERLOGGED))
        {
            pLevel.scheduleTick(pPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }
        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }

    public boolean isOnBlock(LevelAccessor pLevel, BlockPos pPos, BlockState pState) {
        Direction blockDirection = pState.getValue(FACING);
        Block parentBlock = pLevel.getBlockState(pPos.relative(blockDirection.getOpposite())).getBlock();
        return !parentBlock.equals(Blocks.AIR);
    }

    @org.jetbrains.annotations.Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntity) {
        return createTickerHelper(blockEntity, BlockEntityInit.FABRICATOR.get(), FabricatorBlockEntity::tick);
    }

    @Override
    public @org.jetbrains.annotations.Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityInit.FABRICATOR.get().create(pos, state);
    }
}
