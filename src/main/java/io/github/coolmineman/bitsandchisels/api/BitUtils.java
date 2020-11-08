package io.github.coolmineman.bitsandchisels.api;

import org.jetbrains.annotations.Nullable;

import io.github.coolmineman.bitsandchisels.BitsBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BitUtils {
    private BitUtils(){}

    public static void setBit(World world, @Nullable PlayerEntity player, BlockPos block, int x, int y, int z, BlockState state, boolean updateclients) {
        BlockEntity e1 = world.getBlockEntity(block);
        if (e1 instanceof BitsBlockEntity) {
            BitsBlockEntity e = (BitsBlockEntity) e1;
            e.setState(x, y, z, state);
            e.rebuild(false);
            if (updateclients) {
                if (player != null) e.dontupdateuuid = player.getUuid();
                e.sync();
            }
        }
    }
}
