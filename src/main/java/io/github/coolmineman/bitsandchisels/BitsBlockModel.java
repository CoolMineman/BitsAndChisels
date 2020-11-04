package io.github.coolmineman.bitsandchisels;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Pair;

import grondag.frex.api.material.MaterialMap;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.SpriteFinder;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

public class BitsBlockModel implements UnbakedModel, BakedModel, FabricBakedModel {

    private static final Identifier DEFAULT_BLOCK_MODEL = new Identifier("minecraft:block/block");
 
    private ModelTransformation transformation;

    // *Important Stuff

    private static final BitTransform transform = new BitTransform();
    private static BitsBlockEntity e = new BitsBlockEntity();
    private static HashMap<CompoundTag, BitsBlockEntity> entity_cache = new HashMap<>();

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        e = (BitsBlockEntity) blockView.getBlockEntity(pos);
        boolean canvas = RendererAccess.INSTANCE.getRenderer().getClass().getName().equals("grondag.canvas.apiimpl.Canvas");
        if (e != null) {
            context.pushTransform(transform);
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    for (int k = 0; k < 16; k++) {
                        transform.x = i;
                        transform.y = j;
                        transform.z = k;

                        if (canvas) {
                            final int i2 = i;
                            final int j2 = j;
                            final int k2 = k;

                            context.pushTransform(quad -> {
                                Sprite sprite = SpriteFinder.get(MinecraftClient.getInstance().getBakedModelManager().method_24153(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)).find(quad, 0);
                                quad.material(MaterialMap.get(e.getState(i2, j2, k2)).getMapped(sprite));
                                return true;
                            });
                        }
                        
                        if (!e.getState(i, j, k).isAir()) ((FabricBakedModel) MinecraftClient.getInstance().getBlockRenderManager().getModel(e.getState(i, j, k))).emitBlockQuads(blockView, state, pos, randomSupplier, context);

                        if (canvas) context.popTransform();
                    }
                }
            }
            context.popTransform();
        }
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        e = entity_cache.computeIfAbsent(stack.getTag(), discard -> {
            System.out.println("Cache Failed");
            BitsBlockEntity result = new BitsBlockEntity();
            CompoundTag tag = stack.getSubTag("BlockEntityTag");
            if (tag != null) {
                result.fromTag(null, tag);
            }
            return result;
        });

        boolean canvas = RendererAccess.INSTANCE.getRenderer().getClass().getName().equals("grondag.canvas.apiimpl.Canvas");
        
        if (e != null) {
            context.pushTransform(transform);
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    for (int k = 0; k < 16; k++) {
                        transform.x = i;
                        transform.y = j;
                        transform.z = k;

                        if (canvas) {
                            final int i2 = i;
                            final int j2 = j;
                            final int k2 = k;

                            context.pushTransform(quad -> {
                                Sprite sprite = SpriteFinder.get(MinecraftClient.getInstance().getBakedModelManager().method_24153(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)).find(quad, 0);
                                quad.material(MaterialMap.get(e.getState(i2, j2, k2)).getMapped(sprite));
                                return true;
                            });
                        }
                        
                        if (!e.getState(i, j, k).isAir()) ((FabricBakedModel) MinecraftClient.getInstance().getBlockRenderManager().getModel(e.getState(i, j, k))).emitItemQuads(stack, randomSupplier, context);

                        if (canvas) context.popTransform();
                    }
                }
            }
            context.popTransform();
        }
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
        // Load the default block model
        JsonUnbakedModel defaultBlockModel = (JsonUnbakedModel) loader.getOrLoadModel(DEFAULT_BLOCK_MODEL);
        // Get its ModelTransformation
        transformation = defaultBlockModel.getTransformations();
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
        return transformation;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }
}
