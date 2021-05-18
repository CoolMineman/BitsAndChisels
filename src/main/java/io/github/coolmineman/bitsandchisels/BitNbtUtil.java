package io.github.coolmineman.bitsandchisels;

import java.util.ArrayList;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.Tag;

public class BitNbtUtil {
    private BitNbtUtil() { }

    public static boolean read3DBitArray(CompoundTag tag, BlockState[][][] out) {
        ArrayList<BlockState> palette = new ArrayList<>();
        ListTag palletteTag = (ListTag) tag.get("palette");
        if (palletteTag == null) return false;
        for (Tag statetag : palletteTag) {
            palette.add(NbtHelper.toBlockState((CompoundTag) statetag));
        }
        ListTag bits = (ListTag) tag.get("bits");
        ByteArrayTag bits2 = (ByteArrayTag) tag.get("bits_v2");
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

    public static void write3DBitArray(CompoundTag tag, BlockState[][][] in) {
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
        tag.put("bits_v2", new ByteArrayTag(bits));
        ListTag palletteTag = new ListTag();
        for (BlockState state : palette) {
            palletteTag.add(NbtHelper.fromBlockState(state));
        }
        tag.put("palette", palletteTag);
    }
}
