package io.github.coolmineman.bitsandchisels;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import grondag.frex.api.Renderer;
import grondag.frex.api.material.MaterialMap;
import grondag.frex.api.material.RenderMaterial;
import io.github.coolmineman.bitsandchisels.mixin.SimpleVoxelShapeFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.SpriteFinder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
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
    @Environment(EnvType.CLIENT)
    protected Mesh mesh;
    protected VoxelShape shape = VoxelShapes.fullCube();
    boolean fullcube = false;

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
        rebuildShape();
    }

    public void setState(int x, int y, int z, BlockState state) {
        states[x][y][z] = state;
    }

    public void rebuildServer() {
        rebuildShape();
        if (fullcube) {
            world.setBlockState(pos, states[0][0][0]);
        }
    }

    public BlockState getState(int x, int y, int z) {
        return states[x][y][z];
    }

    public boolean quadNeeded(Direction d, int x, int y, int z) {
        switch (d) {
            case UP:
                if (y <= 14) return states[x][y + 1][z].isAir() || (RenderLayers.getBlockLayer(states[x][y + 1][z]) != RenderLayer.getSolid() && states[x][y][z] != states[x][y + 1][z]);
                return true;
            case DOWN:
                if (y >= 1) return states[x][y - 1][z].isAir() || (RenderLayers.getBlockLayer(states[x][y - 1][z]) != RenderLayer.getSolid() && states[x][y][z] != states[x][y - 1][z]);
                return true;
            case SOUTH:
                if (z <= 14) return states[x][y][z + 1].isAir() || (RenderLayers.getBlockLayer(states[x][y][z + 1]) != RenderLayer.getSolid() && states[x][y][z] != states[x][y][z + 1]);
                return true;
            case NORTH:
                if (z >= 1) return states[x][y][z - 1].isAir() || (RenderLayers.getBlockLayer(states[x][y][z - 1]) != RenderLayer.getSolid() && states[x][y][z] != states[x][y][z - 1]);
                return true;
            case EAST:
                if (x <= 14) return states[x + 1][y][z].isAir() || (RenderLayers.getBlockLayer(states[x + 1][y][z]) != RenderLayer.getSolid() && states[x][y][z] != states[x + 1][y][z]);
                return true;
            case WEST:
                if (x >= 1) return states[x - 1][y][z].isAir() || (RenderLayers.getBlockLayer(states[x - 1][y][z]) != RenderLayer.getSolid() && states[x][y][z] != states[x - 1][y][z]);
                return true;
        }
        return true;
    }

    protected void rebuildShape() {
        fullcube = true;
        BitSetVoxelSet set = new BitSetVoxelSet(16, 16, 16);
        BlockState firststate = states[0][0][0];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 16; k++) {
                    BlockState state = states[i][j][k];
                    if (!state.isAir()) {
                        set.set(i, j, k, true, true);
                    }
                    if (firststate != state) {
                        fullcube = false;
                    }
                }
            }
        }
        shape = SimpleVoxelShapeFactory.getSimpleVoxelShape(set);
    }

    @Environment(EnvType.CLIENT)
    protected void rebuildMesh() {
        boolean canvas = RendererAccess.INSTANCE.getRenderer().getClass().getName().equals("grondag.canvas.apiimpl.Canvas");
        MeshBuilder builder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
        QuadEmitter emitter = builder.getEmitter();
        BitTransform transform = new BitTransform();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 16; k++) {
                    BlockState state = states[i][j][k];

                    transform.x = i;
                    transform.y = j;
                    transform.z = k;
                    try {
                        for (Direction d : Direction.values()) {
                            for (BakedQuad vanillaQuad : MinecraftClient.getInstance().getBlockRenderManager().getModel(state).getQuads(state, d, ThreadLocalRandom.current())) {
                                if (quadNeeded(d, i, j, k)) {
                                    MutableQuadView quad;
                                    if (canvas) {
                                        quad = emitter.fromVanilla(vanillaQuad, RendererAccess.INSTANCE.getRenderer().materialFinder().find(), d); //RenderLayer Comes Later
                                    } else {
                                        quad = emitter.fromVanilla(vanillaQuad, RendererAccess.INSTANCE.getRenderer().materialFinder().blendMode(0, BlendMode.fromRenderLayer(RenderLayers.getBlockLayer(state))).find(), d);
                                    }
                                    transform.transform(quad, d);
                                    if (canvas) {
                                        Sprite sprite = SpriteFinder.get(MinecraftClient.getInstance().getBakedModelManager().method_24153(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)).find(quad, 0);
                                        RenderMaterial material = (RenderMaterial) MaterialMap.get(state).getMapped(sprite);
                                        if (material != null) {
                                            quad.material(((Renderer) RendererAccess.INSTANCE.getRenderer()).materialFinder().copyFrom(material).blendMode(BlendMode.fromRenderLayer(RenderLayers.getBlockLayer(state))).find());
                                        } else {
                                            quad.material(((Renderer) RendererAccess.INSTANCE.getRenderer()).materialFinder().blendMode(BlendMode.fromRenderLayer(RenderLayers.getBlockLayer(states[i][j][k]))).find());
                                        }
                                    }
                                    if (vanillaQuad.hasColor()) {
                                        int color = 0xFF000000 | ColorProviderRegistry.BLOCK.get(state.getBlock()).getColor(state, world, pos, vanillaQuad.getColorIndex());
                                        quad.spriteColor(0, color, color, color, color);
                                    }
                                    quad.cullFace(null);
                                    emitter = emitter.emit();
                                }
                            }
                        }
                    } catch (Exception e) {
                        BitsAndChisels.LOGGER.error("Error Building Bit Mesh");
                        BitsAndChisels.LOGGER.error(e);
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
