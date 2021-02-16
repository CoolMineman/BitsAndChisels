package io.github.coolmineman.bitsandchisels;

import grondag.frex.api.Renderer;
import grondag.frex.api.material.MaterialMap;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.texture.Sprite;

public class CanvasHelper {
    private CanvasHelper() { }

    public static final RenderMaterial getMaterial(BlockState state, Sprite sprite) {
        grondag.frex.api.material.RenderMaterial material = (grondag.frex.api.material.RenderMaterial) MaterialMap.get(state).getMapped(sprite);
        if (material != null) {
            return ((Renderer) RendererAccess.INSTANCE.getRenderer()).materialFinder().copyFrom(material).blendMode(BlendMode.fromRenderLayer(RenderLayers.getBlockLayer(state))).find();
        } else {
            return ((Renderer) RendererAccess.INSTANCE.getRenderer()).materialFinder().blendMode(BlendMode.fromRenderLayer(RenderLayers.getBlockLayer(state))).find();
        }
    }
}
