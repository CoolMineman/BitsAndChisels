package io.github.coolmineman.bitsandchisels;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.*;
import net.minecraft.world.*;

public class BitsBlock extends Block implements BlockEntityProvider {

    public BitsBlock(Settings settings) {
        super(settings);
    }

    private static double m(double d) {
        double a = d % 1;
        if (a < 0) return 1 + a;
        return a;
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
        super.afterBreak(world, player, pos, state, blockEntity, stack);
        if (world instanceof ServerWorld && blockEntity != null) {
            dropStack(world, pos, ItemHelpers.blockToItem(this, blockEntity));
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new BitsBlockEntity();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        BitsBlockEntity e = (BitsBlockEntity) world.getBlockEntity(pos);
        if (e != null) {
            return e.shape;
        }
        return VoxelShapes.empty();
    }

    @Override
    public void onSteppedOn(World world, BlockPos uselessPos, Entity entity) {
        BlockPos pos = entity.getBlockPos();
        Vec3d pos1 = entity.getPos();
        Vec3d checkPos = new Vec3d(m(pos1.getX()) * 16, m(pos1.getY()) * 16 - 1, m(pos1.getZ()) * 16);
        if (checkPos.y < 0) {
            pos = pos.add(0, -1, 0);
            checkPos = checkPos.add(0, 16, 0);
        }
        BitsBlockEntity e = (BitsBlockEntity) world.getBlockEntity(pos);
        BlockState state = e.getState((int) checkPos.getX(), (int) checkPos.getY(), (int) checkPos.getZ());
        try {
            state.getBlock().onSteppedOn(world, pos, entity);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onLandedUpon(World world, BlockPos uselessPos, Entity entity, float distance) {
        BlockPos pos = entity.getBlockPos();
        Vec3d pos1 = entity.getPos();
        Vec3d checkPos = new Vec3d(m(pos1.getX()) * 16, m(pos1.getY()) * 16 - 1, m(pos1.getZ()) * 16);
        if (checkPos.y < 0) {
            pos = pos.add(0, -1, 0);
            checkPos = checkPos.add(0, 16, 0);
        }
        BitsBlockEntity e = (BitsBlockEntity) world.getBlockEntity(pos);
        BlockState state = e.getState((int) checkPos.getX(), (int) checkPos.getY(), (int) checkPos.getZ());
        try {
            state.getBlock().onLandedUpon(world, pos, entity, distance);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onEntityLand(BlockView world, Entity entity) {
        BlockPos pos = entity.getBlockPos();
        Vec3d pos1 = entity.getPos();
        Vec3d checkPos = new Vec3d(m(pos1.getX()) * 16, m(pos1.getY()) * 16 - 1, m(pos1.getZ()) * 16);
        if (checkPos.y < 0) {
            pos = pos.add(0, -1, 0);
            checkPos = checkPos.add(0, 16, 0);
        }
        BitsBlockEntity e = (BitsBlockEntity) world.getBlockEntity(pos);
        BlockState state = e.getState((int) checkPos.getX(), (int) checkPos.getY(), (int) checkPos.getZ());
        try {
            state.getBlock().onEntityLand(world, entity);
        } catch (Exception ignored) {
        }
    }
}
