package io.github.coolmineman.bitsandchisels.api.client;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

public interface AirSwingItem {
    void airSwing(ClientPlayerEntity player, ItemStack stack);
}
