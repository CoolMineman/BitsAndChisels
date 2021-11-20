package io.github.coolmineman.bitsandchisels.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.coolmineman.bitsandchisels.api.client.AirSwingItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "doAttack", at = @At("TAIL"))
    void doAttack(CallbackInfo c) {
        MinecraftClient thiz = (MinecraftClient) (Object) this;
        if (thiz.crosshairTarget.getType() == HitResult.Type.MISS) {
            ItemStack stack = thiz.player.getMainHandStack();
            Item item = stack.getItem();
            if (item instanceof AirSwingItem) {
                ((AirSwingItem)item).airSwing(thiz.player, stack);
            }
        }
    }
}
