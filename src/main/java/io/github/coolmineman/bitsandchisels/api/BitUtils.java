package io.github.coolmineman.bitsandchisels.api;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import io.github.coolmineman.bitsandchisels.BitsAndChisels;
import io.github.coolmineman.bitsandchisels.BitsBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BitUtils {
    private BitUtils(){}

    public static boolean setBit(World world, BlockPos block, int x, int y, int z, BlockState state, boolean updateclients) {
        BlockState target_state = world.getBlockState(block);
        BlockEntity e1 = world.getBlockEntity(block);
        if (target_state.isAir()) {
            world.setBlockState(block, BitsAndChisels.BITS_BLOCK.getDefaultState());
        } else if (!target_state.isOf(BitsAndChisels.BITS_BLOCK) && target_state.isFullCube(world, block) && e1 == null) {
            world.setBlockState(block, BitsAndChisels.BITS_BLOCK.getDefaultState(), 0);
            world.setBlockEntity(block, new BitsBlockEntity(target_state));
        }
        e1 = world.getBlockEntity(block);
        if (e1 instanceof BitsBlockEntity) {
            BitsBlockEntity e = (BitsBlockEntity) e1;
            e.setState(x, y, z, state);
            e.rebuild(false);
            if (updateclients) {
                e.sync();
            }
            return true;
        }
        return false;
    }

    public static boolean canPlace(World world, BlockPos block, int x, int y, int z) {
        if (world.getBlockState(block).isAir()) {
            return true;
        }
        BlockEntity e1 = world.getBlockEntity(block);
        if (e1 instanceof BitsBlockEntity) {
            System.out.println(((BitsBlockEntity) e1).getState(x, y, z));
            return ((BitsBlockEntity) e1).getState(x, y, z).isAir();
        }
        return false;
    }

    public static Optional<BlockState> getBit(World world, BlockPos block, int x, int y, int z) {
        BlockState state = world.getBlockState(block);
        BlockEntity e1 = world.getBlockEntity(block);
        if (e1 instanceof BitsBlockEntity) {
            BitsBlockEntity e = (BitsBlockEntity) e1;
            return Optional.of(e.getState(x, y, z));
        } else if (!state.isOf(BitsAndChisels.BITS_BLOCK)) {
            if (state.isFullCube(world, block)) return Optional.of(state);
        }
        return Optional.empty();
    }

    public static boolean exists(Optional<BlockState> bit) {
        return bit.isPresent() ? !bit.get().isAir() : false;
    }

    public static ItemStack getBitItemStack(BlockState state) {
        ItemStack result = new ItemStack(BitsAndChisels.BIT_ITEM);
        result.putSubTag("bit", NbtHelper.fromBlockState(state));
        return result;
    }

    public static BlockState getBit(ItemStack stack) {
        return NbtHelper.toBlockState(stack.getSubTag("bit"));
    }
}
