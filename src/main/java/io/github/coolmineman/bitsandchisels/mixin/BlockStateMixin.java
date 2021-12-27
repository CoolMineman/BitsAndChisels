package io.github.coolmineman.bitsandchisels.mixin;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import io.github.coolmineman.bitsandchisels.BitsAndChisels;
import io.github.coolmineman.bitsandchisels.CanvasHelper;
import io.github.coolmineman.bitsandchisels.duck.CubeRenderStuff;
import java.util.Random;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

@Mixin(BlockState.class)
public class BlockStateMixin implements CubeRenderStuff {
    @Unique
    BakedModel oldModel = null;
    @Unique
    BakedQuad[][] quads = null;
    @Unique
    RenderMaterial[][] materials = null;

    @Unique
    private void recomputeIfNeeded() {
        BlockState thiz = (BlockState)(Object)this;
        BlockRenderManager renderman = MinecraftClient.getInstance().getBlockRenderManager();
        BakedModel newModel = renderman.getModel(thiz);
        if (oldModel == null || oldModel != newModel) {
            oldModel = newModel;
            quads = new BakedQuad[7][];
            materials = new RenderMaterial[7][];
            for (Direction d : Direction.values()) {
                BakedModelManager modelman = MinecraftClient.getInstance().getBakedModelManager();
                List<BakedQuad> dQuads = newModel.getQuads(thiz, d, new Random(0));
                if (dQuads == null || dQuads.isEmpty()) dQuads = modelman.getMissingModel().getQuads(thiz, d, ThreadLocalRandom.current());
                BakedQuad[] localquads = dQuads.toArray(new BakedQuad[0]);
                quads[d.getId()] = localquads;
                materials[d.getId()] = new RenderMaterial[localquads.length];
                for (int i = 0; i < localquads.length; i++) {
                    BakedQuad quad = localquads[i];
                    if (BitsAndChisels.CANVAS) {
                        Sprite sprite = ((BakedQuadAccessor)quad).getSprite();
                        materials[d.getId()][i] = CanvasHelper.getMaterial(thiz, sprite);
                    } else {
                        materials[d.getId()][i] = RendererAccess.INSTANCE.getRenderer().materialFinder().blendMode(0, BlendMode.fromRenderLayer(RenderLayers.getBlockLayer(thiz))).find();
                    }
                }
            }
        }
    }

	@Override
	public BakedQuad[] getQuads(int direction) {
        recomputeIfNeeded();
		return quads[direction];
	}

	@Override
	public RenderMaterial[] getMaterials(int direction) {
        recomputeIfNeeded();
		return materials[direction];
	}
    
}
