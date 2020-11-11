package io.github.coolmineman.bitsandchisels;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import grondag.frex.api.material.MaterialMap;
import io.github.coolmineman.bitsandchisels.mixin.SimpleVoxelShapeFactory;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.SpriteFinder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class BitsBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

    private BlockState[][][] states = new BlockState[16][16][16];
    protected Mesh mesh;
    protected VoxelShape shape = VoxelShapes.fullCube();
    private BitTransform transform = new BitTransform();

    public BitsBlockEntity() {
        this(Blocks.AIR.getDefaultState());
    }

    public BitsBlockEntity(BlockState state) {
        super(BitsAndChisels.BITS_BLOCK_ENTITY);
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 16; k++) {
                    states[i][j][k] = state;
                }
            }
        }
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
        int index = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 16; k++) {
                    states[i][j][k] = palette.get(bits.getShort(index));
                    index++;
                }
            }
        }
        rebuildCollision();
    }

    public void setState(int x, int y, int z, BlockState state) {
        states[x][y][z] = state;
    }

    public void rebuild(boolean client) {
        if (client) rebuildMesh();
        rebuildCollision();
        if (client) MinecraftClient.getInstance().worldRenderer.scheduleBlockRenders(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockState getState(int x, int y, int z) {
        return states[x][y][z];
    }

    public boolean quadNeeded(Direction d, int x, int y, int z) {
        switch (d) {
            case UP:
                if (y <= 14) return states[x][y + 1][z].isAir();
                return true;
            case DOWN:
                if (y >= 1) return states[x][y - 1][z].isAir();
                return true;
            case SOUTH:
                if (z <= 14) return states[x][y][z + 1].isAir();
                return true;
            case NORTH:
                if (z >= 1) return states[x][y][z - 1].isAir();
                return true;
            case EAST:
                if (x <= 14) return states[x + 1][y][z].isAir();
                return true;
            case WEST:
                if (x >= 1) return states[x - 1][y][z].isAir();
                return true;
        }
        return true;
    }

    protected void rebuildCollision() {
        BitSetVoxelSet set = new BitSetVoxelSet(16, 16, 16);
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 16; k++) {
                    BlockState state = states[i][j][k];
                    if (!state.isAir()) set.set(i, j, k, true, true);
                }
            }
        }
        shape = SimpleVoxelShapeFactory.getSimpleVoxelShape(set);
    }

    protected void rebuildMesh() {
        boolean canvas = RendererAccess.INSTANCE.getRenderer().getClass().getName().equals("grondag.canvas.apiimpl.Canvas");
        MeshBuilder builder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
        QuadEmitter emitter = builder.getEmitter();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 16; k++) {
                    transform.x = i;
                    transform.y = j;
                    transform.z = k;

                    for (Direction d : Direction.values()) {
                        for (BakedQuad vanillaQuad : MinecraftClient.getInstance().getBlockRenderManager().getModel(states[i][j][k]).getQuads(states[i][j][k], d, ThreadLocalRandom.current())) {
                            if (quadNeeded(d, i, j, k)) {
                                MutableQuadView quad = emitter.fromVanilla(vanillaQuad, RendererAccess.INSTANCE.getRenderer().materialFinder().find(), d);
                                transform.transform(quad);
                                if (canvas) {
                                    Sprite sprite = SpriteFinder.get(MinecraftClient.getInstance().getBakedModelManager().method_24153(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)).find(quad, 0);
                                    quad.material(MaterialMap.get(states[i][j][k]).getMapped(sprite));
                                }
                                quad.cullFace(null);
                                emitter = emitter.emit();
                            }
                        }
                    }  
                }
            }
        }
        mesh = builder.build();
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        fromTag(null, tag);
        rebuildMesh();
        MinecraftClient.getInstance().worldRenderer.scheduleBlockRenders(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return toTag(tag);
    }

}
