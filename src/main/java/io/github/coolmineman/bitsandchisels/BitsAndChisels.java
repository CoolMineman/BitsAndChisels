package io.github.coolmineman.bitsandchisels;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.coolmineman.bitsandchisels.blueprints.Blueprint;
import io.github.coolmineman.bitsandchisels.chisel.DiamondChisel;
import io.github.coolmineman.bitsandchisels.chisel.IronChisel;
import io.github.coolmineman.bitsandchisels.wrench.WrenchItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BitsAndChisels implements ModInitializer {

	public static final String MODID = "bitsandchisels";

	public static final Logger LOGGER = LogManager.getLogger("BitsAndChisels");

	public static final boolean CANVAS = FabricLoader.getInstance().isModLoaded("canvas");

	public static final BitsBlock BITS_BLOCK = new BitsBlock(FabricBlockSettings.of(Material.METAL).nonOpaque().dynamicBounds().hardness(4.0f));
	public static final BlockItem BITS_BLOCK_ITEM = new BlockItem(BITS_BLOCK, new Item.Settings());
	public static final DiamondChisel DIAMOND_CHISEL = new DiamondChisel(new Item.Settings());
	public static final IronChisel IRON_CHISEL = new IronChisel(new Item.Settings());
	public static final WrenchItem WRENCH_ITEM = new WrenchItem(new Item.Settings());
	public static final Blueprint BLUEPRINT = new Blueprint(new Item.Settings().maxCount(1));
	public static BlockEntityType<BitsBlockEntity> BITS_BLOCK_ENTITY;

	public static final BitItem BIT_ITEM = new BitItem(new Item.Settings().maxCount(1_000_000_000));

	public static final ItemGroup OTHER_GROUP = FabricItemGroupBuilder.create(
		new Identifier(MODID, MODID))
		.icon(() -> new ItemStack(DIAMOND_CHISEL))
		.appendItems(stacks -> {
			stacks.add(new ItemStack(DIAMOND_CHISEL));
			stacks.add(new ItemStack(IRON_CHISEL));
			stacks.add(new ItemStack(WRENCH_ITEM));
			stacks.add(new ItemStack(BLUEPRINT));
		}
	)
	.build();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		Registry.register(Registry.BLOCK, new Identifier(MODID, "bits_block"), BITS_BLOCK);
		BITS_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "bitsandchisels:bits_block_entity", FabricBlockEntityTypeBuilder.create(BitsBlockEntity::new, BITS_BLOCK).build(null));
		Registry.register(Registry.ITEM, new Identifier(MODID, "bits_block"), BITS_BLOCK_ITEM);
		Registry.register(Registry.ITEM, new Identifier(MODID, "diamond_chisel"), DIAMOND_CHISEL);
		DIAMOND_CHISEL.init();
		Registry.register(Registry.ITEM, new Identifier(MODID, "iron_chisel"), IRON_CHISEL);
		IRON_CHISEL.init();
		Registry.register(Registry.ITEM, new Identifier(MODID, "bit_item"), BIT_ITEM);
		BIT_ITEM.init();
		Registry.register(Registry.ITEM, new Identifier(MODID, "wrench"), WRENCH_ITEM);
		Registry.register(Registry.ITEM, new Identifier(MODID, "blueprint"), BLUEPRINT);
	}
	
}
