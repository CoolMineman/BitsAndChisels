package io.github.coolmineman.bitsandchisels.wrench;

import io.github.coolmineman.bitsandchisels.BitsAndChisels;
import io.github.coolmineman.bitsandchisels.BitsBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction.Axis;

public class WrenchItem extends Item {

    public WrenchItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getWorld().isClient && context.getWorld().getBlockState(context.getBlockPos()).isOf(BitsAndChisels.BITS_BLOCK)) {
            BlockEntity e1 = context.getWorld().getBlockEntity(context.getBlockPos());
            if (e1 instanceof BitsBlockEntity) {
                BitsBlockEntity e = (BitsBlockEntity) e1;
                rotate(context.getSide().getAxis(), e);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    //Rotation Algorithm from https://stackoverflow.com/questions/53110374/how-to-rotate-2-d-array-in-java, extended to 3D
    void rotate(Axis axis, BitsBlockEntity e) {
        BlockState[][][] rotated = new BlockState[16][16][16];
        switch(axis) {
            case X:
                for (int i = 0; i < 16; i++) {
                    for (int j = 0; j < 16; j++) {
                        for (int k = 0; k < 16; k++) {
                            rotated[i][j][k] = e.getState(i, 16 - k - 1, j);
                        }
                    }
                }
                break;
            case Y:
                for (int i = 0; i < 16; i++) {
                    for (int j = 0; j < 16; j++) {
                        for (int k = 0; k < 16; k++) {
                            rotated[i][j][k] = e.getState(16 - k - 1, j, i);
                        }
                    }
                }
                break;
            case Z:
                for (int i = 0; i < 16; i++) {
                    for (int j = 0; j < 16; j++) {
                        for (int k = 0; k < 16; k++) {
                            rotated[i][j][k] = e.getState(16 - j - 1, i, k);
                        }
                    }
                }
                break;
        }
        e.setStates(rotated);
        e.rebuildServer();
        e.sync();
    }
    
}
