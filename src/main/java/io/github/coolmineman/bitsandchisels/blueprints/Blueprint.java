package io.github.coolmineman.bitsandchisels.blueprints;

import java.util.HashMap;
import java.util.Map;

import io.github.coolmineman.bitsandchisels.BitNbtUtil;
import io.github.coolmineman.bitsandchisels.BitsAndChisels;
import io.github.coolmineman.bitsandchisels.api.BitUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.block.BlockState;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Blueprint extends Item implements CauldronBehavior {
    private static final String BLUEPRINT_STRING = "blueprint";

    public Blueprint(Settings settings) {
        super(settings);
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.map().put(this, this);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (!world.isClient) {
            if (context.getStack().getSubNbt(BLUEPRINT_STRING) == null) {
                BlockState[][][] target = BitUtils.getBitArray(world, context.getBlockPos());
                if (target != null) {
                    NbtCompound blueprintTag = context.getStack().getOrCreateSubNbt(BLUEPRINT_STRING);
                    BitNbtUtil.write3DBitArray(blueprintTag, target);
                    return ActionResult.SUCCESS;
                }
            } else {
                BlockPos placePos = context.getBlockPos().offset(context.getSide());
                BlockState[][][] bits = new BlockState[16][16][16];
                BitNbtUtil.read3DBitArray(context.getStack().getSubNbt(BLUEPRINT_STRING), bits);
                Map<BlockState, IntList> invMap = indexPlayerInventory(context.getPlayer());
                boolean update = false;
                for (int i = 0; i < 16; i++) {
                    for (int j = 0; j < 16; j++) {
                        for (int k = 0; k < 16; k++) {
                            BlockState bitState = bits[i][j][k];
                            if (BitUtils.canPlace(context.getPlayer(), placePos, i, j, k)) {
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
                BlockState itemState = BitUtils.getBit(stack);
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
        return stack.getSubNbt(BLUEPRINT_STRING) == null ? Text.translatable(this.getTranslationKey() + ".unwritten") : super.getName(stack);
    }

    @Override
    public ActionResult interact(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
        stack.removeSubNbt(BLUEPRINT_STRING);
        return ActionResult.SUCCESS;
    }
    
}
