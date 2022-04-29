package io.github.coolmineman.bitsandchisels.claimsgarbage;

import draylar.goml.api.ClaimUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public enum GomlIsVeryStupid implements Stupid {
    INSTANCE;

    @Override
    public boolean canBreak(ServerPlayerEntity player, BlockPos pos) {
        return ClaimUtils.canModify(player.world, pos, player);
    }

    @Override
    public boolean canPlace(ServerPlayerEntity player, BlockPos pos) {
        return ClaimUtils.canModify(player.world, pos, player);
    }

}
