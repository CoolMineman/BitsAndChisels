package io.github.coolmineman.bitsandchisels;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Pair;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext.QuadTransform;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.BlockRenderView;

public class BitItemModel implements UnbakedModel, BakedModel, FabricBakedModel {

    //Copied from fapi

    private static Transformation makeTransform(float rotationX, float rotationY, float rotationZ, float translationX, float translationY, float translationZ, float scaleX, float scaleY, float scaleZ) {
		Vec3f translation = new Vec3f(translationX, translationY, translationZ);
		translation.scale(0.0625f);
		translation.clamp(-5.0F, 5.0F);
		return new Transformation(new Vec3f(rotationX, rotationY, rotationZ), translation, new Vec3f(scaleX, scaleY, scaleZ));
	}

    public static final Transformation TRANSFORM_BLOCK_GUI = makeTransform(30, 225, 0, 0, 0, 0, 0.625f * 0.5f, 0.625f * 0.5f, 0.625f * 0.5f);
	public static final Transformation TRANSFORM_BLOCK_GROUND = makeTransform(0, 0, 0, 0, 3, 0, 0.25f * 0.5f, 0.25f * 0.5f, 0.25f * 0.5f);
	public static final Transformation TRANSFORM_BLOCK_FIXED = makeTransform(0, 0, 0, 0, 0, 0, 0.5f * 0.5f, 0.5f * 0.5f, 0.5f * 0.5f);
	public static final Transformation TRANSFORM_BLOCK_3RD_PERSON_RIGHT = makeTransform(75, 45, 0, 0, 2.5f, 0, 0.375f * 0.5f, 0.375f * 0.5f, 0.375f * 0.5f);
	public static final Transformation TRANSFORM_BLOCK_1ST_PERSON_RIGHT = makeTransform(0, 45, 0, 0, 0, 0, 0.4f * 0.5f, 0.4f * 0.5f, 0.4f * 0.5f);
    public static final Transformation TRANSFORM_BLOCK_1ST_PERSON_LEFT = makeTransform(0, 225, 0, 0, 0, 0, 0.4f * 0.5f, 0.4f * 0.5f, 0.4f * 0.5f);
    
    private static BlockState transform_state;
    private static final QuadTransform TRANSFORM = quad -> {
        BlockColorProvider colorthing = ColorProviderRegistry.BLOCK.get(transform_state.getBlock());
        if (colorthing != null) {
            int color = 0xFF000000 | colorthing.getColor(transform_state, null, null, quad.colorIndex());
            quad.spriteColor(0, color, color, color, color);
        }
        return true;
    };

	/**
	 * Mimics the vanilla model transformation * 0.5 used for most vanilla blocks,
	 */
    public static final ModelTransformation MODEL_TRANSFORM_BLOCK = new ModelTransformation(TRANSFORM_BLOCK_3RD_PERSON_RIGHT, TRANSFORM_BLOCK_3RD_PERSON_RIGHT, TRANSFORM_BLOCK_1ST_PERSON_LEFT, TRANSFORM_BLOCK_1ST_PERSON_RIGHT, Transformation.IDENTITY, TRANSFORM_BLOCK_GUI, TRANSFORM_BLOCK_GROUND, TRANSFORM_BLOCK_FIXED);
    
    private static LRUCache<CompoundTag, BlockState> cache = new LRUCache<>(200);

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos,
            Supplier<Random> randomSupplier, RenderContext context) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        if (!stack.hasTag() || stack.getSubTag("bit") == null) return;
        transform_state = cache.computeIfAbsent(stack.getTag(), tag -> NbtHelper.toBlockState(stack.getSubTag("bit")));
        context.pushTransform(TRANSFORM);
        context.fallbackConsumer().accept(MinecraftClient.getInstance().getBlockRenderManager().getModel(transform_state));
        context.popTransform();
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction face, Random random) {
        BitsAndChisels.LOGGER.warn("BakedModel.getQuads was just called, this likely means your renderer doesn't support Fabric rendering API. In that case get one that does, I recommend canvas or indigo.");
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
        return true;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public Sprite getSprite() {
        return null;
    }

    @Override
    public ModelTransformation getTransformation() {
        return MODEL_TRANSFORM_BLOCK;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        return Collections.emptyList();
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter,
            Set<Pair<String, String>> unresolvedTextureReferences) {
        return Collections.emptyList();
    }

    @Override
    public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter,
            ModelBakeSettings rotationContainer, Identifier modelId) {
        return this;
    }
    
}
