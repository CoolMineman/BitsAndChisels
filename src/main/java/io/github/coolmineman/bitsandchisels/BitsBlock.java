package io.github.coolmineman.bitsandchisels;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BitsBlock extends Block implements BlockEntityProvider {

    public static final IntProperty LIGHT_LEVEL = IntProperty.of("light_level", 0, 16);

    public BitsBlock(Settings settings) {
        super(settings);
        this.setDefaultState(stateManager.getDefaultState().with(LIGHT_LEVEL, 0));
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(LIGHT_LEVEL);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (world instanceof ServerWorld && blockEntity != null) {
            dropStack(world, pos, ItemHelpers.blockToItem(this, blockEntity));
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new BitsBlockEntity();
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return ItemHelpers.blockToItem(this, world.getBlockEntity(pos));
    }
    
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        BlockEntity bruhWhyMustMcBeLikeThisAhhhhh = world.getBlockEntity(pos);
        if (bruhWhyMustMcBeLikeThisAhhhhh instanceof BitsBlockEntity) {
            return ((BitsBlockEntity)bruhWhyMustMcBeLikeThisAhhhhh).shape;
        }
        return VoxelShapes.empty();
    }

}
