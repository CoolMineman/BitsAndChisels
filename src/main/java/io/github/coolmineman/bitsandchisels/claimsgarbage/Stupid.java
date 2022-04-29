package io.github.coolmineman.bitsandchisels.claimsgarbage;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public interface Stupid {
    boolean canBreak(ServerPlayerEntity player, BlockPos pos);
    boolean canPlace(ServerPlayerEntity player, BlockPos pos);
}
