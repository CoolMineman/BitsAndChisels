package io.github.coolmineman.bitsandchisels.blueprints;

import java.util.HashMap;
import java.util.Map;

import io.github.coolmineman.bitsandchisels.BitNbtUtil;
import io.github.coolmineman.bitsandchisels.BitsAndChisels;
import io.github.coolmineman.bitsandchisels.BitsBlockEntity;
import io.github.coolmineman.bitsandchisels.api.BitUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Blueprint extends Item {
    private static final String BLUEPRINT_STRING = "blueprint";

    public Blueprint(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (!world.isClient) {
            if (context.getStack().getSubTag(BLUEPRINT_STRING) == null) {
                BlockEntity e1 = world.getBlockEntity(context.getBlockPos());
                if (e1 instanceof BitsBlockEntity) {
                    BitsBlockEntity e = (BitsBlockEntity) e1;
                    CompoundTag blueprintTag = context.getStack().getOrCreateSubTag(BLUEPRINT_STRING);
                    BitNbtUtil.write3DBitArray(blueprintTag, e.getStates());
                    return ActionResult.SUCCESS;
                }
            } else {
                BlockPos placePos = context.getBlockPos().offset(context.getSide());
                BlockState[][][] bits = new BlockState[16][16][16];
                BitNbtUtil.read3DBitArray(context.getStack().getSubTag(BLUEPRINT_STRING), bits);
                Map<BlockState, IntList> invMap = indexPlayerInventory(context.getPlayer());
                boolean update = false;
                for (int i = 0; i < 16; i++) {
                    for (int j = 0; j < 16; j++) {
                        for (int k = 0; k < 16; k++) {
                            BlockState bitState = bits[i][j][k];
                            if (BitUtils.canPlace(world, placePos, i, j, k)) {
                                if (context.getPlayer().isCreative()) {
                                    if (BitUtils.setBit(world, placePos, i, j, k, bitState)) update = true;
                                } else {
                                    IntList checkSlots = invMap.get(bitState);
                                    if (checkSlots != null) {
                                        for (int z = 0; z < checkSlots.size(); z++) {
                                            int slot = checkSlots.getInt(z);
                                            ItemStack slotStack = context.getPlayer().getInventory().getStack(slot);
                                            if (!slotStack.isEmpty() && BitUtils.setBit(world, placePos, i, j, k, bitState)) {
                                                slotStack.decrement(1);
                                                update = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (update) {
                    BitUtils.update(world, placePos);
                    context.getPlayer().getItemCooldownManager().set(BitsAndChisels.BLUEPRINT, 5);
                    return ActionResult.SUCCESS;
                }
            }
        }
        return ActionResult.PASS;
    }

    private Map<BlockState, IntList> indexPlayerInventory(PlayerEntity playerEntity) {
        HashMap<BlockState, IntList> result = new HashMap<>();
        for (int i = 0; i < playerEntity.getInventory().size(); i++) {
            ItemStack stack = playerEntity.getInventory().getStack(i);
            if (stack.getItem() == BitsAndChisels.BIT_ITEM) {
                BlockState itemState = NbtHelper.toBlockState(stack.getSubTag("bit"));
                if (!itemState.isAir()) {
                    IntList intList = result.computeIfAbsent(itemState, k -> new IntArrayList(5));
                    intList.add(i);
                }
            }
        }
        return result;
    }

    @Override
    public Text getName(ItemStack stack) {
        return stack.getSubTag(BLUEPRINT_STRING) == null ? new TranslatableText(this.getTranslationKey() + ".unwritten") : super.getName(stack);
    }
    
}
