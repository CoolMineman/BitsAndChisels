package io.github.coolmineman.bitsandchisels.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.coolmineman.bitsandchisels.api.client.RedBoxCallback;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.shape.VoxelShape;

@Mixin(WorldRenderer.class)
public class RedSelectionBoxMixin implements RedBoxCallback.IRedBoxDrawer {

    @Shadow
    private static void drawShapeOutline(MatrixStack matrixStack, VertexConsumer vertexConsumer, VoxelShape voxelShape, double d, double e, double f, float g, float h, float i, float j) {
        throw new RuntimeException("mixin failed");
    }

    @Inject(at = @At("HEAD"), method = "drawBlockOutline")
    private void drawBlockOutlineAddon(MatrixStack matrixStack, VertexConsumer vertexConsumer, Entity entity, double d, double e, double f, BlockPos blockPos, BlockState blockState, CallbackInfo bruh) {
        RedBoxCallback.EVENT.invoker().drawBoxes(this, matrixStack, vertexConsumer, d, e, f);
    }

    public void drawRedBox(MatrixStack matrixStack, VertexConsumer vertexConsumer, BlockPos block, int bitx, int bity, int bitz, int bitx2, int bity2, int bitz2, double worldoffsetx, double worldoffsety, double worldoffsetz) {
        float r = 1f;
        float g = 0f;
        float b = 0f;
        float a = 0.4f;

        double xoffset = block.getX() - worldoffsetx;
        double yoffset = block.getY() - worldoffsety;
        double zoffset = block.getZ() - worldoffsetz;
        
        Matrix4f matrix4f = matrixStack.peek().getModel();

        double x = ((double)bitx) * 0.0625d;
        double y = ((double)bity) * 0.0625d;
        double z = ((double)bitz) * 0.0625d;

        double x1 = ((double)bitx2) * 0.0625d;
        double y1 = ((double)bity2) * 0.0625d;
        double z1 = ((double)bitz2) * 0.0625d;

        //*
        vertexConsumer.vertex(matrix4f, (float)(x + xoffset), (float)(y + yoffset), (float)(z + zoffset)).color(r, g, b, a).next();
        vertexConsumer.vertex(matrix4f, (float)(x1 + xoffset), (float)(y + yoffset), (float)(z + zoffset)).color(r, g, b, a).next();

        vertexConsumer.vertex(matrix4f, (float)(x + xoffset), (float)(y1 + yoffset), (float)(z + zoffset)).color(r, g, b, a).next();
        vertexConsumer.vertex(matrix4f, (float)(x1 + xoffset), (float)(y1 + yoffset), (float)(z + zoffset)).color(r, g, b, a).next();

        vertexConsumer.vertex(matrix4f, (float)(x + xoffset), (float)(y + yoffset), (float)(z1 + zoffset)).color(r, g, b, a).next();
        vertexConsumer.vertex(matrix4f, (float)(x1 + xoffset), (float)(y + yoffset), (float)(z1 + zoffset)).color(r, g, b, a).next();

        vertexConsumer.vertex(matrix4f, (float)(x + xoffset), (float)(y1 + yoffset), (float)(z1 + zoffset)).color(r, g, b, a).next();
        vertexConsumer.vertex(matrix4f, (float)(x1 + xoffset), (float)(y1 + yoffset), (float)(z1 + zoffset)).color(r, g, b, a).next();
        //*
        vertexConsumer.vertex(matrix4f, (float)(x + xoffset), (float)(y + yoffset), (float)(z + zoffset)).color(r, g, b, a).next();
        vertexConsumer.vertex(matrix4f, (float)(x + xoffset), (float)(y1 + yoffset), (float)(z + zoffset)).color(r, g, b, a).next();

        vertexConsumer.vertex(matrix4f, (float)(x1 + xoffset), (float)(y + yoffset), (float)(z + zoffset)).color(r, g, b, a).next();
        vertexConsumer.vertex(matrix4f, (float)(x1 + xoffset), (float)(y1 + yoffset), (float)(z + zoffset)).color(r, g, b, a).next();

        vertexConsumer.vertex(matrix4f, (float)(x + xoffset), (float)(y + yoffset), (float)(z1 + zoffset)).color(r, g, b, a).next();
        vertexConsumer.vertex(matrix4f, (float)(x + xoffset), (float)(y1 + yoffset), (float)(z1 + zoffset)).color(r, g, b, a).next();

        vertexConsumer.vertex(matrix4f, (float)(x1 + xoffset), (float)(y + yoffset), (float)(z1 + zoffset)).color(r, g, b, a).next();
        vertexConsumer.vertex(matrix4f, (float)(x1 + xoffset), (float)(y1 + yoffset), (float)(z1 + zoffset)).color(r, g, b, a).next();
        //*
        vertexConsumer.vertex(matrix4f, (float)(x + xoffset), (float)(y + yoffset), (float)(z + zoffset)).color(r, g, b, a).next();
        vertexConsumer.vertex(matrix4f, (float)(x + xoffset), (float)(y + yoffset), (float)(z1 + zoffset)).color(r, g, b, a).next();

        vertexConsumer.vertex(matrix4f, (float)(x1 + xoffset), (float)(y + yoffset), (float)(z + zoffset)).color(r, g, b, a).next();
        vertexConsumer.vertex(matrix4f, (float)(x1 + xoffset), (float)(y + yoffset), (float)(z1 + zoffset)).color(r, g, b, a).next();

        vertexConsumer.vertex(matrix4f, (float)(x + xoffset), (float)(y1 + yoffset), (float)(z + zoffset)).color(r, g, b, a).next();
        vertexConsumer.vertex(matrix4f, (float)(x + xoffset), (float)(y1 + yoffset), (float)(z1 + zoffset)).color(r, g, b, a).next();

        vertexConsumer.vertex(matrix4f, (float)(x1 + xoffset), (float)(y1 + yoffset), (float)(z + zoffset)).color(r, g, b, a).next();
        vertexConsumer.vertex(matrix4f, (float)(x1 + xoffset), (float)(y1 + yoffset), (float)(z1 + zoffset)).color(r, g, b, a).next();
    }
}
