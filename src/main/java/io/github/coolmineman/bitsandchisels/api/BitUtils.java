package io.github.coolmineman.bitsandchisels.api;

import org.jetbrains.annotations.Nullable;

import io.github.coolmineman.bitsandchisels.BitsAndChisels;
import io.github.coolmineman.bitsandchisels.BitsBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BitUtils {
    private BitUtils(){}

    public static boolean setBit(World world, BlockPos block, int x, int y, int z, BlockState state) {
        BlockState target_state = world.getBlockState(block);
        BlockEntity e1 = world.getBlockEntity(block);
        if (target_state.isAir()) {
            world.setBlockState(block, BitsAndChisels.BITS_BLOCK.getDefaultState());
        } else if (!target_state.isOf(BitsAndChisels.BITS_BLOCK) && target_state.isFullCube(world, block) && e1 == null) {
            world.setBlockState(block, BitsAndChisels.BITS_BLOCK.getDefaultState(), 0);
            world.addBlockEntity(new BitsBlockEntity(target_state, block, BitsAndChisels.BITS_BLOCK.getDefaultState()));
        }
        e1 = world.getBlockEntity(block);
        if (e1 instanceof BitsBlockEntity) {
            BitsBlockEntity e = (BitsBlockEntity) e1;
            e.setState(x, y, z, state);
            return true;
        }
        return false;
    }

    public static void update(World world, BlockPos block) {
        BlockEntity e1 = world.getBlockEntity(block);
        if (e1 instanceof BitsBlockEntity) {
            BitsBlockEntity e = (BitsBlockEntity) e1;
            e.rebuildServer();
            e.sync();
        }
    }

    public static boolean canPlace(World world, BlockPos block, int x, int y, int z) {
        if (world.getBlockState(block).isAir()) {
            return true;
        }
        BlockEntity e1 = world.getBlockEntity(block);
        if (e1 instanceof BitsBlockEntity) {
            return ((BitsBlockEntity) e1).getState(x, y, z).isAir();
        }
        return false;
    }

    public static @Nullable BlockState getBit(World world, BlockPos block, int x, int y, int z) {
        BlockState state = world.getBlockState(block);
        BlockEntity e1 = world.getBlockEntity(block);
        if (e1 instanceof BitsBlockEntity) {
            BitsBlockEntity e = (BitsBlockEntity) e1;
            return e.getState(x, y, z);
        } else if (!state.isOf(BitsAndChisels.BITS_BLOCK) && canChisel(state, world, block) && state.isFullCube(world, block)) {
            return state;
        }
        return null;
    }

    public static boolean exists(@Nullable BlockState bit) {
        return bit != null && !bit.isAir();
    }

    public static ItemStack getBitItemStack(BlockState state) {
        ItemStack result = new ItemStack(BitsAndChisels.BIT_ITEM);
        result.putSubTag("bit", NbtHelper.fromBlockState(state));
        return result;
    }

    public static BlockState getBit(ItemStack stack) {
        return NbtHelper.toBlockState(stack.getSubTag("bit"));
    }

    public static boolean canChisel(BlockState state, World world, BlockPos pos) {
        return state.getHardness(world, pos) <= 100 && state.getHardness(world, pos) >= 0;
    }
}
