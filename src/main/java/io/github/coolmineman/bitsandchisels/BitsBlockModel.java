package io.github.coolmineman.bitsandchisels;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Pair;

import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext.QuadTransform;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

public class BitsBlockModel implements UnbakedModel, BakedModel, FabricBakedModel {

    // *Important Stuff

    // public static class XTransform implements QuadTransform {
    //     int x;

    //     public XTransform(int x) {
    //         this.x = x;
    //     }

    //     @Override
    //     public boolean transform(MutableQuadView quad) {
    //         Vector3f[] vectors = {new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f()};

    //         for (int i = 0; i < 4; i++) {
    //             quad.copyPos(i, vectors[i]);
    //         }

    //         boolean north_side = Arrays.stream(vectors).anyMatch(v -> v.getX() == 0 && v.getY() == 0 && v.getZ() == 0) && Arrays.stream(vectors).anyMatch(v -> v.getX() == 1 && v.getY() == 1);
    //         boolean south_side = Arrays.stream(vectors).anyMatch(v -> v.getX() == 0 && v.getY() == 0 && v.getZ() == 1) && Arrays.stream(vectors).anyMatch(v -> v.getX() == 1 && v.getY() == 1);

    //         for (int i = 0; i < 4; i++) {
    //             Vector3f tmp = vectors[i];
    //             if (tmp.getX() == 1.0f) {
    //                 tmp.set( (((float)x) + 1f)/16f, tmp.getY(), tmp.getZ() );
    //             }
    //             quad.pos(i, tmp);
    //         }

    //         if (north_side) {
    //             float u_scale = (quad.spriteU(2, 0) - quad.spriteU(0, 0)) * (1f/16f);
    //             float v_scale = (quad.spriteV(2, 0) - quad.spriteV(0, 0)) * (1f/16f);
    //             float bottom_u = quad.spriteU(0, 0);
    //             float bottom_v = quad.spriteV(0, 0);

    //             float min_u = bottom_u + (x * u_scale);
    //             float min_v = bottom_v + (x * v_scale);
    //             float max_u = min_u + u_scale;
    //             float max_v = min_v + v_scale;

    //             quad.sprite(0, 0, min_u, min_v);
    //             quad.sprite(1, 0, max_u, min_v);
    //             quad.sprite(2, 0, max_u, max_v);
    //             quad.sprite(3, 0, min_u, max_v);
    //         }

    //         if (south_side) {
    //             float u_scale = (quad.spriteU(2, 0) - quad.spriteU(0, 0)) * (1f/16f);
    //             float v_scale = (quad.spriteV(2, 0) - quad.spriteV(0, 0)) * (1f/16f);
    //             float top_u = quad.spriteU(2, 0);
    //             float bottom_v = quad.spriteV(0, 0);

    //             float min_u = top_u - (x * u_scale);
    //             float min_v = bottom_v + (x * v_scale);
    //             float max_u = min_u - 2 * u_scale;
    //             float max_v = min_v + v_scale;

    //             quad.sprite(0, 0, min_u, min_v);
    //             quad.sprite(1, 0, max_u, min_v);
    //             quad.sprite(2, 0, max_u, max_v);
    //             quad.sprite(3, 0, min_u, max_v);
    //         }

    //         return true;
    //     }
        
    // }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        BitsBlockEntity e = (BitsBlockEntity) blockView.getBlockEntity(pos);
        if (e != null) {
            context.pushTransform(new BitTransform());
                context.fallbackConsumer().accept(MinecraftClient.getInstance().getBlockRenderManager().getModel(e.getState(0, 0, 0)));
            context.popTransform();
        }
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        //todo
    }

    //*Stubs

    @Override
    public Collection<Identifier> getModelDependencies() {
        return Collections.emptySet();
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter,
            Set<Pair<String, String>> unresolvedTextureReferences) {
        return Collections.emptySet();
    }

    @Override
    public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter,
            ModelBakeSettings rotationContainer, Identifier modelId) {
        return this;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction face, Random random) {
        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean hasDepth() {
        return false;
    }

    @Override
    public boolean isSideLit() {
        return false;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public Sprite getSprite() {
        return MinecraftClient.getInstance().getBlockRenderManager().getModel(Blocks.AIR.getDefaultState()).getSprite();
    }

    @Override
    public ModelTransformation getTransformation() {
        return null;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return null;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }
}
