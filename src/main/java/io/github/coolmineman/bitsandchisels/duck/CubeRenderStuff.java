package io.github.coolmineman.bitsandchisels.duck;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedQuad;

public interface CubeRenderStuff {
    BakedQuad[] getQuads(int direction);

    RenderMaterial[] getMaterials(int direction);

    public static CubeRenderStuff of(BlockState state) {
        return (CubeRenderStuff)state;
    }
}
