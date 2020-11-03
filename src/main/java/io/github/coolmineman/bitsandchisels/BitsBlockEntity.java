package io.github.coolmineman.bitsandchisels;

import java.util.ArrayList;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;

public class BitsBlockEntity extends BlockEntity implements BlockEntityClientSerializable {
    private BlockState[][][] states = new BlockState[16][16][16];

    public BitsBlockEntity() {
        super(BitsAndChisels.BITS_BLOCK_ENTITY);
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 16; k++) {
                    states[i][j][k] = Blocks.AIR.getDefaultState();
                }
            }
        }
        states[0][0][0] = Blocks.COBBLESTONE.getDefaultState();
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        ArrayList<BlockState> palette = new ArrayList<>();
        ListTag bits = new ListTag();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 16; k++) {
                    BlockState state = states[i][j][k];
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
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        ArrayList<BlockState> palette = new ArrayList<>();
        ListTag pallette_tag = (ListTag) tag.get("palette");
        for (Tag statetag : pallette_tag) {
            palette.add(NbtHelper.toBlockState((CompoundTag) statetag));
        }
        ListTag bits = (ListTag) tag.get("bits");
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 16; k++) {
                    states[i][j][k] = palette.get(bits.getShort(i));
                }
            }
        }
    }

    public void setState(int x, int y, int z, BlockState state) {
        states[x][y][z] = state;
    }

    public BlockState getState(int x, int y, int z) {
        return states[x][y][z];
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        fromTag(null, tag);
        MinecraftClient.getInstance().worldRenderer.scheduleBlockRenders(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return toTag(tag);
    }

}
