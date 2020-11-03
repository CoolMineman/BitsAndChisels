package io.github.coolmineman.bitsandchisels;

import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.model.SpriteFinder;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext.QuadTransform;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.Vector3f;

public class BitTransform implements QuadTransform {
    private static float ONE_PIXEL = 1f/16f; 

    protected int x;
    protected int y;
    protected int z;

    @Override
    public boolean transform(MutableQuadView quad) {
        transformX(x * ONE_PIXEL, (x + 1) * ONE_PIXEL, quad);
        return true;
    }

    private void transformX(float minx, float maxx, MutableQuadView quad) {
        Vector3f tmp = new Vector3f();

        Sprite sprite = SpriteFinder.get(MinecraftClient.getInstance().getBakedModelManager().method_24153(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)).find(quad, 0);

        for (int i = 0; i < 4; i++) {
            quad.copyPos(i, tmp);

            if (tmp.getX() == 0) {
                tmp.set(minx, tmp.getY(), tmp.getZ());
            }

            if (tmp.getX() == 1) {
                tmp.set(maxx, tmp.getY(), tmp.getZ());
            }
            quad.pos(i, tmp);
            quad.spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV);
        }
    }
    
}
