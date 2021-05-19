package io.github.coolmineman.bitsandchisels.api.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

@SuppressWarnings("all")
public interface RedBoxCallback {
    Event<RedBoxCallback> EVENT = EventFactory.createArrayBacked(RedBoxCallback.class,
        listeners -> (redBoxDrawer, matrixStack, vertexConsumer, worldoffsetx, worldoffsety, worldoffsetz) -> {
            for (RedBoxCallback listener : listeners) {
                listener.drawBoxes(redBoxDrawer, matrixStack, vertexConsumer, worldoffsetx, worldoffsety, worldoffsetz);
            }
        }
    );

    public void drawBoxes(IRedBoxDrawer redBoxDrawer, MatrixStack matrixStack, VertexConsumer vertexConsumer, double worldoffsetx, double worldoffsety, double worldoffsetz);
    
    public static interface IRedBoxDrawer {
        public void drawRedBox(MatrixStack matrixStack, VertexConsumer vertexConsumer, BlockPos block, int bitx, int bity, int bitz, int bitx2, int bity2, int bitz2, double worldoffsetx, double worldoffsety, double worldoffsetz);
    }
}
