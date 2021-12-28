package io.github.coolmineman.bitsandchisels;

import io.vram.frex.api.material.MaterialFinder;
import io.vram.frex.api.material.MaterialMap;
import io.vram.frex.api.renderer.Renderer;
import io.vram.frex.api.rendertype.RenderTypeUtil;
import io.vram.frex.fabric.compat.FabricMaterial;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.texture.Sprite;

public class CanvasHelper {
    private CanvasHelper() { }

    public static RenderMaterial getMaterial(BlockState state, Sprite sprite) {
        io.vram.frex.api.material.RenderMaterial material = MaterialMap.get(state).getMapped(sprite);
        if (material != null) {
            MaterialFinder finder = Renderer.get().materials().materialFinder().copyFrom(material);
            RenderTypeUtil.toMaterialFinder(finder, RenderLayers.getBlockLayer(state));
            return FabricMaterial.of(finder.find());
        } else {
            return FabricMaterial.of(RenderTypeUtil.toMaterial(RenderLayers.getBlockLayer(state)));
        }
    }
}
