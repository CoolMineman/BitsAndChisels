package io.github.coolmineman.bitsandchisels.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.coolmineman.bitsandchisels.BitsAndChisels;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Mixin(ParticleManager.class)
public class ParticleYeeter {
    @Shadow ClientWorld world;

    @Inject(method = "addBlockBreakParticles", at = @At("HEAD"), cancellable = true)
    public void yeet(BlockPos pos, BlockState state, CallbackInfo yeet) {
        if (state.isOf(BitsAndChisels.BITS_BLOCK)) yeet.cancel();
    }

    @Inject(method = "addBlockBreakingParticles", at = @At("HEAD"), cancellable = true)
    public void alsoyeet(BlockPos pos, Direction direction, CallbackInfo yeet) {
        if (this.world.getBlockState(pos).isOf(BitsAndChisels.BITS_BLOCK)) yeet.cancel();
    }
}
