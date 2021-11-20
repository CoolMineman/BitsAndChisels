package io.github.coolmineman.bitsandchisels.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.coolmineman.bitsandchisels.BitsAndChisels;
import net.minecraft.client.network.ClientPlayerEntity;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "tickMovement", at = @At("RETURN"))
    void e(CallbackInfo funniVarName) {
        if (!(((ClientPlayerEntity)(Object)this).isSneaking())) BitsAndChisels.BIT_ITEM.haspos1 = false;
    }
}
