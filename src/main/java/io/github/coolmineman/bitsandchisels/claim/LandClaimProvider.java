package io.github.coolmineman.bitsandchisels.claim;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class LandClaimProvider {
    public abstract boolean canBreak(ServerPlayerEntity entity, BlockPos pos);
    public abstract boolean canPlace(ServerPlayerEntity entity, BlockPos pos);
}