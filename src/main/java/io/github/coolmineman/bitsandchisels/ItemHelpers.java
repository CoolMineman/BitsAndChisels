package io.github.coolmineman.bitsandchisels;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

public class ItemHelpers {
    private ItemHelpers(){}

    public static ItemStack blockToItem(Block block, BlockEntity entity) {
        return addBlockEntityNbt(new ItemStack(block), entity);
    }

    public static ItemStack addBlockEntityNbt(ItemStack stack, BlockEntity blockEntity) {
        NbtCompound compoundTag = blockEntity.createNbt();
        NbtCompound compoundTag3;
        stack.setSubNbt("BlockEntityTag", compoundTag);
        compoundTag3 = new NbtCompound();
        NbtList listTag = new NbtList();
        listTag.add(NbtString.of("\"(+NBT)\""));
        compoundTag3.put("Lore", listTag);
        stack.setSubNbt("display", compoundTag3);
        return stack;
    }
}
