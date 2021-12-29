package io.github.coolmineman.bitsandchisels.api;

import org.jetbrains.annotations.Nullable;

import io.github.coolmineman.bitsandchisels.BitNbtUtil;
import io.github.coolmineman.bitsandchisels.BitsAndChisels;
import io.github.coolmineman.bitsandchisels.BitsBlockEntity;
import io.github.coolmineman.bitsandchisels.FlanIsStupid;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
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

    public static boolean canPlace(PlayerEntity player, BlockPos block, int x, int y, int z) {
        if (BitsAndChisels.FLAN && player instanceof ServerPlayerEntity && !FlanIsStupid.canPlace((ServerPlayerEntity) player, block)) return false;
        World world = player.getEntityWorld();
        if (!world.canPlayerModifyAt(player, block)) return false;
        if (world.getBlockState(block).isAir()) {
            return true;
        }
        BlockEntity e1 = world.getBlockEntity(block);
        if (e1 instanceof BitsBlockEntity) {
            return ((BitsBlockEntity) e1).getState(x, y, z).isAir();
        }
        return false;
    }
    
    public static boolean canBreak(PlayerEntity player, BlockPos block, int x, int y, int z) {
        if (BitsAndChisels.FLAN && player instanceof ServerPlayerEntity && !FlanIsStupid.canBreak((ServerPlayerEntity) player, block)) return false;
        World world = player.getEntityWorld();
        if (!world.canSetBlock(block)) return false;
        if (!world.canPlayerModifyAt(player, block)) return false;
        return true;
    }

    public static @Nullable BlockState getBit(World world, BlockPos block, int x, int y, int z) {
        BlockState state = world.getBlockState(block);
        BlockEntity e1 = world.getBlockEntity(block);
        if (e1 instanceof BitsBlockEntity) {
            BitsBlockEntity e = (BitsBlockEntity) e1;
            return e.getState(x, y, z);
        } else if (!state.isOf(BitsAndChisels.BITS_BLOCK) && canBeChiseled(state, world, block) && state.isFullCube(world, block)) {
            return state;
        }
        return null;
    }

    public static @Nullable BlockState[][][] getBitArray(World world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof BitsBlockEntity) {
            return ((BitsBlockEntity)be).getStates();
        }
        return null;
    }

    public static boolean exists(@Nullable BlockState bit) {
        return bit != null && !bit.isAir();
    }

    public static ItemStack getBitItemStack(BlockState state) {
        ItemStack result = new ItemStack(BitsAndChisels.BIT_ITEM);
        result.setSubNbt("bit", BitNbtUtil.fromBlockState(state));
        return result;
    }

    public static BlockState getBit(ItemStack stack) {
        return BitNbtUtil.toBlockState(stack.getSubNbt("bit"));
    }

    public static boolean canBeChiseled(BlockState state, World world, BlockPos pos) {
        return state.getHardness(world, pos) <= 100 && state.getHardness(world, pos) >= 0;
    }
    
    public static void attemptBreak(ServerPlayerEntity player, BlockPos pos, int x, int y, int z) {
        if (canBreak(player, pos, x, y, z)) {
            World world = player.world;
            BlockState oldstate = BitUtils.getBit(world, pos, x, y, z);
            if (BitUtils.exists(oldstate) && BitUtils.setBit(world, pos, x, y, z, Blocks.AIR.getDefaultState())) {
                BitUtils.update(world, pos);
                player.getInventory().offerOrDrop(BitUtils.getBitItemStack(oldstate));
            }
        }
    }
    
    public static void attemptBreakRegion(ServerPlayerEntity player, BlockPos rootPos, int x1, int y1, int z1, int x2, int y2, int z2) {
        World world = player.world;
        BlockPos.Mutable mut = new BlockPos.Mutable();
        Object2IntArrayMap<BlockState> drops = new Object2IntArrayMap<>();
        for (int i = x1; i <= x2; i++) {
            for (int j = y1; j <= y2; j++) {
                for (int k = z1; k <= z2; k++) {
                    mut.set(rootPos.getX() + Math.floorDiv(i, 16), rootPos.getY() + Math.floorDiv(j, 16), rootPos.getZ() + Math.floorDiv(k, 16));
                    int x = Math.floorMod(i, 16);
                    int y = Math.floorMod(j, 16);
                    int z = Math.floorMod(k, 16);
                    if (canBreak(player, mut, x, y, z)) {
                        BlockState oldstate = BitUtils.getBit(world, mut, x, y, z);
                        if (BitUtils.exists(oldstate) && BitUtils.setBit(world, mut, x, y, z, Blocks.AIR.getDefaultState())) {
                            drops.put(oldstate, drops.getOrDefault(oldstate, 0) + 1);
                        }
                    }
                }
            }
        }
        int blockx1 = rootPos.getX() + Math.floorDiv(x1, 16);
        int blocky1 = rootPos.getY() + Math.floorDiv(y1, 16);
        int blockz1 = rootPos.getZ() + Math.floorDiv(z1, 16);
        int blockx2 = rootPos.getX() + Math.floorDiv(x2, 16);
        int blocky2 = rootPos.getY() + Math.floorDiv(y2, 16);
        int blockz2 = rootPos.getZ() + Math.floorDiv(z2, 16);
        for (int i = blockx1; i <= blockx2; i++) {
            for (int j = blocky1; j <= blocky2; j++) {
                for (int k = blockz1; k <= blockz2; k++) {
                    mut.set(i, j, k);
                    BitUtils.update(world, mut);
                }
            }
        }
        for (Object2IntMap.Entry<BlockState> e : drops.object2IntEntrySet()) {
            ItemStack stack = BitUtils.getBitItemStack(e.getKey());
            stack.setCount(e.getIntValue());
            player.getInventory().offerOrDrop(stack);
        }
    }
}
