package io.github.coolmineman.bitsandchisels.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.coolmineman.bitsandchisels.BitsBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(ThreadedAnvilChunkStorage.class)
public class ThreadedAnvilChunkStorageMixin {
    @Inject(at = @At("TAIL"), method = "sendChunkDataPackets")
    private void bcOnSendChunkDataPackets(ServerPlayerEntity player, Packet<?>[] packets, WorldChunk chunk, CallbackInfo oops) {
        for (Map.Entry<BlockPos, BlockEntity> a : chunk.getBlockEntities().entrySet()) {
            if (a.getValue() instanceof BitsBlockEntity) {
                ((BitsBlockEntity)a.getValue()).sync(); // Avoid packet limits
            }
        }
    }
}
