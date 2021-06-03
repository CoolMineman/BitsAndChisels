package io.github.coolmineman.bitsandchisels.tellreistopbad;

import io.github.coolmineman.bitsandchisels.BitsAndChisels;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.item.ItemStack;

public class TellReiStopBad implements REIClientPlugin {
    @Override
    public void registerEntries(EntryRegistry registry) {
        registry.removeEntryIf(
            entry -> entry.getType() == VanillaEntryTypes.ITEM && (entry.<ItemStack>castValue().getItem() == BitsAndChisels.BIT_ITEM || entry.<ItemStack>castValue().getItem() == BitsAndChisels.BITS_BLOCK_ITEM)
        );
    }
}
