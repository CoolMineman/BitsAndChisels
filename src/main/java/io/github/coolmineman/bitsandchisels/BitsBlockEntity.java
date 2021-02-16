package io.github.coolmineman.bitsandchisels;

import java.util.Arrays;

import org.jetbrains.annotations.Nullable;

import io.github.coolmineman.bitsandchisels.duck.CubeRenderStuff;
import io.github.coolmineman.bitsandchisels.mixin.SimpleVoxelShapeFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class BitsBlockEntity extends BlockEntity implements BlockEntityClientSerializable, RenderAttachmentBlockEntity {
    private static final Direction[] X_DIRECTIONS = {Direction.EAST, Direction.WEST};
    private static final Direction[] Y_DIRECTIONS = {Direction.UP, Direction.DOWN};
    private static final Direction[] Z_DIRECTIONS = {Direction.SOUTH, Direction.NORTH};
    

    private BlockState[][][] states;
    @Environment(EnvType.CLIENT)
    protected Mesh mesh;
    protected VoxelShape shape = VoxelShapes.fullCube();
    boolean fullcube = false;

    public BitsBlockEntity() {
        this(Blocks.AIR.getDefaultState());
    }

    public BitsBlockEntity(BlockState fillState) {
        this(new BlockState[16][16][16]);
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 16; k++) {
                    states[i][j][k] = fillState;
                }
            }
        }
    }

    public BitsBlockEntity(BlockState[][][] states) {
        super(BitsAndChisels.BITS_BLOCK_ENTITY);
        this.states = states;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        BitNbtUtil.write3DBitArray(tag, states);
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        BitNbtUtil.read3DBitArray(tag, states);
        rebuildShape();
    }

    public void setState(int x, int y, int z, BlockState state) {
        states[x][y][z] = state;
    }

    /**
     * Used to replace the backing array, don't call this w/o a good reason.
     */
    public void setStates(BlockState[][][] states) {
        this.states = states;
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

    public BlockState[][][] getStates() {
        return states;
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

    public boolean canCull(Direction d, int x, int y, int z) {
        switch (d) {
            case UP:
                return y == 15;
            case DOWN:
                return y == 0;
            case SOUTH:
                return z == 15;
            case NORTH:
                return z == 0;
            case EAST:
                return x == 15;
            case WEST:
                return x == 0;
        }
        return false;
    }

    @Environment(EnvType.CLIENT)
    private void doQuad(QuadEmitter emitter, Vector3f tmp, Direction d, BlockState state, int minx, int miny, int minz, int maxx, int maxy, int maxz) {
        CubeRenderStuff cubeRenderStuff = CubeRenderStuff.of(state);
        for (int z = 0; z < cubeRenderStuff.getQuads(d.getId()).length; z++) {
            BakedQuad vanillaQuad = cubeRenderStuff.getQuads(d.getId())[z];
            MutableQuadView quad = emitter.fromVanilla(vanillaQuad, cubeRenderStuff.getMaterials(d.getId())[z], d);
            BitTransform.transform(quad, d, minx, miny, minz, maxx, maxy, maxz, tmp);
            if (vanillaQuad.hasColor()) {
                int color = 0xFF000000 | ColorProviderRegistry.BLOCK.get(state.getBlock()).getColor(state, world, pos, vanillaQuad.getColorIndex());
                quad.spriteColor(0, color, color, color, color);
            }
            if (!canCull(d, minx, miny, minz)) quad.cullFace(null);
            emitter = emitter.emit();
        }
    }

    private static void clear(boolean[][] a) {
        for(int i = 0; i < a.length; i++) {
            Arrays.fill(a[i], false);
        }
    }

    @Environment(EnvType.CLIENT)
    protected void rebuildMesh() {
        MeshBuilder builder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
        QuadEmitter emitter = builder.getEmitter();
        Vector3f tmp = new Vector3f();
        boolean[][] used = new boolean[16][16];

        //X
        for (Direction d : X_DIRECTIONS) {
            for (int cx = 0; cx < 16; cx++) {
                clear(used);
                for (int cy = 0; cy < 16; cy++) {
                    for (int cz = 0; cz < 16; cz++) {
                        BlockState state = states[cx][cy][cz];
                        if (state.isAir() || used[cy][cz] || !quadNeeded(d, cx, cy, cz)) continue;
                        int cy2 = cy;
                        int cz2 = cz;
                        //Greed Y
                        for (int ty = cy; ty < 16; ty++) {
                            if (states[cx][ty][cz] == state && !used[ty][cz] && quadNeeded(d, cx, ty, cz)) {
                                cy2 = ty;
                            } else {
                                break;
                            }
                        }
                        // Greed Z
                        greedz: for (int tz = cz; tz < 16; tz++) {
                            for (int ty = cy; ty <= cy2; ty++) {
                                if (states[cx][ty][tz] != state || used[ty][tz] || !quadNeeded(d, cx, ty, tz)) {
                                    break greedz;
                                }
                            }
                            cz2 = tz;
                        }
                        for (int qy = cy; qy <= cy2; qy++) {
                            for (int qz = cz; qz <= cz2; qz++) {
                                used[qy][qz] = true;
                            }
                        }

                        doQuad(emitter, tmp, d, state, cx, cy, cz, cx, cy2, cz2);
                    }
                }
            }
        }

        //Y
        for (Direction d : Y_DIRECTIONS) {
            for (int cy = 0; cy < 16; cy++) {
                clear(used);
                for (int cx = 0; cx < 16; cx++) {
                    for (int cz = 0; cz < 16; cz++) {
                        BlockState state = states[cx][cy][cz];
                        if (state.isAir() || used[cx][cz] || !quadNeeded(d, cx, cy, cz)) continue;
                        int cx2 = cx;
                        int cz2 = cz;
                        //Greed X
                        for (int tx = cx; tx < 16; tx++) {
                            if (states[tx][cy][cz] == state && !used[tx][cz] && quadNeeded(d, tx, cy, cz)) {
                                cx2 = tx;
                            } else {
                                break;
                            }
                        }
                        // Greed Z
                        greedz: for (int tz = cz; tz < 16; tz++) {
                            for (int tx = cx; tx <= cx2; tx++) {
                                if (states[tx][cy][tz] != state || used[tx][tz] || !quadNeeded(d, tx, cy, tz)) {
                                    break greedz;
                                }
                            }
                            cz2 = tz;
                        }
                        for (int qx = cx; qx <= cx2; qx++) {
                            for (int qz = cz; qz <= cz2; qz++) {
                                used[qx][qz] = true;
                            }
                        }

                        doQuad(emitter, tmp, d, state, cx, cy, cz, cx2, cy, cz2);
                    }
                }
            }
        }

        //Z
        for (Direction d : Z_DIRECTIONS) {
            for (int cz = 0; cz < 16; cz++) {
                clear(used);
                for (int cx = 0; cx < 16; cx++) {
                    for (int cy = 0; cy < 16; cy++) {
                        BlockState state = states[cx][cy][cz];
                        if (state.isAir() || used[cx][cy] || !quadNeeded(d, cx, cy, cz)) continue;
                        int cx2 = cx;
                        int cy2 = cy;
                        //Greed X
                        for (int tx = cx; tx < 16; tx++) {
                            if (states[tx][cy][cz] == state && !used[tx][cz] && quadNeeded(d, tx, cy, cz)) {
                                cx2 = tx;
                            } else {
                                break;
                            }
                        }
                        // Greed Y
                        greedy: for (int ty = cy; ty < 16; ty++) {
                            for (int tx = cx; tx <= cx2; tx++) {
                                if (states[tx][ty][cz] != state || used[tx][ty] || !quadNeeded(d, tx, ty, cz)) {
                                    break greedy;
                                }
                            }
                            cy2 = ty;
                        }
                        for (int qx = cx; qx <= cx2; qx++) {
                            for (int qy = cy; qy <= cy2; qy++) {
                                used[qx][qy] = true;
                            }
                        }

                        doQuad(emitter, tmp, d, state, cx, cy, cz, cx2, cy2, cz);
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

    @Environment(EnvType.CLIENT)
	@Override
	public @Nullable Object getRenderAttachmentData() {
		return mesh;
	}

}
