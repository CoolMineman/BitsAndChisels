package io.github.coolmineman.bitsandchisels.claim;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class DummyLandClaimProvider extends LandClaimProvider {
    @Override
    public boolean canBreak(ServerPlayerEntity entity, BlockPos pos) {
        return true;
    }

    @Override
    public boolean canPlace(ServerPlayerEntity entity, BlockPos pos) {
        return true;
    }
}
