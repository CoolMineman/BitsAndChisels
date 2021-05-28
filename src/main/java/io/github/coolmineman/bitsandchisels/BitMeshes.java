package io.github.coolmineman.bitsandchisels;

import java.util.Arrays;

import org.jetbrains.annotations.Nullable;

import io.github.coolmineman.bitsandchisels.duck.CubeRenderStuff;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.SpriteFinder;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

public class BitMeshes {
    private static final Direction[] X_DIRECTIONS = {Direction.EAST, Direction.WEST};
    private static final Direction[] Y_DIRECTIONS = {Direction.UP, Direction.DOWN};
    private static final Direction[] Z_DIRECTIONS = {Direction.SOUTH, Direction.NORTH};
    private static final float ONE_PIXEL = 1f/16f; 

    private BitMeshes() { }

    public static Mesh createMesh(BlockState[][][] states, @Nullable World world, @Nullable BlockPos pos) {
        MeshBuilder builder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
        QuadEmitter emitter = builder.getEmitter();
        Vec3f tmp = new Vec3f();
        boolean[][] used = new boolean[16][16];

        //X
        for (Direction d : X_DIRECTIONS) {
            for (int cx = 0; cx < 16; cx++) {
                clear(used);
                for (int cy = 0; cy < 16; cy++) {
                    for (int cz = 0; cz < 16; cz++) {
                        BlockState state = states[cx][cy][cz];
                        if (state.isAir() || used[cy][cz] || !quadNeeded(states, d, cx, cy, cz)) continue;
                        int cy2 = cy;
                        int cz2 = cz;
                        //Greed Y
                        for (int ty = cy; ty < 16; ty++) {
                            if (states[cx][ty][cz] == state && !used[ty][cz] && quadNeeded(states, d, cx, ty, cz)) {
                                cy2 = ty;
                            } else {
                                break;
                            }
                        }
                        // Greed Z
                        greedz: for (int tz = cz; tz < 16; tz++) {
                            for (int ty = cy; ty <= cy2; ty++) {
                                if (states[cx][ty][tz] != state || used[ty][tz] || !quadNeeded(states, d, cx, ty, tz)) {
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

                        doQuad(world, pos, emitter, tmp, d, state, cx, cy, cz, cx, cy2, cz2);
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
                        if (state.isAir() || used[cx][cz] || !quadNeeded(states, d, cx, cy, cz)) continue;
                        int cx2 = cx;
                        int cz2 = cz;
                        //Greed X
                        for (int tx = cx; tx < 16; tx++) {
                            if (states[tx][cy][cz] == state && !used[tx][cz] && quadNeeded(states, d, tx, cy, cz)) {
                                cx2 = tx;
                            } else {
                                break;
                            }
                        }
                        // Greed Z
                        greedz: for (int tz = cz; tz < 16; tz++) {
                            for (int tx = cx; tx <= cx2; tx++) {
                                if (states[tx][cy][tz] != state || used[tx][tz] || !quadNeeded(states, d, tx, cy, tz)) {
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

                        doQuad(world, pos, emitter, tmp, d, state, cx, cy, cz, cx2, cy, cz2);
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
                        if (state.isAir() || used[cx][cy] || !quadNeeded(states, d, cx, cy, cz)) continue;
                        int cx2 = cx;
                        int cy2 = cy;
                        //Greed X
                        for (int tx = cx; tx < 16; tx++) {
                            if (states[tx][cy][cz] == state && !used[tx][cz] && quadNeeded(states, d, tx, cy, cz)) {
                                cx2 = tx;
                            } else {
                                break;
                            }
                        }
                        // Greed Y
                        greedy: for (int ty = cy; ty < 16; ty++) {
                            for (int tx = cx; tx <= cx2; tx++) {
                                if (states[tx][ty][cz] != state || used[tx][ty] || !quadNeeded(states, d, tx, ty, cz)) {
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

                        doQuad(world, pos, emitter, tmp, d, state, cx, cy, cz, cx2, cy2, cz);
                    }
                }
            }
        }
        return builder.build();
    }

    private static void doQuad(@Nullable World world, @Nullable BlockPos pos, QuadEmitter emitter, Vec3f tmp, Direction d, BlockState state, int minx, int miny, int minz, int maxx, int maxy, int maxz) {
        CubeRenderStuff cubeRenderStuff = CubeRenderStuff.of(state);
        for (int z = 0; z < cubeRenderStuff.getQuads(d.getId()).length; z++) {
            BakedQuad vanillaQuad = cubeRenderStuff.getQuads(d.getId())[z];
            MutableQuadView quad = emitter.fromVanilla(vanillaQuad, cubeRenderStuff.getMaterials(d.getId())[z], d);
            BitMeshes.transform(quad, d, minx, miny, minz, maxx, maxy, maxz, tmp);
            if (vanillaQuad.hasColor()) {
                int color = 0xFF000000 | MinecraftClient.getInstance().getBlockColors().getColor(state, world, pos, vanillaQuad.getColorIndex());
                quad.spriteColor(0, color, color, color, color);
            }
            if (!canCull(d, minx, miny, minz)) quad.cullFace(null);
            emitter = emitter.emit();
        }
    }

    private static boolean quadNeeded(BlockState[][][] states, Direction d, int x, int y, int z) {
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

    private static void clear(boolean[][] a) {
        for(int i = 0; i < a.length; i++) {
            Arrays.fill(a[i], false);
        }
    }

    private static boolean canCull(Direction d, int x, int y, int z) {
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

    private static void transform(MutableQuadView quad, Direction direction, int minx, int miny, int minz, int maxx, int maxy, int maxz, Vec3f tmp) {
        Sprite sprite = SpriteFinder.get(MinecraftClient.getInstance().getBakedModelManager().getAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)).find(quad, 0);
        transform0(minx * ONE_PIXEL, (maxx + 1) * ONE_PIXEL, miny * ONE_PIXEL, (maxy + 1) * ONE_PIXEL, minz * ONE_PIXEL, (maxz + 1) * ONE_PIXEL, quad, tmp);
        int bake_flags = MutableQuadView.BAKE_LOCK_UV;
        if (direction == Direction.UP) bake_flags = bake_flags | MutableQuadView.BAKE_FLIP_V;
        quad.spriteBake(0, sprite, bake_flags);
    }

    private static void transform0(float minx, float maxx, float miny, float maxy, float minz, float maxz, MutableQuadView quad, Vec3f tmp) {
        for (int i = 0; i < 4; i++) {
            quad.copyPos(i, tmp);
            float _x = tmp.getX();
            float _y = tmp.getY();
            float _z = tmp.getZ();

            if (approxEqual(_x, 0)) {
                _x = minx;
            }

            if (approxEqual(_x, 1)) {
                _x = maxx;
            }

            if (approxEqual(_y, 0)) {
                _y = miny;
            }

            if (approxEqual(_y, 1)) {
                _y = maxy;
            }

            if (approxEqual(_z, 0)) {
                _z = minz;
            }

            if (approxEqual(_z, 1)) {
                _z = maxz;
            }

            tmp.set(_x, _y, _z);

            quad.pos(i, tmp);
        }
    }

    private static boolean approxEqual(float actualValue, float desiredValue) {
        float diff = Math.abs(desiredValue - actualValue);
        return diff < 0.01f;
    }
    
}
