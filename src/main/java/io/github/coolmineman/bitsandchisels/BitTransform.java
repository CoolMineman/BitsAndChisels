package io.github.coolmineman.bitsandchisels;

import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.model.SpriteFinder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

public class BitTransform {
    private static float ONE_PIXEL = 1f/16f; 

    private BitTransform() { }

    public static void transform(MutableQuadView quad, Direction direction, int minx, int miny, int minz, int maxx, int maxy, int maxz, Vec3f tmp) {
        Sprite sprite = SpriteFinder.get(MinecraftClient.getInstance().getBakedModelManager().getAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)).find(quad, 0);
        transformer(minx * ONE_PIXEL, (maxx + 1) * ONE_PIXEL, miny * ONE_PIXEL, (maxy + 1) * ONE_PIXEL, minz * ONE_PIXEL, (maxz + 1) * ONE_PIXEL, quad, tmp);
        int bake_flags = MutableQuadView.BAKE_LOCK_UV;
        if (direction == Direction.UP) bake_flags = bake_flags | MutableQuadView.BAKE_FLIP_V;
        quad.spriteBake(0, sprite, bake_flags);
    }

    private static void transformer(float minx, float maxx, float miny, float maxy, float minz, float maxz, MutableQuadView quad, Vec3f tmp) {
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
