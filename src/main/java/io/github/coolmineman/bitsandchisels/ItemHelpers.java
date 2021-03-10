package io.github.coolmineman.bitsandchisels;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

public class ItemHelpers {
    private ItemHelpers(){}

    public static ItemStack blockToItem(Block block, BlockEntity entity) {
        return addBlockEntityNbt(new ItemStack(block), entity);
    }

    public static ItemStack addBlockEntityNbt(ItemStack stack, BlockEntity blockEntity) {
        CompoundTag compoundTag = blockEntity.writeNbt(new CompoundTag());
        CompoundTag compoundTag3;
        stack.putSubTag("BlockEntityTag", compoundTag);
        compoundTag3 = new CompoundTag();
        ListTag listTag = new ListTag();
        listTag.add(StringTag.of("\"(+NBT)\""));
        compoundTag3.put("Lore", listTag);
        stack.putSubTag("display", compoundTag3);
        return stack;
    }
}
