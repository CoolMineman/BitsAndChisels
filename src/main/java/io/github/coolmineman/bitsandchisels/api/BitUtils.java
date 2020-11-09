package io.github.coolmineman.bitsandchisels.api;

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

    public static boolean setBit(World world, @Nullable PlayerEntity player, BlockPos block, int x, int y, int z, BlockState state, boolean updateclients) {
        BlockEntity e1 = world.getBlockEntity(block);
        if (e1 instanceof BitsBlockEntity) {
            BitsBlockEntity e = (BitsBlockEntity) e1;
            e.setState(x, y, z, state);
            e.rebuild(false);
            if (updateclients) {
                if (player != null) e.dontupdateuuid = player.getUuid();
                e.sync();
            }
            return true;
        }
        return false;
    }

    public static boolean setBitClient(World world, BlockPos block, int x, int y, int z, BlockState state, boolean rebuild) {
        BlockEntity e1 = world.getBlockEntity(block);
        if (e1 instanceof BitsBlockEntity) {
            BitsBlockEntity e = (BitsBlockEntity) e1;
            e.setState(x, y, z, state);
            if (rebuild) e.rebuild(true);
            return true;
        }
        return false;
    }

    public static BlockState getBit(World world, BlockPos block, int x, int y, int z) {
        BlockEntity e1 = world.getBlockEntity(block);
        if (e1 instanceof BitsBlockEntity) {
            BitsBlockEntity e = (BitsBlockEntity) e1;
            return e.getState(x, y, z);
        }
        return Blocks.AIR.getDefaultState();
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
