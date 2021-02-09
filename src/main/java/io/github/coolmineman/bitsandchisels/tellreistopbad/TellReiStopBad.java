package io.github.coolmineman.bitsandchisels.tellreistopbad;

import io.github.coolmineman.bitsandchisels.BitsAndChisels;
import me.shedaniel.rei.api.EntryRegistry;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.minecraft.util.Identifier;

public class TellReiStopBad implements REIPluginV0 {

	@Override
	public Identifier getPluginIdentifier() {
		return new Identifier(BitsAndChisels.MODID, "tellreistopbad");
	}
    
    @Override
    public void postRegister() {
        EntryRegistry.getInstance().removeEntryIf(entitry -> entitry.getItem() == BitsAndChisels.BIT_ITEM || entitry.getItem() == BitsAndChisels.BITS_BLOCK_ITEM);
    }
}
