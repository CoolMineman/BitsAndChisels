package io.github.coolmineman.bitsandchisels;

import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;

public class BitNbtUtil {
    private BitNbtUtil() { }

    public static boolean read3DBitArray(NbtCompound tag, BlockState[][][] out) {
        ArrayList<BlockState> palette = new ArrayList<>();
        NbtList palletteTag = (NbtList) tag.get("palette");
        if (palletteTag == null) return false;
        for (NbtElement statetag : palletteTag) {
            palette.add(BitNbtUtil.toBlockState((NbtCompound) statetag));
        }
        NbtList bits = (NbtList) tag.get("bits");
        NbtByteArray bits2 = (NbtByteArray) tag.get("bits_v2");
        int index = 0;
        if (bits != null) {
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    for (int k = 0; k < 16; k++) {
                        out[i][j][k] = palette.get(bits.getShort(index));
                        index++;
                    }
                }
            }
        } else if (bits2 != null) {
            byte[] bitsBytes = bits2.getByteArray();
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    for (int k = 0; k < 16; k++) {
                        out[i][j][k] = palette.get((bitsBytes[index + 1] << 8 | bitsBytes[index] & 0xFF));
                        index += 2;
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }

    public static void write3DBitArray(NbtCompound tag, BlockState[][][] in) {
        ArrayList<BlockState> palette = new ArrayList<>();
        byte[] bits = new byte[8192];
        int arrayIndex = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 16; k++) {
                    BlockState state = in[i][j][k];
                    short index = (short) palette.indexOf(state);
                    if (index == -1) {
                        palette.add(state);
                        index = (short) (palette.size() - 1);
                    }
                    bits[arrayIndex] = (byte)(index & 0xff);
                    bits[arrayIndex + 1] = (byte)((index >> 8) & 0xff);
                    arrayIndex += 2;
                }
            }
        }
        tag.put("bits_v2", new NbtByteArray(bits));
        NbtList palletteTag = new NbtList();
        for (BlockState state : palette) {
            palletteTag.add(BitNbtUtil.fromBlockState(state));
        }
        tag.put("palette", palletteTag);
    }

    public static BlockState toBlockState(@Nullable NbtCompound tag) {
        if (tag == null) {
            return Blocks.AIR.getDefaultState();
        } else {
            return NbtHelper.toBlockState(tag);
        }
    }

    public static NbtCompound fromBlockState(@Nullable BlockState state) {
        if (state == null) {
            return NbtHelper.fromBlockState(Blocks.AIR.getDefaultState());
        } else {
            return NbtHelper.fromBlockState(state);
        }
    }
}
