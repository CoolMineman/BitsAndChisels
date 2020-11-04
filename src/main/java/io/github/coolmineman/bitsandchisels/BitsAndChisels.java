package io.github.coolmineman.bitsandchisels;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BitsAndChisels implements ModInitializer {

	public static final BitsBlock BITS_BLOCK = new BitsBlock(FabricBlockSettings.of(Material.METAL).hardness(4.0f));
	public static BlockEntityType<BitsBlockEntity> BITS_BLOCK_ENTITY;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		Registry.register(Registry.BLOCK, new Identifier("bitsandchisels", "bits_block"), BITS_BLOCK);
		BITS_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "bitsandchisels:bits_block_entity", BlockEntityType.Builder.create(BitsBlockEntity::new, BITS_BLOCK).build(null));
		Registry.register(Registry.ITEM, new Identifier("bitsandchisels", "bits_block"), new BlockItem(BITS_BLOCK, new Item.Settings()));
	}
	
}
