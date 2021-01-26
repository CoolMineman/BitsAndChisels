package io.github.coolmineman.bitsandchisels;

import java.util.ArrayList;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;

public class BitNbtUtil {
    private BitNbtUtil() { }

    public static void read3DBitArray(CompoundTag tag, BlockState[][][] out) {
        ArrayList<BlockState> palette = new ArrayList<>();
        ListTag pallette_tag = (ListTag) tag.get("palette");
        for (Tag statetag : pallette_tag) {
            palette.add(NbtHelper.toBlockState((CompoundTag) statetag));
        }
        ListTag bits = (ListTag) tag.get("bits");
        int index = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 16; k++) {
                    out[i][j][k] = palette.get(bits.getShort(index));
                    index++;
                }
            }
        }
    }

    public static void write3DBitArray(CompoundTag tag, BlockState[][][] in) {
        ArrayList<BlockState> palette = new ArrayList<>();
        ListTag bits = new ListTag();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 16; k++) {
                    BlockState state = in[i][j][k];
                    short index = (short) palette.indexOf(state);
                    if (index == -1) {
                        palette.add(state);
                        index = (short) (palette.size() - 1);
                    }
                    bits.add(ShortTag.of(index));
                }
            }
        }
        tag.put("bits", bits);
        ListTag pallette_tag = new ListTag();
        for (BlockState state : palette) {
            pallette_tag.add(NbtHelper.fromBlockState(state));
        }
        tag.put("palette", pallette_tag);
    }
}
